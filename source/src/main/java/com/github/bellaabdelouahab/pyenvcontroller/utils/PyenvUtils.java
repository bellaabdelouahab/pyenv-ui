/**
 * @author Adminella
 * Date: Jan 13, 2025
 * Time: 11:13:18 AM
*/
package com.github.bellaabdelouahab.pyenvcontroller.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;

public class PyenvUtils {

    public static String getPyenvPath() {
        String userHome = System.getProperty("user.home");
        return userHome + "\\.pyenv\\pyenv-win\\bin\\pyenv.bat";
    }

    public static void setupPyenvEnvironment(ProcessBuilder processBuilder) {
        Map<String, String> env = processBuilder.environment();
        String userHome = System.getProperty("user.home");
        env.put("PYENV", userHome + "\\.pyenv\\pyenv-win");
        env.put("PYENV_ROOT", userHome + "\\.pyenv\\pyenv-win");
        env.put("PYENV_HOME", userHome + "\\.pyenv\\pyenv-win");
    }

    public static Set<String> getInstalledVersions() {
        Set<String> versions = new HashSet<>();
        System.out.println("Fetching installed Python versions...");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(getPyenvPath(), "versions");
            setupPyenvEnvironment(processBuilder);
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Extract just the version number
                line = line.replaceAll("\\*?\\s*([\\d.]+).*", "$1").trim();
                if (!line.isEmpty()) {
                    versions.add(line);
                }
            }
            process.waitFor();
            System.out.println("Installed versions fetched successfully.");
        } catch (Exception e) {
            System.out.println("Error fetching installed versions:");
            e.printStackTrace();
        }
        return versions;
    }

    public static List<String> getAvailableVersions() {
        List<String> versions = new ArrayList<>();
        System.out.println("Fetching available Python versions...");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(getPyenvPath(), "install", "--list");
            setupPyenvEnvironment(processBuilder);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Available versions line: " + line);
                line = line.trim();
                if (line.isEmpty()) continue;
                // Assume all non-empty lines are versions
                if (line.matches("\\d+\\.\\d+\\.\\d+")) { // Simple version pattern
                    versions.add(line);
                }
            }
            process.waitFor();
            System.out.println("Available versions fetched successfully.");
        } catch (Exception e) {
            System.out.println("Error fetching available versions:");
            e.printStackTrace();
        }
        System.out.println("Available Versions: " + versions);
        return versions;
    }

    public static void switchPythonVersion(String version) {
        System.out.println("Switching to Python version: " + version);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(getPyenvPath(), "global", version);
            setupPyenvEnvironment(processBuilder);
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("Switched to Python version " + version + " successfully.");
        } catch (Exception e) {
            System.out.println("Error switching to Python version " + version + ":");
            e.printStackTrace();
        }
    }

    public static void installVersion(String version) {
        System.out.println("Installing Python version: " + version);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(getPyenvPath(), "install", version);
            setupPyenvEnvironment(processBuilder);
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("Python version " + version + " installed successfully.");
        } catch (Exception e) {
            System.out.println("Error installing Python version " + version + ":");
            e.printStackTrace();
        }
    }
}
