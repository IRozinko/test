package fintech.filestorage.gcs

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.RandomStringUtils

import java.nio.charset.StandardCharsets

class ManualTestOfGcs {

    public static void main(String[] args) {
        PrivateKeyCredentialsProvider credentialsProvider = new PrivateKeyCredentialsProvider()
        GoogleFileStorageProvider provider = new GoogleFileStorageProvider(
                projectId: "enter",
                bucketName: "enter",
                credentialsProvider: credentialsProvider
        )

        def key = RandomStringUtils.randomAlphabetic(10)
        provider.store(key, new ByteArrayInputStream([65, 65, 65] as byte[]))
        def is = provider.getContent(key)
        println IOUtils.toString(is, StandardCharsets.UTF_8)
    }
}
