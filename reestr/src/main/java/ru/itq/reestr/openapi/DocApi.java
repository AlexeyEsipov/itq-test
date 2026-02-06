package ru.itq.reestr.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itq.reestr.dto.*;

import java.util.List;
@Tag(name = "Document Controller", description = "API для управления реестром документов")
public interface DocApi {

    @PostMapping("/")
    @Operation(
            summary = "Создать новый документ",
            description = "Создает запись о документе в реестре и возвращает её идентификатор")
    ResponseEntity<Long> createDocument(
            @Parameter(
                    description = "DTO с данными для создания документа",
                    required = true)
            @RequestBody DocCreateDto createDto);

    @GetMapping("/{innerId}")
    @Operation(
            summary = "Получить документ по innerId",
            description = "Возвращает историю и данные документа по его внутреннему идентификатору")
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Документ найден",
                    content = @Content(schema = @Schema(implementation = DocHistory.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Документ не найден")})
    ResponseEntity<DocHistory> get(
            @Parameter(
                    description = "Внутренний идентификатор документа",
                    example = "12345",
                    required = true)
            @PathVariable(value = "innerId") String innerId);

    @GetMapping("/")
    @Operation(
            summary = "Получить список документов",
            description = "Возвращает страницу со списком документов с учетом пагинации и параметров BathRequest")
    ResponseEntity<Page<DocDto>> getAll(
            @Parameter(
                    description = "Параметры запроса (пагинация, сортировка и фильтры)")
            @ModelAttribute BathRequest bathRequest);

    @GetMapping("/filter")
    @Operation(
            summary = "Поиск документов по фильтрам",
            description = "Ищет документы, соответствующие критериям поиска")
    ResponseEntity<Page<DocDtoStatus>> findWithFilter(
            @Parameter(
                    description = "Критерии поиска")
            @ModelAttribute SearchValue searchValue);

    @PostMapping("/submit")
    @Operation(
            summary = "Пакетное согласование документов",
            description = "Пытается согласовать пакет документов и возвращает результаты попыток")
    ResponseEntity<List<ResultAttempt>> batchSubmit(
            @Parameter(
                    description = "Список документов для согласования",
                    required = true)
            @RequestBody DocBatch batch);

    @PostMapping("/approve")
    @Operation(
            summary = "Пакетное утверждение документов",
            description = "Пытается утвердить пакет документов и возвращает результаты попыток")
    ResponseEntity<List<ResultAttempt>> batchApprove(
            @Parameter(
                    description = "Список документов для утверждения",
                    required = true)
            @RequestBody DocBatch batch);

    @PostMapping("/approvecon")
    @Operation(
            summary = "Тестовое многопоточное утверждение документа",
            description = "Обрабатывает запрос на утверждение в условиях конкурентного доступа")
    ResponseEntity<ConcurrentResult> concurrentApprove(
            @Parameter(
                    description = "Параметры запроса для тестирования",
                    required = true)
            @RequestBody ConcurrentTestRequest request);
}
