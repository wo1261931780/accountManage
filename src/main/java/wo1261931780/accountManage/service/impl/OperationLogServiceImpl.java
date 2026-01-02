package wo1261931780.accountManage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import wo1261931780.accountManage.entity.SysOperationLog;
import wo1261931780.accountManage.mapper.SysOperationLogMapper;
import wo1261931780.accountManage.service.OperationLogService;

/**
 * 操作日志服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements OperationLogService {

    @Override
    @Async
    public void saveLog(SysOperationLog operationLog) {
        try {
            save(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }
}
