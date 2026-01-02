package wo1261931780.accountManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import wo1261931780.accountManage.dto.request.AccountQueryDTO;
import wo1261931780.accountManage.entity.Account;

import java.util.List;

/**
 * 账号 Mapper 接口
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 分页查询账号（带平台信息）
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<Account> selectAccountPage(Page<Account> page, @Param("query") AccountQueryDTO query);

    /**
     * 根据 ID 查询账号（带平台信息）
     *
     * @param id 账号ID
     * @return 账号信息
     */
    @Select("""
            SELECT a.*, p.platform_name, pt.type_name as platform_type_name
            FROM sys_account a
            LEFT JOIN sys_platform p ON a.platform_id = p.id AND p.deleted = 0
            LEFT JOIN sys_platform_type pt ON p.platform_type_id = pt.id AND pt.deleted = 0
            WHERE a.id = #{id} AND a.deleted = 0
            """)
    Account selectAccountById(@Param("id") Long id);

    /**
     * 根据平台ID查询账号列表
     *
     * @param platformId 平台ID
     * @return 账号列表
     */
    @Select("""
            SELECT a.*, p.platform_name, pt.type_name as platform_type_name
            FROM sys_account a
            LEFT JOIN sys_platform p ON a.platform_id = p.id AND p.deleted = 0
            LEFT JOIN sys_platform_type pt ON p.platform_type_id = pt.id AND pt.deleted = 0
            WHERE a.platform_id = #{platformId} AND a.deleted = 0
            ORDER BY a.create_time DESC
            """)
    List<Account> selectByPlatformId(@Param("platformId") Long platformId);
}
