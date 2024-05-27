package fintech.filestorage;


import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface FileStorageService {

    CloudFile save(SaveFileCommand command);

    Optional<CloudFile> get(Long fileId);

    void readContents(Long fileId, Consumer<InputStream> consumer);

    <T> T readContents(Long fileId, Function<InputStream, T> function);

    String readContentAsString(Long fileId, Charset charset);
}
