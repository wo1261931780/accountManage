package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import wo1261931780.accountManage.dto.backup.BackupCreateDTO;
import wo1261931780.accountManage.dto.backup.BackupVO;

/**
 * 数据备份服务接口
 */
public interface BackupService {

    /**
     * 创建备份
     * @param dto 备份参数
     * @return 备份信息
     */
    BackupVO createBackup(BackupCreateDTO dto);

    /**
     * 恢复备份
     * @param backupId 备份ID
     * @return 是否成功
     */
    boolean restoreBackup(Long backupId);

    /**
     * 分页查询备份列表
     * @param page 分页参数
     * @param backupType 备份类型
     * @param status 状态
     * @return 分页结果
     */
    Page<BackupVO> listBackups(Page<BackupVO> page, Integer backupType, Integer status);

    /**
     * 获取备份详情
     * @param backupId 备份ID
     * @return 备份信息
     */
    BackupVO getBackupById(Long backupId);

    /**
     * 删除备份
     * @param backupId 备份ID
     * @return 是否成功
     */
    boolean deleteBackup(Long backupId);

    /**
     * 下载备份文件
     * @param backupId 备份ID
     * @return 备份文件路径
     */
    String getBackupFilePath(Long backupId);
}
