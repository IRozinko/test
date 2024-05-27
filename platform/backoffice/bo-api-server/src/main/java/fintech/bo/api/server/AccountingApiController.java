package fintech.bo.api.server;

import com.google.common.base.Throwables;
import fintech.accounting.AccountTrialBalance;
import fintech.accounting.AccountingReports;
import fintech.accounting.ReportQuery;
import fintech.bo.api.model.accounting.AccountTrialBalanceExportResponse;
import fintech.excel.ExcelDocument;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
public class AccountingApiController {

    @Autowired
    private AccountingReports accountingReports;

    @PostMapping("/api/bo/accounting/trial-balance")
    public List<AccountTrialBalance> get(@RequestBody ReportQuery query) {
        return accountingReports.getTrialBalance(query);
    }

    @PostMapping("/api/bo/accounting/trial-balance-export")
    public AccountTrialBalanceExportResponse export(@RequestBody ReportQuery query) {
        List<AccountTrialBalance> accounts = accountingReports.getTrialBalance(query);

        ExcelDocument document = new ExcelDocument("Account balance");
        document.header(new String[]{
            "Account code", "Account name",
            "Opening debit", "Opening credit",
            "Turnover debit", "Turnover credit",
            "Closing debit", "Closing credit"
        });
        accounts.forEach(a -> document.row(new Object[]{
            a.getAccountCode(), a.getAccountName(),
            a.getOpeningDebit(), a.getOpeningCredit(),
            a.getTurnoverDebit(), a.getTurnoverCredit(),
            a.getClosingDebit(), a.getClosingCredit()
        }));

        CloudFile cloudFile = saveToCloudFile(String.format("%s_account_balance.xlsx", LocalDate.now()), document);

        return new AccountTrialBalanceExportResponse(cloudFile.getFileId(), cloudFile.getOriginalFileName());
    }

    @Autowired
    private FileStorageService fileStorageService;

    private CloudFile saveToCloudFile(String fileName, ExcelDocument document) {
        log.info("Saving excel file [{}] to cloud storage", fileName);
        File tempFile = null;
        try {
            tempFile = File.createTempFile(fileName, "export");
            @Cleanup FileOutputStream fos = new FileOutputStream(tempFile);
            document.write(fos);
            @Cleanup FileInputStream fis = new FileInputStream(tempFile);

            SaveFileCommand command = new SaveFileCommand();
            command.setDirectory("excel_export");
            command.setContentType("application/vnd.ms-excel");
            command.setInputStream(fis);
            command.setOriginalFileName(fileName);
            return fileStorageService.save(command);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }
}
