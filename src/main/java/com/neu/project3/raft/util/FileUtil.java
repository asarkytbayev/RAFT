package com.neu.project3.raft.util;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;

public class FileUtil {

    public static String readFile(String filePath) throws IOException {
        File file = ResourceUtils.getFile("classpath:" + filePath);
        String content = new String(Files.readAllBytes(file.toPath()));
        return content;
    }

}
