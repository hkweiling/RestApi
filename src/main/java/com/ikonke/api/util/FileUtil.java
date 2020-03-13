package com.ikonke.api.util;

import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    /**
     * 加载resources文件夹下的文件内容
     */
    public static String loadResource(String location) throws IOException {
        File file = ResourceUtils.getFile(location);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public static void appendLine(String location, String line) throws IOException {
        File file = new File(location);
        if (!file.exists()) {
            boolean created = file.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.append(line);
        }
    }

}
