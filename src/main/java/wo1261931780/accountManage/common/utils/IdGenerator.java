package wo1261931780.accountManage.common.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

/**
 * ID 生成器
 * 使用雪花算法生成唯一 ID
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Component
public class IdGenerator {

    /**
     * 雪花算法实例
     * workerId: 0-31
     * datacenterId: 0-31
     */
    private final Snowflake snowflake;

    public IdGenerator() {
        // 工作机器ID和数据中心ID，可以根据实际情况配置
        this.snowflake = IdUtil.getSnowflake(1, 1);
    }

    /**
     * 生成唯一 ID
     *
     * @return Long 类型的唯一 ID
     */
    public Long nextId() {
        return snowflake.nextId();
    }

    /**
     * 生成唯一 ID 字符串
     *
     * @return String 类型的唯一 ID
     */
    public String nextIdStr() {
        return snowflake.nextIdStr();
    }
}
