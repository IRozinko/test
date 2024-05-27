package fintech.spain.asnef;

import fintech.spain.asnef.commands.ExportFileCommand;
import fintech.spain.asnef.commands.GenerateFotoaltasFileCommand;
import fintech.spain.asnef.commands.GenerateRpFileCommand;
import fintech.spain.asnef.commands.ImportFileCommand;

public interface AsnefService {

    Log get(Long logId);

    void makeExhausted(Long loanId);

    Long generateRpFile(GenerateRpFileCommand command);

    Long generateFotoaltasFile(GenerateFotoaltasFileCommand command);

    void exportFile(ExportFileCommand command);

    void importRpFile(ImportFileCommand command);

    void importFotoaltasFile(ImportFileCommand command);

    void deleteFile(Long logId);
}
