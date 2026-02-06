package ru.itq.util.tasks;


public abstract class AbstractTask implements Runnable {
    protected final String taskId;
    protected volatile boolean isRunning = false;

    protected AbstractTask(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        if (!isRunning) {
            return;
        }
        execute();
    }

    protected abstract void execute();

    public void onStart() {
    }

    public void onStop() {
    }
}
