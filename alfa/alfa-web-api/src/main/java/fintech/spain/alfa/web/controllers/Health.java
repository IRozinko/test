package fintech.spain.alfa.web.controllers;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class Health {

    private final HealthEndpoint healthEndpoint;

    @Autowired
    Health(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Status status = healthEndpoint.invoke().getStatus();
        ResponseEntity.BodyBuilder responseEntity;
        if (status.equals(Status.OUT_OF_SERVICE)) {
            responseEntity = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE);
        } else {
            responseEntity = ResponseEntity.status(HttpStatus.OK);
        }
        return responseEntity.body(ImmutableMap.of("status", status.getCode()));
    }
}
