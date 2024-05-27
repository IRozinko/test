package fintech.presence.settings;

import lombok.Data;

@Data
public class PresenceSettings {
    public static final String PRESENCE_SETTINGS = "PresenceSettings";

    private Integer serviceId;
    private Integer loadId;
}
