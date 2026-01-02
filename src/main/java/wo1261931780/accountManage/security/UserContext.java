package wo1261931780.accountManage.security;

/**
 * 登录用户上下文
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    /**
     * 设置当前登录用户
     *
     * @param loginUser 登录用户
     */
    public static void setUser(LoginUser loginUser) {
        USER_HOLDER.set(loginUser);
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户
     */
    public static LoginUser getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        LoginUser user = getUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        LoginUser user = getUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 清除当前登录用户
     */
    public static void clear() {
        USER_HOLDER.remove();
    }

    /**
     * 判断是否已登录
     *
     * @return 是否已登录
     */
    public static boolean isLogin() {
        return getUser() != null;
    }
}
