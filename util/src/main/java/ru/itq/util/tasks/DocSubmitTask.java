package ru.itq.util.tasks;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.itq.util.tasks.executors.DocSubmitExecutor;

@Component
@Slf4j
public class DocSubmitTask extends AbstractTask {

    private final DocSubmitExecutor executor;
    public DocSubmitTask(DocSubmitExecutor executor) {
        super("docSubmit");
        this.executor = executor;
    }

    @Override
    public void execute() {
        executor.execute();
    }

    @Override
    public void onStart() {
        log.info("Задача docSubmit обработки документов стартовала");
    }

    @Override
    public void onStop() {
        log.info("Задача docSubmit обработки документов остановлена");
    }
}
