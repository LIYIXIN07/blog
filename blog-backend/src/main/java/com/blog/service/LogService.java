package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.PageResult;
import com.blog.dto.request.ScheduledTaskRequest;
import com.blog.dto.response.*;
import com.blog.entity.*;
import com.blog.repository.*;
import com.blog.util.IpUtils;
import com.blog.util.UserAgentUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LoginLogRepository loginLogRepository;
    private final OperationLogRepository operationLogRepository;
    private final ExceptionLogRepository exceptionLogRepository;
    private final AccessLogRepository accessLogRepository;
    private final TaskLogRepository taskLogRepository;

    public PageResult<LoginLogDTO> getLoginLogs(Integer pageNum, Integer pageSize,
                                                LocalDateTime startTime, LocalDateTime endTime) {
        return queryLogs(pageNum, pageSize, startTime, endTime,
                loginLogRepository::findByTimeRange, this::toLoginDTO);
    }

    public PageResult<OperationLogDTO> getOperationLogs(Integer pageNum, Integer pageSize,
                                                        LocalDateTime startTime, LocalDateTime endTime) {
        return queryLogs(pageNum, pageSize, startTime, endTime,
                operationLogRepository::findByTimeRange, this::toOperationDTO);
    }

    public PageResult<ExceptionLogDTO> getExceptionLogs(Integer pageNum, Integer pageSize,
                                                        LocalDateTime startTime, LocalDateTime endTime) {
        return queryLogs(pageNum, pageSize, startTime, endTime,
                exceptionLogRepository::findByTimeRange, this::toExceptionDTO);
    }

    public PageResult<AccessLogDTO> getAccessLogs(Integer pageNum, Integer pageSize,
                                                  LocalDateTime startTime, LocalDateTime endTime,
                                                  String visitorUuid) {
        Page<AccessLog> page = accessLogRepository.findByTimeRange(
                visitorUuid, startTime, endTime, PageRequest.of(pageNum - 1, pageSize));
        return new PageResult<>(page.getContent().stream().map(this::toAccessDTO).toList(),
                page.getTotalElements(), pageNum, pageSize);
    }

    public PageResult<TaskLogDTO> getTaskLogs(Integer pageNum, Integer pageSize,
                                              LocalDateTime startTime, LocalDateTime endTime) {
        return queryLogs(pageNum, pageSize, startTime, endTime,
                taskLogRepository::findByTimeRange, this::toTaskDTO);
    }

    @Transactional
    public void deleteLog(String type, Long id) {
        switch (type) {
            case "login" -> loginLogRepository.deleteById(id);
            case "operation" -> operationLogRepository.deleteById(id);
            case "exception" -> exceptionLogRepository.deleteById(id);
            case "access" -> accessLogRepository.deleteById(id);
            case "task" -> taskLogRepository.deleteById(id);
            default -> throw new BusinessException("不支持的日志类型");
        }
    }

    @Transactional
    public void recordLogin(String username, String ip, String userAgent, boolean success, String message) {
        LoginLog log = new LoginLog();
        log.setUsername(username);
        log.setIp(ip);
        log.setIpSource(IpUtils.resolveIpSource(ip));
        log.setOs(UserAgentUtils.parseOs(userAgent));
        log.setBrowser(UserAgentUtils.parseBrowser(userAgent));
        log.setStatus(success ? 1 : 0);
        log.setMessage(message);
        loginLogRepository.save(log);
    }

    @Transactional
    public void recordOperation(String username, String module, String description,
                                HttpServletRequest request, String params) {
        OperationLog log = new OperationLog();
        log.setUsername(username);
        log.setModule(module);
        log.setDescription(description);
        if (request != null) {
            log.setMethod(request.getMethod());
            log.setUri(request.getRequestURI());
            log.setIp(IpUtils.getClientIp(request));
        }
        log.setParams(params);
        operationLogRepository.save(log);
    }

    @Transactional
    public void recordException(String username, HttpServletRequest request, Exception exception) {
        ExceptionLog log = new ExceptionLog();
        log.setUsername(username);
        if (request != null) {
            log.setUri(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setIp(IpUtils.getClientIp(request));
        }
        log.setExceptionName(exception.getClass().getSimpleName());
        log.setMessage(exception.getMessage());
        log.setStackTrace(getStackTrace(exception));
        exceptionLogRepository.save(log);
    }

    @Transactional
    public void recordAccess(String visitorUuid, String uri, String ip, String userAgent) {
        AccessLog log = new AccessLog();
        log.setVisitorUuid(visitorUuid);
        log.setUri(uri != null ? uri : "/");
        log.setIp(ip);
        log.setIpSource(IpUtils.resolveIpSource(ip));
        log.setOs(UserAgentUtils.parseOs(userAgent));
        log.setBrowser(UserAgentUtils.parseBrowser(userAgent));
        accessLogRepository.save(log);
    }

    @Transactional
    public void recordTaskLog(String taskName, int status, String message, long durationMs) {
        TaskLog log = new TaskLog();
        log.setTaskName(taskName);
        log.setStatus(status);
        log.setMessage(message);
        log.setDurationMs(durationMs);
        taskLogRepository.save(log);
    }

    private <E, D> PageResult<D> queryLogs(Integer pageNum, Integer pageSize,
                                             LocalDateTime startTime, LocalDateTime endTime,
                                             TimeRangeQuery<E> query,
                                             Function<E, D> mapper) {
        Page<E> page = query.query(startTime, endTime, PageRequest.of(pageNum - 1, pageSize));
        return new PageResult<>(page.getContent().stream().map(mapper).toList(),
                page.getTotalElements(), pageNum, pageSize);
    }

    private LoginLogDTO toLoginDTO(LoginLog log) {
        LoginLogDTO dto = new LoginLogDTO();
        dto.setId(log.getId());
        dto.setUsername(log.getUsername());
        dto.setIp(log.getIp());
        dto.setIpSource(IpUtils.resolveIpSource(log.getIp()));
        dto.setOs(log.getOs());
        dto.setBrowser(log.getBrowser());
        dto.setStatus(log.getStatus());
        dto.setMessage(log.getMessage());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    private OperationLogDTO toOperationDTO(OperationLog log) {
        OperationLogDTO dto = new OperationLogDTO();
        dto.setId(log.getId());
        dto.setUsername(log.getUsername());
        dto.setModule(log.getModule());
        dto.setDescription(log.getDescription());
        dto.setMethod(log.getMethod());
        dto.setUri(log.getUri());
        dto.setIp(log.getIp());
        dto.setParams(log.getParams());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    private ExceptionLogDTO toExceptionDTO(ExceptionLog log) {
        ExceptionLogDTO dto = new ExceptionLogDTO();
        dto.setId(log.getId());
        dto.setUsername(log.getUsername());
        dto.setUri(log.getUri());
        dto.setMethod(log.getMethod());
        dto.setExceptionName(log.getExceptionName());
        dto.setMessage(log.getMessage());
        dto.setStackTrace(log.getStackTrace());
        dto.setIp(log.getIp());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    private AccessLogDTO toAccessDTO(AccessLog log) {
        AccessLogDTO dto = new AccessLogDTO();
        dto.setId(log.getId());
        dto.setVisitorUuid(log.getVisitorUuid());
        dto.setUri(log.getUri());
        dto.setIp(log.getIp());
        dto.setIpSource(IpUtils.resolveIpSource(log.getIp()));
        dto.setOs(log.getOs());
        dto.setBrowser(log.getBrowser());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    private TaskLogDTO toTaskDTO(TaskLog log) {
        TaskLogDTO dto = new TaskLogDTO();
        dto.setId(log.getId());
        dto.setTaskName(log.getTaskName());
        dto.setStatus(log.getStatus());
        dto.setMessage(log.getMessage());
        dto.setDurationMs(log.getDurationMs());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    private String getStackTrace(Exception exception) {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        String trace = writer.toString();
        return trace.length() > 4000 ? trace.substring(0, 4000) : trace;
    }

    @FunctionalInterface
    private interface TimeRangeQuery<E> {
        Page<E> query(LocalDateTime startTime, LocalDateTime endTime, PageRequest pageable);
    }
}
