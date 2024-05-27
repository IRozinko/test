package fintech.task.spi;


public interface TaskListener {

    void handle(TaskContext context);
}
