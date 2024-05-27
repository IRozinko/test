package fintech.filestorage.db;

import fintech.db.BaseEntity;
import fintech.filestorage.CloudFile;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true, of = {"fileSize", "fileUuid"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "cloud_file", schema = Entities.SCHEMA)
@OptimisticLocking(type = OptimisticLockType.NONE)
public class CloudFileEntity extends BaseEntity {

    @Column(nullable = false)
    private String fileUuid;

    @Column(nullable = false)
    private String directory;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long timesDownloaded = 0;

    private LocalDateTime lastDownloadedAt;

    public CloudFile toValueObject() {
        CloudFile cf = new CloudFile();
        cf.setFileId(this.getId());
        cf.setFileSize(this.getFileSize());
        cf.setOriginalFileName(this.getOriginalFileName());
        cf.setContentType(this.getContentType());
        cf.setTimesDownloaded(this.getTimesDownloaded());
        cf.setLastDownloadedAt(this.getLastDownloadedAt());
        return cf;
    }
}
