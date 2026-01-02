package wo1261931780.accountManage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.dto.excel.AccountExportDTO;
import wo1261931780.accountManage.dto.excel.AccountImportDTO;
import wo1261931780.accountManage.dto.excel.ImportResultVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.entity.PlatformType;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.mapper.PlatformTypeMapper;
import wo1261931780.accountManage.service.AccountExcelService;
import wo1261931780.accountManage.common.utils.PasswordUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 账号导入导出服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountExcelServiceImpl implements AccountExcelService {

    private final AccountMapper accountMapper;
    private final PlatformMapper platformMapper;
    private final PlatformTypeMapper platformTypeMapper;
    private final PasswordUtils passwordUtils;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void exportAll(HttpServletResponse response) {
        List<Account> accounts = accountMapper.selectList(null);
        doExport(accounts, response, "全部账号");
    }

    @Override
    public void exportByType(Long typeId, HttpServletResponse response) {
        // 查询该类型下的所有平台
        List<Platform> platforms = platformMapper.selectList(
                new LambdaQueryWrapper<Platform>().eq(Platform::getPlatformTypeId, typeId));
        List<Long> platformIds = platforms.stream().map(Platform::getId).toList();

        if (platformIds.isEmpty()) {
            doExport(new ArrayList<>(), response, "账号列表");
            return;
        }

        List<Account> accounts = accountMapper.selectList(
                new LambdaQueryWrapper<Account>().in(Account::getPlatformId, platformIds));

        PlatformType type = platformTypeMapper.selectById(typeId);
        String fileName = type != null ? type.getTypeName() + "_账号" : "账号列表";
        doExport(accounts, response, fileName);
    }

    @Override
    public void exportByPlatform(Long platformId, HttpServletResponse response) {
        List<Account> accounts = accountMapper.selectList(
                new LambdaQueryWrapper<Account>().eq(Account::getPlatformId, platformId));

        Platform platform = platformMapper.selectById(platformId);
        String fileName = platform != null ? platform.getPlatformName() + "_账号" : "账号列表";
        doExport(accounts, response, fileName);
    }

    /**
     * 执行导出
     */
    private void doExport(List<Account> accounts, HttpServletResponse response, String fileName) {
        try {
            // 构建平台和类型映射
            Map<Long, Platform> platformMap = new HashMap<>();
            Map<Long, PlatformType> typeMap = new HashMap<>();

            if (!accounts.isEmpty()) {
                List<Long> platformIds = accounts.stream()
                        .map(Account::getPlatformId)
                        .distinct()
                        .toList();
                List<Platform> platforms = platformMapper.selectBatchIds(platformIds);
                platformMap = platforms.stream()
                        .collect(Collectors.toMap(Platform::getId, p -> p));

                List<Long> typeIds = platforms.stream()
                        .map(Platform::getPlatformTypeId)
                        .distinct()
                        .toList();
                if (!typeIds.isEmpty()) {
                    List<PlatformType> types = platformTypeMapper.selectBatchIds(typeIds);
                    typeMap = types.stream()
                            .collect(Collectors.toMap(PlatformType::getId, t -> t));
                }
            }

            // 转换数据
            List<AccountExportDTO> exportData = new ArrayList<>();
            for (Account account : accounts) {
                AccountExportDTO dto = new AccountExportDTO();

                Platform platform = platformMap.get(account.getPlatformId());
                if (platform != null) {
                    dto.setPlatformName(platform.getPlatformName());
                    PlatformType type = typeMap.get(platform.getPlatformTypeId());
                    if (type != null) {
                        dto.setPlatformTypeName(type.getTypeName());
                    }
                }

                dto.setAccountName(account.getAccountName());
                dto.setUsername(account.getAccountName());
                // 解密密码用于导出
                try {
                    dto.setPassword(passwordUtils.decrypt(account.getPasswordEncrypted(), account.getPasswordSalt()));
                } catch (Exception e) {
                    dto.setPassword("*解密失败*");
                }
                dto.setEmail(account.getBindEmail());
                dto.setPhone(account.getBindPhone());
                dto.setUrl(null); // Account实体没有URL字段
                dto.setRemark(account.getRemark());
                dto.setCreateTime(account.getCreateTime() != null ?
                        account.getCreateTime().format(FORMATTER) : "");

                exportData.add(dto);
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                    StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".xlsx");

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), AccountExportDTO.class)
                    .sheet("账号列表")
                    .doWrite(exportData);

        } catch (IOException e) {
            log.error("导出账号失败", e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ImportResultVO importAccounts(MultipartFile file) {
        List<ImportResultVO.FailDetail> failDetails = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(0);

        // 预加载平台和类型数据
        Map<String, PlatformType> typeNameMap = platformTypeMapper.selectList(null).stream()
                .collect(Collectors.toMap(PlatformType::getTypeName, t -> t, (a, b) -> a));
        Map<String, Platform> platformNameMap = platformMapper.selectList(null).stream()
                .collect(Collectors.toMap(Platform::getPlatformName, p -> p, (a, b) -> a));

        try {
            EasyExcel.read(file.getInputStream(), AccountImportDTO.class, new ReadListener<AccountImportDTO>() {

                @Override
                public void invoke(AccountImportDTO data, AnalysisContext context) {
                    int rowNum = context.readRowHolder().getRowIndex() + 1;
                    totalCount.incrementAndGet();

                    try {
                        // 验证必填字段
                        if (StrUtil.isBlank(data.getPlatformName())) {
                            addFailDetail(failDetails, rowNum, "平台名称不能为空", data);
                            return;
                        }
                        if (StrUtil.isBlank(data.getAccountName())) {
                            addFailDetail(failDetails, rowNum, "账号名称不能为空", data);
                            return;
                        }
                        if (StrUtil.isBlank(data.getUsername())) {
                            addFailDetail(failDetails, rowNum, "用户名不能为空", data);
                            return;
                        }
                        if (StrUtil.isBlank(data.getPassword())) {
                            addFailDetail(failDetails, rowNum, "密码不能为空", data);
                            return;
                        }

                        // 查找或创建平台
                        Platform platform = platformNameMap.get(data.getPlatformName());
                        if (platform == null) {
                            // 尝试创建平台
                            PlatformType type = null;
                            if (StrUtil.isNotBlank(data.getPlatformTypeName())) {
                                type = typeNameMap.get(data.getPlatformTypeName());
                            }
                            if (type == null) {
                                // 使用默认类型"其他"，如果没有则创建
                                type = typeNameMap.get("其他");
                                if (type == null) {
                                    type = new PlatformType();
                                    type.setTypeName("其他");
                                    type.setIcon("folder");
                                    type.setSortOrder(999);
                                    platformTypeMapper.insert(type);
                                    typeNameMap.put("其他", type);
                                }
                            }

                            platform = new Platform();
                            platform.setPlatformTypeId(type.getId());
                            platform.setPlatformName(data.getPlatformName());
                            platform.setPlatformUrl(data.getUrl());
                            platform.setSortOrder(0);
                            platformMapper.insert(platform);
                            platformNameMap.put(data.getPlatformName(), platform);
                        }

                        // 创建账号
                        Account account = new Account();
                        account.setPlatformId(platform.getId());
                        account.setAccountName(data.getAccountName());
                        // 使用用户名作为账号别名
                        account.setAccountAlias(data.getUsername());

                        // 加密密码
                        String salt = passwordUtils.generateSalt();
                        account.setPasswordSalt(salt);
                        account.setPasswordEncrypted(passwordUtils.encrypt(data.getPassword(), salt));
                        account.setPasswordUpdateTime(LocalDateTime.now());

                        account.setBindEmail(data.getEmail());
                        account.setBindPhone(data.getPhone());
                        account.setRemark(data.getRemark());
                        account.setAccountStatus(1); // 默认正常状态

                        accountMapper.insert(account);
                        successCount.incrementAndGet();

                    } catch (Exception e) {
                        log.error("导入第{}行失败", rowNum, e);
                        addFailDetail(failDetails, rowNum, e.getMessage(), data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("账号导入完成，总计{}条，成功{}条", totalCount.get(), successCount.get());
                }
            }).sheet().doRead();

        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new BusinessException("读取文件失败: " + e.getMessage());
        }

        return ImportResultVO.builder()
                .totalCount(totalCount.get())
                .successCount(successCount.get())
                .failCount(failDetails.size())
                .failDetails(failDetails)
                .build();
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try {
            // 创建示例数据
            List<AccountImportDTO> templateData = new ArrayList<>();
            AccountImportDTO example = new AccountImportDTO();
            example.setPlatformTypeName("社交媒体");
            example.setPlatformName("微信");
            example.setAccountName("我的微信");
            example.setUsername("user@example.com");
            example.setPassword("MyPassword123!");
            example.setEmail("user@example.com");
            example.setPhone("13800138000");
            example.setUrl("https://weixin.qq.com");
            example.setRemark("这是一个示例账号");
            templateData.add(example);

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode("账号导入模板", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".xlsx");

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), AccountImportDTO.class)
                    .sheet("导入模板")
                    .doWrite(templateData);

        } catch (IOException e) {
            log.error("下载模板失败", e);
            throw new BusinessException("下载模板失败: " + e.getMessage());
        }
    }

    /**
     * 添加失败详情
     */
    private void addFailDetail(List<ImportResultVO.FailDetail> failDetails, int rowNum,
                               String reason, AccountImportDTO data) {
        failDetails.add(ImportResultVO.FailDetail.builder()
                .rowNum(rowNum)
                .reason(reason)
                .data(data.getAccountName() + " - " + data.getUsername())
                .build());
    }
}
