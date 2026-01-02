package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.dto.request.AccountCreateDTO;
import wo1261931780.accountManage.dto.request.AccountQueryDTO;
import wo1261931780.accountManage.dto.request.AccountUpdateDTO;
import wo1261931780.accountManage.dto.response.AccountVO;
import wo1261931780.accountManage.entity.Account;

import java.util.List;

/**
 * 账号 Service 接口
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
public interface AccountService extends IService<Account> {

    /**
     * 分页查询账号
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<AccountVO> page(AccountQueryDTO query);

    /**
     * 根据ID获取账号（包含解密后的密码）
     *
     * @param id ID
     * @return 账号信息
     */
    AccountVO getById(Long id);

    /**
     * 根据平台ID获取账号列表
     *
     * @param platformId 平台ID
     * @return 账号列表
     */
    List<AccountVO> listByPlatformId(Long platformId);

    /**
     * 获取账号的解密密码
     *
     * @param id 账号ID
     * @return 解密后的密码
     */
    String getPassword(Long id);

    /**
     * 创建账号
     *
     * @param dto 创建请求
     * @return 创建结果
     */
    Account create(AccountCreateDTO dto);

    /**
     * 更新账号
     *
     * @param dto 更新请求
     * @return 更新结果
     */
    boolean update(AccountUpdateDTO dto);

    /**
     * 删除账号
     *
     * @param id ID
     * @return 删除结果
     */
    boolean delete(Long id);

    /**
     * 批量删除账号
     *
     * @param ids ID列表
     * @return 删除结果
     */
    boolean batchDelete(List<Long> ids);
}
