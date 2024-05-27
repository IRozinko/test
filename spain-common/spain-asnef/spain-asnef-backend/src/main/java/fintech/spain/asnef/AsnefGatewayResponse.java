package fintech.spain.asnef;

import lombok.Value;

import java.util.List;

@Value
public class AsnefGatewayResponse {

    List<Entry> entries;

    @Value
    public static class Entry {

        String filename;

        String content;
    }
}
