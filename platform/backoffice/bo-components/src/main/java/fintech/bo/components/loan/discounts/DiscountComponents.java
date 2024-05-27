package fintech.bo.components.loan.discounts;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Upload;
import fintech.bo.api.client.DiscountApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.IdRequest;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.Refreshable;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.notifications.Notifications;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

import static fintech.bo.components.background.BackgroundOperations.callApi;
import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.lending.tables.Discount.DISCOUNT;

@Component
public class DiscountComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private DiscountApiClient discountApiClient;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    public DiscountDataProvider dataProvider() {
        return new DiscountDataProvider(db, jooqClientDataService);
    }

    public Grid<Record> grid(DiscountDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(DISCOUNT.CLIENT_ID)));
        builder.addColumn(DISCOUNT.RATE_IN_PERCENT);
        builder.addColumn(DISCOUNT.EFFECTIVE_FROM);
        builder.addColumn(DISCOUNT.EFFECTIVE_TO);
        builder.addAuditColumns(DISCOUNT);
        builder.addColumn(DISCOUNT.ID);
        builder.sortDesc(DISCOUNT.CREATED_AT);
        return builder.build(dataProvider);
    }

    public Upload generateUploadButton(Refreshable refreshable) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Upload upload = new Upload("", (Upload.Receiver) (filename, mimeType) -> output);
        upload.addFinishedListener(event -> {
            RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.MIXED)
                .addFormDataPart("file", event.getFilename(), MultipartBody.create(MediaType.parse(event.getMIMEType()), output.toByteArray()))
                .build();

            callApi("Uploading file", fileApiClient.upload(body, "discounts"), t ->
                callApi("Applying discounts", discountApiClient.apply(new IdRequest(t.getId())), t2 -> {
                    refreshable.refresh();
                }, Notifications::errorNotification), Notifications::errorNotification);
        });
        upload.setButtonCaption("Import");

        return upload;
    }
}
