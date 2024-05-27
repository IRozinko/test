package fintech.bo.components.utils;

import com.google.common.base.Throwables;
import com.vaadin.server.StreamResource;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.DownloadCloudFileRequest;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class CloudFileResource extends StreamResource {

    private FileApiClient fileApiClient;
    
    public CloudFileResource(CloudFile cloudFile, FileApiClient fileApiClient, Consumer<CloudFile> callback) {
        
        
        super(new StreamSource() {
            @Override
            public InputStream getStream() {
                DownloadCloudFileRequest fileDownloadRequest = new DownloadCloudFileRequest();
                fileDownloadRequest.setFileId(cloudFile.getId());

                try {
                    Response<ResponseBody> fileResponse = fileApiClient.download(fileDownloadRequest).execute();
                    if (fileResponse.isSuccessful()) {
                        callback.accept(cloudFile);
                        return new ByteArrayInputStream(fileResponse.body().bytes());
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    Throwables.propagate(e);
                }

                return null;
            }
        }, cloudFile.getName());
        this.fileApiClient = fileApiClient;
    }
}
