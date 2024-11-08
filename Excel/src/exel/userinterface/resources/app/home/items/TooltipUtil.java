package exel.userinterface.resources.app.home.items;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import java.util.function.Function;

public class TooltipUtil<T> extends Tooltip {

    //private static final Tooltip tooltip = new Tooltip();
    private ListView<T> listView;

    public TooltipUtil(ListView<T> listView, Function<T, String> tooltipTextProvider) {
        this.listView = listView;
        super.setShowDelay(Duration.millis(600));
        super.setHideDelay(Duration.millis(100));
        setUpTooltip(tooltipTextProvider, this);
    }

    private void setUpTooltip(Function<T, String> tooltipTextProvider, Tooltip tooltip) {
        // Configure the ListView's cell factory to add tooltips
        listView.setCellFactory(lv -> {
            ListCell<T> cell = new ListCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);

                    // Clear previous content
                    setText(null);
                    setTooltip(null);

                    if (!empty && item != null)
                    {
                        // Set cell text (you might want to customize this as needed)
                        setText(item.toString()); // Customize display text if needed

                        // Attach the tooltip to the cell
                        setTooltip(tooltip);
                    }
                }
            };

            // Show tooltip when hovering over an item
            cell.setOnMouseEntered(event -> {
                if (!cell.isEmpty() && cell.getItem() != null) {
                    showTooltip(event, cell.getItem(), tooltipTextProvider);
                }
            });

            // Hide tooltip when not hovering
            cell.setOnMouseExited(event -> tooltip.hide());

            return cell;
        });
    }

    private <T> void showTooltip(MouseEvent event, T item, Function<T, String> tooltipTextProvider) {
        this.setText(tooltipTextProvider.apply(item)); // Set tooltip text based on the item
        ListCell<?> cell = (ListCell<?>) event.getSource();
        this.show(cell.getListView(), event.getScreenX() + 10, event.getScreenY() + 10);
    }
}

