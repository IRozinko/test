package fintech.bo.components.utils;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import fintech.bo.components.ExportableDataProvider;
import fintech.bo.components.background.BackgroundOperationFeedback;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import fintech.excel.ExcelDocument;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableField;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class DataProviderExporter {

    private static final int QUERY_LIMIT = 3000;

    public static FileDownloader xlsDownloader(DataProvider dataProvider, String fileName, String sheetName) {
        StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> {
            PipedInputStream is = new PipedInputStream();
            BackgroundOperations.run("Export",
                feedback -> DataProviderExporter.exportXls(dataProvider, sheetName, is, feedback),
                outputStream -> Notifications.trayNotification("File exported"), Notifications::errorNotification);
            return is;
        }, fileName);

        // In theory <=0 disables caching. In practice Chrome, Safari (and, apparently, IE) all ignore <=0. Set to 1s
        streamResource.setCacheTime(1000);
        return new FileDownloader(streamResource);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static OutputStream exportXls(DataProvider dataProvider, String sheetName, PipedInputStream is, BackgroundOperationFeedback feedback) {
        @Cleanup PipedOutputStream xlsOutputStream = new PipedOutputStream(is);

        ExcelDocument document = new ExcelDocument(sheetName);
        document.header(headers(dataProvider));

        int size = dataProvider.size(new Query());
        AtomicInteger i = new AtomicInteger();
        Stream<Record> records;
        do {
            records = dataProvider.fetch(new Query(i.intValue(), QUERY_LIMIT, Collections.emptyList(), null, null));
            records.forEach(record -> {
                Object[] values = tableFields(dataProvider).stream().map(record::get).toArray();
                document.row(values);
                int count = i.incrementAndGet();
                feedback.update("Creating spreadsheet...", count / (float) size);
            });
        } while (i.intValue() < size);

        document.write(xlsOutputStream);
        return xlsOutputStream;
    }

    private static List<TableField> tableFields(DataProvider dataProvider) {
        return ((ExportableDataProvider) dataProvider).exportableColumns();
    }

    private static String[] headers(DataProvider dataProvider) {
        return tableFields(dataProvider).stream().map(Field::getName).toArray(String[]::new);
    }

}
