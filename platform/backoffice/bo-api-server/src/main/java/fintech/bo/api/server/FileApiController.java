package fintech.bo.api.server;

import com.google.common.base.Throwables;
import fintech.bo.api.model.DownloadCloudFileRequest;
import fintech.bo.api.model.IdResponse;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RestController
public class FileApiController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(path = "api/bo/files/download")
    void downloadFile(@RequestBody DownloadCloudFileRequest request, HttpServletResponse response) throws IOException {
        final CloudFile cloudFile = fileStorageService.get(request.getFileId()).orElseThrow(() -> new IllegalArgumentException("File not found"));

        writeContentsToOutputStream(cloudFile, response);
    }

    @SneakyThrows
    private void writeContentsToOutputStream(CloudFile cloudFile, HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {
            fileStorageService.readContents(cloudFile.getFileId(), (is) -> {
                try {
                    IOUtils.copy(is, outStream);

                    String fileName = cloudFile.getOriginalFileName();
                    response.setContentType(cloudFile.getContentType());
                    response.setContentLength(cloudFile.getFileSize().intValue());
                    response.setHeader("x-filename", fileName);
                    response.setHeader("content-disposition", "attachment; filename=\"" + fileName + "\"");
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            });
        } catch (Exception e) {
            log.error("Error while serving file", e);
        }
    }

    @PostMapping(path = "/api/bo/files/upload")
    public IdResponse uploadFile(@RequestParam("file") MultipartFile multiPart, @RequestParam("directory") String directory) throws IOException {
        SaveFileCommand saveFileCommand = new SaveFileCommand();
        saveFileCommand.setInputStream(multiPart.getInputStream());
        saveFileCommand.setOriginalFileName(multiPart.getOriginalFilename());
        saveFileCommand.setContentType(multiPart.getContentType());
        saveFileCommand.setDirectory(directory);
        CloudFile file = fileStorageService.save(saveFileCommand);

        return new IdResponse(file.getFileId());
    }


}
