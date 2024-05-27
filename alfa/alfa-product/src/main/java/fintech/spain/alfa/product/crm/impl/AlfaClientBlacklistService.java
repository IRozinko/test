package fintech.spain.alfa.product.crm.impl;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.commands.AddCheckListEntryCommand;
import fintech.risk.checklist.commands.DeleteCheckListEntryCommand;
import fintech.risk.checklist.model.CheckListQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_DNI;
import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_EMAIL;
import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_PHONE;

@AllArgsConstructor
@Transactional
@Service
public class AlfaClientBlacklistService {

    private final CheckListService checkListService;
    private final ClientService clientService;

    public void blacklistClient(Long clientId, String comment) {
        Client client = clientService.get(clientId);

        checkListService.addEntry(AddCheckListEntryCommand.builder().type(CHECKLIST_TYPE_EMAIL).comment(comment).value1(client.getEmail()).build());
        checkListService.addEntry(AddCheckListEntryCommand.builder().type(CHECKLIST_TYPE_PHONE).comment(comment).value1(client.getPhone()).build());
        checkListService.addEntry(AddCheckListEntryCommand.builder().type(CHECKLIST_TYPE_DNI).comment(comment).value1(client.getDocumentNumber()).build());
    }

    public void unblacklistClient(Long clientId) {
        Client client = clientService.get(clientId);

        checkListService.find(new CheckListQuery(CHECKLIST_TYPE_EMAIL, client.getEmail()))
            .forEach(entry -> checkListService.delete(new DeleteCheckListEntryCommand(entry.getId())));

        checkListService.find(new CheckListQuery(CHECKLIST_TYPE_PHONE, client.getPhone()))
            .forEach(entry -> checkListService.delete(new DeleteCheckListEntryCommand(entry.getId())));

        checkListService.find(new CheckListQuery(CHECKLIST_TYPE_DNI, client.getDocumentNumber()))
            .forEach(entry -> checkListService.delete(new DeleteCheckListEntryCommand(entry.getId())));
    }

}
