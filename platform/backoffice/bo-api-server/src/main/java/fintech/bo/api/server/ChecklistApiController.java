package fintech.bo.api.server;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.risk.checklist.*;
import fintech.filestorage.CloudFile;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.commands.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class ChecklistApiController {

    @Autowired
    private CheckListService checkListService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CHECKLIST_EDIT})
    @PostMapping("/api/bo/checklist/edit")
    public void updateChecklist(@Valid @RequestBody UpdateChecklistRequest request) {
        checkListService.updateEntry(UpdateCheckListEntryCommand.builder()
            .id(request.getId())
            .type(request.getType())
            .value1(request.getValue1())
            .comment(request.getComment())
            .build());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CHECKLIST_EDIT})
    @PostMapping("/api/bo/checklist/add")
    public void addChecklist(@Valid @RequestBody AddChecklistRequest request) {
        checkListService.addEntry(AddCheckListEntryCommand.builder()
            .type(request.getType())
            .value1(request.getValue1())
            .comment(request.getComment())
            .build());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CHECKLIST_EDIT})
    @PostMapping("/api/bo/checklist/export")
    public ExportChecklistResponse export(@RequestBody ExportChecklistRequest request) {
        CloudFile cloudFile = checkListService.exportChecklistEntries(new ExportCheckListEntryCommand(request.getType()));
        ExportChecklistResponse response = new ExportChecklistResponse();
        response.setFileId(cloudFile.getFileId());
        response.setFileName(cloudFile.getOriginalFileName());
        return response;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CHECKLIST_EDIT})
    @PostMapping("/api/bo/checklist/delete")
    public void delete(@RequestBody DeleteChecklistRequest request) {
        checkListService.delete(new DeleteCheckListEntryCommand(request.getId()));
    }


    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CHECKLIST_EDIT})
    @PostMapping("/api/bo/checklist/import")
    public void importChecklist(@RequestBody ImportChecklistRequest request) {
        checkListService.importChecklistEntries(ImportChecklistEntryCommand.builder()
            .type(request.getType())
            .fileId(request.getFileId())
            .override(request.isOverride())
            .build());
    }

}
