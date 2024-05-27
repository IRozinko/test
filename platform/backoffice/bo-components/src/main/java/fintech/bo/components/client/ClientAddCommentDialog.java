package fintech.bo.components.client;


import com.vaadin.data.Binder;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import fintech.bo.api.client.ClientEventApi;
import fintech.bo.api.model.AddCommentRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;


public class ClientAddCommentDialog extends ActionDialog{
    private final ClientEventApi clientEventApi;
    private final AddCommentRequest request;

    public ClientAddCommentDialog(ClientEventApi clientEventApi, AddCommentRequest request) {
        super("Add Comment", "Add");
        this.clientEventApi = clientEventApi;
        this.request = request;
        setDialogContent(form());
        setWidth(400, Sizeable.Unit.PIXELS);
    }

    @Override
    protected void executeAction() {
        BackgroundOperations.callApi("Adding Comment", clientEventApi.addComment(request), t -> {
            Notifications.trayNotification("Comment Added");
            close();
        }, Notifications::errorNotification);
    }

    private Component form() {

        TextArea textField = new TextArea();
        textField.setWidth(100, Sizeable.Unit.PERCENTAGE);
        textField.setRows(5);

        textField.focus();

        Binder<AddCommentRequest> binder = new Binder<>();
        binder.bind(textField, AddCommentRequest::getComment, AddCommentRequest::setComment);
        binder.setBean(this.request);

        FormLayout form = new FormLayout();
        form.addComponent(textField);
        return form;
    }
}
