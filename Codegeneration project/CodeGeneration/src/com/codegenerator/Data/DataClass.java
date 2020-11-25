package com.codegenerator.Data;

import com.codegenerator.CodeClassGenerator;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by taray on 22/06/2017.
 */
public abstract class DataClass implements CodeClassGenerator {

    JSONObject model;
    String name;
    String filename;
    String appid;
    ArrayList<String> kafkaChannelId= new ArrayList<String>();



    public DataClass(String json) {
        if(json==null) return;
        JSONParser parser = new JSONParser();
        try {
            model = (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    String getImportsCode(){
        String code="";

        code +="package com;\n" +
                "import org.apache.kafka.clients.producer.KafkaProducer;\n" +
                "import org.apache.kafka.clients.producer.ProducerConfig;\n" +
                "import org.apache.kafka.clients.producer.ProducerRecord;\n" +
                "import twitter4j.*;\n" +
                "import twitter4j.JSONObject;"+
                "import twitter4j.conf.ConfigurationBuilder;\n" +
                "import java.util.List;\n" +
                "import java.util.Properties;";
        code += "import java.util.Scanner;\n" +
                "import com.satori.rtm.*;\n" +
                "import com.satori.rtm.model.*;\n" +
                "import java.util.concurrent.*;\n" +
                "import org.json.simple.*;\n" +
                "import org.json.simple.parser.JSONParser;\n" +
                "import org.json.simple.parser.ParseException;";
        code +="import java.io.BufferedReader;\n" +
                "import java.io.IOException;\n" +
                "import java.io.InputStreamReader;\n" +
                "import java.util.Properties;";
        code+="import org.apache.http.HttpResponse;\n" +
                "import org.apache.http.client.HttpClient;\n" +
                "import org.apache.http.client.methods.HttpGet;\n" +
                "import org.apache.http.impl.client.DefaultHttpClient;";

        return code;
    }

    String getValueFromKey(JSONArray array, String key){

        for (Object o : array) {
            JSONObject jsonLineItem = (JSONObject) o;
            String name = jsonLineItem.get("name").toString();
            String val = jsonLineItem.get("value").toString();

            if(name.equals(key)){
                return val;
            }
        }
        return "";
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

    String generatePathJson(String attName,  String path){
        String newPath= "                    jsonChild= json;\n" ;

        String[] parts = path.split("\\.");
        int numArrayPath = parts.length;
        int index=0;

        for (String str : parts)
        {
            index++;
            if(index==numArrayPath){
                newPath +="                    info.put(\"" + attName + "\", jsonChild.get(\""+ str +"\"));\n";
            }else{
                if(str.split("\\[").length==1){
                    newPath +="                    jsonChild = (org.json.simple.JSONObject) jsonChild.get(\""+ str +"\");\n";
                }else {
                    int indexArray = Integer.parseInt(str.split("\\[")[1].split("\\]")[0]);
                    newPath +="                    jsonChild = (org.json.simple.JSONObject)((org.json.simple.JSONArray)"+
                            "jsonChild.get(\""+ str.split("\\[")[0] +"\")).get(" + indexArray+");\n";
                }
            }

        }


        System.out.println(newPath);
        return  newPath;
    }

    String generatePathJsonXml(String attName,  String path){
        String newPath= "                    jsonChild= json;\n" ;

        String[] parts = path.split("\\.");
        int numArrayPath = parts.length;
        int index=0;

        for (String str : parts)
        {
            index++;
            if(index==numArrayPath){
                newPath +="                    info.put(\"" + attName + "\", jsonChild.get(\""+ str +"\"));\n";
            }else{
                if(str.split("\\[").length==1){
                    newPath +="                    jsonChild = (org.json.JSONObject) jsonChild.get(\""+ str +"\");\n";
                }else {
                    int indexArray = Integer.parseInt(str.split("\\[")[1].split("\\]")[0]);
                    newPath +="                    jsonChild = (org.json.JSONObject)((org.json.JSONArray)"+
                            "jsonChild.get(\""+ str.split("\\[")[0] +"\")).get(" + indexArray+");\n";
                }
            }

        }


        System.out.println(newPath);
        return  newPath;
    }

    @Override
    public ArrayList<String> getKafkaChannels() {
        return kafkaChannelId;
    }
}
