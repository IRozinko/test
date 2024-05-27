package fintech;

import org.hashids.Hashids;

import java.util.Objects;
import java.util.Optional;

public class FileHashId {
    
    private static Hashids hashids = new Hashids("1BPz7gxWVgm8GepwK5lk", 20);

    public static String encodeFileId(Long clientId, Long fileId) {
        return hashids.encode(clientId, fileId);
    }
    
    public static Optional<Long> decodeFileId(Long expectedClientId, String fileHashId) {
        try {
            long[] decode = hashids.decode(fileHashId);

            if (decode.length != 2) {
                Optional.empty();
            }

            Long decodedClientId = decode[0];
            Long fileId = decode[1];

            if (!Objects.equals(decodedClientId, expectedClientId)) {
                return Optional.empty();
            }

            return Optional.of(fileId);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

}
