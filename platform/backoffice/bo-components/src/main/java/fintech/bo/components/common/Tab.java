package fintech.bo.components.common;

import com.vaadin.ui.Component;
import fintech.bo.components.client.dto.ClientDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Tab {

    protected String caption;
    protected ClientDTO client;

    public abstract Component build();

}
