package fintech.task.spi;

import java.time.LocalDateTime;

public interface ExpiredTaskConsumer {

    int consume(LocalDateTime when);
}
