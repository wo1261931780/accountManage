package wo1261931780.accountManage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 最近使用账号VO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "最近使用账号信息")
public class RecentAccountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    @Schema(description = "是否已收藏")
    private Boolean isFavorite;

    @Schema(description = "最后访问时间")
    private LocalDateTime lastAccessTime;

    @Schema(description = "访问次数")
    private Integer accessCount;
}
