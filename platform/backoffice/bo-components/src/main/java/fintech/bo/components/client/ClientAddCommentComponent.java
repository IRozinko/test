package fintech.bo.components.client;


import fintech.bo.api.client.ClientEventApi;
import fintech.bo.api.model.AddCommentRequest;
import fintech.bo.components.client.dto.ClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientAddCommentComponent {

    @Autowired
    private ClientEventApi clientEventApi;

    public ClientAddCommentDialog clientAddCommentDialog(AddCommentRequest request) {
        return new ClientAddCommentDialog(clientEventApi, request);
    }

    public ClientAddCommentDialog clientAddCommentDialog(ClientDTO client) {
        AddCommentRequest request = new AddCommentRequest();
        request.setClientId(client.getId());
        return new ClientAddCommentDialog(clientEventApi, request);
    }
}
