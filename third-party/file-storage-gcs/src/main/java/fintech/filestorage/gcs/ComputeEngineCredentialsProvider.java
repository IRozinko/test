package fintech.filestorage.gcs;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.base.Throwables;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Credential provider to be used when application is deployed to Compute Engine VMs
 */
@Component(ComputeEngineCredentialsProvider.NAME)
class ComputeEngineCredentialsProvider implements CredentialsProvider {

    public static final String NAME = "gcs-compute-engine-credentials-provider";

    @Override
    public Credential get() {
        try {
            return GoogleCredential.getApplicationDefault();
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return null;
    }
}
