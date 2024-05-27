package fintech.filestorage

import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired

import java.util.function.Consumer

class FileStorageTest extends BaseSpecification {

    @Autowired
    FileStorageService service

    def "Store and read"() {
        when:
        def file = service.save(new SaveFileCommand(
                originalFileName: "test.pdf",
                directory: "test",
                contentType: "application/pdf",
                inputStream: new ByteArrayInputStream([1, 2, 3] as byte[])
        ))

        then:
        assert file.fileId > 0

        when:
        file = service.get(file.fileId).get()

        then:
        file.fileSize == 3L
        file.originalFileName == "test.pdf"

        when:
        def bytes = [] as byte[]
        service.readContents(file.fileId, new Consumer<InputStream>() {
            @Override
            void accept(InputStream inputStream) {
                bytes = IOUtils.toByteArray(inputStream)
            }
        })
        file = service.get(file.fileId).get()

        then:
        bytes == [1, 2, 3] as byte[]

        and:
        file.timesDownloaded == 1L
        file.lastDownloadedAt != null
    }
}
