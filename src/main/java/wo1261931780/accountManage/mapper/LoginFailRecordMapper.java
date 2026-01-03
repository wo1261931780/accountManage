package wo1261931780.accountManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import wo1261931780.accountManage.entity.LoginFailRecord;

import java.time.LocalDateTime;

/**
 * 登录失败记录Mapper
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Mapper
public interface LoginFailRecordMapper extends BaseMapper<LoginFailRecord> {

    /**
     * 统计指定时间范围内某IP的失败次数
     *
     * @param ipAddress IP地址
     * @param startTime 开始时间
     * @return 失败次数
     */
    @Select("SELECT COUNT(1) FROM login_fail_record " +
            "WHERE ip_address = #{ipAddress} AND fail_time >= #{startTime}")
    int countFailsByIpSince(@Param("ipAddress") String ipAddress,
                            @Param("startTime") LocalDateTime startTime);

    /**
     * 删除某IP的所有失败记录
     *
     * @param ipAddress IP地址
     * @return 影响行数
     */
    @Delete("DELETE FROM login_fail_record WHERE ip_address = #{ipAddress}")
    int deleteByIp(@Param("ipAddress") String ipAddress);

    /**
     * 清理指定时间之前的记录
     *
     * @param beforeTime 时间点
     * @return 影响行数
     */
    @Delete("DELETE FROM login_fail_record WHERE fail_time < #{beforeTime}")
    int cleanOldRecords(@Param("beforeTime") LocalDateTime beforeTime);
}
