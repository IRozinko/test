package fintech.bo.components.dowjones;

import com.google.common.base.Joiner;
import com.vaadin.ui.ComboBox;
import fintech.bo.components.dowjones.dto.SearchResultDTO;
import fintech.bo.components.dowjones.repository.SearchResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Component
public class SearchResultComponent {

    @Autowired
    private SearchResultRepository searchResultRepository;

    public ComboBox<SearchResultDTO> searchResultComboBox() {
        ComboBox<SearchResultDTO> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Select name search result...");
        comboBox.setPageLength(20);
        comboBox.setDataProvider(searchResultRepository);
        comboBox.setPopupWidth("600px");
        comboBox.setItemCaptionGenerator(item -> String.format("%s | %s | %s | %s",
            item.getClientNumber(),
            Joiner.on(" ").join(
                capitalizeFully(firstNonNull(item.getFirstName(), "")),
                capitalizeFully(firstNonNull(item.getLastName(), "")),
                capitalizeFully(firstNonNull(item.getSecondLastName(), ""))
            ),
            lowerCase(firstNonNull(item.getEmail(), "")),
            firstNonNull(item.getPhone(), "")));
        return comboBox;
    }
}
