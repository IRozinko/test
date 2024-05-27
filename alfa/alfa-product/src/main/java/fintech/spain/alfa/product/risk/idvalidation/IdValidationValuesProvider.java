package fintech.spain.alfa.product.risk.idvalidation;

import fintech.ScoringProperties;
import fintech.crm.CrmConstants;
import fintech.crm.address.ClientAddress;
import fintech.crm.address.ClientAddressService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.country.Country;
import fintech.crm.documents.IdentityDocument;
import fintech.crm.documents.IdentityDocumentService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.db.IdentificationDocumentEntity;
import fintech.spain.alfa.product.db.IdentificationDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;

import static fintech.spain.alfa.product.db.Entities.identificationDocument;

@Slf4j
@Component
@Transactional
public class IdValidationValuesProvider {

    @Autowired
    private ClientService clientService;

    @Autowired
    private IdentityDocumentService identityDocumentService;

    @Autowired
    private ClientAddressService clientAddressService;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private IdentificationDocumentRepository identificationDocumentRepository;

    public Properties provide(Long clientId, Long applicationId, Long identificationDocumentId) {
        Client client = clientService.get(clientId);
        LoanApplication loanApplication = loanApplicationService.get(applicationId);
        Optional<IdentityDocument> primaryDocument = identityDocumentService.findPrimaryDocument(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI);
        Optional<ClientAddress> clientPrimaryAddress = clientAddressService.getClientPrimaryAddress(clientId, AlfaConstants.ADDRESS_TYPE_ACTUAL);
        Optional<IdentificationDocumentEntity> identificationDocument = findIdentificationDocument(clientId, identificationDocumentId);

        ScoringProperties properties = new ScoringProperties();
        properties.put("application_id", applicationId);
        properties.put("client_id", clientId);

        properties.put("document_type", primaryDocument.map(IdentityDocument::getType).orElse(null));
        properties.put("document_number", primaryDocument.map(IdentityDocument::getNumber).orElse(null));
        properties.put("nationality", primaryDocument.map(IdentityDocument::getNationality).map(Country::getNationality).orElse(null));

        properties.put("name", client.getFirstName());
        properties.put("surname_1", client.getLastName());
        properties.put("surname_2", client.getSecondLastName());
        properties.put("gender", Optional.ofNullable(client.getGender()).map(Enum::name).orElse(null));
        properties.put("year_of_birth", Optional.ofNullable(client.getDateOfBirth()).map(LocalDate::getYear).orElse(null));
        properties.put("month_of_birth", Optional.ofNullable(client.getDateOfBirth()).map(LocalDate::getMonthValue).orElse(null));
        properties.put("day_of_birth", Optional.ofNullable(client.getDateOfBirth()).map(LocalDate::getDayOfMonth).orElse(null));

        properties.put("street", clientPrimaryAddress.map(ClientAddress::getStreet).orElse(null));
        properties.put("house_number", clientPrimaryAddress.map(ClientAddress::getHouseNumber).orElse(null));
        properties.put("city", clientPrimaryAddress.map(ClientAddress::getCity).orElse(null));
        properties.put("province", clientPrimaryAddress.map(ClientAddress::getProvince).orElse(null));

        properties.put("identification_document_document_type", identificationDocument.map(IdentificationDocumentEntity::getDocumentType).orElse(null));
        properties.put("identification_document_document_number", identificationDocument.map(IdentificationDocumentEntity::getDocumentNumber).orElse(null));
        properties.put("identification_document_name", identificationDocument.map(IdentificationDocumentEntity::getName).orElse(null));
        properties.put("identification_document_surname_1", identificationDocument.map(IdentificationDocumentEntity::getSurname1).orElse(null));
        properties.put("identification_document_surname_2", identificationDocument.map(IdentificationDocumentEntity::getSurname2).orElse(null));
        properties.put("identification_document_gender", identificationDocument.map(IdentificationDocumentEntity::getGender).orElse(null));
        properties.put("identification_document_nationality", identificationDocument.map(IdentificationDocumentEntity::getNationality).orElse(null));

        properties.put("identification_document_year_of_birth", identificationDocument.map(IdentificationDocumentEntity::getDateOfBirth).map(LocalDate::getYear).orElse(null));
        properties.put("identification_document_month_of_birth", identificationDocument.map(IdentificationDocumentEntity::getDateOfBirth).map(LocalDate::getMonthValue).orElse(null));
        properties.put("identification_document_day_of_birth", identificationDocument.map(IdentificationDocumentEntity::getDateOfBirth).map(LocalDate::getDayOfMonth).orElse(null));

        properties.put("identification_document_year_of_id_expiration", identificationDocument.map(IdentificationDocumentEntity::getExpirationDate).map(LocalDate::getYear).orElse(null));
        properties.put("identification_document_month_of_id_expiration", identificationDocument.map(IdentificationDocumentEntity::getExpirationDate).map(LocalDate::getMonthValue).orElse(null));
        properties.put("identification_document_day_of_id_expiration", identificationDocument.map(IdentificationDocumentEntity::getExpirationDate).map(LocalDate::getDayOfMonth).orElse(null));

        properties.put("identification_document_street", identificationDocument.map(IdentificationDocumentEntity::getStreet).orElse(null));
        properties.put("identification_document_house", identificationDocument.map(IdentificationDocumentEntity::getHouse).orElse(null));
        properties.put("identification_document_city", identificationDocument.map(IdentificationDocumentEntity::getCity).orElse(null));
        properties.put("identification_document_province", identificationDocument.map(IdentificationDocumentEntity::getProvince).orElse(null));
        properties.put("identification_document_place_of_birth", identificationDocument.map(IdentificationDocumentEntity::getPlaceOfBirth).orElse(null));

        properties.put("identification_document_days_before_id_expiration", identificationDocument.map(IdentificationDocumentEntity::getExpirationDate)
            .map(exp -> ChronoUnit.DAYS.between(loanApplication.getSubmittedAt().toLocalDate(), exp))
            .orElse(null));

        return properties;
    }

    private Optional<IdentificationDocumentEntity> findIdentificationDocument(Long clientId, Long identificationDocumentId) {
        if (identificationDocumentId == null) {
            return Optional.empty();
        }
        Page<IdentificationDocumentEntity> page = identificationDocumentRepository.findAll(
            identificationDocument.clientId.eq(clientId)
                .and(identificationDocument.id.eq(identificationDocumentId)),
            new QPageRequest(0, 1, identificationDocument.id.desc()));
        return page.getContent().stream().findFirst();
    }
}
