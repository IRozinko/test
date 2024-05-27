package fintech.spain.alfa.web.controllers.web;

import com.google.common.base.Throwables;
import fintech.FileHashId;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.spain.alfa.web.config.security.WebApiUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RestController
class FileApi {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping(path = "/api/web/files/{fileHashId}")
    @ResponseBody
    public void readDocument(@AuthenticationPrincipal WebApiUser user,
                             @PathVariable("fileHashId") String fileHashId,
                             HttpServletResponse response) throws IOException {

        Long fileId = FileHashId.decodeFileId(user.getClientId(), fileHashId).orElseThrow(() -> new AccessDeniedException("Wrong file hash id"));
        CloudFile cloudFile = fileStorageService.get(fileId).orElseThrow(RuntimeException::new);

        try (OutputStream outStream = response.getOutputStream()) {
            fileStorageService.readContents(cloudFile.getFileId(), (is) -> {
                try {
                    IOUtils.copy(is, outStream);

                    response.setContentType(cloudFile.getContentType());
                    response.setContentLength(cloudFile.getFileSize().intValue());
                    response.setHeader("x-filename", cloudFile.getOriginalFileName());
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            });
        } catch (Exception e) {
            Throwables.propagate(e);
        }
    }

}
