package cloud.zimometaverse.backend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果
 * 
 * <p>所有 API 接口统一返回此格式，便于前端统一处理
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 成功返回（带数据）
 * return Result.success(user);
 * 
 * // 成功返回（无数据）
 * return Result.success();
 * 
 * // 失败返回
 * return Result.error("用户不存在");
 * 
 * // 自定义状态码
 * return Result.error(ResultCode.UNAUTHORIZED);
 * }</pre>
 * 
 * @param <T> 响应数据类型
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间
     */
    private LocalDateTime timestamp;

    /**
     * 成功响应（无数据）
     * 
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应（带数据）
     * 
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return success(ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（自定义消息）
     * 
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                message,
                data,
                LocalDateTime.now()
        );
    }

    /**
     * 失败响应（默认消息）
     * 
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> error() {
        return error(ResultCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 失败响应（自定义消息）
     * 
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        return error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    /**
     * 失败响应（使用状态码枚举）
     * 
     * @param resultCode 状态码枚举
     * @param <T>        数据类型
     * @return Result
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 失败响应（自定义状态码和消息）
     * 
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(
                code,
                message,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * 判断是否成功
     * 
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}

