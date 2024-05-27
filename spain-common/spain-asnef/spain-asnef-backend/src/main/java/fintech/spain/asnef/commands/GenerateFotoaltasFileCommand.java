package fintech.spain.asnef.commands;

import com.google.common.collect.Lists;
import fintech.spain.asnef.models.FotoaltasOutputControlRecord;
import fintech.spain.asnef.models.FotoaltasOutputHeaderRecord;
import fintech.spain.asnef.models.FotoaltasOutputRecord;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GenerateFotoaltasFileCommand {

    private LocalDate preparedAt;

    private FotoaltasOutputHeaderRecord headerRecord;

    private List<OutputRecordHolder<FotoaltasOutputRecord>> outputRecordHolders = Lists.newArrayList();

    private FotoaltasOutputControlRecord controlRecord;
}
