package fintech.spain.alfa.product.asnef.internal;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.spain.asnef.AsnefConstants;
import fintech.spain.asnef.AsnefFtpGateway;
import fintech.spain.asnef.AsnefFtpProperties;
import fintech.spain.asnef.AsnefService;
import fintech.spain.asnef.Log;
import fintech.spain.asnef.LogRowStatus;
import fintech.spain.asnef.LogStatus;
import fintech.spain.asnef.LogType;
import fintech.spain.asnef.ReportingEntityProvider;
import fintech.spain.asnef.commands.ExportFileCommand;
import fintech.spain.asnef.commands.GenerateFotoaltasFileCommand;
import fintech.spain.asnef.commands.GenerateRpFileCommand;
import fintech.spain.asnef.commands.ImportFileCommand;
import fintech.spain.asnef.commands.OutputRecordHolder;
import fintech.spain.asnef.db.LogEntity;
import fintech.spain.asnef.db.LogRepository;
import fintech.spain.asnef.db.LogRowEntity;
import fintech.spain.asnef.db.LogRowRepository;
import fintech.spain.asnef.models.FotoaltasOutputControlRecord;
import fintech.spain.asnef.models.FotoaltasOutputHeaderRecord;
import fintech.spain.asnef.models.FotoaltasOutputRecord;
import fintech.spain.asnef.models.RpOutputControlRecord;
import fintech.spain.asnef.models.RpOutputHeaderRecord;
import fintech.spain.asnef.models.RpOutputRecord;
import org.apache.commons.io.IOUtils;
import org.beanio.Marshaller;
import org.beanio.StreamFactory;
import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static fintech.spain.asnef.db.Entities.logRow;

@Component
@Transactional
class AsnefServiceBean implements AsnefService {

    @Autowired
    private ReportingEntityProvider reportingEntityProvider;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogRowRepository logRowRepository;

    @Autowired
    private AsnefFtpProperties asnefFtpProperties;

    @Resource(name = "${asnef.ftp.gateway:" + MockAsnefFtpGateway.NAME + "}")
    private AsnefFtpGateway asnefFtpGateway;

    @Autowired
    private AsnefRpImport asnefRpImport;

    @Autowired
    private AsnefFotoaltasImport asnefFotoaltasImport;

    @Override
    public Log get(Long logId) {
        return logRepository.getRequired(logId).toValueObject();
    }

    @Override
    public void makeExhausted(Long loanId) {
        List<LogRowEntity> entities = logRowRepository.findAll(logRow.log.type.eq(LogType.NOTIFICA_RP).and(logRow.loanId.eq(loanId)));
        entities.stream()
            .filter(logRowEntity -> logRowEntity.getStatus() != LogRowStatus.EXHAUSTED)
            .peek(logRowEntity -> logRowEntity.setStatus(LogRowStatus.EXHAUSTED))
            .forEach(logRowRepository::save);
    }

    @Override
    public Long generateRpFile(GenerateRpFileCommand command) {
        Marshaller marshaller = getRpStreamFactory().createMarshaller("asnef");

        LogEntity log = new LogEntity();
        log.setType(LogType.NOTIFICA_RP);
        log.setStatus(LogStatus.PREPARED);
        log.setPreparedAt(command.getPreparedAt());

        List<LogRowEntity> logRows = getLogRows(command.getOutputRecordHolders(), marshaller, log);

        List<String> lines = Lists.newArrayList();

        lines.add(marshaller.marshal(command.getHeaderRecord()).toString());
        lines.addAll(logRows.stream().map(LogRowEntity::getOutgoingRow).collect(Collectors.toList()));
        lines.add(marshaller.marshal(command.getControlRecord()).toString());

        log.setOutgoingFileId(saveFile(AsnefConstants.Rp.getFilenameTxt(reportingEntityProvider.getRpNotificaReportingEntity()), lines));
        log.setLogRows(logRows);

        return logRepository.save(log).getId();
    }

    @Override
    public Long generateFotoaltasFile(GenerateFotoaltasFileCommand command) {
        Marshaller marshaller = getFotoaltasStreamFactory().createMarshaller("asnef");

        LogEntity log = new LogEntity();
        log.setType(LogType.FOTOALTAS);
        log.setStatus(LogStatus.PREPARED);
        log.setPreparedAt(command.getPreparedAt());

        List<LogRowEntity> logRows = getLogRows(command.getOutputRecordHolders(), marshaller, log);

        List<String> lines = Lists.newArrayList();

        lines.add(marshaller.marshal(command.getHeaderRecord()).toString());
        lines.addAll(logRows.stream().map(LogRowEntity::getOutgoingRow).collect(Collectors.toList()));
        lines.add(marshaller.marshal(command.getControlRecord()).toString());

        log.setOutgoingFileId(saveFile(AsnefConstants.Fotoaltas.getFilenameTxt(reportingEntityProvider.getFotoaltasReportingEntity()), lines));
        log.setLogRows(logRows);

        return logRepository.save(log).getId();
    }

    private <T> List<LogRowEntity> getLogRows(List<OutputRecordHolder<T>> outputRecordHolders, Marshaller marshaller, LogEntity log) {
        return outputRecordHolders.stream().map(outputRecordHolder -> {
            LogRowEntity logRow = new LogRowEntity();
            logRow.setStatus(LogRowStatus.PREPARED);
            logRow.setClientId(outputRecordHolder.getClientId());
            logRow.setLoanId(outputRecordHolder.getLoanId());
            logRow.setOperationIdentifier(outputRecordHolder.getOperationIdentifier());
            logRow.setNumber(outputRecordHolder.getNumber());
            logRow.setOutgoingRow(marshaller.marshal(outputRecordHolder.getOutputRecord()).toString());
            logRow.setLog(log);

            return logRow;
        }).collect(Collectors.toList());
    }

    private Long saveFile(String filename, List<String> lines) {
        SaveFileCommand command = new SaveFileCommand();
        command.setOriginalFileName(filename);
        command.setDirectory("asnef");
        command.setInputStream(IOUtils.toInputStream(Joiner.on(IOUtils.LINE_SEPARATOR_WINDOWS).join(lines), StandardCharsets.US_ASCII));
        command.setContentType("text/plain");

        return fileStorageService.save(command).getFileId();
    }

    @Override
    public void exportFile(ExportFileCommand command) {
        LogEntity log = logRepository.getRequired(command.getLogId());

        Preconditions.checkState(log.getStatus() == LogStatus.PREPARED, "Unable to export asnef log [id = %s, type = %s, status = %s]", log.getId(), log.getType(), log.getStatus());

        fileStorageService.readContents(log.getOutgoingFileId(), (Consumer<InputStream>) input -> asnefFtpGateway.upload(asnefFtpProperties.get(log.getType()), input));

        log.setStatus(LogStatus.EXPORTED);
        log.setExportedAt(command.getExportedAt());

        // EXHAUSTED status should not be rewritten
        log.getLogRows().stream()
            .filter(logRowEntity -> logRowEntity.getStatus() != LogRowStatus.EXHAUSTED)
            .forEach(logRow -> logRow.setStatus(LogRowStatus.EXPORTED));
    }

    @Override
    public void importRpFile(ImportFileCommand command) {
        asnefRpImport.importRpFile(command);
    }

    @Override
    public void importFotoaltasFile(ImportFileCommand command) {
        asnefFotoaltasImport.importFotoaltasFile(command);
    }

    @Override
    public void deleteFile(Long logId) {
        LogEntity log = logRepository.getRequired(logId);

        Preconditions.checkState(log.getType() == LogType.NOTIFICA_RP, "It's possible to delete only NOTIFICA files [id = %s, type = %s, status = %s]", log.getId(), log.getType(), log.getStatus());
        Preconditions.checkState(log.getStatus() == LogStatus.PREPARED, "Unable to delete asnef log [id = %s, type = %s, status = %s]", log.getId(), log.getType(), log.getStatus());
        log.setStatus(LogStatus.DELETED);

        // EXHAUSTED status should not be rewritten
        log.getLogRows().stream()
            .filter(logRowEntity -> logRowEntity.getStatus() != LogRowStatus.EXHAUSTED)
            .forEach(logRow -> logRow.setStatus(LogRowStatus.DELETED));
    }

    private StreamFactory getRpStreamFactory() {
        StreamFactory factory = StreamFactory.newInstance();
        factory.define(new StreamBuilder("asnef")
            .format("fixedlength")
            .parser(new FixedLengthParserBuilder())
            .addTypeHandler(BigDecimal.class, new RpBigDecimalTypeHandler())
            .addTypeHandler(LocalDate.class, new LocalDateTypeHandler())
            .addRecord(RpOutputHeaderRecord.class)
            .addRecord(RpOutputRecord.class)
            .addRecord(RpOutputControlRecord.class));

        return factory;
    }

    private StreamFactory getFotoaltasStreamFactory() {
        StreamFactory factory = StreamFactory.newInstance();
        factory.define(new StreamBuilder("asnef")
            .format("fixedlength")
            .parser(new FixedLengthParserBuilder())
            .addTypeHandler(BigDecimal.class, new FotoaltasBigDecimalTypeHandler())
            .addTypeHandler(LocalDate.class, new LocalDateTypeHandler())
            .addRecord(FotoaltasOutputHeaderRecord.class)
            .addRecord(FotoaltasOutputRecord.class)
            .addRecord(FotoaltasOutputControlRecord.class));

        return factory;
    }
}
