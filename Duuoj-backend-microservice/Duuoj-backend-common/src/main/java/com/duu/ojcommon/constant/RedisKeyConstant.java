package com.duu.ojcommon.constant;

public interface RedisKeyConstant {

    // IP 和 sessionId 的 key 前缀
    String USER_EXTRA_INFO = "user:extra:";
    // Spring Session 中 session 信息的后缀（sessionId 前面）
    String SESSION_KEY_POSTFIX = "sessions";
    // session 中保存的属性的前缀
    String SESSION_ATTRIBUTE_PREFIX = "sessionAttr";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    String IP = "ip";

    String SESSION_ID = "sessionId";
    /**
     * 缓存题目 key
     */
    String QUESTION_ID="questionId:";
    /**
     * 缓存做题进度
     */
    String QUESTION_STATISTICS_KEY = "question_statistics:";
    /**
     * getQuestion锁 （解决缓存击穿）
     */
    String GET_QUESTION_LOCK = "get_question_lock:";
    /**
     * getQuestion 过滤器 （解决缓存穿透）
     */
    String GET_QUESTION_FILTER = "get_question_filter";
    /**
     * 接口限流
     */
    String REQUEST_LIMIT = "req_lim:";
    /**
     * 评论点赞
     */
    String COMMENT_LIKE = "comment_like:";
}