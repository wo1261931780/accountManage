package wo1261931780.accountManage.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import wo1261931780.accountManage.dto.excel.ImportResultVO;

/**
 * 账号导入导出服务接口
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface AccountExcelService {

    /**
     * 导出所有账号
     *
     * @param response HTTP响应
     */
    void exportAll(HttpServletResponse response);

    /**
     * 按平台类型导出账号
     *
     * @param typeId   平台类型ID
     * @param response HTTP响应
     */
    void exportByType(Long typeId, HttpServletResponse response);

    /**
     * 按平台导出账号
     *
     * @param platformId 平台ID
     * @param response   HTTP响应
     */
    void exportByPlatform(Long platformId, HttpServletResponse response);

    /**
     * 导入账号
     *
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResultVO importAccounts(MultipartFile file);

    /**
     * 下载导入模板
     *
     * @param response HTTP响应
     */
    void downloadTemplate(HttpServletResponse response);
}
