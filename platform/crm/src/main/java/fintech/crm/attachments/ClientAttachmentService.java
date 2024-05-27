package fintech.crm.attachments;

import fintech.filestorage.CloudFile;
import lombok.Data;

import java.util.List;
import java.util.Optional;

public interface ClientAttachmentService {

    Long addAttachment(AddAttachmentCommand command);

    List<Attachment> findAttachments(AttachmentQuery query);

    Optional<Attachment> findLastAttachment(AttachmentQuery query);

    void updateStatus(Long attachmentId, String status, String statusDetail);

    void setLoanId(Long attachmentId, Long loanId);

    Attachment get(Long attachmentId);

    void autoApproveAttachments();

    CloudFile exportToZipArchive(List<Long> attachmentIds, String fileName);

    @Data
    class AttachmentQuery {
        private Long clientId;
        private Long applicationId;
        private Long loanId;
        private Long fileId;
        private String type;
        private String status;

        public static AttachmentQuery byClient(Long clientId) {
            return byClient(clientId, null);
        }

        public static AttachmentQuery byClient(Long clientId, String type) {
            AttachmentQuery query = new AttachmentQuery();
            query.setClientId(clientId);
            query.setType(type);
            return query;
        }

        public static AttachmentQuery byApplication(Long applicationId) {
            return byApplication(applicationId, null);
        }

        public static AttachmentQuery byApplication(Long applicationId, String type) {
            AttachmentQuery query = new AttachmentQuery();
            query.setApplicationId(applicationId);
            query.setType(type);
            return query;
        }

        public static AttachmentQuery byLoan(Long loanId) {
            return byLoan(loanId, null);
        }

        public static AttachmentQuery byLoan(Long loanId, String type) {
            AttachmentQuery query = new AttachmentQuery();
            query.setLoanId(loanId);
            query.setType(type);
            return query;
        }

        public static AttachmentQuery byType(String type, String status) {
            AttachmentQuery query = new AttachmentQuery();
            query.setType(type);
            query.setStatus(status);
            return query;
        }

        public static AttachmentQuery byFileId(Long fileId, String type) {
            AttachmentQuery query = new AttachmentQuery();
            query.setType(type);
            query.setFileId(fileId);
            return query;
        }
    }
}
