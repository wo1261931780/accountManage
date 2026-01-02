package wo1261931780.accountManage.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 账号导出Excel DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "账号导出数据")
public class AccountExportDTO {

    @ExcelProperty("平台类型")
    @ColumnWidth(15)
    private String platformTypeName;

    @ExcelProperty("平台名称")
    @ColumnWidth(20)
    private String platformName;

    @ExcelProperty("账号名称")
    @ColumnWidth(20)
    private String accountName;

    @ExcelProperty("用户名")
    @ColumnWidth(25)
    private String username;

    @ExcelProperty("密码")
    @ColumnWidth(30)
    private String password;

    @ExcelProperty("邮箱")
    @ColumnWidth(30)
    private String email;

    @ExcelProperty("手机号")
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty("网址")
    @ColumnWidth(40)
    private String url;

    @ExcelProperty("备注")
    @ColumnWidth(50)
    private String remark;

    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private String createTime;
}
