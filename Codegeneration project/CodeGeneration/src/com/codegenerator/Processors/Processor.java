package com.codegenerator.Processors;

import com.codegenerator.CodeClassGenerator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by taray on 03/09/2018.
 */
public class Processor implements CodeClassGenerator  {

    String name;
    JSONObject model;
    String filename;

    public Processor (String json){
        if(json==null) return;
        JSONParser parser = new JSONParser();
        try {
            model = (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    String getImportsCode() {
        String code = "";

        code += "package com;\n" +
                "\n" +
                "import org.json.simple.JSONObject;\n" +
                "import java.io.BufferedWriter;\n" +
                "import java.io.File;\n" +
                "import java.io.FileWriter;\n" +
                "import java.io.IOException;\n" +
                "import java.text.SimpleDateFormat;\n"+
                "import java.util.*;\n\n";

        return code;
    }

    @Override
    public void generateCode() {

    }

    @Override
    public void saveFile(String filename, String code) {
        // write file on disk
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(code);  //Replace with the string
            //you are trying to write
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getId() {
        if(model==null) return "";
        else {
            return model.get("id").toString();
        }
    }

    @Override
    public JSONObject getJSON() {
        return model;
    }

    @Override
    public ArrayList<String> getKafkaChannels() {
        ArrayList<String> empty = new ArrayList<>();
        return empty;
    }

    public boolean isDataClass() {
        return false;
    }


    public boolean isPipelineClass() {
        return false;
    }

    public boolean isProcessorClass() {
        return true;
    }
}
