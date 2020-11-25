package com.codegenerator.Pipeline;

import com.codegenerator.CodeClassGenerator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taray on 30/06/2017.
 */
public abstract  class PipelineClass implements CodeClassGenerator {

    JSONObject model;
    String name;
    String filename;
    List<CodeClassGenerator> arrayModels;
    ArrayList<String> kafkaChannelId = new ArrayList<String>();

    public PipelineClass(String json, List<CodeClassGenerator> array){
        if(json==null) return;
        JSONParser parser = new JSONParser();
        try {
            model = (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        arrayModels= array;
        //kafkaChannelId= app +"-"+getId() ;
    }

    String getImportsCode() {
        String code = "";

        code += "package com;\n" +
                "\n" +
                "import org.apache.kafka.clients.consumer.ConsumerConfig;\n" +
                "import org.apache.kafka.clients.consumer.ConsumerRecord;\n" +
                "import org.apache.kafka.clients.consumer.ConsumerRecords;\n" +
                "import org.apache.kafka.clients.consumer.KafkaConsumer;\n" +
                "import org.apache.kafka.common.errors.WakeupException;\n" +
                "import org.json.simple.JSONObject;\n" +
                "import org.json.simple.parser.JSONParser;\n" +
                "import org.json.simple.parser.ParseException;\n" +
                "import java.net.ServerSocket;\n" +
                "import java.net.Socket;\n" +
                "import java.io.BufferedReader;\n" +
                "import java.io.InputStreamReader;\n" +
                "import java.io.IOException;\n" +
                "import java.util.*;\n\n";

        return code;
    }

    public String getName() { return name; }

    public String getFilename(){ return filename;}

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

    public CodeClassGenerator getModelById(String id, List<CodeClassGenerator> array){
        for(CodeClassGenerator item : array){
            if(item.getId().equals(id)){
                return item;
            }
        }
        return null;
    }

    @Override
    public String getId(){
        if(model==null) return "";
        else {
            return model.get("id").toString();
        }
    }

    @Override
    public JSONObject getJSON(){
        return model;
    }

    public ArrayList<String> getKafkaChannels(){
        return kafkaChannelId;
    }
}
