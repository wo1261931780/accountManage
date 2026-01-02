package wo1261931780.accountManage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.dto.request.AccountSearchDTO;
import wo1261931780.accountManage.dto.response.AccountSearchVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.AccountFavorite;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.entity.Tag;
import wo1261931780.accountManage.mapper.AccountFavoriteMapper;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.AccountTagMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.security.UserContext;
import wo1261931780.accountManage.service.AccountSearchService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 账号搜索增强服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSearchServiceImpl implements AccountSearchService {

    private final AccountMapper accountMapper;
    private final PlatformMapper platformMapper;
    private final AccountFavoriteMapper accountFavoriteMapper;
    private final AccountTagMapper accountTagMapper;

    @Override
    public PageResult<AccountSearchVO> search(AccountSearchDTO dto) {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getDeleted, 0);

        // 关键词搜索（多字段模糊匹配）
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            String keyword = "%" + dto.getKeyword().trim() + "%";
            wrapper.and(w -> w
                    .like(Account::getAccountName, dto.getKeyword())
                    .or().like(Account::getAccountAlias, dto.getKeyword())
                    .or().like(Account::getUid, dto.getKeyword())
                    .or().like(Account::getBindPhone, dto.getKeyword())
                    .or().like(Account::getBindEmail, dto.getKeyword())
                    .or().like(Account::getRemark, dto.getKeyword())
                    .or().like(Account::getTags, dto.getKeyword())
            );
        }

        // 平台ID列表筛选
        if (dto.getPlatformIds() != null && !dto.getPlatformIds().isEmpty()) {
            wrapper.in(Account::getPlatformId, dto.getPlatformIds());
        }

        // 平台类型ID筛选（需要先查询平台）
        if (dto.getPlatformTypeIds() != null && !dto.getPlatformTypeIds().isEmpty()) {
            List<Long> platformIds = getPlatformIdsByTypeIds(dto.getPlatformTypeIds());
            if (!platformIds.isEmpty()) {
                wrapper.in(Account::getPlatformId, platformIds);
            } else {
                // 如果没有匹配的平台，返回空结果
                return new PageResult<>(new ArrayList<>(), 0L, dto.getSize(), dto.getCurrent());
            }
        }

        // 账号状态筛选
        if (dto.getAccountStatuses() != null && !dto.getAccountStatuses().isEmpty()) {
            wrapper.in(Account::getAccountStatus, dto.getAccountStatuses());
        }

        // 重要程度筛选
        if (dto.getImportanceLevels() != null && !dto.getImportanceLevels().isEmpty()) {
            wrapper.in(Account::getImportanceLevel, dto.getImportanceLevels());
        }

        // 只看收藏
        if (Boolean.TRUE.equals(dto.getFavoritesOnly())) {
            Long userId = UserContext.getUserId();
            if (userId != null) {
                List<Long> favoriteAccountIds = accountFavoriteMapper.selectAccountIdsByUserId(userId);
                if (!favoriteAccountIds.isEmpty()) {
                    wrapper.in(Account::getId, favoriteAccountIds);
                } else {
                    return new PageResult<>(new ArrayList<>(), 0L, dto.getSize(), dto.getCurrent());
                }
            }
        }

        // 密码即将过期筛选
        if (Boolean.TRUE.equals(dto.getPasswordExpiring())) {
            wrapper.isNotNull(Account::getPasswordValidDays)
                    .gt(Account::getPasswordValidDays, 0)
                    .apply("DATE_ADD(password_update_time, INTERVAL password_valid_days DAY) <= DATE_ADD(NOW(), INTERVAL 7 DAY)");
        }

        // 排序
        applySorting(wrapper, dto.getSortField(), dto.getSortOrder());

        // 分页查询
        Page<Account> page = new Page<>(dto.getCurrent(), dto.getSize());
        Page<Account> result = accountMapper.selectPage(page, wrapper);

        // 获取收藏状态
        Set<Long> favoriteIds = getFavoriteAccountIds(result.getRecords());

        // 转换为VO
        List<AccountSearchVO> voList = result.getRecords().stream()
                .map(account -> convertToSearchVO(account, favoriteIds))
                .toList();

        return PageResult.of(result, voList);
    }

    @Override
    public PageResult<AccountSearchVO> fullTextSearch(String keyword, int page, int size) {
        AccountSearchDTO dto = new AccountSearchDTO();
        dto.setKeyword(keyword);
        dto.setCurrent((long) page);
        dto.setSize((long) size);
        return search(dto);
    }

    @Override
    public List<String> searchSuggestions(String keyword, int limit) {
        if (StrUtil.isBlank(keyword)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getDeleted, 0)
                .and(w -> w
                        .likeRight(Account::getAccountName, keyword)
                        .or().likeRight(Account::getAccountAlias, keyword)
                        .or().likeRight(Account::getUid, keyword)
                )
                .select(Account::getAccountName, Account::getAccountAlias, Account::getUid)
                .last("LIMIT " + limit * 3);

        List<Account> accounts = accountMapper.selectList(wrapper);

        // 收集建议词
        List<String> suggestions = new ArrayList<>();
        for (Account account : accounts) {
            if (StrUtil.isNotBlank(account.getAccountName()) &&
                    account.getAccountName().toLowerCase().contains(keyword.toLowerCase())) {
                suggestions.add(account.getAccountName());
            }
            if (StrUtil.isNotBlank(account.getAccountAlias()) &&
                    account.getAccountAlias().toLowerCase().contains(keyword.toLowerCase())) {
                suggestions.add(account.getAccountAlias());
            }
        }

        return suggestions.stream()
                .distinct()
                .limit(limit)
                .toList();
    }

    private List<Long> getPlatformIdsByTypeIds(List<Long> typeIds) {
        LambdaQueryWrapper<Platform> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Platform::getPlatformTypeId, typeIds)
                .eq(Platform::getDeleted, 0)
                .select(Platform::getId);
        return platformMapper.selectList(wrapper).stream()
                .map(Platform::getId)
                .toList();
    }

    private Set<Long> getFavoriteAccountIds(List<Account> accounts) {
        Long userId = UserContext.getUserId();
        if (userId == null || accounts.isEmpty()) {
            return Set.of();
        }

        List<Long> accountIds = accounts.stream().map(Account::getId).toList();
        LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFavorite::getUserId, userId)
                .in(AccountFavorite::getAccountId, accountIds);

        return accountFavoriteMapper.selectList(wrapper).stream()
                .map(AccountFavorite::getAccountId)
                .collect(Collectors.toSet());
    }

    private AccountSearchVO convertToSearchVO(Account account, Set<Long> favoriteIds) {
        AccountSearchVO vo = new AccountSearchVO();
        vo.setId(account.getId());
        vo.setPlatformId(account.getPlatformId());
        vo.setAccountName(account.getAccountName());
        vo.setAccountAlias(account.getAccountAlias());
        vo.setUid(account.getUid());
        vo.setBindPhone(maskPhone(account.getBindPhone()));
        vo.setBindEmail(maskEmail(account.getBindEmail()));
        vo.setAccountStatus(account.getAccountStatus());
        vo.setAccountStatusName(AccountSearchVO.getStatusName(account.getAccountStatus()));
        vo.setImportanceLevel(account.getImportanceLevel());
        vo.setImportanceLevelName(AccountSearchVO.getImportanceName(account.getImportanceLevel()));
        vo.setIsFavorite(favoriteIds.contains(account.getId()));
        vo.setLastLoginTime(account.getLastLoginTime());
        vo.setCreateTime(account.getCreateTime());

        // 获取平台信息
        if (account.getPlatformId() != null) {
            Platform platform = platformMapper.selectById(account.getPlatformId());
            if (platform != null) {
                vo.setPlatformName(platform.getPlatformName());
                vo.setPlatformIcon(platform.getPlatformIcon());
            }
        }

        // 获取标签
        List<Tag> tags = accountTagMapper.selectTagsByAccountId(account.getId());
        if (tags != null && !tags.isEmpty()) {
            vo.setTags(tags.stream().map(tag -> {
                AccountSearchVO.TagSimpleVO tagVO = new AccountSearchVO.TagSimpleVO();
                tagVO.setId(tag.getId());
                tagVO.setName(tag.getTagName());
                tagVO.setColor(tag.getTagColor());
                return tagVO;
            }).toList());
        }

        // 密码过期检查
        if (account.getPasswordValidDays() != null && account.getPasswordValidDays() > 0
                && account.getPasswordUpdateTime() != null) {
            LocalDateTime expireTime = account.getPasswordUpdateTime()
                    .plusDays(account.getPasswordValidDays());
            long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), expireTime);
            vo.setPasswordRemainingDays((int) remainingDays);
            vo.setPasswordExpiring(remainingDays <= 7);
        }

        return vo;
    }

    private void applySorting(LambdaQueryWrapper<Account> wrapper, String sortField, String sortOrder) {
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);

        if (StrUtil.isBlank(sortField)) {
            wrapper.orderByDesc(Account::getUpdateTime);
            return;
        }

        switch (sortField.toLowerCase()) {
            case "createtime" -> {
                if (isAsc) wrapper.orderByAsc(Account::getCreateTime);
                else wrapper.orderByDesc(Account::getCreateTime);
            }
            case "updatetime" -> {
                if (isAsc) wrapper.orderByAsc(Account::getUpdateTime);
                else wrapper.orderByDesc(Account::getUpdateTime);
            }
            case "accountname" -> {
                if (isAsc) wrapper.orderByAsc(Account::getAccountName);
                else wrapper.orderByDesc(Account::getAccountName);
            }
            case "lastlogintime" -> {
                if (isAsc) wrapper.orderByAsc(Account::getLastLoginTime);
                else wrapper.orderByDesc(Account::getLastLoginTime);
            }
            case "importancelevel" -> {
                if (isAsc) wrapper.orderByAsc(Account::getImportanceLevel);
                else wrapper.orderByDesc(Account::getImportanceLevel);
            }
            default -> wrapper.orderByDesc(Account::getUpdateTime);
        }
    }

    private String maskPhone(String phone) {
        if (StrUtil.isBlank(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String maskEmail(String email) {
        if (StrUtil.isBlank(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
