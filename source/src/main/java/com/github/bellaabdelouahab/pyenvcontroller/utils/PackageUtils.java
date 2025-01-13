/**
 * @author Admin
 * Date: Jan 13, 2025
 * Time: 11:13:32 AM
*/
package com.github.bellaabdelouahab.pyenvcontroller.utils;

import com.github.bellaabdelouahab.pyenvcontroller.src.HelloController.Package;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PackageUtils {

    public static ObservableList<Package> listInstalledPackages(String selectedVersion) {
        System.out.println("Listing installed packages for version: " + selectedVersion);
        ObservableList<Package> packages = FXCollections.observableArrayList();
        try {
            // Ensure the command is correctly formed
            String pyenvPath = PyenvUtils.getPyenvPath();
            ProcessBuilder processBuilder = new ProcessBuilder(pyenvPath, "exec", "pip", "list");
            PyenvUtils.setupPyenvEnvironment(processBuilder);
            processBuilder.environment().put("PYENV_VERSION", selectedVersion); // Set the PYENV_VERSION environment variable
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { // Skip header
                    firstLine = false;
                    continue;
                }
                if (line.contains("-----")) { // Skip lines with dashes
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String name = parts[0];
                    String version = parts[1];
                    packages.add(new Package(name, version));
                    System.out.println("Found package: " + name + " " + version); // Print each found package
                }
            }
            process.waitFor();
            System.out.println("Installed packages listed successfully.");
        } catch (Exception e) {
            System.out.println("Error listing installed packages for version " + selectedVersion + ":");
            e.printStackTrace();
        }
        return packages;
    }

    public static String installPackage(String selectedVersion, String packageName) {
        System.out.println("Installing package: " + packageName + " for version: " + selectedVersion);
        try {
            String pyenvPath = PyenvUtils.getPyenvPath();
            ProcessBuilder processBuilder = new ProcessBuilder(pyenvPath, "exec", "pip", "install", packageName);
            PyenvUtils.setupPyenvEnvironment(processBuilder);
            processBuilder.environment().put("PYENV_VERSION", selectedVersion); // Set the PYENV_VERSION environment variable
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            System.out.println("Package " + packageName + " installed successfully.");
            return "Package " + packageName + " installed successfully.";
        } catch (Exception e) {
            System.out.println("Error installing package " + packageName + " for version " + selectedVersion + ":");
            e.printStackTrace();
            return "Error installing package " + packageName + ": " + e.getMessage();
        }
    }

    public static String uninstallPackage(String selectedVersion, String packageName) {
        System.out.println("Uninstalling package: " + packageName + " for version: " + selectedVersion);
        try {
            String pyenvPath = PyenvUtils.getPyenvPath();
            ProcessBuilder processBuilder = new ProcessBuilder(pyenvPath, "exec", "pip", "uninstall", "-y", packageName);
            PyenvUtils.setupPyenvEnvironment(processBuilder);
            processBuilder.environment().put("PYENV_VERSION", selectedVersion); // Set the PYENV_VERSION environment variable
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            System.out.println("Package " + packageName + " uninstalled successfully.");
            return "Package " + packageName + " uninstalled successfully.";
        } catch (Exception e) {
            System.out.println("Error uninstalling package " + packageName + " for version " + selectedVersion + ":");
            e.printStackTrace();
            return "Error uninstalling package " + packageName + ": " + e.getMessage();
        }
    }
}
