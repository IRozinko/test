package fintech.filestorage.gcs;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import fintech.filestorage.spi.FileInfo;
import fintech.filestorage.spi.FileStorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(GoogleFileStorageProvider.NAME)
class GoogleFileStorageProvider implements FileStorageProvider {

    public static final String NAME = "gcs-file-storage-provider";

    @Value("${gcs.projectId:fake-project-id}")
    String projectId;

    @Value("${gcs.bucketName:fake-bucket-name}")
    String bucketName;

    @Resource(name = "${gcs.credentialsProvider:" + PrivateKeyCredentialsProvider.NAME + "}")
    CredentialsProvider credentialsProvider;

    @Override
    public FileInfo store(String fileKey, InputStream is) {
        Storage storage = getStorage();

        StorageObject storageObject = new StorageObject()
            .setBucket(bucketName)
            .setName(fileKey);

        return upload(storage, storageObject, is);
    }

    private Storage getStorage() {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            Credential credential = credentialsProvider.get();

            return new Storage.Builder(httpTransport, jsonFactory, credential).setApplicationName(projectId).build();
        } catch (GeneralSecurityException | IOException e) {
            Throwables.propagate(e);
        }
        return null;
    }

    @Override
    public InputStream getContent(String fileKey) {
        Storage storage = getStorage();
        return download(fileKey, storage);
    }

    private InputStream download(String fileKey, Storage storage) {
        try {
            Storage.Objects.Get getObject = storage.objects().get(bucketName, fileKey);
            return getObject.executeMediaAsInputStream();
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return null;
    }

    private FileInfo upload(Storage storage, StorageObject object,
                            InputStream data) {
        InputStreamContent mediaContent = new InputStreamContent(object.getContentType(), data);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Storage.Objects.Insert insertObject = storage.objects()
                .insert(object.getBucket(), object, mediaContent);
            StorageObject responseObject = insertObject.execute();

            return getFileInfo(responseObject);
        } catch (IOException e) {
            Throwables.propagate(e);
        } finally {
            log.info("Completed GoogleFile upload filekey {} in {} ms", object.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        return null;
    }

    private FileInfo getFileInfo(StorageObject storageObject) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setSize(storageObject.getSize().longValue());
        return fileInfo;
    }
}
