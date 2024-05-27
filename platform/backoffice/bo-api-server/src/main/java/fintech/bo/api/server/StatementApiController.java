package fintech.bo.api.server;

import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.statements.ImportStatementRequest;
import fintech.payments.StatementService;
import fintech.payments.commands.StatementImportCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class StatementApiController {

    @Autowired
    private StatementService statementService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.STATEMENT_IMPORT})
    @PostMapping(path = "/api/bo/statements/add")
    IdResponse importStatement(@Valid @RequestBody ImportStatementRequest request) {
        StatementImportCommand command = new StatementImportCommand();
        command.setInstitutionId(request.getInstitutionId());
        command.setFileId(request.getFileId());
        Long id = statementService.importStatement(command);
        return new IdResponse(id);
    }

}
