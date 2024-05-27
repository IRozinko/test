package fintech.spain.asnef.db;

import com.google.common.collect.Lists;
import fintech.db.BaseEntity;
import fintech.spain.asnef.Log;
import fintech.spain.asnef.LogStatus;
import fintech.spain.asnef.LogType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "log", schema = Entities.SCHEMA)
public class LogEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogStatus status;

    @Column(nullable = false)
    private LocalDate preparedAt;

    private LocalDate exportedAt;

    private LocalDate responseReceivedAt;

    @Column(nullable = false)
    private Long outgoingFileId;

    private Long incomingFileId;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL)
    private List<LogRowEntity> logRows = Lists.newArrayList();

    public Log toValueObject() {
        Log log = new Log();
        log.setId(id);
        log.setType(type);
        log.setStatus(status);
        log.setPreparedAt(preparedAt);
        log.setExportedAt(exportedAt);
        log.setResponseReceivedAt(responseReceivedAt);
        log.setOutgoingFileId(outgoingFileId);
        log.setIncomingFileId(incomingFileId);
        log.setLogRows(logRows.stream().map(LogRowEntity::toValueObject).collect(Collectors.toList()));

        return log;
    }
}
