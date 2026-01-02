package wo1261931780.accountManage.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 账号导入Excel DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "账号导入数据")
public class AccountImportDTO {

    @ExcelProperty("平台类型")
    private String platformTypeName;

    @ExcelProperty("平台名称")
    private String platformName;

    @ExcelProperty("账号名称")
    private String accountName;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("密码")
    private String password;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("网址")
    private String url;

    @ExcelProperty("备注")
    private String remark;
}
