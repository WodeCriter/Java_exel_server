package exel.userinterface.util;

import javafx.scene.control.Alert;

public class ScreenUtils
{
    public static void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
