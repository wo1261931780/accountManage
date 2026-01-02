package wo1261931780.accountManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import wo1261931780.accountManage.dto.request.PlatformQueryDTO;
import wo1261931780.accountManage.entity.Platform;

import java.util.List;

/**
 * 平台 Mapper 接口
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Mapper
public interface PlatformMapper extends BaseMapper<Platform> {

    /**
     * 分页查询平台（带类型名称）
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<Platform> selectPlatformPage(Page<Platform> page, @Param("query") PlatformQueryDTO query);

    /**
     * 根据 ID 查询平台（带类型名称）
     *
     * @param id 平台ID
     * @return 平台信息
     */
    @Select("""
            SELECT p.*, pt.type_name as platform_type_name
            FROM sys_platform p
            LEFT JOIN sys_platform_type pt ON p.platform_type_id = pt.id AND pt.deleted = 0
            WHERE p.id = #{id} AND p.deleted = 0
            """)
    Platform selectPlatformById(@Param("id") Long id);

    /**
     * 查询所有启用的平台（下拉选择用）
     *
     * @return 平台列表
     */
    @Select("""
            SELECT p.*, pt.type_name as platform_type_name
            FROM sys_platform p
            LEFT JOIN sys_platform_type pt ON p.platform_type_id = pt.id AND pt.deleted = 0
            WHERE p.deleted = 0 AND p.status = 1
            ORDER BY p.sort_order, p.id
            """)
    List<Platform> selectAllEnabled();

    /**
     * 根据类型ID查询平台
     *
     * @param typeId 类型ID
     * @return 平台列表
     */
    @Select("""
            SELECT p.*, pt.type_name as platform_type_name
            FROM sys_platform p
            LEFT JOIN sys_platform_type pt ON p.platform_type_id = pt.id AND pt.deleted = 0
            WHERE p.platform_type_id = #{typeId} AND p.deleted = 0 AND p.status = 1
            ORDER BY p.sort_order, p.id
            """)
    List<Platform> selectByTypeId(@Param("typeId") Long typeId);
}
