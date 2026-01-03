package wo1261931780.accountManage.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import wo1261931780.accountManage.dto.ip.IpBlacklistAddDTO;
import wo1261931780.accountManage.dto.ip.IpBlacklistQueryDTO;
import wo1261931780.accountManage.entity.IpBlacklist;
import wo1261931780.accountManage.entity.LoginFailRecord;
import wo1261931780.accountManage.mapper.IpBlacklistMapper;
import wo1261931780.accountManage.mapper.LoginFailRecordMapper;
import wo1261931780.accountManage.service.impl.IpBlacklistServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * IP黑名单服务单元测试
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IP黑名单服务测试")
class IpBlacklistServiceTest {

    @Mock
    private IpBlacklistMapper ipBlacklistMapper;

    @Mock
    private LoginFailRecordMapper loginFailRecordMapper;

    @InjectMocks
    private IpBlacklistServiceImpl ipBlacklistService;

    @BeforeEach
    void setUp() {
        // 设置配置值
        ReflectionTestUtils.setField(ipBlacklistService, "failThreshold", 10);
        ReflectionTestUtils.setField(ipBlacklistService, "failWindowMinutes", 30);
        ReflectionTestUtils.setField(ipBlacklistService, "autoBlockMinutes", 60);
        ReflectionTestUtils.setField(ipBlacklistService, "enabled", true);
    }

    // 创建模拟的 IpBlacklist 对象匹配器
    private IpBlacklist anyIpBlacklist() {
        return any(IpBlacklist.class);
    }

    private LoginFailRecord anyLoginFailRecord() {
        return any(LoginFailRecord.class);
    }

    @Test
    @DisplayName("检查IP是否被封禁 - IP不在黑名单中")
    void isBlocked_NotBlocked() {
        String ip = "192.168.1.100";
        when(ipBlacklistMapper.isBlocked(ip)).thenReturn(false);

        boolean result = ipBlacklistService.isBlocked(ip);

        assertFalse(result);
        verify(ipBlacklistMapper).isBlocked(ip);
    }

    @Test
    @DisplayName("检查IP是否被封禁 - IP在黑名单中")
    void isBlocked_Blocked() {
        String ip = "192.168.1.100";
        when(ipBlacklistMapper.isBlocked(ip)).thenReturn(true);

        boolean result = ipBlacklistService.isBlocked(ip);

        assertTrue(result);
    }

    @Test
    @DisplayName("检查IP是否被封禁 - 功能禁用时返回false")
    void isBlocked_Disabled() {
        ReflectionTestUtils.setField(ipBlacklistService, "enabled", false);
        String ip = "192.168.1.100";

        boolean result = ipBlacklistService.isBlocked(ip);

        assertFalse(result);
        verify(ipBlacklistMapper, never()).isBlocked(any());
    }

    @Test
    @DisplayName("检查IP是否被封禁 - IP为null时返回false")
    void isBlocked_NullIp() {
        boolean result = ipBlacklistService.isBlocked(null);

        assertFalse(result);
        verify(ipBlacklistMapper, never()).isBlocked(any());
    }

    @Test
    @DisplayName("添加IP到黑名单 - 成功")
    void addToBlacklist_Success() {
        IpBlacklistAddDTO dto = new IpBlacklistAddDTO();
        dto.setIpAddress("192.168.1.100");
        dto.setReason("恶意攻击");
        dto.setIsPermanent(false);
        dto.setDurationMinutes(60);

        when(ipBlacklistMapper.findActiveByIp(dto.getIpAddress())).thenReturn(null);
        when(ipBlacklistMapper.insert(any(IpBlacklist.class))).thenReturn(1);

        boolean result = ipBlacklistService.addToBlacklist(dto);

        assertTrue(result);
        verify(ipBlacklistMapper).insert(any(IpBlacklist.class));
    }

    @Test
    @DisplayName("添加IP到黑名单 - IP已存在")
    void addToBlacklist_AlreadyExists() {
        IpBlacklistAddDTO dto = new IpBlacklistAddDTO();
        dto.setIpAddress("192.168.1.100");

        IpBlacklist existing = new IpBlacklist();
        existing.setIpAddress(dto.getIpAddress());
        when(ipBlacklistMapper.findActiveByIp(dto.getIpAddress())).thenReturn(existing);

        boolean result = ipBlacklistService.addToBlacklist(dto);

        assertFalse(result);
        verify(ipBlacklistMapper, never()).insert(anyIpBlacklist());
    }

    @Test
    @DisplayName("自动封禁IP - 成功")
    void autoBlock_Success() {
        String ip = "192.168.1.100";
        String reason = "登录失败过多";
        int duration = 60;

        when(ipBlacklistMapper.findActiveByIp(ip)).thenReturn(null);
        when(ipBlacklistMapper.insert(anyIpBlacklist())).thenReturn(1);

        boolean result = ipBlacklistService.autoBlock(ip, reason, duration);

        assertTrue(result);
        verify(ipBlacklistMapper).insert((IpBlacklist) argThat(entity ->
            ((IpBlacklist) entity).getIpAddress().equals(ip) &&
            ((IpBlacklist) entity).getReason().equals(reason) &&
            ((IpBlacklist) entity).getSource().equals("AUTO") &&
            !((IpBlacklist) entity).getIsPermanent()
        ));
    }

    @Test
    @DisplayName("自动封禁IP - IP已存在，更新失败次数")
    void autoBlock_UpdateExisting() {
        String ip = "192.168.1.100";
        IpBlacklist existing = new IpBlacklist();
        existing.setIpAddress(ip);
        existing.setFailCount(3);

        when(ipBlacklistMapper.findActiveByIp(ip)).thenReturn(existing);
        when(ipBlacklistMapper.updateFailCount(ip, 4)).thenReturn(1);

        boolean result = ipBlacklistService.autoBlock(ip, "测试", 60);

        assertTrue(result);
        verify(ipBlacklistMapper).updateFailCount(ip, 4);
    }

    @Test
    @DisplayName("解封IP - 成功")
    void unblock_Success() {
        Long id = 1L;
        IpBlacklist entity = new IpBlacklist();
        entity.setId(id);
        entity.setIpAddress("192.168.1.100");
        entity.setStatus(true);

        when(ipBlacklistMapper.selectById(id)).thenReturn(entity);
        when(ipBlacklistMapper.updateById(anyIpBlacklist())).thenReturn(1);

        boolean result = ipBlacklistService.unblock(id);

        assertTrue(result);
        verify(ipBlacklistMapper).updateById((IpBlacklist) argThat(e -> !((IpBlacklist) e).getStatus()));
    }

    @Test
    @DisplayName("解封IP - 记录不存在")
    void unblock_NotFound() {
        Long id = 999L;
        when(ipBlacklistMapper.selectById(id)).thenReturn(null);

        boolean result = ipBlacklistService.unblock(id);

        assertFalse(result);
    }

    @Test
    @DisplayName("记录登录失败 - 未达到阈值")
    void recordLoginFail_BelowThreshold() {
        String ip = "192.168.1.100";
        String username = "testuser";

        when(loginFailRecordMapper.insert(any(LoginFailRecord.class))).thenReturn(1);
        when(loginFailRecordMapper.countFailsByIpSince(eq(ip), any(LocalDateTime.class))).thenReturn(5);

        ipBlacklistService.recordLoginFail(ip, username);

        verify(loginFailRecordMapper).insert(anyLoginFailRecord());
        verify(ipBlacklistMapper, never()).insert(anyIpBlacklist()); // 未达到阈值，不应触发封禁
    }

    @Test
    @DisplayName("记录登录失败 - 达到阈值自动封禁")
    void recordLoginFail_ReachThreshold() {
        String ip = "192.168.1.100";
        String username = "testuser";

        when(loginFailRecordMapper.insert(anyLoginFailRecord())).thenReturn(1);
        when(loginFailRecordMapper.countFailsByIpSince(eq(ip), any(LocalDateTime.class))).thenReturn(10);
        when(ipBlacklistMapper.findActiveByIp(ip)).thenReturn(null);
        when(ipBlacklistMapper.insert(anyIpBlacklist())).thenReturn(1);

        ipBlacklistService.recordLoginFail(ip, username);

        verify(ipBlacklistMapper).insert(anyIpBlacklist()); // 达到阈值，应触发封禁
    }

    @Test
    @DisplayName("记录登录失败 - 功能禁用时不记录")
    void recordLoginFail_Disabled() {
        ReflectionTestUtils.setField(ipBlacklistService, "enabled", false);
        String ip = "192.168.1.100";

        ipBlacklistService.recordLoginFail(ip, "testuser");

        verify(loginFailRecordMapper, never()).insert(anyLoginFailRecord());
    }

    @Test
    @DisplayName("清除登录失败记录")
    void clearLoginFailRecords() {
        String ip = "192.168.1.100";
        when(loginFailRecordMapper.deleteByIp(ip)).thenReturn(5);

        ipBlacklistService.clearLoginFailRecords(ip);

        verify(loginFailRecordMapper).deleteByIp(ip);
    }

    @Test
    @DisplayName("获取所有封禁IP列表")
    void getAllBlockedIps() {
        List<String> blockedIps = List.of("192.168.1.1", "192.168.1.2", "10.0.0.1");
        when(ipBlacklistMapper.findAllBlockedIps()).thenReturn(blockedIps);

        Set<String> result = ipBlacklistService.getAllBlockedIps();

        assertEquals(3, result.size());
        assertTrue(result.contains("192.168.1.1"));
        assertTrue(result.contains("192.168.1.2"));
        assertTrue(result.contains("10.0.0.1"));
    }

    @Test
    @DisplayName("清理过期封禁记录")
    void cleanExpired() {
        when(ipBlacklistMapper.unblockExpired()).thenReturn(5);
        when(ipBlacklistMapper.findAllBlockedIps()).thenReturn(List.of());

        int result = ipBlacklistService.cleanExpired();

        assertEquals(5, result);
        verify(ipBlacklistMapper).unblockExpired();
    }

    @Test
    @DisplayName("删除黑名单记录")
    void delete() {
        Long id = 1L;
        IpBlacklist entity = new IpBlacklist();
        entity.setId(id);
        entity.setIpAddress("192.168.1.100");

        when(ipBlacklistMapper.selectById(id)).thenReturn(entity);
        when(ipBlacklistMapper.deleteById(id)).thenReturn(1);

        boolean result = ipBlacklistService.delete(id);

        assertTrue(result);
        verify(ipBlacklistMapper).deleteById(id);
    }

    @Test
    @DisplayName("批量删除黑名单记录")
    void batchDelete() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<IpBlacklist> entities = List.of(
            createIpBlacklist(1L, "192.168.1.1"),
            createIpBlacklist(2L, "192.168.1.2"),
            createIpBlacklist(3L, "192.168.1.3")
        );

        when(ipBlacklistMapper.selectBatchIds(ids)).thenReturn(entities);
        when(ipBlacklistMapper.deleteBatchIds(ids)).thenReturn(3);

        int result = ipBlacklistService.batchDelete(ids);

        assertEquals(3, result);
    }

    @Test
    @DisplayName("批量删除 - 空列表")
    void batchDelete_EmptyList() {
        int result = ipBlacklistService.batchDelete(List.of());

        assertEquals(0, result);
        verify(ipBlacklistMapper, never()).deleteBatchIds(anyList());
    }

    private IpBlacklist createIpBlacklist(Long id, String ip) {
        IpBlacklist entity = new IpBlacklist();
        entity.setId(id);
        entity.setIpAddress(ip);
        return entity;
    }
}
