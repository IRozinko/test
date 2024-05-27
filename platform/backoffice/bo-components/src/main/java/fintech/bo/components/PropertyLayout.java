package fintech.bo.components;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;

import static fintech.bo.components.Formats.DECIMAL_FORMAT;


public class PropertyLayout extends VerticalLayout {

    public static final int DEFAULT_LABEL_WIDTH_IN_PIXELS = 150;

    private final int labelWidthInPixels;
    private Label titleLabel;

    public PropertyLayout() {
        this(null, DEFAULT_LABEL_WIDTH_IN_PIXELS);
    }

    public PropertyLayout(String title) {
        this(title, DEFAULT_LABEL_WIDTH_IN_PIXELS);
    }

    public PropertyLayout(String title, int labelWidthInPixels) {
        this.labelWidthInPixels = labelWidthInPixels;
        setSpacing(false);
        setMargin(true);
        setWidth(100, Unit.PERCENTAGE);
        if (title != null) {
            titleLabel = new Label(WordUtils.capitalizeFully(title));
            titleLabel.addStyleName(ValoTheme.LABEL_H4);
            titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
            titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
            titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
            addComponent(titleLabel);
        }
        addStyleName("bo-property-layout");
    }

    public PropertyLayout noTitle() {
        if (titleLabel != null) {
            removeComponent(titleLabel);
        }
        return this;
    }

    public PropertyLayout setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setValue(title);
        }
        return this;
    }

    public PropertyLayout add(String label, String value, Component... actions) {
        Label lbl = new Label(StringUtils.isNotBlank(value) ? value : "-");
        lbl.addStyleName(ValoTheme.LABEL_BOLD);
        return add(label, lbl, actions);
    }

    public PropertyLayout addTextArea(String label, String value) {
        if (StringUtils.isBlank(value)) {
            return add(label, value);
        } else {
            TextArea area = new TextArea();
            area.setReadOnly(true);
            area.setValue(value);
            area.setWidth(100, Unit.PERCENTAGE);
            return add(label, area);
        }
    }

    public PropertyLayout addPreformatted(String label, String value) {
        Label component = new Label(StringUtils.isNotBlank(value) ? value : "-");
        component.setContentMode(ContentMode.PREFORMATTED);
        return add(label, component);
    }

    public PropertyLayout addLink(String label, String value, String link) {
        return add(label, new Link(value, new InternalResource(link)));
    }

    public PropertyLayout addLink(String label, Long value, String link) {
        return add(label, new Link(Objects.toString(value, ""), new InternalResource(link)));
    }

    public PropertyLayout add(String label, Number value) {
        return add(label, Objects.toString(value, "-"));
    }

    public PropertyLayout add(String label, Boolean value) {
        return add(label, Objects.toString(value, "-"));
    }

    public PropertyLayout add(String label, BigDecimal value) {
        if (value == null) {
            return add(label, "-");
        } else {
            return add(label, new DecimalFormat(DECIMAL_FORMAT).format(value));
        }
    }

    public PropertyLayout addWarning(String label, BigDecimal value) {
        if (value == null) {
            return add(label, "-");
        } else {
            Label lbl = new Label(new DecimalFormat(DECIMAL_FORMAT).format(value));
            lbl.addStyleName(ValoTheme.LABEL_BOLD);
            lbl.addStyleName(BackofficeTheme.TEXT_DANGER);
            return add(label, lbl);
        }
    }

    public PropertyLayout addPercentage(String label, BigDecimal value) {
        if (value == null) {
            return add(label, "-");
        } else {
            return add(label, new DecimalFormat(DECIMAL_FORMAT).format(value) + "%");
        }
    }

    public PropertyLayout addDiscount(String label, BigDecimal discountPercent, BigDecimal discountAmount) {
        if (discountPercent == null) {
            return add(label, "-");
        } else {
            return add(label, String.format("%s%% (%s€)", new DecimalFormat(DECIMAL_FORMAT).format(discountPercent), new DecimalFormat(DECIMAL_FORMAT).format(discountAmount)));
        }
    }

    public PropertyLayout add(String label, LocalDateTime value) {
        if (value == null) {
            return add(label, "-");
        } else {
            return add(label, value.format(DateTimeFormatter.ofPattern(Formats.DATE_TIME_FORMAT)));
        }
    }

    public PropertyLayout add(String label, LocalDate value) {
        if (value == null) {
            return add(label, "-");
        } else {
            return add(label, value.format(DateTimeFormatter.ofPattern(Formats.DATE_FORMAT)));
        }
    }

    public PropertyLayout addWarning(String text) {
        Label label = new Label(text);
        label.addStyleName(BackofficeTheme.TEXT_DANGER);
        label.addStyleName(ValoTheme.LABEL_BOLD);
        label.addStyleName(ValoTheme.LABEL_SMALL);
        addComponentsAndExpand(label);
        return this;
    }

    public PropertyLayout add(String label, Component component) {
        HorizontalLayout row = row(label);
        row.addComponent(component);
        row.setExpandRatio(component, 1);
        addComponent(row);
        return this;
    }

    public PropertyLayout add(String label, Component component, Component... actionComponents) {
        HorizontalLayout row = row(label);
        row.addComponent(component);
        row.setExpandRatio(component, 1);
        Stream.of(actionComponents)
            .forEach(row::addComponent);
        addComponent(row);
        return this;
    }

    public PropertyLayout addSpacer() {
        addComponent(new Label());
        return this;
    }

    private HorizontalLayout row(String label) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addStyleName("bo-property-layout-row");
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        layout.setSpacing(true);
        Label lbl = new Label(WordUtils.capitalizeFully(label));
        lbl.setWidth(labelWidthInPixels, Unit.PIXELS);
        lbl.addStyleName(BackofficeTheme.TEXT_DARK_GRAY);
        lbl.addStyleName(ValoTheme.LABEL_SMALL);
        layout.addComponent(lbl);
        return layout;
    }
}
