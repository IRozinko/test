package fintech.spain.alfa.web.models.convertor;

import com.google.common.base.Converter;
import fintech.crm.client.Client;
import fintech.spain.alfa.web.models.PersonalDetailsResponse;

public class PersonalDetailsResponseConverter extends Converter<Client, PersonalDetailsResponse> {

    @Override
    protected PersonalDetailsResponse doForward(Client client) {
        PersonalDetailsResponse response = new PersonalDetailsResponse();
        response.setClientId(client.getId());
        response.setNumber(client.getNumber());
        response.setFirstName(client.getFirstName());
        response.setLastName(client.getLastName());
        response.setSecondFirstName(client.getSecondLastName());
        response.setPhone(client.getPhone());
        response.setEmail(client.getEmail());
        response.setDocumentNumber(client.getDocumentNumber());
        response.setAccountNumber(client.getAccountNumber());
        response.setGender(client.getGender());
        response.setDateOfBirth(client.getDateOfBirth());
        response.setAcceptTerms(client.isAcceptTerms());
        response.setAcceptVerification(client.isAcceptVerification());
        response.setAcceptMarketing(client.isAcceptMarketing());
        return response;
    }

    @Override
    protected Client doBackward(PersonalDetailsResponse personalDetailsResponse) {
        throw new UnsupportedOperationException();
    }
}
