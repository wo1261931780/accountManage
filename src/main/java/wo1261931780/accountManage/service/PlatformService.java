package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.dto.request.PlatformCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformQueryDTO;
import wo1261931780.accountManage.dto.request.PlatformUpdateDTO;
import wo1261931780.accountManage.dto.response.PlatformVO;
import wo1261931780.accountManage.entity.Platform;

import java.util.List;

/**
 * 平台 Service 接口
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
public interface PlatformService extends IService<Platform> {

    /**
     * 分页查询平台
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<PlatformVO> page(PlatformQueryDTO query);

    /**
     * 获取所有启用的平台（下拉选择用）
     *
     * @return 平台列表
     */
    List<PlatformVO> listAll();

    /**
     * 根据ID获取平台
     *
     * @param id ID
     * @return 平台信息
     */
    PlatformVO getById(Long id);

    /**
     * 根据类型ID获取平台列表
     *
     * @param typeId 类型ID
     * @return 平台列表
     */
    List<PlatformVO> listByTypeId(Long typeId);

    /**
     * 根据编码获取平台
     *
     * @param code 编码
     * @return 平台信息
     */
    Platform getByCode(String code);

    /**
     * 创建平台
     *
     * @param dto 创建请求
     * @return 创建结果
     */
    Platform create(PlatformCreateDTO dto);

    /**
     * 更新平台
     *
     * @param dto 更新请求
     * @return 更新结果
     */
    boolean update(PlatformUpdateDTO dto);

    /**
     * 删除平台
     *
     * @param id ID
     * @return 删除结果
     */
    boolean delete(Long id);
}
