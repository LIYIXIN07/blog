package com.blog.common.exception;



import com.blog.common.result.Result;

import com.blog.service.LogService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.validation.BindException;

import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.context.request.RequestContextHolder;

import org.springframework.web.context.request.ServletRequestAttributes;



import jakarta.validation.ConstraintViolation;

import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;



/**

 * 全局异常处理器

 */

@Slf4j

@RestControllerAdvice

@RequiredArgsConstructor

public class GlobalExceptionHandler {



    private final LogService logService;



    /**

     * 处理业务异常

     */

    @ExceptionHandler(BusinessException.class)

    public Result<Void> handleBusinessException(BusinessException e) {

        log.error("业务异常：{}", e.getMessage());

        return Result.error(e.getCode(), e.getMessage());

    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e) {
        return Result.error(401, "用户名或密码错误");
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleDisabledException(DisabledException e) {
        return Result.error(403, "账号已被禁用");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        return Result.error(401, "未登录或登录已过期");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "没有权限访问");
    }



    /**

     * 处理参数校验异常

     */

    @ExceptionHandler(MethodArgumentNotValidException.class)

    @ResponseStatus(HttpStatus.BAD_REQUEST)

    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors()

                .stream()

                .map(FieldError::getDefaultMessage)

                .collect(Collectors.joining(", "));

        log.error("参数校验异常：{}", message);

        return Result.error(400, message);

    }



    /**

     * 处理绑定异常

     */

    @ExceptionHandler(BindException.class)

    @ResponseStatus(HttpStatus.BAD_REQUEST)

    public Result<Void> handleBindException(BindException e) {

        String message = e.getBindingResult().getFieldErrors()

                .stream()

                .map(FieldError::getDefaultMessage)

                .collect(Collectors.joining(", "));

        log.error("参数绑定异常：{}", message);

        return Result.error(400, message);

    }



    /**

     * 处理约束违反异常

     */

    @ExceptionHandler(ConstraintViolationException.class)

    @ResponseStatus(HttpStatus.BAD_REQUEST)

    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {

        String message = e.getConstraintViolations()

                .stream()

                .map(ConstraintViolation::getMessage)

                .collect(Collectors.joining(", "));

        log.error("约束违反异常：{}", message);

        return Result.error(400, message);

    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据约束异常：", e);
        return Result.error(400, "保存失败，请检查首图 URL、摘要长度或字段格式是否正确");
    }



    /**

     * 处理其他异常

     */

    @ExceptionHandler(Exception.class)

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

    public Result<Void> handleException(Exception e) {

        log.error("系统异常：", e);

        logService.recordException(getCurrentUsername(), getCurrentRequest(), e);

        return Result.error("系统异常，请联系管理员");

    }



    private HttpServletRequest getCurrentRequest() {

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return attrs != null ? attrs.getRequest() : null;

    }



    private String getCurrentUsername() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {

            return authentication.getName();

        }

        return "anonymous";

    }

}

