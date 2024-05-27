package fintech.spain.alfa.app.admintools;

import fintech.Validate;
import fintech.admintools.AdminAction;
import fintech.admintools.AdminActionContext;
import fintech.filestorage.CloudFile;
import fintech.spain.alfa.product.dc.impl.DebtExportService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExportDebtPortfolioAdminAction implements AdminAction {

    @Autowired
    private DebtExportService exportService;

    @Override
    @Transactional
    @SneakyThrows
    public void execute(AdminActionContext context) {
        String params = context.getParams();
        Validate.notEmpty(params, "Invalid argument - expected a list of loan ids separated by newline");
        Set<Long> loanNumbers = Arrays.stream(params.split("\n"))
            .map(Long::parseLong)
            .collect(Collectors.toSet());

        CloudFile archive = exportService.export(loanNumbers);
        context.updateProgress("Exported to Cloud file: " + archive.getFileId());
    }

    @Override
    public String getName() {
        return "Export debt portfolio";
    }

}
