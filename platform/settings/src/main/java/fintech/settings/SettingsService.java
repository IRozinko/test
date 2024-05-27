package fintech.settings;

import fintech.settings.commands.UpdatePropertyCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public interface SettingsService {

    boolean getBoolean(String name);

    String getString(String name);

    <T> T getJson(String name, Class<T> objectClass);

    Long getNumber(String name);

    BigDecimal getDecimal(String name);

    LocalDate getDate(String name);

    LocalDateTime getDateTime(String name);

    void initProperty(String name, Long defaultValue, String description, Consumer<Long> validator);

    void initProperty(String name, Boolean value, String description, Consumer<Boolean> validator);

    void initProperty(String name, BigDecimal value, String description, Consumer<BigDecimal> validator);

    void initProperty(String name, String value, String description, Consumer<String> validator);

    void initProperty(String name, LocalDate value, String description, Consumer<LocalDate> validator);

    void initProperty(String name, LocalDateTime value, String description, Consumer<LocalDateTime> validator);

    void removeProperty(String name);

    List<Property> listAll();

    void update(UpdatePropertyCommand updatePropertyCommand);

}
