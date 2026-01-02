package wo1261931780.accountManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import wo1261931780.accountManage.entity.AccountAccessLog;

import java.util.List;
import java.util.Map;

/**
 * 账号访问记录Mapper
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Mapper
public interface AccountAccessLogMapper extends BaseMapper<AccountAccessLog> {

    /**
     * 获取用户最近访问的账号ID列表（去重后按最近访问时间排序）
     */
    @Select("""
            SELECT account_id, MAX(access_time) AS last_access_time, COUNT(*) AS access_count
            FROM sys_account_access_log
            WHERE user_id = #{userId}
            GROUP BY account_id
            ORDER BY last_access_time DESC
            LIMIT #{limit}
            """)
    List<Map<String, Object>> selectRecentAccountIds(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 获取账号访问次数统计
     */
    @Select("""
            SELECT account_id, COUNT(*) AS access_count
            FROM sys_account_access_log
            WHERE user_id = #{userId}
            GROUP BY account_id
            ORDER BY access_count DESC
            LIMIT #{limit}
            """)
    List<Map<String, Object>> selectMostAccessedAccounts(@Param("userId") Long userId, @Param("limit") int limit);
}
