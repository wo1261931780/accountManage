package wo1261931780.accountManage.service;

import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.dto.request.AccountSearchDTO;
import wo1261931780.accountManage.dto.response.AccountSearchVO;

/**
 * 账号搜索增强服务
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface AccountSearchService {

    /**
     * 高级搜索账号
     *
     * @param dto 搜索条件
     * @return 搜索结果
     */
    PageResult<AccountSearchVO> search(AccountSearchDTO dto);

    /**
     * 全文搜索账号
     *
     * @param keyword 关键词
     * @param page    页码
     * @param size    每页数量
     * @return 搜索结果
     */
    PageResult<AccountSearchVO> fullTextSearch(String keyword, int page, int size);

    /**
     * 智能搜索建议（自动补全）
     *
     * @param keyword 关键词
     * @param limit   返回数量
     * @return 建议列表
     */
    java.util.List<String> searchSuggestions(String keyword, int limit);
}
