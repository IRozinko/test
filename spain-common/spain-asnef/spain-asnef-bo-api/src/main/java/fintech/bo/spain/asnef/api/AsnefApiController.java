//package fintech.bo.spain.asnef.api;
//
//import com.google.common.collect.ImmutableMap;
//import fintech.TimeMachine;
//import fintech.bo.api.model.permissions.BackofficePermissions;
//import fintech.bo.spain.asnef.api.model.ExportAsnefFileRequest;
//import fintech.bo.spain.asnef.api.model.GenerateAsnefFileRequest;
//import fintech.bo.spain.asnef.api.model.ImportAsnefFileRequest;
//import fintech.spain.asnef.AsnefService;
//import fintech.spain.asnef.FileAsnefFotoaltasGenerateConsumer;
//import fintech.spain.asnef.FileAsnefRpGenerateConsumer;
//import fintech.spain.asnef.LogType;
//import fintech.spain.asnef.commands.ExportFileCommand;
//import fintech.spain.asnef.commands.ImportFileCommand;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//import java.util.function.Consumer;
//
//@RestController
//public class AsnefApiController {
//
//    private final AsnefService asnefService;
//
//    private final Map<LogType, Consumer<GenerateAsnefFileRequest>> generateHandlers;
//    private final Map<LogType, Consumer<ImportFileCommand>> importHandlers;
//
//    @Autowired
//    public AsnefApiController(FileAsnefRpGenerateConsumer fileAsnefRpGenerateConsumer,
//                              FileAsnefFotoaltasGenerateConsumer fileAsnefFotoaltasGenerateConsumer,
//                              AsnefService asnefService) {
//        this.asnefService = asnefService;
//
//        importHandlers = ImmutableMap.of(
//            LogType.NOTIFICA_RP, asnefService::importRpFile,
//            LogType.FOTOALTAS, asnefService::importFotoaltasFile
//        );
//
//        generateHandlers = ImmutableMap.of(
//            LogType.NOTIFICA_RP, request -> fileAsnefRpGenerateConsumer.consume(request.getBatchDate(), request.getLimit()),
//            LogType.FOTOALTAS, request -> fileAsnefFotoaltasGenerateConsumer.consume(request.getBatchDate(), request.getLimit())
//        );
//    }
//
//    @Secured(BackofficePermissions.ADMIN)
//    @PostMapping("api/bo/asnef/generate")
//    public void generateAsnefFile(@RequestBody GenerateAsnefFileRequest request) {
//        generateHandlers.getOrDefault(LogType.valueOf(request.getType()), r -> {
//            throw new IllegalStateException("Unknown file type: " + request.getType());
//        }).accept(request);
//    }
//
//    @Secured(BackofficePermissions.ADMIN)
//    @PostMapping("api/bo/asnef/export")
//    public void exportAsnefFile(@RequestBody ExportAsnefFileRequest request) {
//        ExportFileCommand command = new ExportFileCommand();
//        command.setLogId(request.getLogId());
//        command.setExportedAt(TimeMachine.today());
//
//        asnefService.exportFile(command);
//    }
//
//    @Secured(BackofficePermissions.ADMIN)
//    @PostMapping("api/bo/asnef/import")
//    public void importAsnefFile(@RequestBody ImportAsnefFileRequest request) {
//        ImportFileCommand command = new ImportFileCommand();
//        command.setFileId(request.getFileId());
//        command.setResponseReceivedAt(TimeMachine.today());
//
//        importHandlers.getOrDefault(LogType.valueOf(request.getType()), r -> {
//            throw new IllegalStateException("Unknown file type: " + request.getType());
//        }).accept(command);
//    }
//
//    @Secured(BackofficePermissions.ADMIN)
//    @DeleteMapping("api/bo/asnef/{logId}")
//    public void exportAsnefFile(@PathVariable("logId") Long logId) {
//        asnefService.deleteFile(logId);
//    }
//}
