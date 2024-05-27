package fintech.bo.api.server;


import fintech.Validate;
import fintech.bo.api.model.attachement.SaveAttachmentRequest;
import fintech.bo.api.model.attachement.UpdateStatusRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AttachmentApiController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_EDIT})
    @PostMapping("/api/bo/attachments/save")
    void saveAttachment(@RequestBody SaveAttachmentRequest request) {
        CloudFile file = fileStorageService.get(request.getFileId()).orElseThrow(() -> new IllegalArgumentException("File not found"));
        AddAttachmentCommand addAttachmentCommand = new AddAttachmentCommand();
        addAttachmentCommand.setClientId(request.getClientId());
        addAttachmentCommand.setFileId(file.getFileId());
        addAttachmentCommand.setAttachmentType(request.getType());
        addAttachmentCommand.setName(file.getOriginalFileName());
        clientAttachmentService.addAttachment(addAttachmentCommand);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_EDIT})
    @PostMapping("/api/bo/attachments/status")
    void updateStatuses(@RequestBody List<UpdateStatusRequest> requests) {
        Validate.isTrue(requests.size() <= 100);
        requests.forEach((request) -> clientAttachmentService.updateStatus(request.getAttachmentId(), request.getStatus(), request.getStatusDetail()));
    }
}
