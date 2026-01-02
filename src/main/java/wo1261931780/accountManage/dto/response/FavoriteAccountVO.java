package wo1261931780.accountManage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏账号VO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "收藏账号信息")
public class FavoriteAccountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "收藏ID")
    private Long favoriteId;

    @Schema(description = "账号ID")
    private Long accountId;

    @Schema(description = "平台名称")
    private String platformName;

    @Schema(description = "平台图标")
    private String platformIcon;

    @Schema(description = "账号名称")
    private String accountName;

    @Schema(description = "账号别名")
    private String accountAlias;

    @Schema(description = "账号状态")
    private Integer accountStatus;

    @Schema(description = "账号状态名称")
    private String accountStatusName;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "收藏时间")
    private LocalDateTime favoriteTime;
}
