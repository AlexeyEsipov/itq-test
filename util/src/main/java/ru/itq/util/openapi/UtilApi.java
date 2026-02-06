package ru.itq.util.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itq.util.dto.TaskStatus;
import ru.itq.util.service.GenerateService;
import ru.itq.util.service.TaskManagerService;


@Tag(name = "Utility Controller", description = "API для управления утилитой пакетной обработки документов")
public interface UtilApi {

    @PostMapping("/generate")
    @Operation(
            summary = "Запустить генерацию документов",
            description = "Асинхронно запускает процесс создания документов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Задача на генерацию успешно принята в обработку"),
            @ApiResponse(responseCode = "500",
                    description = "Внутренняя ошибка сервера при запуске")
    })
    ResponseEntity<HttpStatus> generateDocs();

    @PostMapping("/{taskId}/enable")
    @Operation(
            summary = "Включить пакетную отправку docSubmit или docApprove",
            description = "Запускает выполнение задачи docSubmit или docApprove по расписанию с указанными параметрами")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Задача успешно запущена",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный ID задачи или аргументы",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    ResponseEntity<String> enableTask(
            @Parameter(
                    description = "Идентификатор задачи docSubmit или docApprove",
                    required = true,
                    example = "docApprove")
            @PathVariable String taskId,

            @Parameter(
                    description = "Задержка перед первым запуском (в секундах)",
                    example = "5")
            @RequestParam(defaultValue = "5") long initialDelay,

            @Parameter(
                    description = "Период между запусками (в секундах)",
                    example = "10")
            @RequestParam(defaultValue = "10") long period);

    @PostMapping("/{taskId}/disable")
    @Operation(
            summary = "Остановить пакетную отправку docSubmit или docApprove",
            description = "Отключает выполнение задачи docSubmit или docApprove по расписанию")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Задача успешно остановлена",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный ID задачи",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    ResponseEntity<String> disableTask(@PathVariable String taskId);

    @GetMapping("/{taskId}/status")
    @Operation(
            summary = "Получить статус задачи docSubmit или docApprove",
            description = "Возвращает информацию о том, запущена ли задача docSubmit или docApprove в данный момент")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус успешно получен",
                    content = @Content(schema = @Schema(implementation = TaskStatus.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Задача не найдена")
    })
    ResponseEntity<TaskStatus> getTaskStatus(
            @Parameter(
                    description = "Идентификатор задачи docSubmit или docApprove",
                    required = true,
                    example = "cleanupTask")
            @PathVariable String taskId);
}
