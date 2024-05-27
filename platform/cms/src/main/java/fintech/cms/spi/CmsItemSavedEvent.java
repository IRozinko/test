package fintech.cms.spi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CmsItemSavedEvent {

    private final CmsItem item;
}
