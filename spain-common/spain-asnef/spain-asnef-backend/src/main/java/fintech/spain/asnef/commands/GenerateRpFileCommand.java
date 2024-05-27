package fintech.spain.asnef.commands;

import com.google.common.collect.Lists;
import fintech.spain.asnef.models.RpOutputControlRecord;
import fintech.spain.asnef.models.RpOutputHeaderRecord;
import fintech.spain.asnef.models.RpOutputRecord;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GenerateRpFileCommand {

    private LocalDate preparedAt;

    private RpOutputHeaderRecord headerRecord;

    private List<OutputRecordHolder<RpOutputRecord>> outputRecordHolders = Lists.newArrayList();

    private RpOutputControlRecord controlRecord;
}
