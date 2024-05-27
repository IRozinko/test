package fintech.risk.checklist;

import fintech.filestorage.CloudFile;
import fintech.risk.checklist.commands.AddCheckListEntryCommand;
import fintech.risk.checklist.commands.AddCheckListTypeCommand;
import fintech.risk.checklist.commands.DeleteCheckListEntryCommand;
import fintech.risk.checklist.commands.ExportCheckListEntryCommand;
import fintech.risk.checklist.commands.ImportChecklistEntryCommand;
import fintech.risk.checklist.commands.UpdateCheckListEntryCommand;
import fintech.risk.checklist.model.CheckListQuery;

import java.util.List;

public interface CheckListService {

    Long addType(AddCheckListTypeCommand command);

    Long addEntry(AddCheckListEntryCommand command);

    Long updateEntry(UpdateCheckListEntryCommand command);

    boolean isAllowed(CheckListQuery query);

    void importChecklistEntries(ImportChecklistEntryCommand command);

    CloudFile exportChecklistEntries(ExportCheckListEntryCommand command);

    List<CheckListEntry> find(CheckListQuery query);

    void delete(DeleteCheckListEntryCommand deleteCheckListEntryCommand);
}
