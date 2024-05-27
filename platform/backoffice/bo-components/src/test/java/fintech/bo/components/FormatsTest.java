package fintech.bo.components;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FormatsTest {

    @Test
    public void shouldFormat() {
        Formats.formatDate(LocalDate.now());
        Formats.formatDateTime(LocalDateTime.now());
    }

}
