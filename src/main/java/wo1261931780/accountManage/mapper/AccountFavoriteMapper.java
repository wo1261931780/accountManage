package wo1261931780.accountManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import wo1261931780.accountManage.entity.AccountFavorite;

import java.util.List;

/**
 * 账号收藏Mapper
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Mapper
public interface AccountFavoriteMapper extends BaseMapper<AccountFavorite> {

    /**
     * 获取用户收藏的账号ID列表
     */
    @Select("SELECT account_id FROM sys_account_favorite WHERE user_id = #{userId} ORDER BY sort_order ASC, create_time DESC")
    List<Long> selectAccountIdsByUserId(@Param("userId") Long userId);

    /**
     * 获取用户最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM sys_account_favorite WHERE user_id = #{userId}")
    Integer selectMaxSortOrder(@Param("userId") Long userId);
}
