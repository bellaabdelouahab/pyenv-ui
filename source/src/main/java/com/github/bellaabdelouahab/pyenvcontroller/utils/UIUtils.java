/**
 * @author Adminella
 * Date: Jan 13, 2025
 * Time: 11:13:36 AM
*/
package com.github.bellaabdelouahab.pyenvcontroller.utils;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class UIUtils {



    public static HBox createPackageRow(String name, String version) {
        HBox row = new HBox();
        row.setSpacing(10);
        row.setStyle("-fx-padding: 5; -fx-background-color: #3e3e3e; -fx-border-color: #2e2e2e; -fx-border-width: 1;");
        
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-pref-width: 150px; -fx-padding: 5 5 5 25");
        
        Label versionLabel = new Label(version);
        versionLabel.setStyle("-fx-text-fill: white; -fx-pref-width: 100px;");
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        row.getChildren().addAll(nameLabel, spacer, versionLabel);
        return row;
    }
}
