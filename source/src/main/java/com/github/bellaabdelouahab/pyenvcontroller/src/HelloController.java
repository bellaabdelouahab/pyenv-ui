package com.github.bellaabdelouahab.pyenvcontroller.src;

import com.github.bellaabdelouahab.pyenvcontroller.utils.PyenvUtils;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.github.bellaabdelouahab.pyenvcontroller.utils.PackageUtils;
import com.github.bellaabdelouahab.pyenvcontroller.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class HelloController {
    private static final Logger LOGGER = Logger.getLogger(HelloController.class.getName());

    @FXML
    private VBox avaialeversions;

    @FXML
    private ScrollPane scrollPaneVersions;

    @FXML
    private ScrollPane scrollPanePackages;

    @FXML
    private Button installButton;

    @FXML
    private TextField versionSearchField;

    @FXML
    private TextField packageSearchField;

    @FXML
    private TextField packageVersionField;

    @FXML
    private VBox packageVBox;

    @FXML
    private Button installPackageButton;

    @FXML
    private Button uninstallPackageButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button switchButton;

    private String selectedVersion = null;
    private Set<String> installedVersions;
    private List<String> availableVersions;
    private ObservableList<Package> packages;

    @FXML
    public void initialize() {
        LOGGER.info("Initializing HelloController...");
        loadPythonVersions();
        // Apply styling
        String css = this.getClass().getResource("/com/github/bellaabdelouahab/pyenvcontroller/src/styles.css").toExternalForm();
        scrollPaneVersions.getStylesheets().add(css);
        scrollPanePackages.getStylesheets().add(css);
        avaialeversions.setSpacing(5); // Add spacing between buttons
        avaialeversions.setPadding(new javafx.geometry.Insets(10)); // Add padding around buttons
        
        // Add listeners for search fields if implementing search functionality
        versionSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterAvailableVersions(newValue));
        packageSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterPackages(newValue));

        // Initialize switch button
        switchButton.setOnAction(e -> handleSwitchButton());
        LOGGER.info("HelloController initialized successfully.");
    }

    private void loadPythonVersions() {
        LOGGER.info("Loading Python versions...");
        installedVersions = PyenvUtils.getInstalledVersions();
        LOGGER.info("Installed Versions: " + installedVersions);
        availableVersions = PyenvUtils.getAvailableVersions();
        LOGGER.info("Available Versions: " + availableVersions);

        displayPythonVersions(installedVersions, availableVersions);
    }

    private void displayPythonVersions(Set<String> installedVersions, List<String> availableVersions) {
        LOGGER.info("Displaying Python versions...");
        avaialeversions.getChildren().clear(); // Clear existing buttons

        // Add installed versions first
        for (String version : installedVersions) {
            Button versionButton = createVersionButton(version, true);
            avaialeversions.getChildren().add(versionButton);
        }

        // Filter out installed versions from available versions
        List<String> filteredAvailableVersions = availableVersions.stream()
                .filter(version -> !installedVersions.contains(version))
                .collect(Collectors.toList());

        // Add available versions
        for (String version : filteredAvailableVersions) {
            Button versionButton = createVersionButton(version, false);
            avaialeversions.getChildren().add(versionButton);
        }
        LOGGER.info("Python versions displayed successfully.");
    }

    private Button createVersionButton(String version, boolean isInstalled) {
        Button versionButton = new Button(version);
        versionButton.setMaxWidth(Double.MAX_VALUE);

        if (isInstalled) {
            versionButton.getStyleClass().add("installed-version");
            versionButton.setOnAction(e -> selectVersion(version, versionButton));
        } else {
            versionButton.getStyleClass().add("available-version");
            versionButton.setOnAction(e -> selectVersion(version, versionButton));
        }

        return versionButton;
    }

    private void selectVersion(String version, Button button) {
        LOGGER.info("Selecting version: " + version);
        // Deselect previously selected button
        if (selectedVersion != null) {
            for (javafx.scene.Node node : avaialeversions.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    if (btn.getText().equals(selectedVersion)) {
                        btn.getStyleClass().remove("selected-version");
                        break;
                    }
                }
            }
        }

        // Select the new version
        selectedVersion = version;
        button.getStyleClass().add("selected-version");
        LOGGER.info("Selected version: " + selectedVersion);
        listInstalledPackages(); // Invoke the method to list packages
    }

    private void listInstalledPackages() {
        LOGGER.info("Listing installed packages for version: " + selectedVersion);
        packages = PackageUtils.listInstalledPackages(selectedVersion);

        // Update the VBox on the JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            // Clear existing package rows, but keep the header row
            packageVBox.getChildren().removeIf(node -> node != packageVBox.getChildren().get(0));
            for (Package pkg : packages) {
                HBox row = UIUtils.createPackageRow(pkg.getName(), pkg.getVersion());
                packageVBox.getChildren().add(row);
            }
            statusLabel.setText("Packages loaded for version " + selectedVersion);
            LOGGER.info("Installed packages listed successfully.");
        });
    }

    private void filterAvailableVersions(String query) {
        LOGGER.info("Filtering available versions with query: " + query);
        List<String> filteredAvailableVersions = availableVersions.stream()
                .filter(version -> version.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        displayPythonVersions(installedVersions, filteredAvailableVersions);
    }

    private void filterPackages(String query) {
        LOGGER.info("Filtering packages with query: " + query);
        ObservableList<Package> filteredPackages = packages.stream()
                .filter(pkg -> pkg.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Update the VBox on the JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            // Clear all existing package rows
            packageVBox.getChildren().clear();
            for (Package pkg : filteredPackages) {
                HBox row = UIUtils.createPackageRow(pkg.getName(), pkg.getVersion());
                packageVBox.getChildren().add(row);
            }

            // Enable or disable the install package button based on exact match
            boolean exactMatch = filteredPackages.stream()
                    .anyMatch(pkg -> pkg.getName().equalsIgnoreCase(packageSearchField.getText().trim()) &&
                            pkg.getVersion().equalsIgnoreCase(packageVersionField.getText().trim()));
            installPackageButton.setDisable(exactMatch || packageSearchField.getText().trim().isEmpty());
        });
    }

    @FXML
    private void handleInstallButton() {
        if (selectedVersion == null) {
            LOGGER.warning("No version selected for installation.");
            System.out.println("No version selected for installation.");
            return;
        }
        LOGGER.info("Install button clicked for version: " + selectedVersion);
        PyenvUtils.installVersion(selectedVersion);
        // Deselect after installation
        selectedVersion = null;
        loadPythonVersions();
    }

    @FXML
    private void handleSwitchButton() {
        if (selectedVersion == null) {
            LOGGER.warning("No version selected to switch.");
            System.out.println("No version selected to switch.");
            statusLabel.setText("No version selected to switch.");
            return;
        }
        PyenvUtils.switchPythonVersion(selectedVersion);
        statusLabel.setText("Switched to Python version " + selectedVersion);
    }

    @FXML
    private void handleInstallPackageButton() {
        String packageName = packageSearchField.getText().trim();
        String packageVersion = packageVersionField.getText().trim();
        if (packageName.isEmpty()) {
            statusLabel.setText("Please enter a package name to install.");
            return;
        }

        // Check if the package with the same version is already installed
        boolean exactMatch = packages.stream()
                .anyMatch(pkg -> pkg.getName().equalsIgnoreCase(packageName) &&
                        pkg.getVersion().equalsIgnoreCase(packageVersion));

        if (exactMatch) {
            statusLabel.setText("Package " + packageName + " version " + packageVersion + " is already installed.");
            return;
        }

        // Check if the package with a different version is installed
        Package installedPackage = packages.stream()
                .filter(pkg -> pkg.getName().equalsIgnoreCase(packageName))
                .findFirst()
                .orElse(null);

        if (installedPackage != null) {
            // Uninstall the existing package
            String uninstallResult = PackageUtils.uninstallPackage(selectedVersion, packageName);
            if (!uninstallResult.contains("successfully")) {
                statusLabel.setText("Failed to uninstall existing package " + packageName + ": " + uninstallResult);
                return;
            }
        }

        // Install the new package version or latest if version is empty
        String installResult;
        if (packageVersion.isEmpty()) {
            installResult = PackageUtils.installPackage(selectedVersion, packageName);
        } else {
            installResult = PackageUtils.installPackage(selectedVersion, packageName + "==" + packageVersion);
        }

        if (installResult.contains("successfully")) {
            statusLabel.setText("Package " + packageName + (packageVersion.isEmpty() ? "" : " version " + packageVersion) + " installed successfully.");
        } else {
            // Reinstall the old package if the new installation fails
            if (installedPackage != null) {
                PackageUtils.installPackage(selectedVersion, packageName + "==" + installedPackage.getVersion());
            }
            statusLabel.setText("Failed to install package " + packageName + (packageVersion.isEmpty() ? "" : " version " + packageVersion) + ": " + installResult);
        }
        listInstalledPackages(); // Refresh the package list
    }

    @FXML
    private void handleUninstallPackageButton() {
        String packageName = packageSearchField.getText().trim();
        if (packageName.isEmpty()) {
            statusLabel.setText("Please enter a package name to uninstall.");
            return;
        }
        String result = PackageUtils.uninstallPackage(selectedVersion, packageName);
        statusLabel.setText(result);
        listInstalledPackages(); // Refresh the package list
    }

    // Add the Package class
    public static class Package {
        private String name;
        private String version;

        public Package(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }
    }
}