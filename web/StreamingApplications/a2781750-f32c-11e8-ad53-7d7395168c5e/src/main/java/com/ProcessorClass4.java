package com;

import org.json.simple.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.sql.Timestamp;
public class ProcessorClass4 extends ProcessorClass{

    String logName = "4.txt";
    String path    = "./logs/";

    static int inialization = 0;
    public  ProcessorClass4 (){
        if (inialization==0) {
            try {
                // create empty file
                BufferedWriter bw = new BufferedWriter(new FileWriter(path + logName));
                bw.write("");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inialization++;
        }

    }
    

    public JSONObject processData(JSONObject data) {
        try {
            data.put("timestampFile",  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + logName, true));
            writer.write(data.toJSONString());
            writer.newLine();
            writer.flush();
            writer.close();
            data.remove("timestampFile");
            return data;
        } catch (IOException ex) {
            System.out.println(ex);
            System.out.println("File could not be created");
            return data;
        }
    }
}