package ru.itq.util.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.itq.util.tasks.executors.DocApproveExecutor;

@Component
@Slf4j
public class DocApproveTask extends AbstractTask {

    private final DocApproveExecutor executor;


    public DocApproveTask(DocApproveExecutor executor) {
        super("docApprove");
        this.executor = executor;
    }

    @Override
    public void execute() {
        executor.execute();
    }

    @Override
    public void onStart() {
        log.info("Задача docApprove обработки документов стартовала");
    }

    @Override
    public void onStop() {
        log.info("Задача docApprove обработки документов остановлена");
    }
}
