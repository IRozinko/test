package fintech.instantor;

import fintech.scoring.values.spi.ScoringValuesProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Properties;

import static fintech.instantor.model.InstantorResponseQuery.byClientIdAndResponseStatus;
import static fintech.instantor.model.InstantorResponseStatus.OK;

@Slf4j
@Component
@Transactional
public class InstantorScoringValuesProvider implements ScoringValuesProvider {

    public static final String INSTANTOR_RESPONSE_ID = "InstantorResponseId";

    @Autowired
    private InstantorService instantorService;

    @Override
    public Properties provide(long clientId) {
        return instantorService.findLatest(byClientIdAndResponseStatus(clientId, OK))
            .map(InstantorScoringValues::new)
            .map(v -> flattenPojo("instantor", v))
            .orElseGet(() -> {
                log.warn("There is no OK Instantor Response for client id: {}", clientId);
                return new Properties();
            });
    }
}
