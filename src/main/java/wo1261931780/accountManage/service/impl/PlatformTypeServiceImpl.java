package wo1261931780.accountManage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.common.utils.IdGenerator;
import wo1261931780.accountManage.dto.request.PlatformTypeCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformTypeUpdateDTO;
import wo1261931780.accountManage.entity.PlatformType;
import wo1261931780.accountManage.mapper.PlatformTypeMapper;
import wo1261931780.accountManage.service.PlatformTypeService;

import java.util.List;

/**
 * 平台类型 Service 实现类
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformTypeServiceImpl extends ServiceImpl<PlatformTypeMapper, PlatformType>
        implements PlatformTypeService {

    private final IdGenerator idGenerator;

    @Override
    public List<PlatformType> listAll() {
        return list(new LambdaQueryWrapper<PlatformType>()
                .orderByAsc(PlatformType::getSortOrder)
                .orderByAsc(PlatformType::getId));
    }

    @Override
    public List<PlatformType> listEnabled() {
        return list(new LambdaQueryWrapper<PlatformType>()
                .eq(PlatformType::getStatus, 1)
                .orderByAsc(PlatformType::getSortOrder)
                .orderByAsc(PlatformType::getId));
    }

    @Override
    public PlatformType getById(Long id) {
        PlatformType platformType = super.getById(id);
        if (platformType == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "平台类型不存在");
        }
        return platformType;
    }

    @Override
    public PlatformType getByCode(String code) {
        return getOne(new LambdaQueryWrapper<PlatformType>()
                .eq(PlatformType::getTypeCode, code));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PlatformType create(PlatformTypeCreateDTO dto) {
        // 检查编码是否已存在
        PlatformType existing = getByCode(dto.getTypeCode());
        if (existing != null) {
            throw new BusinessException(ResultCode.DATA_EXIST, "类型编码已存在: " + dto.getTypeCode());
        }

        // 创建实体
        PlatformType platformType = new PlatformType();
        BeanUtil.copyProperties(dto, platformType);
        platformType.setId(idGenerator.nextId());

        // 保存
        save(platformType);
        log.info("创建平台类型成功: id={}, code={}", platformType.getId(), platformType.getTypeCode());
        return platformType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(PlatformTypeUpdateDTO dto) {
        // 检查是否存在
        PlatformType existing = super.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "平台类型不存在");
        }

        // 检查编码是否重复
        if (dto.getTypeCode() != null && !dto.getTypeCode().equals(existing.getTypeCode())) {
            PlatformType byCode = getByCode(dto.getTypeCode());
            if (byCode != null) {
                throw new BusinessException(ResultCode.DATA_EXIST, "类型编码已存在: " + dto.getTypeCode());
            }
        }

        // 更新
        PlatformType platformType = new PlatformType();
        BeanUtil.copyProperties(dto, platformType);
        boolean result = updateById(platformType);
        log.info("更新平台类型成功: id={}", dto.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 检查是否存在
        PlatformType existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "平台类型不存在");
        }

        // 逻辑删除
        boolean result = removeById(id);
        log.info("删除平台类型成功: id={}", id);
        return result;
    }
}
