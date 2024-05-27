package fintech

import spock.lang.Specification
import spock.lang.Unroll

class FileHashIdTest extends Specification {

    @Unroll
    def "DecodeFileId"() {
        given:
        def hash = FileHashId.encodeFileId(clientId, fileId)

        when:
        def decodedFileId = FileHashId.decodeFileId(clientId, hash)

        then:
        decodedFileId.isPresent()
        decodedFileId.get() == fileId

        where:
        clientId << [10L, 20L, 30L, 55L]
        fileId << [99L, 100L, 555L, 100L]
    }
}
