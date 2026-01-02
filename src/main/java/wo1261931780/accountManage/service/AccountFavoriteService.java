package wo1261931780.accountManage.service;

import wo1261931780.accountManage.dto.response.FavoriteAccountVO;

import java.util.List;

/**
 * 账号收藏服务
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface AccountFavoriteService {

    /**
     * 获取用户的收藏列表
     *
     * @return 收藏账号列表
     */
    List<FavoriteAccountVO> getFavorites();

    /**
     * 添加收藏
     *
     * @param accountId 账号ID
     * @return 是否成功
     */
    boolean addFavorite(Long accountId);

    /**
     * 取消收藏
     *
     * @param accountId 账号ID
     * @return 是否成功
     */
    boolean removeFavorite(Long accountId);

    /**
     * 检查是否已收藏
     *
     * @param accountId 账号ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long accountId);

    /**
     * 批量检查是否已收藏
     *
     * @param accountIds 账号ID列表
     * @return 已收藏的账号ID列表
     */
    List<Long> getFavoriteAccountIds(List<Long> accountIds);

    /**
     * 更新收藏排序
     *
     * @param accountId 账号ID
     * @param sortOrder 排序值
     * @return 是否成功
     */
    boolean updateSortOrder(Long accountId, Integer sortOrder);

    /**
     * 批量更新收藏排序
     *
     * @param accountIds 账号ID列表（按顺序）
     * @return 是否成功
     */
    boolean batchUpdateSortOrder(List<Long> accountIds);

    /**
     * 置顶收藏
     *
     * @param accountId 账号ID
     * @return 是否成功
     */
    boolean pinToTop(Long accountId);
}
