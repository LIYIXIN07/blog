package com.blog.controller;

import com.blog.common.result.PageResult;
import com.blog.common.result.Result;
import com.blog.dto.request.AccountUpdateRequest;
import com.blog.dto.request.ScheduledTaskRequest;
import com.blog.dto.response.ScheduledTaskDTO;
import com.blog.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @PutMapping("/account")
    public Result<Void> updateAccount(@RequestBody AccountUpdateRequest request,
                                      Authentication authentication) {
        systemService.updateAccount(authentication.getName(), request);
        return Result.success();
    }

    @GetMapping("/tasks")
    public Result<PageResult<ScheduledTaskDTO>> getTasks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(systemService.getTasks(pageNum, pageSize));
    }

    @PostMapping("/tasks")
    public Result<Void> createTask(@RequestBody ScheduledTaskRequest request,
                                   Authentication authentication) {
        systemService.createTask(request, authentication.getName());
        return Result.success();
    }

    @PutMapping("/tasks/{id}")
    public Result<Void> updateTask(@PathVariable Long id,
                                   @RequestBody ScheduledTaskRequest request,
                                   Authentication authentication) {
        systemService.updateTask(id, request, authentication.getName());
        return Result.success();
    }

    @DeleteMapping("/tasks/{id}")
    public Result<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        systemService.deleteTask(id, authentication.getName());
        return Result.success();
    }

    @PostMapping("/tasks/{id}/run")
    public Result<Void> runTask(@PathVariable Long id, Authentication authentication) {
        systemService.runTask(id, authentication.getName());
        return Result.success();
    }
}
