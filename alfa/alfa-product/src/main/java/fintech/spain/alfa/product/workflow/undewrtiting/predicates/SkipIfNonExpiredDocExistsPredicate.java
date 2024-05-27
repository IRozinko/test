package fintech.spain.alfa.product.workflow.undewrtiting.predicates;

import fintech.TimeMachine;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.db.IdentificationDocumentEntity;
import fintech.spain.alfa.product.db.IdentificationDocumentRepository;
import fintech.workflow.spi.ActivityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class SkipIfNonExpiredDocExistsPredicate implements Predicate<ActivityContext> {

    @Autowired
    private IdentificationDocumentRepository identificationDocumentRepository;

    @Override
    public boolean test(ActivityContext context) {
        long clientId = context.getClientId();

        List<IdentificationDocumentEntity> idDocuments = identificationDocumentRepository.findAll(Entities.identificationDocument.clientId.eq(clientId));

        return idDocuments.stream().anyMatch(d -> d.getExpirationDate() != null && d.getExpirationDate().isAfter(TimeMachine.today()));
    }
}
