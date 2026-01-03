package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 登录失败记录实体类
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Data
@Accessors(chain = true)
@TableName("login_fail_record")
public class LoginFailRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 尝试的用户名
     */
    private String username;

    /**
     * 失败时间
     */
    private LocalDateTime failTime;
}
