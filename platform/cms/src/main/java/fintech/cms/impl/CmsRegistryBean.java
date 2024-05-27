package fintech.cms.impl;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import fintech.Validate;
import fintech.cms.CmsDocumentationGenerator;
import fintech.cms.db.CmsItemEntity;
import fintech.cms.db.CmsItemRepository;
import fintech.cms.db.Entities;
import fintech.cms.db.LocaleEntity;
import fintech.cms.db.LocaleRepository;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemDeleteEvent;
import fintech.cms.spi.CmsItemSavedEvent;
import fintech.cms.spi.CmsItemType;
import fintech.cms.spi.CmsRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static fintech.cms.db.Entities.item;

@Slf4j
@Component
@RequiredArgsConstructor
public class CmsRegistryBean implements CmsRegistry {

    private final CmsItemRepository cmsItemRepository;
    private final LocaleRepository localeRepository;
    private final ApplicationEventPublisher eventPublisher;

    private Map<String, Supplier<Object>> testingContext;
    private Map<String, Object> defaultContext = ImmutableMap.of();

    @Transactional
    @Override
    public Long saveItem(CmsItem cmsItem, boolean overwrite) {
        Validate.notNull(cmsItem.getItemType(), "Null type");
        Validate.notBlank(cmsItem.getKey(), "Blank key");
        Validate.notBlank(cmsItem.getLocale(), "Blank locale");

        CmsItemEntity entity = cmsItemRepository.findOne(
            item.itemType.eq(cmsItem.getItemType())
                .and(item.key.eq(cmsItem.getKey()))
                .and(item.locale.eq(cmsItem.getLocale())));

        boolean exists = entity != null;
        if (exists && !overwrite) {
            return entity.getId();
        }

        entity = MoreObjects.firstNonNull(entity, new CmsItemEntity());
        entity.setKey(cmsItem.getKey());
        entity.setLocale(cmsItem.getLocale());
        entity.setDescription(cmsItem.getDescription());
        entity.setScope(cmsItem.getScope());
        entity.setItemType(cmsItem.getItemType());
        entity.setEmailSubjectTemplate(cmsItem.getEmailSubjectTemplate());
        entity.setEmailBodyTemplate(cmsItem.getEmailBodyTemplate());
        entity.setSmsTextTemplate(cmsItem.getSmsTextTemplate());
        entity.setContentTemplate(cmsItem.getContentTemplate());
        entity.setTitleTemplate(cmsItem.getTitleTemplate());
        entity.setHeaderTemplate(cmsItem.getHeaderTemplate());
        entity.setFooterTemplate(cmsItem.getFooterTemplate());
        log.info("Saved CMS item [{}]", cmsItem.getKey());

        Long id = cmsItemRepository.saveAndFlush(entity).getId();
        eventPublisher.publishEvent(new CmsItemSavedEvent(cmsItem));
        return id;
    }

    @Transactional
    @Override
    public void deleteItem(String key) {
        log.info("Deleting CMS item by key [{}]", key);
        List<CmsItemEntity> cmsItems = cmsItemRepository.findAll(item.key.eq(key));
        Validate.isTrue(!cmsItems.isEmpty(), "Item not found");
        cmsItemRepository.delete(cmsItems);
        eventPublisher.publishEvent(new CmsItemDeleteEvent());
    }

    @Transactional
    @Override
    public Optional<CmsItem> findItem(CmsItemType type, String key, String locale) {
        Validate.notNull(type, "Null type");
        Validate.notBlank(key, "Blank key");
        Validate.notBlank(locale, "Blank locale");
        CmsItemEntity entity = cmsItemRepository.findOne(
            item.itemType.eq(type)
                .and(item.key.eq(key))
                .and(item.locale.eq(locale)));
        if (entity == null) {
            entity = cmsItemRepository.findOne(
                item.itemType.eq(type)
                    .and(item.key.eq(key)).and(item.locale.eq("es")));
        }
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(entity.toValueObject());
    }

    @Transactional
    @Override
    public Optional<CmsItem> findItem(String key, String locale) {
        Optional<CmsItemEntity> entity = cmsItemRepository.getOptional(item.key.eq(key).and(item.locale.eq(locale)));
        return entity.map(CmsItemEntity::toValueObject);
    }

    @Override
    public void setTestingContext(Map<String, Supplier<Object>> testingContext) {
        Validate.notNull(testingContext, "Template preview context is not initialized");
        this.testingContext = testingContext;
    }

    @Override
    public Map<String, Object> getTestingContext() {
        Validate.notNull(testingContext, "Testing context not set");
        return testingContext.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entity -> entity.getValue().get()));
    }

    @Override
    public void setDefaultContext(Map<String, Object> defaultContext) {
        this.defaultContext = ImmutableMap.copyOf(defaultContext);
    }

    @Override
    public Map<String, Object> getDefaultContext() {
        return this.defaultContext;
    }


    @Override
    @SneakyThrows
    public String getTestingContextDocumentation() {
        Map<String, Object> context = new LinkedHashMap<>();
        context.putAll(getTestingContext());
        context.putAll(getDefaultContext());
        try (CmsDocumentationGenerator generator = new CmsDocumentationGenerator()) {
            return generator.generateContextDocumentation(context);
        }
    }

    @Transactional
    @Override
    public void addLocale(String locale) {
        Validate.notBlank(locale, "Locale name must be provided");
        LocaleEntity existingLocale = localeRepository.findOneOrNull(Entities.locale.locale.eq(locale));
        Validate.isTrue(existingLocale == null, "Locale %s already exists", locale);

        LocaleEntity localeEntity = new LocaleEntity();
        localeEntity.setLocale(locale.toLowerCase());
        localeEntity.setIsDefault(false);
        localeRepository.save(localeEntity);
    }

    @Transactional
    @Override
    public void deleteLocale(String locale) {
        LocaleEntity localeEntity = localeRepository.findOneOrNull(Entities.locale.locale.eq(locale));
        Validate.notNull(localeEntity, "Locale not found: %s", locale);
        List<CmsItemEntity> cmsItems = cmsItemRepository.findAll(item.locale.eq(locale));
        cmsItemRepository.delete(cmsItems);
        localeRepository.delete(localeEntity);
    }
}
