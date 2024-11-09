package exel.userinterface.resources.app.mainWindows.home.items;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import java.util.function.Function;

public class TooltipUtil<T> extends Tooltip {

    private ListView<T> listView;

    // Constructor for tooltip-only functionality
    public TooltipUtil(ListView<T> listView, Function<T, String> tooltipTextProvider) {
        this.listView = listView;
        super.setShowDelay(Duration.millis(600));
        super.setHideDelay(Duration.millis(100));
        setUpTooltip(tooltipTextProvider, this);
    }

    // Constructor for tooltip and color styling functionality
    public TooltipUtil(ListView<T> listView, Function<T, String> tooltipTextProvider, Function<T, String> styleClassProvider) {
        this.listView = listView;
        super.setShowDelay(Duration.millis(600));
        super.setHideDelay(Duration.millis(100));
        setUpTooltipAndStyle(tooltipTextProvider, styleClassProvider, this);
    }

    // Tooltip-only setup
    private void setUpTooltip(Function<T, String> tooltipTextProvider, Tooltip tooltip) {
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                // Clear previous content
                setText(null);
                setTooltip(null);

                if (!empty && item != null) {
                    // Set cell text
                    setText(item.toString());

                    // Attach the tooltip to the cell
                    setTooltip(tooltip);
                    tooltip.setText(tooltipTextProvider.apply(item));
                }
            }
        });
    }

    // Combined tooltip and color styling setup
    private void setUpTooltipAndStyle(Function<T, String> tooltipTextProvider, Function<T, String> styleClassProvider, Tooltip tooltip) {
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                // Clear previous styles and tooltip content
                getStyleClass().clear();
                setText(null);
                setTooltip(null);

                if (!empty && item != null) {
                    // Set cell text
                    setText(item.toString());

                    // Set the tooltip text
                    setTooltip(tooltip);
                    tooltip.setText(tooltipTextProvider.apply(item));

                    // Apply the color style based on the item's property
                    String styleClass = styleClassProvider.apply(item);
                    if (styleClass != null && !styleClass.isEmpty()) {
                        getStyleClass().add(styleClass);
                    }
                }
            }
        });
    }
}


