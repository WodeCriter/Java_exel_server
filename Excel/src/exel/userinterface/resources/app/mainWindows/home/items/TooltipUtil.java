package exel.userinterface.resources.app.mainWindows.home.items;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import java.util.function.Function;

public final class TooltipUtil {

    // Private constructor to prevent instantiation
    private TooltipUtil() {}

    // Static method for tooltip-only functionality
    public static <T> void applyTooltip(ListView<T> listView, Function<T, String> tooltipTextProvider) {
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

                    // Create and set a new tooltip for this cell
                    Tooltip cellTooltip = new Tooltip(tooltipTextProvider.apply(item));
                    cellTooltip.setShowDelay(Duration.millis(600));
                    cellTooltip.setHideDelay(Duration.millis(100));
                    setTooltip(cellTooltip);
                }
            }
        });
    }

    // Static method for tooltip and color styling functionality
    public static <T> void applyTooltipAndStyle(ListView<T> listView, Function<T, String> tooltipTextProvider, Function<T, String> styleClassProvider) {
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                // Clear previous styles and tooltip content
                getStyleClass().clear(); // Adjust as needed
                setText(null);
                setTooltip(null);

                if (!empty && item != null) {
                    // Set cell text
                    setText(item.toString());

                    // Create and set a new tooltip for this cell
                    Tooltip cellTooltip = new Tooltip(tooltipTextProvider.apply(item));
                    cellTooltip.setShowDelay(Duration.millis(600));
                    cellTooltip.setHideDelay(Duration.millis(100));
                    setTooltip(cellTooltip);

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



