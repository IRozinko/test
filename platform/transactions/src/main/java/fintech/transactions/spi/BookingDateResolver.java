package fintech.transactions.spi;

import java.time.LocalDate;

public interface BookingDateResolver {

    LocalDate get(LocalDate valueDate);

}
