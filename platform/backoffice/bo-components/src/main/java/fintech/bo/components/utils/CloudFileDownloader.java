package fintech.bo.components.utils;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CloudFileDownloader extends FileDownloader {

    private FileApiClient fileApiClient;
    private Supplier<CloudFile> cloudFileIdSupplier;
    private Consumer<CloudFile> callback;

    public CloudFileDownloader(FileApiClient fileApiClient, Supplier<CloudFile> cloudFileIdSupplier, Consumer<CloudFile> callback) {
        super((new StreamResource(null, null)));
        this.fileApiClient = fileApiClient;
        this.cloudFileIdSupplier = cloudFileIdSupplier;
        this.callback = callback;
        ((StreamResource) getResource("dl")).setCacheTime(0);
    }

    @Override
    public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) throws IOException {

        VaadinSession.getCurrent().lock();
        try {
            if (cloudFileIdSupplier != null) {
                CloudFile cloudFile = cloudFileIdSupplier.get();
                if (cloudFile != null) {
                    setResource("dl", new CloudFileResource(cloudFile, fileApiClient, callback));
                    return super.handleConnectorRequest(request, response, path);
                }
            }
        } finally {
            VaadinSession.getCurrent().unlock();
        }
        return false;
    }
}
