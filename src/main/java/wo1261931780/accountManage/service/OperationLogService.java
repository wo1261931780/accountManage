package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wo1261931780.accountManage.entity.SysOperationLog;

/**
 * 操作日志服务接口
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface OperationLogService extends IService<SysOperationLog> {

    /**
     * 保存操作日志
     *
     * @param log 操作日志
     */
    void saveLog(SysOperationLog log);
}
