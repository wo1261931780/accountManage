package wo1261931780.accountManage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.common.utils.IdGenerator;
import wo1261931780.accountManage.dto.request.PlatformCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformQueryDTO;
import wo1261931780.accountManage.dto.request.PlatformUpdateDTO;
import wo1261931780.accountManage.dto.response.PlatformVO;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.service.PlatformService;
import wo1261931780.accountManage.service.PlatformTypeService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台 Service 实现类
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformServiceImpl extends ServiceImpl<PlatformMapper, Platform>
        implements PlatformService {

    private final IdGenerator idGenerator;
    private final PlatformTypeService platformTypeService;

    @Override
    public PageResult<PlatformVO> page(PlatformQueryDTO query) {
        Page<Platform> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<Platform> result = baseMapper.selectPlatformPage(page, query);

        List<PlatformVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result, voList);
    }

    @Override
    public List<PlatformVO> listAll() {
        List<Platform> platforms = baseMapper.selectAllEnabled();
        return platforms.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public PlatformVO getById(Long id) {
        Platform platform = baseMapper.selectPlatformById(id);
        if (platform == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "平台不存在");
        }
        return convertToVO(platform);
    }

    @Override
    public List<PlatformVO> listByTypeId(Long typeId) {
        List<Platform> platforms = baseMapper.selectByTypeId(typeId);
        return platforms.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Platform getByCode(String code) {
        return getOne(new LambdaQueryWrapper<Platform>()
                .eq(Platform::getPlatformCode, code));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Platform create(PlatformCreateDTO dto) {
        // 检查编码是否已存在
        Platform existing = getByCode(dto.getPlatformCode());
        if (existing != null) {
            throw new BusinessException(ResultCode.DATA_EXIST, "平台编码已存在: " + dto.getPlatformCode());
        }

        // 检查平台类型是否存在
        platformTypeService.getById(dto.getPlatformTypeId());

        // 创建实体
        Platform platform = new Platform();
        BeanUtil.copyProperties(dto, platform);
        platform.setId(idGenerator.nextId());

        // 保存
        save(platform);
        log.info("创建平台成功: id={}, code={}", platform.getId(), platform.getPlatformCode());
        return platform;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(PlatformUpdateDTO dto) {
        // 检查是否存在
        Platform existing = super.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "平台不存在");
        }

        // 检查编码是否重复
        if (dto.getPlatformCode() != null && !dto.getPlatformCode().equals(existing.getPlatformCode())) {
            Platform byCode = getByCode(dto.getPlatformCode());
            if (byCode != null) {
                throw new BusinessException(ResultCode.DATA_EXIST, "平台编码已存在: " + dto.getPlatformCode());
            }
        }

        // 检查平台类型是否存在
        if (dto.getPlatformTypeId() != null) {
            platformTypeService.getById(dto.getPlatformTypeId());
        }

        // 更新
        Platform platform = new Platform();
        BeanUtil.copyProperties(dto, platform);
        boolean result = updateById(platform);
        log.info("更新平台成功: id={}", dto.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 检查是否存在
        Platform existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "平台不存在");
        }

        // 逻辑删除
        boolean result = removeById(id);
        log.info("删除平台成功: id={}", id);
        return result;
    }

    /**
     * 转换为 VO
     */
    private PlatformVO convertToVO(Platform platform) {
        PlatformVO vo = new PlatformVO();
        BeanUtil.copyProperties(platform, vo);
        vo.setPlatformTypeName(platform.getPlatformTypeName());
        return vo;
    }
}
