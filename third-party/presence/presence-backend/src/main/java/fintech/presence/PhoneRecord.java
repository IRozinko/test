package fintech.presence;

import fintech.presence.model.PhoneDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneRecord {

    private String number;
    private PhoneDescription description;
}
