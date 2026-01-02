package wo1261931780.accountManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import wo1261931780.accountManage.entity.PasswordHistory;

/**
 * 密码修改历史Mapper
 */
@Mapper
public interface PasswordHistoryMapper extends BaseMapper<PasswordHistory> {
}
