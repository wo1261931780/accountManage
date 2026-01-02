package wo1261931780.accountManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.dto.response.FavoriteAccountVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.AccountFavorite;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.mapper.AccountFavoriteMapper;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.security.UserContext;
import wo1261931780.accountManage.service.AccountFavoriteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 账号收藏服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountFavoriteServiceImpl implements AccountFavoriteService {

    private final AccountFavoriteMapper accountFavoriteMapper;
    private final AccountMapper accountMapper;
    private final PlatformMapper platformMapper;

    @Override
    public List<FavoriteAccountVO> getFavorites() {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFavorite::getUserId, userId)
                .orderByAsc(AccountFavorite::getSortOrder)
                .orderByDesc(AccountFavorite::getCreateTime);

        List<AccountFavorite> favorites = accountFavoriteMapper.selectList(wrapper);
        if (favorites.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询账号信息
        List<Long> accountIds = favorites.stream()
                .map(AccountFavorite::getAccountId)
                .toList();
        Map<Long, Account> accountMap = accountMapper.selectBatchIds(accountIds).stream()
                .collect(Collectors.toMap(Account::getId, a -> a));

        // 批量查询平台信息
        List<Long> platformIds = accountMap.values().stream()
                .map(Account::getPlatformId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        Map<Long, Platform> platformMap = platformMapper.selectBatchIds(platformIds).stream()
                .collect(Collectors.toMap(Platform::getId, p -> p));

        // 转换为VO
        return favorites.stream()
                .map(fav -> {
                    Account account = accountMap.get(fav.getAccountId());
                    if (account == null || account.getDeleted() == 1) {
                        return null;
                    }

                    FavoriteAccountVO vo = new FavoriteAccountVO();
                    vo.setFavoriteId(fav.getId());
                    vo.setAccountId(fav.getAccountId());
                    vo.setAccountName(account.getAccountName());
                    vo.setAccountAlias(account.getAccountAlias());
                    vo.setAccountStatus(account.getAccountStatus());
                    vo.setAccountStatusName(getStatusName(account.getAccountStatus()));
                    vo.setSortOrder(fav.getSortOrder());
                    vo.setFavoriteTime(fav.getCreateTime());

                    if (account.getPlatformId() != null) {
                        Platform platform = platformMap.get(account.getPlatformId());
                        if (platform != null) {
                            vo.setPlatformName(platform.getPlatformName());
                            vo.setPlatformIcon(platform.getPlatformIcon());
                        }
                    }

                    return vo;
                })
                .filter(vo -> vo != null)
                .toList();
    }

    @Override
    @Transactional
    public boolean addFavorite(Long accountId) {
        Long userId = getCurrentUserId();

        // 检查账号是否存在
        Account account = accountMapper.selectById(accountId);
        if (account == null || account.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "账号不存在");
        }

        // 检查是否已收藏
        LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFavorite::getUserId, userId)
                .eq(AccountFavorite::getAccountId, accountId);
        if (accountFavoriteMapper.selectCount(wrapper) > 0) {
            return true; // 已经收藏过了
        }

        // 获取最大排序值
        Integer maxSort = accountFavoriteMapper.selectMaxSortOrder(userId);

        // 添加收藏
        AccountFavorite favorite = new AccountFavorite();
        favorite.setUserId(userId);
        favorite.setAccountId(accountId);
        favorite.setSortOrder(maxSort + 1);

        accountFavoriteMapper.insert(favorite);
        log.info("添加收藏, userId: {}, accountId: {}", userId, accountId);
        return true;
    }

    @Override
    @Transactional
    public boolean removeFavorite(Long accountId) {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFavorite::getUserId, userId)
                .eq(AccountFavorite::getAccountId, accountId);

        int deleted = accountFavoriteMapper.delete(wrapper);
        log.info("取消收藏, userId: {}, accountId: {}, deleted: {}", userId, accountId, deleted);
        return deleted > 0;
    }

    @Override
    public boolean isFavorite(Long accountId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return false;
        }

        LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFavorite::getUserId, userId)
                .eq(AccountFavorite::getAccountId, accountId);

        return accountFavoriteMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Long> getFavoriteAccountIds(List<Long> accountIds) {
        Long userId = UserContext.getUserId();
        if (userId == null || accountIds == null || accountIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFavorite::getUserId, userId)
                .in(AccountFavorite::getAccountId, accountIds);

        return accountFavoriteMapper.selectList(wrapper).stream()
                .map(AccountFavorite::getAccountId)
                .toList();
    }

    @Override
    @Transactional
    public boolean updateSortOrder(Long accountId, Integer sortOrder) {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFavorite::getUserId, userId)
                .eq(AccountFavorite::getAccountId, accountId);

        AccountFavorite favorite = accountFavoriteMapper.selectOne(wrapper);
        if (favorite == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "收藏不存在");
        }

        favorite.setSortOrder(sortOrder);
        accountFavoriteMapper.updateById(favorite);
        return true;
    }

    @Override
    @Transactional
    public boolean batchUpdateSortOrder(List<Long> accountIds) {
        Long userId = getCurrentUserId();

        for (int i = 0; i < accountIds.size(); i++) {
            Long accountId = accountIds.get(i);
            LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AccountFavorite::getUserId, userId)
                    .eq(AccountFavorite::getAccountId, accountId);

            AccountFavorite favorite = accountFavoriteMapper.selectOne(wrapper);
            if (favorite != null) {
                favorite.setSortOrder(i);
                accountFavoriteMapper.updateById(favorite);
            }
        }

        log.info("批量更新收藏排序, userId: {}, count: {}", userId, accountIds.size());
        return true;
    }

    @Override
    @Transactional
    public boolean pinToTop(Long accountId) {
        return updateSortOrder(accountId, 0);
    }

    private Long getCurrentUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }
        return userId;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "已注销";
            case 1 -> "正常";
            case 2 -> "已冻结";
            case 3 -> "待验证";
            default -> "未知";
        };
    }
}
