package fintech.spain.alfa.product.asnef.internal;

import com.google.common.base.Preconditions;
import fintech.JsonUtils;
import fintech.filestorage.FileStorageService;
import fintech.spain.asnef.LogRowStatus;
import fintech.spain.asnef.LogStatus;
import fintech.spain.asnef.LogType;
import fintech.spain.asnef.commands.ImportFileCommand;
import fintech.spain.asnef.db.Entities;
import fintech.spain.asnef.db.LogEntity;
import fintech.spain.asnef.db.LogRepository;
import fintech.spain.asnef.models.FotoaltasInputControlRecord;
import fintech.spain.asnef.models.FotoaltasInputHeaderRecord;
import fintech.spain.asnef.models.FotoaltasInputRecord;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.beanio.StreamFactory;
import org.beanio.Unmarshaller;
import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
class AsnefFotoaltasImport {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LogRepository logRepository;

    void importFotoaltasFile(ImportFileCommand command) {
        Unmarshaller unmarshaller = streamFactory().createUnmarshaller("asnef");

        List<String> lines = lines(command.getFileId());

        FotoaltasInputHeaderRecord headerRecord = (FotoaltasInputHeaderRecord) unmarshaller.unmarshal(lines.get(0));

        LogEntity log = logRepository.findOne(Entities.log.type.eq(LogType.FOTOALTAS).and(Entities.log.preparedAt.eq(headerRecord.getUpdateFileHeaderRecord().getDateOfProcessing())));

        Preconditions.checkNotNull(log, "Unable to find FOTOALTAS log record for date: %s", headerRecord.getUpdateFileHeaderRecord().getDateOfProcessing());

        Map<String, FotoaltasInputRecord> inputRecords = IntStream.range(1, lines.size() - 1)
            .mapToObj(i -> (FotoaltasInputRecord) unmarshaller.unmarshal(lines.get(i)))
            .collect(Collectors.toMap(r -> r.getOriginalRecord().getOperationIdentifier(), r -> r));

        // EXHAUSTED status should not be rewritten
        log.getLogRows().stream()
            .filter(logRowEntity -> logRowEntity.getStatus() != LogRowStatus.EXHAUSTED)
            .forEach(logRow -> {
                Optional<FotoaltasInputRecord> maybeInputRecord = Optional.ofNullable(inputRecords.get(logRow.getOperationIdentifier()));

                logRow.setStatus(maybeInputRecord.map(r -> LogRowStatus.FAILED).orElse(LogRowStatus.SUCCEED));
                logRow.setIncomingRow(maybeInputRecord.map(JsonUtils::writeValueAsString).orElse(null));
            });

        log.setStatus(LogStatus.RESPONSE_RECEIVED);
        log.setResponseReceivedAt(command.getResponseReceivedAt());
        log.setIncomingFileId(command.getFileId());
    }

    private StreamFactory streamFactory() {
        StreamFactory factory = StreamFactory.newInstance();
        factory.define(new StreamBuilder("asnef")
            .format("fixedlength")
            .parser(new FixedLengthParserBuilder())
            .addTypeHandler(BigDecimal.class, new FotoaltasBigDecimalTypeHandler())
            .addTypeHandler(LocalDate.class, new LocalDateTypeHandler())
            .addRecord(FotoaltasInputHeaderRecord.class)
            .addRecord(FotoaltasInputRecord.class)
            .addRecord(FotoaltasInputControlRecord.class)
        );

        return factory;
    }

    private List<String> lines(Long fileId) {
        return fileStorageService.readContents(fileId, (Function<InputStream, List<String>>) this::lines);
    }

    @SneakyThrows
    private List<String> lines(InputStream input) {
        return IOUtils.readLines(input, StandardCharsets.US_ASCII);
    }
}
