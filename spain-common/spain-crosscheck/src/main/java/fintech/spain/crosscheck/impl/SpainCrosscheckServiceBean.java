package fintech.spain.crosscheck.impl;

import com.google.common.base.Throwables;
import fintech.Validate;
import fintech.spain.crosscheck.SpainCrosscheckService;
import fintech.spain.crosscheck.db.SpainCrosscheckLogEntity;
import fintech.spain.crosscheck.db.SpainCrosscheckLogRepository;
import fintech.spain.crosscheck.model.SpainCrosscheckInput;
import fintech.spain.crosscheck.model.SpainCrosscheckRequestCommand;
import fintech.spain.crosscheck.model.SpainCrosscheckResult;
import fintech.spain.crosscheck.model.SpainCrosscheckStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Transactional
@Component
public class SpainCrosscheckServiceBean implements SpainCrosscheckService {

    @Autowired
    private SpainCrosscheckLogRepository logRepository;

    @Resource(name = "${spain.crosscheck.provider:" + MockSpainCrosscheckProvider.NAME + "}")
    private SpainCrosscheckProvider provider;

    @Override
    public SpainCrosscheckResult requestCrossCheck(SpainCrosscheckRequestCommand command) {
        log.info("Requesting crosscheck: [{}]", command);
        Validate.notBlank(command.getDni(), "DNI is blank");
        SpainCrosscheckLogEntity entity = new SpainCrosscheckLogEntity();
        entity.setClientId(command.getClientId());
        entity.setLoanId(command.getLoanId());
        entity.setApplicationId(command.getApplicationId());
        entity.setDni(command.getDni());
        try {
            SpainCrosscheckInput input = new SpainCrosscheckInput()
                .setDni(command.getDni())
                .setEmail(command.getEmail())
                .setPhone(command.getPhone());

            SpainCrosscheckResponse response = provider.request(input);

            if (response.isError()) {
                entity.setStatus(SpainCrosscheckStatus.ERROR);
            } else {
                SpainCrosscheckResponse.Attributes attributes = response.getAttributes();
                entity.setStatus(attributes.isFound() ? SpainCrosscheckStatus.FOUND : SpainCrosscheckStatus.NOT_FOUND);
                entity.setMaxDpd((long) attributes.getMaxDpd());
                entity.setOpenLoans((long) attributes.getOpenLoans());
                entity.setBlacklisted(attributes.isBlacklisted());
                entity.setRepeatedClient(attributes.isRepeatedClient());
                entity.setActiveRequest(attributes.isActiveRequest());
                entity.setActiveRequestStatus(attributes.getActiveRequestStatus());
            }
            entity.setResponseBody(response.getResponseBody());
            entity.setError(response.getErrorMessage());
            entity.setResponseStatusCode(response.getResponseStatusCode());

        } catch (Exception e) {
            log.error("Crosscheck request failed", e);
            entity.setStatus(SpainCrosscheckStatus.ERROR);
            entity.setResponseStatusCode(-1);
            entity.setError(Throwables.getRootCause(e).getMessage());
        }
        entity = logRepository.saveAndFlush(entity);
        SpainCrosscheckResult result = entity.toValueObject();
        log.info("Returning crosscheck result: [{}]", result);
        return result;
    }

    @Override
    public SpainCrosscheckResult get(Long id) {
        return logRepository.getRequired(id).toValueObject();
    }
}
