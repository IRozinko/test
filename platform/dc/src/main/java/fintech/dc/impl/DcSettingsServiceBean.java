package fintech.dc.impl;

import fintech.JsonUtils;
import fintech.dc.DcSettingsService;
import fintech.dc.db.DcSettingsEntity;
import fintech.dc.db.DcSettingsRepository;
import fintech.dc.model.DcSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;

@Slf4j
@Component
@Transactional
public class DcSettingsServiceBean implements DcSettingsService {

    public static final String DC_SETTINGS = "dcSettings";

    private final DcSettingsRepository dcSettingsRepository;

    public DcSettingsServiceBean(DcSettingsRepository dcSettingsRepository) {
        this.dcSettingsRepository = dcSettingsRepository;
    }

    @Override
    @Cacheable(DC_SETTINGS)
    public DcSettings getSettings() {
        List<DcSettingsEntity> all = dcSettingsRepository.findAll();
        isTrue(all.size() == 1, "Did not find exactly one settings entity");
        DcSettingsEntity entity = all.get(0);
        return JsonUtils.readValue(entity.getSettingsJson(), DcSettings.class);
    }

    @Override
    @CacheEvict(DC_SETTINGS)
    public void saveSettings(DcSettings settings, boolean overwrite) {
        List<DcSettingsEntity> all = dcSettingsRepository.findAll();
        if (all.isEmpty()) {
            log.info("Adding DC settings");
            DcSettingsEntity entity = new DcSettingsEntity();
            entity.setSettingsJson(JsonUtils.writeValueAsString(settings));
            dcSettingsRepository.saveAndFlush(entity);
        } else if (!overwrite) {
            log.info("DC settings already set, not overwriting");
        } else {
            log.info("Saving DC settings");
            isTrue(all.size() == 1, "Did not find exactly one settings entity");
            DcSettingsEntity entity = all.get(0);
            entity.setSettingsJson(JsonUtils.writeValueAsString(settings));
        }
    }
}
