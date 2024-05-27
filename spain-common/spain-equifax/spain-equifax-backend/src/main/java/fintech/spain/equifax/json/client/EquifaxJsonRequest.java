package fintech.spain.equifax.json.client;

import fintech.spain.equifax.model.EquifaxRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EquifaxJsonRequest {

    private Applicants applicants;

    public EquifaxJsonRequest(String countyCode, String idType, String personType, EquifaxRequest request) {
        if (request.getFirstName() != null && request.getLastName() != null) {
            List<Name> names = new ArrayList<>();
            names.add(new Name(request.getFirstName(), request.getLastName(), request.getSecondLastName()));
            List<Address> addresses = new ArrayList<>();
            addresses.add(new Address(request.getPostalCode()));
            applicants = new Applicants(new PrimaryConsumer(
                new PersonalInformation(countyCode, idType, request.getDocumentNumber(), personType, addresses, names)));
        } else {
            applicants = new Applicants(new PrimaryConsumer(new PersonalInformation().setIdCountryCode(countyCode).setIdType(idType).setIdCode(request.getDocumentNumber())));
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class Applicants {
        private PrimaryConsumer primaryConsumer;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class PrimaryConsumer {
        private PersonalInformation personalInformation;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    private class PersonalInformation {

        private String idCountryCode;
        private String idType;
        private String idCode;
        private String personType;
        private List<Address> addresses;
        private List<Name> name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Name {
        private String firstName;
        private String lastName;
        private String secondLastName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Address {
        private String postalCode;
    }

}
