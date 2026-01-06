package cloud.zimometaverse.backend.exception;

import cloud.zimometaverse.backend.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 
 * <p>用于业务逻辑中主动抛出的异常，会被全局异常处理器捕获并返回友好的错误信息
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 方式一：使用消息
 * if (user == null) {
 *     throw new BizException("用户不存在");
 * }
 * 
 * // 方式二：使用状态码枚举
 * if (user == null) {
 *     throw new BizException(ResultCode.USER_NOT_FOUND);
 * }
 * 
 * // 方式三：自定义状态码和消息
 * throw new BizException(1001, "用户不存在");
 * }</pre>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造方法：使用默认业务错误码
     * 
     * @param message 错误消息
     */
    public BizException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法：使用状态码枚举
     * 
     * @param resultCode 状态码枚举
     */
    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造方法：自定义状态码和消息
     * 
     * @param code    错误码
     * @param message 错误消息
     */
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法：使用状态码枚举和自定义消息
     * 
     * @param resultCode 状态码枚举
     * @param message    自定义错误消息
     */
    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 构造方法：带原始异常
     * 
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法：使用状态码枚举和原始异常
     * 
     * @param resultCode 状态码枚举
     * @param cause      原始异常
     */
    public BizException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}

