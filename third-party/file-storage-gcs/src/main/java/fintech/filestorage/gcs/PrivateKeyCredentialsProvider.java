package fintech.filestorage.gcs;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.storage.StorageScopes;
import com.google.common.base.Throwables;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Credentials provider to be used with service account key created at https://console.cloud.google.com/apis/credentials
 * This will allow to interact with GCS from any host.
 */
@Component(PrivateKeyCredentialsProvider.NAME)
class PrivateKeyCredentialsProvider implements CredentialsProvider {

    public static final String NAME = "gcs-private-key-credentials-provider";

    @Override
    public Credential get() {
        try {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/service-acc-pk.json");
            return GoogleCredential.fromStream(resourceAsStream).createScoped(StorageScopes.all());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}

