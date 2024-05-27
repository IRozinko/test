package fintech.filestorage.spi;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileContent {

    private String name;
    private byte[] content;
}
