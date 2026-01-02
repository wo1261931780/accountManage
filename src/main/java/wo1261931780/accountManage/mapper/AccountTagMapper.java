package wo1261931780.accountManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import wo1261931780.accountManage.entity.AccountTag;
import wo1261931780.accountManage.entity.Tag;

import java.util.List;

/**
 * 账号-标签关联Mapper
 */
@Mapper
public interface AccountTagMapper extends BaseMapper<AccountTag> {

    /**
     * 查询账号关联的标签列表
     */
    @Select("SELECT t.* FROM sys_tag t " +
            "INNER JOIN sys_account_tag at ON t.id = at.tag_id " +
            "WHERE at.account_id = #{accountId} AND t.deleted = 0 " +
            "ORDER BY t.sort_order")
    List<Tag> selectTagsByAccountId(@Param("accountId") Long accountId);

    /**
     * 查询标签关联的账号ID列表
     */
    @Select("SELECT account_id FROM sys_account_tag WHERE tag_id = #{tagId}")
    List<Long> selectAccountIdsByTagId(@Param("tagId") Long tagId);
}
