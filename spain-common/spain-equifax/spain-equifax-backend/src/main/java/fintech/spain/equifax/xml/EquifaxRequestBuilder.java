package fintech.spain.equifax.xml;

import com.equifax.xml.xmlschema.interconnect.*;
import fintech.spain.equifax.model.DocumentType;

import java.time.LocalDateTime;

import static com.equifax.xml.xmlschema.interconnect.SubjectIdentifierType.PRIMARY;
import static fintech.spain.equifax.xml.EquifaxUtils.toXmlGregorianCalendar;

public class EquifaxRequestBuilder {

    public static final String SCHEMA_VERSION = "2.0";
    public static final String REQUEST_MESSAGE_ID = "1014";

    private final RequestInput input;

    public EquifaxRequestBuilder(RequestInput input) {
        this.input = input;
    }

    public InterConnectRequestType build() {
        InterConnectRequestType interConnectRequest = new InterConnectRequestType();
        interConnectRequest.setSchemaVersion(SCHEMA_VERSION);
        interConnectRequest.setInteractionControl(interactionControl());
        interConnectRequest.setConsumerSubjects(consumerSubjects());
        return interConnectRequest;
    }

    private InterConnectRequestType.ConsumerSubjects consumerSubjects() {
        ConsumerSubjectRequestType subject = new ConsumerSubjectRequestType();

        PersonIdentificationType personIdentification = new PersonIdentificationType();
        personIdentification.setSpainDocument(input.getDocumentNumber());
        personIdentification.setSpainDocumentType(DocumentType.getTypeOfDocumentNumber(input.getDocumentNumber()).name());

        subject.getIdentification().add(personIdentification);
        subject.setSubjectIdentifier(PRIMARY);

        InterConnectRequestType.ConsumerSubjects subjects = new InterConnectRequestType.ConsumerSubjects();
        subjects.getConsumerSubject().add(subject);
        return subjects;
    }


    private InteractionControlRequestType interactionControl() {
        InteractionControlRequestType interactionControl = new InteractionControlRequestType();
        interactionControl.setMessageId(REQUEST_MESSAGE_ID);
        interactionControl.setTimestamp(toXmlGregorianCalendar(LocalDateTime.now().toString()));

        AuthenticationType authenticationType = new AuthenticationType();
        authenticationType.setUserId(input.getUserId());
        authenticationType.setPassword(input.getPassword());
        interactionControl.setAuthentication(authenticationType);

        OrganizationType organization = new OrganizationType();
        organization.setCode(input.getOrganizationCode());
        interactionControl.setOrganization(organization);

        OrchestrationType orchestration = new OrchestrationType();
        orchestration.setCode(input.getOrchestrationCode());
        interactionControl.setOrchestration(orchestration);
        return interactionControl;
    }
}
