package com.codegenerator;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by taray on 21/06/2017.
 */
public interface CodeClassGenerator {


     void generateCode();

     void saveFile(String filename, String code);

     String getName();

     String getFilename();

     String getId();

     JSONObject getJSON();

     ArrayList<String> getKafkaChannels();
}
