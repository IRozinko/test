package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.spain.alfa.product.documents.IdentificationDocumentsService;
import fintech.spain.alfa.product.documents.InvalidateIdentificationDocument;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.db.IdentificationDocumentEntity;
import fintech.spain.alfa.product.db.IdentificationDocumentRepository;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class InvalidateIdDocument implements ActivityListener {

    @Autowired
    private IdentificationDocumentsService identificationDocumentsService;

    @Autowired
    private IdentificationDocumentRepository identificationDocumentRepository;

    @Override
    public void handle(ActivityContext context) {
        long clientId = context.getClientId();

        IdentificationDocumentEntity idDocument = identificationDocumentRepository.findOne(Entities.identificationDocument.clientId.eq(clientId).and(Entities.identificationDocument.isValid.isTrue()));
        if (idDocument != null) {
            identificationDocumentsService.invalidateIdentificationDocument(new InvalidateIdentificationDocument().setIdentificationDocumentId(idDocument.getId()));
        } else {
            throw new IllegalStateException("No Valid ID document found");
        }

    }
}
