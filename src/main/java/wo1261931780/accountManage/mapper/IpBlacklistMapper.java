package wo1261931780.accountManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import wo1261931780.accountManage.entity.IpBlacklist;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IP黑名单Mapper
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Mapper
public interface IpBlacklistMapper extends BaseMapper<IpBlacklist> {

    /**
     * 根据IP地址查询封禁记录
     *
     * @param ipAddress IP地址
     * @return 封禁记录
     */
    @Select("SELECT * FROM ip_blacklist WHERE ip_address = #{ipAddress} AND status = 1")
    IpBlacklist findActiveByIp(@Param("ipAddress") String ipAddress);

    /**
     * 检查IP是否在黑名单中（未过期）
     *
     * @param ipAddress IP地址
     * @return 是否封禁中
     */
    @Select("SELECT COUNT(1) > 0 FROM ip_blacklist " +
            "WHERE ip_address = #{ipAddress} AND status = 1 " +
            "AND (is_permanent = 1 OR expire_time IS NULL OR expire_time > NOW())")
    boolean isBlocked(@Param("ipAddress") String ipAddress);

    /**
     * 解封过期的IP
     *
     * @return 影响行数
     */
    @Update("UPDATE ip_blacklist SET status = 0 " +
            "WHERE is_permanent = 0 AND expire_time IS NOT NULL " +
            "AND expire_time <= NOW() AND status = 1")
    int unblockExpired();

    /**
     * 更新失败次数
     *
     * @param ipAddress IP地址
     * @param failCount 失败次数
     * @return 影响行数
     */
    @Update("UPDATE ip_blacklist SET fail_count = #{failCount}, updated_time = NOW() " +
            "WHERE ip_address = #{ipAddress}")
    int updateFailCount(@Param("ipAddress") String ipAddress, @Param("failCount") int failCount);

    /**
     * 获取所有封禁中的IP列表
     *
     * @return IP列表
     */
    @Select("SELECT ip_address FROM ip_blacklist " +
            "WHERE status = 1 AND (is_permanent = 1 OR expire_time IS NULL OR expire_time > NOW())")
    List<String> findAllBlockedIps();
}
