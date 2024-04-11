package com.nowcoder.community.community.util;

/**
 * ClassName: RedisKeyUtil
 * Package: com.nowcoder.community.community.util
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/8 18:32
 * @Version 1.0
 */
public class RedisKeyUtil {

    private static final String SPLIT =":";

    // 存帖子 or 评论的赞   =》 存储实体的赞
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWEE = "followee";

    private static final String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_KAPTCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";


    // 某个 实体的赞
    // like:entity:entityType:entityId  ->  set(userId)
    public static String getEntityLikeKey(int entityType,int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT +entityId;
    }

    // 某个用户的 赞
    // like:user:userId  -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的  实体
    // followee:userId:entityType  ->  zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体  拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 登录验证码
    // kaptcha:owner
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA +SPLIT + owner;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT +ticket;
    }

    // 用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }
}
