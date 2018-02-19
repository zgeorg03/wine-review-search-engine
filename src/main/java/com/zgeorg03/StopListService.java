package com.zgeorg03;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;

@Service
public class StopListService {
    private final TreeSet<String> words;

    private final String file;

    @Autowired
    public StopListService(@Value("${stopwords.file}") String file) throws IOException {
        this.file = file;
        words = new TreeSet<>();
        Files.readAllLines(Paths.get(file)).stream().forEach(line -> words.add(line.trim()));
    }
    public boolean isStopWord(String word){
         return words.contains(word);
    }
}
