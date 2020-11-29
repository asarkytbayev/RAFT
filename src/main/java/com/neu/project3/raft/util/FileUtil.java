package com.neu.project3.raft.util;

import java.io.*;
import java.util.stream.Collectors;

public class FileUtil {

    public static String readFile(String filePath) throws IOException {
        try (InputStream inputStream = FileUtil.class.getResourceAsStream("/peer_list.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String contents = reader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            return contents;
        }
//        File file = new ClassPathResource(
//                "static/peer_list.txt").getFile();
//        // File file = ResourceUtils.getFile("classpath:" + filePath);
//        String content = new String(Files.readAllBytes(file.toPath()));
//        return content;
    }

}
