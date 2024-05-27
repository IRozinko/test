package fintech.ekomi.impl;

import com.google.common.base.Stopwatch;
import fintech.ekomi.EKomiService;
import fintech.ekomi.api.EKomiApiClient;
import fintech.ekomi.api.json.Snapshot;
import fintech.ekomi.api.json.SnapshotInfo;
import fintech.ekomi.config.EKomiConfig;
import fintech.ekomi.exception.EKomiException;
import fintech.ekomi.model.EKomiRating;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EKomiServiceBean implements EKomiService {

    private final EKomiApiClient eKomiApiClient;

    public EKomiServiceBean(EKomiApiClient eKomiApiClient) {
        this.eKomiApiClient = eKomiApiClient;
    }

    @Override
    @Cacheable(EKomiConfig.EKOMI_SNAPSHOT_CACHE_NAME)
    public Optional<EKomiRating> getCompanyRating() throws EKomiException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            return doGetCompanyRating();
        } catch (Exception e) {
            log.error("Error getting rating snapshot", e);
            throw new EKomiException(e);
        } finally {
            log.info("Completed EKomi request in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private Optional<EKomiRating> doGetCompanyRating() throws IOException {
        Response<Snapshot> snapshot = eKomiApiClient.getSnapshot().execute();
        if (snapshot.isSuccessful()) {
            return Optional.of(eKomiRating(snapshot.body().getInfo()));
        } else {
            return Optional.empty();
        }
    }

    private EKomiRating eKomiRating(SnapshotInfo snapshotInfo) {
        return new EKomiRating()
            .setAverage(getAverage(snapshotInfo))
            .setCount(snapshotInfo.getCount());
    }

    private BigDecimal getAverage(SnapshotInfo snapshotInfo) {
        if (snapshotInfo.getAverage() != null) {
            return snapshotInfo.getAverage().multiply(BigDecimal.valueOf(2));
        } else {
            return null;
        }
    }


}
