package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账号-标签关联实体
 */
@Data
@TableName("sys_account_tag")
@Schema(description = "账号-标签关联")
public class AccountTag {

    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "账号ID")
    private Long accountId;

    @Schema(description = "标签ID")
    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
