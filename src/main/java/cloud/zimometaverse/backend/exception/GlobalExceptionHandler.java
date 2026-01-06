package cloud.zimometaverse.backend.exception;

import cloud.zimometaverse.backend.common.Result;
import cloud.zimometaverse.backend.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * <p>职责：
 * <ul>
 *   <li>捕获并处理所有异常</li>
 *   <li>返回统一的错误响应格式</li>
 *   <li>记录异常日志</li>
 *   <li>保护敏感信息不泄露</li>
 * </ul>
 * 
 * <p>处理的异常类型：
 * <ul>
 *   <li>业务异常：BizException</li>
 *   <li>参数验证异常：MethodArgumentNotValidException、BindException</li>
 *   <li>参数类型异常：MethodArgumentTypeMismatchException</li>
 *   <li>404 异常：NoHandlerFoundException</li>
 *   <li>方法不支持异常：HttpRequestMethodNotSupportedException</li>
 *   <li>系统异常：Exception</li>
 * </ul>
 * 
 * @author Smart Learning Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 
     * @param e BizException
     * @return Result
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常：code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常（@RequestBody 参数验证失败）
     * 
     * @param e MethodArgumentNotValidException
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("参数验证失败：{}", message);
        return Result.error(ResultCode.VALIDATION_FAILED.getCode(), message);
    }

    /**
     * 处理参数绑定异常（@ModelAttribute 参数验证失败）
     * 
     * @param e BindException
     * @return Result
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("参数绑定失败：{}", message);
        return Result.error(ResultCode.VALIDATION_FAILED.getCode(), message);
    }

    /**
     * 处理约束违反异常（@Validated 方法参数验证失败）
     * 
     * @param e ConstraintViolationException
     * @return Result
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("参数约束违反：{}", message);
        return Result.error(ResultCode.VALIDATION_FAILED.getCode(), message);
    }

    /**
     * 处理缺少请求参数异常
     * 
     * @param e MissingServletRequestParameterException
     * @return Result
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        String message = String.format("缺少必需参数：%s", e.getParameterName());
        log.warn(message);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理参数类型不匹配异常
     * 
     * @param e MethodArgumentTypeMismatchException
     * @return Result
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        String message = String.format("参数类型错误：%s 应为 %s 类型",
                e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn(message);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理 404 异常
     * 
     * @param e NoHandlerFoundException
     * @return Result
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        String message = String.format("接口不存在：%s %s", e.getHttpMethod(), e.getRequestURL());
        log.warn(message);
        return Result.error(ResultCode.NOT_FOUND.getCode(), message);
    }

    /**
     * 处理请求方法不支持异常
     * 
     * @param e HttpRequestMethodNotSupportedException
     * @return Result
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        String message = String.format("不支持 %s 请求方法", e.getMethod());
        log.warn(message);
        return Result.error(ResultCode.METHOD_NOT_ALLOWED.getCode(), message);
    }

    /**
     * 处理其他未捕获的异常
     * 
     * <p>注意：生产环境不应该暴露详细的错误信息，应返回通用错误消息
     * 
     * @param e Exception
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        // 记录完整的异常堆栈（包含敏感信息）
        log.error("系统异常", e);
        
        // 返回通用错误消息（不暴露内部细节）
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR);
    }
}

