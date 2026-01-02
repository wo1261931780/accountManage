package wo1261931780.accountManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.dto.tag.TagDTO;
import wo1261931780.accountManage.dto.tag.TagVO;
import wo1261931780.accountManage.entity.AccountTag;
import wo1261931780.accountManage.entity.Tag;
import wo1261931780.accountManage.mapper.AccountTagMapper;
import wo1261931780.accountManage.mapper.TagMapper;
import wo1261931780.accountManage.service.TagService;

import java.util.List;

/**
 * 标签服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final AccountTagMapper accountTagMapper;

    @Override
    public List<TagVO> getAllTags() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getDeleted, 0)
                .orderByAsc(Tag::getSortOrder);

        List<Tag> tags = tagMapper.selectList(wrapper);
        return tags.stream()
                .map(this::convertToVO)
                .toList();
    }

    @Override
    public TagVO getTagById(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null || tag.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "标签不存在");
        }
        return convertToVO(tag);
    }

    @Override
    @Transactional
    public TagVO createTag(TagDTO dto) {
        // 检查名称是否重复
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getTagName, dto.getTagName())
                .eq(Tag::getDeleted, 0);
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "标签名称已存在");
        }

        Tag tag = new Tag();
        tag.setTagName(dto.getTagName());
        tag.setTagColor(dto.getTagColor() != null ? dto.getTagColor() : "#409EFF");
        tag.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        tag.setDeleted(0);

        tagMapper.insert(tag);
        log.info("创建标签: {}", tag.getTagName());
        return convertToVO(tag);
    }

    @Override
    @Transactional
    public TagVO updateTag(Long tagId, TagDTO dto) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null || tag.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "标签不存在");
        }

        // 检查名称是否重复（排除自己）
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getTagName, dto.getTagName())
                .eq(Tag::getDeleted, 0)
                .ne(Tag::getId, tagId);
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "标签名称已存在");
        }

        tag.setTagName(dto.getTagName());
        if (dto.getTagColor() != null) {
            tag.setTagColor(dto.getTagColor());
        }
        if (dto.getSortOrder() != null) {
            tag.setSortOrder(dto.getSortOrder());
        }

        tagMapper.updateById(tag);
        log.info("更新标签: {}", tag.getTagName());
        return convertToVO(tag);
    }

    @Override
    @Transactional
    public boolean deleteTag(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null || tag.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "标签不存在");
        }

        // 删除标签（逻辑删除）
        tag.setDeleted(1);
        tagMapper.updateById(tag);

        // 删除账号-标签关联
        LambdaQueryWrapper<AccountTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountTag::getTagId, tagId);
        accountTagMapper.delete(wrapper);

        log.info("删除标签: {}", tag.getTagName());
        return true;
    }

    @Override
    public List<Tag> getTagsByAccountId(Long accountId) {
        return accountTagMapper.selectTagsByAccountId(accountId);
    }

    @Override
    @Transactional
    public void setAccountTags(Long accountId, List<Long> tagIds) {
        // 删除原有关联
        LambdaQueryWrapper<AccountTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountTag::getAccountId, accountId);
        accountTagMapper.delete(wrapper);

        // 添加新关联
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                AccountTag accountTag = new AccountTag();
                accountTag.setAccountId(accountId);
                accountTag.setTagId(tagId);
                accountTagMapper.insert(accountTag);
            }
        }
        log.debug("设置账号标签, accountId: {}, tagIds: {}", accountId, tagIds);
    }

    @Override
    @Transactional
    public void addTagToAccount(Long accountId, Long tagId) {
        // 检查是否已存在
        LambdaQueryWrapper<AccountTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountTag::getAccountId, accountId)
                .eq(AccountTag::getTagId, tagId);
        if (accountTagMapper.selectCount(wrapper) > 0) {
            return; // 已存在，不重复添加
        }

        AccountTag accountTag = new AccountTag();
        accountTag.setAccountId(accountId);
        accountTag.setTagId(tagId);
        accountTagMapper.insert(accountTag);
        log.debug("添加账号标签, accountId: {}, tagId: {}", accountId, tagId);
    }

    @Override
    @Transactional
    public void removeTagFromAccount(Long accountId, Long tagId) {
        LambdaQueryWrapper<AccountTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountTag::getAccountId, accountId)
                .eq(AccountTag::getTagId, tagId);
        accountTagMapper.delete(wrapper);
        log.debug("移除账号标签, accountId: {}, tagId: {}", accountId, tagId);
    }

    @Override
    public List<Long> getAccountIdsByTagId(Long tagId) {
        return accountTagMapper.selectAccountIdsByTagId(tagId);
    }

    private TagVO convertToVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setTagName(tag.getTagName());
        vo.setTagColor(tag.getTagColor());
        vo.setSortOrder(tag.getSortOrder());
        vo.setCreateTime(tag.getCreateTime());
        vo.setUpdateTime(tag.getUpdateTime());

        // 统计关联账号数量
        LambdaQueryWrapper<AccountTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountTag::getTagId, tag.getId());
        vo.setAccountCount(accountTagMapper.selectCount(wrapper).intValue());

        return vo;
    }
}
