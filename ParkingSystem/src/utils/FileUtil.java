package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    
    
    public static void createFileIfNotExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    }
    
    
    public static List<String> readAllLines(String fileName) {
        List<String> lines = new ArrayList<>();
        createFileIfNotExists(fileName);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }
    
    
    public static void writeAllLines(String fileName, List<String> lines) {
        createFileIfNotExists(fileName);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    
    
    public static void appendLine(String fileName, String line) {
        createFileIfNotExists(fileName);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to file: " + e.getMessage());
        }
    }
}