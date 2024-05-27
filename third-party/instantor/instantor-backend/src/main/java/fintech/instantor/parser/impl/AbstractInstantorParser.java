package fintech.instantor.parser.impl;

import fintech.instantor.parser.InstantorParser;
import fintech.instantor.db.InstantorResponseEntity;
import fintech.instantor.model.InstantorProcessingStatus;
import fintech.instantor.model.InstantorResponseStatus;
import fintech.instantor.model.SaveInstantorResponseCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractInstantorParser implements InstantorParser {

    @Override
    public InstantorResponseEntity parseResponse(SaveInstantorResponseCommand command) {
        log.info("Parsing instantor request: [{}]", command);
        InstantorResponseEntity entity = new InstantorResponseEntity();
        entity.setStatus(command.getStatus());
        entity.setPayloadJson(command.getPayloadJson());
        entity.setError(command.getError());
        entity.setParamMessageId(command.getParamMessageId());
        entity.setParamSource(command.getParamSource());
        entity.setParamTimestamp(command.getParamTimestamp());
        entity.setClientId(command.getClientId());
        entity.setProcessingStatus(InstantorProcessingStatus.PENDING);
        if (command.getStatus() == InstantorResponseStatus.OK) {
            parseJsonInstantorData(entity, command);
        }
        return entity;
    }

    protected abstract void parseJsonInstantorData(InstantorResponseEntity entity, SaveInstantorResponseCommand command);

}
