package ru.itq.util.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import ru.itq.util.dto.TaskStatus;
import ru.itq.util.openapi.UtilApi;
import ru.itq.util.service.GenerateService;
import ru.itq.util.service.TaskManagerService;

@RestController
@RequiredArgsConstructor
@EnableScheduling
@RequestMapping("/util")
public class UtilController implements UtilApi {

    private final GenerateService generateService;
    private final TaskManagerService taskManagerService;

    @PostMapping("/generate")
    public ResponseEntity<HttpStatus> generateDocs() {
        generateService.createDoc();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{taskId}/enable")
    public ResponseEntity<String> enableTask(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "5") long initialDelay,
            @RequestParam(defaultValue = "10") long period) {
        try {
            taskManagerService.enableTask(taskId, initialDelay, period);
            return ResponseEntity.ok("Задача '" + taskId + "' запущена");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{taskId}/disable")
    public ResponseEntity<String> disableTask(@PathVariable String taskId) {
        try {
            taskManagerService.disableTask(taskId);
            return ResponseEntity.ok("Задача '" + taskId + "' остановлена");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{taskId}/status")
    public ResponseEntity<TaskStatus> getTaskStatus(@PathVariable String taskId) {
        boolean isRunning = taskManagerService.isTaskRunning(taskId);
        TaskStatus status = new TaskStatus(taskId, isRunning);
        return ResponseEntity.ok(status);
    }
}
