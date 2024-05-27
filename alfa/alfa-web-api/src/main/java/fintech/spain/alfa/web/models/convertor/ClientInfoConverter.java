package fintech.spain.alfa.web.models.convertor;

import com.google.common.base.Converter;
import fintech.crm.client.Client;
import fintech.spain.alfa.web.models.ClientInfoResponse;

public class ClientInfoConverter extends Converter<Client, ClientInfoResponse> {

    @Override
    protected ClientInfoResponse doForward(Client client) {
        ClientInfoResponse info = new ClientInfoResponse();
        info.setId(client.getId());
        info.setDocumentNumber(client.getDocumentNumber());
        info.setNumber(client.getNumber());
        return info;
    }

    @Override
    protected Client doBackward(ClientInfoResponse clientInfoResponse) {
        throw new UnsupportedOperationException();
    }

}
