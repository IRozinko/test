package fintech.filestorage.gcs;

import com.google.api.client.auth.oauth2.Credential;

interface CredentialsProvider {
    Credential get();
}
