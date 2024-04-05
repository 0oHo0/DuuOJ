package com.duu.ojuserservice.manager;

import cn.hutool.core.util.StrUtil;
import com.duu.ojcommon.utils.NetUtils;
import com.duu.ojcommon.utils.RedisKeyUtil;
import com.duu.ojmodel.model.entity.User;
import com.duu.ojmodel.model.entity.UserLoginRedisInfo;
import com.duu.ojuserservice.service.UserService;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.concurrent.TimeUnit;

import static com.duu.ojcommon.constant.RedisKeyConstant.*;

@Component
@Slf4j
public class SessionManager {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisIndexedSessionRepository sessionRepository;

    @Value("${spring.session.timeout}")
    private long sessionTimeout;

    @Lazy
    @Resource
    private UserService userService;

    /**
     * 设置属性
     *
     * @param request 请求信息
     * @param key     键
     * @param value   值
     * @param login   登录
     */
    public void setAttribute(HttpServletRequest request, String key, Object value, boolean login) {
        HttpSession session = request.getSession();
        if (login) {
            UserLoginRedisInfo userLoginRedisInfo = (UserLoginRedisInfo) value;
            User user = userLoginRedisInfo.getUser();
            // 存储登录态
            session.setAttribute(key, user);
            // 存储 sessionId 和 ip 信息
            String sessionId = session.getId();
            String userExtraInfoKey = RedisKeyUtil.getUserExtraInfoKey(user.getId());
            stringRedisTemplate.opsForHash().put(userExtraInfoKey, SESSION_ID, sessionId);
            stringRedisTemplate.opsForHash().put(userExtraInfoKey, IP, userLoginRedisInfo.getIp());
            stringRedisTemplate.expire(userExtraInfoKey, sessionTimeout, TimeUnit.SECONDS);
        } else {
            session.setAttribute(key, value);
        }
    }

    /**
     * 设置登录属性
     *
     * @param request            请求信息
     * @param loginKey           登录键
     * @param userLoginRedisInfo 用户信息
     */
    public void setLoginAttribute(HttpServletRequest request, String loginKey, UserLoginRedisInfo userLoginRedisInfo) {
        setAttribute(request, loginKey, userLoginRedisInfo, true);
    }

    /**
     * 删除属性
     *
     * @param request 请求信息
     * @param key     键
     */
    public void removeAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        session.removeAttribute(key);
    }

    /**
     * 退出登录
     *
     * @param request 请求信息
     */
    public void logout(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        removeAttribute(request, USER_LOGIN_STATE);
        stringRedisTemplate.delete(RedisKeyUtil.getUserExtraInfoKey(loginUser.getId()));
    }

    /**
     * 检查是否已在其他端登录
     *
     * @param userId 用户 id
     * @return {@link String} 如果已在其他端登录，则返回其他端的 sessionId ，否则返回 null
     */
    public String checkOtherLogin(Long userId, String currentIp,HttpServletRequest request) {
        // 校验 sessionId
        Object oldSessionIdObj = stringRedisTemplate.opsForHash().get(RedisKeyUtil.getUserExtraInfoKey(userId), SESSION_ID);
        String oldSessionId = null;
        if (oldSessionIdObj != null) {
            oldSessionId = (String) oldSessionIdObj;
        }
        // 校验 ip
        Object oldIpObj = stringRedisTemplate.opsForHash().get(RedisKeyUtil.getUserExtraInfoKey(userId), IP);
        String oldIP = null;
        if (oldIpObj != null) {
            oldIP = (String) oldIpObj;
        }
        // 判断 sessionId 如果
        //      为空或相等 返回 null
        //      不等，判断 ip 如果
        //          为空或相等，返回 null
        //          不等，返回 oldSessionId
        if (StrUtil.isBlank(oldSessionId) || oldSessionId.equals(request.getSession().getId())) {
            return null;
        } else {
            if (StrUtil.isBlank(oldIP) || oldIP.equals(currentIp)) {
                return null;
            }
            return oldSessionId;
        }
    }

    /**
     * 删除其他 session 的登录属性
     *
     * @param sessionId sessionId
     */
    public void removeOtherSessionLoginAttribute(String sessionId, Long userId) {
        String sessionKey = RedisKeyUtil.getSessionKey(sessionId);
        String sessionAttrKey = RedisKeyUtil.getSessionAttrKey(USER_LOGIN_STATE);
        // 删除用户的额外信息
        Boolean userExtraInfoDelete = stringRedisTemplate.delete(RedisKeyUtil.getUserExtraInfoKey(userId));
        Long delete = sessionRepository.getSessionRedisOperations().opsForHash().delete(sessionKey, sessionAttrKey);

        log.info("oldSessionId: {}, user extra info delete result: {}, user login state delete result: {}", sessionId, userExtraInfoDelete, delete);
    }

    /**
     * 登录
     *
     * @param user    用户
     * @param request 请求信息
     * @return {@link String}
     */
    public String login(User user, HttpServletRequest request) {
        String message = "登录成功";
        String ipAddress = NetUtils.getIpAddress(request);
        String oldSessionId = this.checkOtherLogin(user.getId(), ipAddress,request);
        // 不为空，说明已在其他端登录
        if (StrUtil.isNotBlank(oldSessionId)) {
            // 删除 oldSessionId 的登录态
            this.removeOtherSessionLoginAttribute(oldSessionId, user.getId());
            message += "，已移除其他设备的登录";
        }
        UserLoginRedisInfo userLoginRedisInfo = UserLoginRedisInfo.builder()
                .user(user)
                .ip(ipAddress)
                .build();
        this.setLoginAttribute(request, USER_LOGIN_STATE, userLoginRedisInfo);
        return message;
    }
}