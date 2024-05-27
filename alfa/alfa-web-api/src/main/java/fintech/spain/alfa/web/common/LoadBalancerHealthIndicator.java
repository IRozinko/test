package fintech.spain.alfa.web.common;

import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class LoadBalancerHealthIndicator implements HealthIndicator {

    private volatile boolean offline;

    @Override
    public Health health() {
        if (offline) {
            return Health.down().outOfService().build();
        }
        return Health.up().build();
    }

    @Component
    public class LoadBalancerPauseEndpoint implements Endpoint<Void> {

        @Override
        public String getId() {
            return "pause";
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean isSensitive() {
            return true;
        }

        @Override
        public Void invoke() {
            offline = true;
            return null;
        }
    }

    @Component
    public class LoadBalancerUnPauseEndpoint implements Endpoint<Void> {

        @Override
        public String getId() {
            return "unpause";
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean isSensitive() {
            return true;
        }

        @Override
        public Void invoke() {
            offline = false;
            return null;
        }
    }
}
