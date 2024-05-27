package fintech.crm.client;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class UpdateClientCommand {

    private Long clientId;
    private String firstName;
    private String secondFirstName;
    private String lastName;
    private String secondLastName;
    private String maidenName;
    private Gender gender;
    private String title;
    private LocalDate dateOfBirth;
    private boolean acceptTerms;
    private boolean acceptMarketing;
    private boolean acceptVerification;
    private boolean acceptPrivacyPolicy;
    private boolean blockCommunication;
    private boolean excludedFromASNEF;
    private boolean transferredToLoc;
    private Map<String, String> attributes = new HashMap<>();

    public static UpdateClientCommand fromClient(Client client) {
        UpdateClientCommand command = new UpdateClientCommand();
        command.setClientId(client.getId());
        command.setFirstName(client.getFirstName());
        command.setSecondFirstName(client.getSecondFirstName());
        command.setLastName(client.getLastName());
        command.setSecondLastName(client.getSecondLastName());
        command.setMaidenName(client.getMaidenName());
        command.setGender(client.getGender());
        command.setTitle(client.getTitle());
        command.setDateOfBirth(client.getDateOfBirth());
        command.setAcceptTerms(client.isAcceptTerms());
        command.setAcceptMarketing(client.isAcceptMarketing());
        command.setAcceptVerification(client.isAcceptVerification());
        command.setAcceptPrivacyPolicy(client.isAcceptPrivacyPolicy());
        command.setBlockCommunication(client.isBlockCommunication());
        command.setExcludedFromASNEF(client.isExcludedFromASNEF());
        command.setAttributes(new HashMap<>(client.getAttributes()));
        return command;
    }
}
