package fintech.presence.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fintech.presence.PhoneRecord;
import fintech.presence.PresenceJsonUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonSerialize(using = PresenceJsonUtils.PhoneRecordsSerializer.class)
@JsonDeserialize(using = PresenceJsonUtils.PhoneRecordsDeserializer.class)
public class PhoneRecordsWrapper {

    private List<PhoneRecord> phoneRecords;

    public PhoneRecordsWrapper(List<PhoneRecord> phoneRecords) {
        this.phoneRecords = phoneRecords;
    }
}
