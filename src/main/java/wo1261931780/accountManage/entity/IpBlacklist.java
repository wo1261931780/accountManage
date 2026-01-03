package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * IP黑名单实体类
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Data
@Accessors(chain = true)
@TableName("ip_blacklist")
public class IpBlacklist {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * IP地址（支持IPv6）
     */
    private String ipAddress;

    /**
     * 封禁原因
     */
    private String reason;

    /**
     * 来源
     */
    private String source;

    /**
     * 失败次数
     */
    private Integer failCount;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否永久封禁
     */
    private Boolean isPermanent;

    /**
     * 状态：0-已解封, 1-封禁中
     */
    private Boolean status;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 封禁来源枚举
     */
    public enum Source {
        /**
         * 手动添加
         */
        MANUAL,
        /**
         * 自动封禁
         */
        AUTO,
        /**
         * 登录失败过多
         */
        LOGIN_FAIL
    }
}
