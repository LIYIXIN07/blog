package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.PageResult;
import com.blog.dto.request.AccountUpdateRequest;
import com.blog.dto.request.ScheduledTaskRequest;
import com.blog.dto.response.ScheduledTaskDTO;
import com.blog.entity.ScheduledTask;
import com.blog.entity.User;
import com.blog.repository.ScheduledTaskRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final UserRepository userRepository;
    private final ScheduledTaskRepository scheduledTaskRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Transactional
    public void updateAccount(String username, AccountUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getOldPassword() == null || request.getOldPassword().isBlank()) {
                throw new BusinessException("修改密码需要填写原密码");
            }
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new BusinessException("原密码不正确");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        logService.recordOperation(username, "系统管理", "修改账户信息", null, null);
    }

    public PageResult<ScheduledTaskDTO> getTasks(Integer pageNum, Integer pageSize) {
        ensureDefaultTasks();
        migrateLegacyTasks();
        Page<ScheduledTask> page = scheduledTaskRepository.findAllByOrderByIdAsc(
                PageRequest.of(pageNum - 1, pageSize));
        List<ScheduledTaskDTO> list = page.getContent().stream().map(this::toDTO).toList();
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Transactional
    public void createTask(ScheduledTaskRequest request, String username) {
        validateTaskRequest(request, true);
        ScheduledTask task = new ScheduledTask();
        applyTaskFields(task, request, true);
        scheduledTaskRepository.save(task);
        logService.recordOperation(username, "系统管理", "新增定时任务：" + task.getBeanName(), null, null);
    }

    @Transactional
    public void updateTask(Long id, ScheduledTaskRequest request, String username) {
        ScheduledTask task = scheduledTaskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        applyTaskFields(task, request, false);
        scheduledTaskRepository.save(task);
        logService.recordOperation(username, "系统管理", "更新定时任务：" + task.getBeanName(), null, null);
    }

    @Transactional
    public void deleteTask(Long id, String username) {
        ScheduledTask task = scheduledTaskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        scheduledTaskRepository.delete(task);
        logService.recordOperation(username, "系统管理", "删除定时任务：" + task.getBeanName(), null, null);
    }

    @Transactional
    public void runTask(Long id, String username) {
        ScheduledTask task = scheduledTaskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        long start = System.currentTimeMillis();
        try {
            task.setLastRunTime(LocalDateTime.now());
            scheduledTaskRepository.save(task);
            String taskName = task.getBeanName() != null ? task.getBeanName() : task.getJobKey();
            logService.recordTaskLog(taskName, 1, "任务执行成功", System.currentTimeMillis() - start);
            logService.recordOperation(username, "系统管理", "手动执行任务：" + taskName, null, null);
        } catch (Exception e) {
            String taskName = task.getBeanName() != null ? task.getBeanName() : task.getJobKey();
            logService.recordTaskLog(taskName, 0, e.getMessage(), System.currentTimeMillis() - start);
            throw new BusinessException("任务执行失败：" + e.getMessage());
        }
    }

    public void ensureDefaultTasks() {
        if (scheduledTaskRepository.count() > 0) {
            return;
        }
        saveTask("accessLogCleanScheduleTask", "cleanAccessLog", "0 0 3 * * ?", 1,
                "每天凌晨3点清理30天前的访问日志");
        saveTask("databaseBackupScheduleTask", "backupDatabase", "0 0 2 * * ?", 0,
                "每天凌晨2点备份数据库");
        saveTask("imageBedSyncScheduleTask", "syncImageBed", "0 0/30 * * * ?", 0,
                "每30分钟同步图床配置");
    }

    private void migrateLegacyTasks() {
        scheduledTaskRepository.findAll().forEach(task -> {
            boolean changed = false;
            if (task.getBeanName() == null || task.getBeanName().isBlank()) {
                task.setBeanName(task.getJobKey());
                changed = true;
            }
            if (task.getMethodName() == null || task.getMethodName().isBlank()) {
                task.setMethodName("execute");
                changed = true;
            }
            if (task.getParams() == null) {
                task.setParams("");
                changed = true;
            }
            if (changed) {
                scheduledTaskRepository.save(task);
            }
        });
    }

    private void saveTask(String beanName, String jobKey, String cron, int status, String remark) {
        ScheduledTask task = new ScheduledTask();
        task.setName(beanName);
        task.setJobKey(jobKey);
        task.setBeanName(beanName);
        task.setMethodName("execute");
        task.setParams("");
        task.setCronExpression(cron);
        task.setStatus(status);
        task.setRemark(remark);
        scheduledTaskRepository.save(task);
    }

    private void validateTaskRequest(ScheduledTaskRequest request, boolean creating) {
        if (creating) {
            if (request.getBeanName() == null || request.getBeanName().isBlank()) {
                throw new BusinessException("请输入 Bean 名称");
            }
            if (request.getMethodName() == null || request.getMethodName().isBlank()) {
                throw new BusinessException("请输入方法名");
            }
            if (request.getCronExpression() == null || request.getCronExpression().isBlank()) {
                throw new BusinessException("请输入 Cron 表达式");
            }
        }
    }

    private void applyTaskFields(ScheduledTask task, ScheduledTaskRequest request, boolean creating) {
        if (request.getBeanName() != null && !request.getBeanName().isBlank()) {
            task.setBeanName(request.getBeanName());
            task.setName(request.getBeanName());
            task.setJobKey(request.getBeanName());
        } else if (creating) {
            throw new BusinessException("请输入 Bean 名称");
        }

        if (request.getMethodName() != null && !request.getMethodName().isBlank()) {
            task.setMethodName(request.getMethodName());
        } else if (creating) {
            task.setMethodName("execute");
        }

        if (request.getParams() != null) {
            task.setParams(request.getParams());
        } else if (creating && task.getParams() == null) {
            task.setParams("");
        }

        if (request.getCronExpression() != null) {
            task.setCronExpression(request.getCronExpression());
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        } else if (creating && task.getStatus() == null) {
            task.setStatus(0);
        }

        if (request.getRemark() != null) {
            task.setRemark(request.getRemark());
        }
    }

    private ScheduledTaskDTO toDTO(ScheduledTask task) {
        ScheduledTaskDTO dto = new ScheduledTaskDTO();
        dto.setId(task.getId());
        dto.setBeanName(task.getBeanName() != null ? task.getBeanName() : task.getJobKey());
        dto.setMethodName(task.getMethodName() != null ? task.getMethodName() : "execute");
        dto.setParams(task.getParams() != null ? task.getParams() : "");
        dto.setCronExpression(task.getCronExpression());
        dto.setStatus(task.getStatus());
        dto.setRemark(task.getRemark());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }
}
