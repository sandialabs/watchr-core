package gov.sandia.watchr.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WatchrRunStrategySerializer {

    /////////////////
    // SAVE / LOAD //
    /////////////////

    public void save(File parentDir, String runFileName, WatchrRunStrategy run) throws IOException {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        File runFile = new File(parentDir, runFileName);
        try(FileWriter writer = new FileWriter(runFile)) {
            writer.write(gson.toJson(run));   
        }
    }

    public void delete(File parentDir, String runFileName) throws IOException {
        File runFile = new File(parentDir, runFileName);
        Files.delete(runFile.toPath());
    }

    public WatchrRunStrategy load(File parentDir, String runFileName) throws IOException {
        File file = new File(parentDir, runFileName);
        if(file.exists()) {
            GsonBuilder builder = new GsonBuilder(); 
            Gson gson = builder.create(); 
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                return gson.fromJson(bufferedReader, WatchrRunStrategy.class); 
            }
        }
        return null;
    }
}
