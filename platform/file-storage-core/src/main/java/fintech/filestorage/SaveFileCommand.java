package fintech.filestorage;

import lombok.Data;
import lombok.ToString;

import java.io.InputStream;

@Data
@ToString(of = {"originalFileName"})
public class SaveFileCommand {

    public static final String CONTENT_TYPE_PDF = "application/pdf";

    private String originalFileName;

    private String directory;

    private InputStream inputStream;
    
    private String contentType;
}
