package wo1261931780.accountManage.service;

import wo1261931780.accountManage.dto.tag.TagDTO;
import wo1261931780.accountManage.dto.tag.TagVO;
import wo1261931780.accountManage.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {

    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<TagVO> getAllTags();

    /**
     * 根据ID获取标签
     * @param tagId 标签ID
     * @return 标签信息
     */
    TagVO getTagById(Long tagId);

    /**
     * 创建标签
     * @param dto 标签信息
     * @return 创建的标签
     */
    TagVO createTag(TagDTO dto);

    /**
     * 更新标签
     * @param tagId 标签ID
     * @param dto 标签信息
     * @return 更新后的标签
     */
    TagVO updateTag(Long tagId, TagDTO dto);

    /**
     * 删除标签
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean deleteTag(Long tagId);

    /**
     * 获取账号关联的标签
     * @param accountId 账号ID
     * @return 标签列表
     */
    List<Tag> getTagsByAccountId(Long accountId);

    /**
     * 为账号设置标签（替换）
     * @param accountId 账号ID
     * @param tagIds 标签ID列表
     */
    void setAccountTags(Long accountId, List<Long> tagIds);

    /**
     * 为账号添加标签
     * @param accountId 账号ID
     * @param tagId 标签ID
     */
    void addTagToAccount(Long accountId, Long tagId);

    /**
     * 移除账号的标签
     * @param accountId 账号ID
     * @param tagId 标签ID
     */
    void removeTagFromAccount(Long accountId, Long tagId);

    /**
     * 根据标签查询账号ID列表
     * @param tagId 标签ID
     * @return 账号ID列表
     */
    List<Long> getAccountIdsByTagId(Long tagId);
}
