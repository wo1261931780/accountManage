package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号收藏实体
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@TableName("sys_account_favorite")
@Schema(description = "账号收藏")
public class AccountFavorite implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 账号ID
     */
    @Schema(description = "账号ID")
    private Long accountId;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序")
    private Integer sortOrder;

    /**
     * 收藏时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "收藏时间")
    private LocalDateTime createTime;
}
