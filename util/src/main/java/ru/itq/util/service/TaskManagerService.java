package ru.itq.util.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itq.util.tasks.AbstractTask;
import ru.itq.util.tasks.DocApproveTask;
import ru.itq.util.tasks.DocSubmitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TaskManagerService {

    private final ScheduledExecutorService scheduledExecutorService;
    private final Map<String, AbstractTask> tasks = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    public TaskManagerService(ScheduledExecutorService scheduledExecutorService,
                              DocSubmitTask docSubmitTask,
                              DocApproveTask docApproveTask) {
        this.scheduledExecutorService = scheduledExecutorService;
        registerTask(docSubmitTask);
        registerTask(docApproveTask);
    }

    private void registerTask(AbstractTask task) {
        tasks.put(task.getTaskId(), task);
    }

    public void enableTask(String taskId, long initialDelay, long period) {
        AbstractTask task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Задача с ID " + taskId + " не найдена");
        }
        if (task.isRunning()) {
            log.info("Задача {} уже запущена", taskId);
            return;
        }
        task.setRunning(true);
        task.onStart();
        ScheduledFuture<?> future = scheduledExecutorService.scheduleWithFixedDelay(
                task,
                initialDelay,
                period,
                TimeUnit.SECONDS
        );
        scheduledFutures.put(taskId, future);
        log.info("Задача {} успешно запущена с периодом {} сек", taskId, period);
    }

    public void disableTask(String taskId) {
        AbstractTask task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Задача с ID " + taskId + " не найдена");
        }
        if (!task.isRunning()) {
            log.info("Задача {} уже остановлена", taskId);
            return;
        }
        task.setRunning(false);
        task.onStop();
        ScheduledFuture<?> future = scheduledFutures.get(taskId);
        if (future != null) {
            future.cancel(true);
            scheduledFutures.remove(taskId);
        }
        log.info("Задача {} остановлена", taskId);
    }

    public boolean isTaskRunning(String taskId) {
        AbstractTask task = tasks.get(taskId);
        return task != null && task.isRunning();
    }
}
