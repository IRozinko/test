package fintech.spain.alfa.product.filestorage;

import fintech.cms.Pdf;
import fintech.filestorage.SaveFileCommand;

import java.io.ByteArrayInputStream;

public class FileStorageCommandFactory {

    public static SaveFileCommand fromPdf(String directory, Pdf pdf) {
        SaveFileCommand savePdf = new SaveFileCommand();
        savePdf.setContentType(SaveFileCommand.CONTENT_TYPE_PDF);
        savePdf.setDirectory(directory);
        savePdf.setInputStream(new ByteArrayInputStream(pdf.getContent()));
        savePdf.setOriginalFileName(pdf.getName());
        return savePdf;
    }
}
