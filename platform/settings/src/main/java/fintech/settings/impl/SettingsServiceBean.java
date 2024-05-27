package fintech.settings.impl;

import com.google.common.base.Preconditions;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.settings.Property;
import fintech.settings.SettingsService;
import fintech.settings.commands.UpdatePropertyCommand;
import fintech.settings.db.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Transactional
@Component
class SettingsServiceBean implements SettingsService {

    private final PropertyRepository propertyRepository;

    private final Map<String, Consumer<Long>> numberValidators = new HashMap<>();
    private final Map<String, Consumer<Boolean>> booleanValidators = new HashMap<>();
    private final Map<String, Consumer<String>> textValidators = new HashMap<>();
    private final Map<String, Consumer<BigDecimal>> decimalValidators = new HashMap<>();
    private final Map<String, Consumer<LocalDate>> dateValidators = new HashMap<>();
    private final Map<String, Consumer<LocalDateTime>> dateTimeValidators = new HashMap<>();

    public SettingsServiceBean(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean getBoolean(String name) {
        PropertyEntity one = get(name);
        if (!(one instanceof BooleanPropertyEntity)) {
            throw new IllegalArgumentException("Property " + name + " doesn't exist or has different type");
        }
        return ((BooleanPropertyEntity) one).getBooleanValue();
    }

    @Override
    @Transactional(readOnly = true)
    public String getString(String name) {
        PropertyEntity one = get(name);
        if (!(one instanceof TextPropertyEntity)) {
            throw new IllegalArgumentException("Property " + name + " doesn't exist or has different type");
        }
        return ((TextPropertyEntity) one).getTextValue();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T getJson(String name, Class<T> objectClass) {
        String json = getString(name);
        return JsonUtils.readValue(json, objectClass);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNumber(String name) {
        PropertyEntity one = get(name);
        if (!(one instanceof NumberPropertyEntity)) {
            throw new IllegalArgumentException("Property " + name + " doesn't exist or has different type");
        }
        return ((NumberPropertyEntity) one).getNumberValue();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getDecimal(String name) {
        PropertyEntity one = get(name);
        if (!(one instanceof DecimalPropertyEntity)) {
            throw new IllegalArgumentException("Property " + name + " doesn't exist or has different type");
        }
        return ((DecimalPropertyEntity) one).getDecimalValue();
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDate getDate(String name) {
        PropertyEntity one = get(name);
        if (!(one instanceof DatePropertyEntity)) {
            throw new IllegalArgumentException("Property " + name + " doesn't exist or has different type");
        }
        return ((DatePropertyEntity) one).getDateValue();
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime getDateTime(String name) {
        PropertyEntity one = get(name);
        if (!(one instanceof DateTimePropertyEntity)) {
            throw new IllegalArgumentException("Property " + name + " doesn't exist or has different type");
        }
        return ((DateTimePropertyEntity) one).getDateTimeValue();
    }

    @Override
    public void initProperty(String name, Long defaultValue, String description, Consumer<Long> validator) {
        validator = withDefaultValidator(validator);
        validator.accept(defaultValue);
        numberValidators.put(name, validator);
        setProperty(name, description, NumberPropertyEntity.class, (property) -> property.setNumberValue(defaultValue));
    }

    @Override
    public void initProperty(String name, Boolean defaultValue, String description, Consumer<Boolean> validator) {
        validator = withDefaultValidator(validator);
        validator.accept(defaultValue);
        booleanValidators.put(name, validator);
        setProperty(name, description, BooleanPropertyEntity.class, (property) -> property.setBooleanValue(defaultValue));
    }

    @Override
    public void initProperty(String name, BigDecimal defaultValue, String description, Consumer<BigDecimal> validator) {
        validator = withDefaultValidator(validator);
        validator.accept(defaultValue);
        decimalValidators.put(name, validator);
        setProperty(name, description, DecimalPropertyEntity.class, (property) -> property.setDecimalValue(defaultValue));
    }

    @Override
    public void initProperty(String name, String defaultValue, String description, Consumer<String> validator) {
        validator = withDefaultValidator(validator);
        validator.accept(defaultValue);
        textValidators.put(name, validator);
        setProperty(name, description, TextPropertyEntity.class, (property) -> property.setTextValue(defaultValue));
    }

    @Override
    public void initProperty(String name, LocalDate defaultValue, String description, Consumer<LocalDate> validator) {
        validator = withDefaultValidator(validator);
        validator.accept(defaultValue);
        dateValidators.put(name, validator);
        setProperty(name, description, DatePropertyEntity.class, (property) -> property.setDateValue(defaultValue));
    }

    @Override
    public void initProperty(String name, LocalDateTime defaultValue, String description, Consumer<LocalDateTime> validator) {
        validator = withDefaultValidator(validator);
        validator.accept(defaultValue);
        dateTimeValidators.put(name, validator);
        setProperty(name, description, DateTimePropertyEntity.class, (property) -> property.setDateTimeValue(defaultValue));
    }

    @Override
    public void removeProperty(String name) {
        PropertyEntity property = get(name);
        if (property != null) {
            propertyRepository.delete(property);
            propertyRepository.flush();
        }
    }

    @Override
    public List<Property> listAll() {
        List<PropertyEntity> all = propertyRepository.findAll();
        return all.stream().map(PropertyEntity::toValueObject).collect(Collectors.toList());
    }

    @SneakyThrows
    private <T extends PropertyEntity> void setProperty(String name, String description, Class<T> clazz, Consumer<T> valueSetter) {
        Preconditions.checkArgument(isNotBlank(name), "Property name can't be empty");
        PropertyEntity property = propertyRepository.findOne(Entities.propertyEntity.name.equalsIgnoreCase(name));
        if (property == null) {
            T newProperty = clazz.newInstance();
            valueSetter.accept(newProperty);
            newProperty.setName(name);
            newProperty.setDescription(description);
            propertyRepository.saveAndFlush(newProperty);
        } else {
            // do not overwrite value
        }
    }


    @Override
    @Transactional
    public void update(UpdatePropertyCommand command) {
        Preconditions.checkArgument(command != null, "Command shouldn't be null");
        PropertyEntity property = get(command.getName());

        if (property instanceof BooleanPropertyEntity) {
            booleanValidators.getOrDefault(command.getName(), Validate::notNull).accept(command.getBooleanValue());
            ((BooleanPropertyEntity) property).setBooleanValue(command.getBooleanValue());
        } else if (property instanceof NumberPropertyEntity) {
            numberValidators.getOrDefault(command.getName(), Validate::notNull).accept(command.getNumberValue());
            ((NumberPropertyEntity) property).setNumberValue(command.getNumberValue());
        } else if (property instanceof DecimalPropertyEntity) {
            decimalValidators.getOrDefault(command.getName(), Validate::notNull).accept(command.getDecimalValue());
            ((DecimalPropertyEntity) property).setDecimalValue(command.getDecimalValue());
        } else if (property instanceof TextPropertyEntity) {
            textValidators.getOrDefault(command.getName(), Validate::notNull).accept(command.getTextValue());
            ((TextPropertyEntity) property).setTextValue(command.getTextValue());
        } else if (property instanceof DateTimePropertyEntity) {
            dateTimeValidators.getOrDefault(command.getName(), Validate::notNull).accept(command.getDateTimeValue());
            ((DateTimePropertyEntity) property).setDateTimeValue(command.getDateTimeValue());
        } else if (property instanceof DatePropertyEntity) {
            dateValidators.getOrDefault(command.getName(), Validate::notNull).accept(command.getDateValue());
            ((DatePropertyEntity) property).setDateValue(command.getDateValue());
        } else {
            throw new IllegalArgumentException("Unknown property type " + property.getClass());
        }
    }

    private PropertyEntity get(String name) {
        Preconditions.checkArgument(isNotBlank(name), "Property name can't be empty");
        return propertyRepository.findOne(Entities.propertyEntity.name.equalsIgnoreCase(name));
    }

    private <T> Consumer<T> withDefaultValidator(Consumer<T> validator) {
        Preconditions.checkNotNull(validator, "Null validator");
        return ((Consumer<T>) t -> Validate.notNull(t, "Null default value")).andThen(validator);
    }
}
