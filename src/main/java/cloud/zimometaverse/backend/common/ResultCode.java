package cloud.zimometaverse.backend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 * 
 * <p>状态码规范：
 * <ul>
 *   <li>200-299：成功</li>
 *   <li>400-499：客户端错误</li>
 *   <li>500-599：服务器错误</li>
 * </ul>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ========== 成功 ==========
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    // ========== 客户端错误（4xx）==========
    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * 未认证（未登录）
     */
    UNAUTHORIZED(401, "未认证，请先登录"),

    /**
     * 无权限访问
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求参数验证失败
     */
    VALIDATION_FAILED(422, "请求参数验证失败"),

    /**
     * 请求过于频繁
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    // ========== 服务器错误（5xx）==========
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务暂时不可用，请稍后再试"),

    // ========== 业务错误（1xxx）==========
    /**
     * 业务错误
     */
    BUSINESS_ERROR(1000, "业务处理失败"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1001, "用户不存在"),

    /**
     * 用户名或密码错误
     */
    INVALID_CREDENTIALS(1002, "用户名或密码错误"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(1003, "用户已存在"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1004, "用户已被禁用"),

    /**
     * Token 无效
     */
    INVALID_TOKEN(1010, "Token 无效"),

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(1011, "Token 已过期"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILED(1020, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED(1021, "文件类型不支持"),

    /**
     * 文件大小超过限制
     */
    FILE_SIZE_EXCEEDED(1022, "文件大小超过限制"),

    /**
     * 数据已存在
     */
    DATA_ALREADY_EXISTS(1030, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(1031, "数据不存在"),

    /**
     * 数据状态异常
     */
    DATA_STATUS_ERROR(1032, "数据状态异常"),

    /**
     * 操作失败（乐观锁）
     */
    OPTIMISTIC_LOCK_FAILED(1040, "数据已被修改，请刷新后重试");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}

