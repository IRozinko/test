package fintech.bo.components;

import org.jooq.TableField;

import java.util.List;

public interface ExportableDataProvider {

    List<TableField> exportableColumns();

}
