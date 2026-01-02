/*
 * Author: junw wo1261931780@gmail.com
 * Date: 2025-12-30 16:33:48
 * LastEditors: junw wo1261931780@gmail.com
 * LastEditTime: 2025-12-30 16:39:38
 * FilePath: /accountManage/src/main/java/wo1261931780/accountManage/service/PlatformTypeService.java
 * Description: 1111
 *
 * Copyright (c) 2025 by ${git_name_email}, All Rights Reserved.
 */
package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wo1261931780.accountManage.dto.request.PlatformTypeCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformTypeUpdateDTO;
import wo1261931780.accountManage.entity.PlatformType;

import java.util.List;

/**
 * 平台类型 Service 接口
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
public interface PlatformTypeService extends IService<PlatformType> {

    /**
     * 获取所有平台类型
     *
     * @return 平台类型列表
     */
    List<PlatformType> listAll();

    /**
     * 获取所有启用的平台类型
     *
     * @return 平台类型列表
     */
    List<PlatformType> listEnabled();

    /**
     * 根据ID获取平台类型
     *
     * @param id ID
     * @return 平台类型
     */
    PlatformType getById(Long id);

    /**
     * 根据编码获取平台类型
     *
     * @param code 编码
     * @return 平台类型
     */
    PlatformType getByCode(String code);

    /**
     * 创建平台类型
     *
     * @param dto 创建请求
     * @return 创建结果
     */
    PlatformType create(PlatformTypeCreateDTO dto);

    /**
     * 更新平台类型
     *
     * @param dto 更新请求
     * @return 更新结果
     */
    boolean update(PlatformTypeUpdateDTO dto);

    /**
     * 删除平台类型
     *
     * @param id ID
     * @return 删除结果
     */
    boolean delete(Long id);
}
