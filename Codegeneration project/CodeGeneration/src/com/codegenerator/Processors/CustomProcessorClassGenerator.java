package com.codegenerator.Processors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Created by taray on 29/01/2019.
 */
public class CustomProcessorClassGenerator extends Processor {

    String appid;

    public CustomProcessorClassGenerator (String json, String path, String id) {
        super(json);
        name= "ProcessorClass"+ model.get("id");
        filename = path + "ProcessorClass"+ model.get("id") + ".java";
        appid= id;
    }


    @Override
    public void generateCode() {
        String result="";
        try {
             result = java.net.URLDecoder.decode(model.get("codeProcessor").toString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // not going to happen - value came from JDK's own StandardCharsets
        }


        String code = getImportsCode();
        code += "import java.sql.Timestamp;\n" +
                "import org.json.simple.parser.JSONParser;\n" +
                "import java.text.ParseException;\n" +
                "import twitter4j.*;\n" +
                "import twitter4j.conf.Configuration;\n" +
                "import java.text.DateFormat;\n" +
                "import twitter4j.conf.ConfigurationBuilder;\n\n\n";
        code += "public class ProcessorClass" +  model.get("id") +" extends ProcessorClass{\n" +
                "    String consumerID=\"" + appid + "-" + model.get("id") + "\"; \n" +
                //constructor
                "    public  ProcessorClass" + model.get("id") +" (){\n" +
                "    }\n" +
                "    \n" +


                //code here
                "    public JSONObject processData(JSONObject data) {\n" +
                "        String field = (String) data.get(\"" + model.get("fieldToFilter") + "\");\n" +
                "if(field == null){\n" +
                "            //field = ((JSONObject) data.get(\"data\")).get(\"" + model.get("fieldToFilter")+ "\").toString();\n" +
                "            field =  data.get(\"data\").toString();\n" +
                "\n" +
                "            JSONParser parser = new JSONParser();\n" +
                "            try {\n" +
                "                JSONObject json = (JSONObject) parser.parse(field);\n" +
                "                field=null;\n" +
                "                String name = (String) json.get(\"name\");\n" +
                "                if(name.equals(\"" + model.get("fieldToFilter")+ "\")){\n" +
                "                    field =   json.get(\"value\").toString();\n" +
                "                }\n" +
                "                if(field==null){\n" +
                "                    return null;\n" +
                "                }\n" +
                "\n" +
                "            } catch (org.json.simple.parser.ParseException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "           " +
                "        }\n" +
                "        String response = this.processField(field";
        // parameters
        JSONArray connections = (JSONArray) model.get("parameters");
        int index= 0;
        for(Object o: connections) {
            if(index!=connections.size())
                code += " , " ;
            JSONObject param = (JSONObject) o;
            String value = param.get("value").toString();
            System.out.println("--------------------------------------------------------->");
            System.out.println(param);
            String type = param.get("paramType").toString();
            if(type.equals("String"))
                code +=   " \"" + value  + "\" " ;
            else
                code +=   " " + value  + " " ;

            index++;
        }

        code += ");\n" +
                "        if(response==null)\n" +
                "            return null;\n" ;
        if(model.get("fieldReductor").toString().equals("true"))
            code += "        data = new JSONObject();\n";

        code +=
                "        data.put(\"" + model.get("fieldToFilter") + "\", response);\n" +
                        "        if(data!=null)" +
                "              sendData(data);\n" +
                "        return data;\n" +
                "}\n" +

                "    public String processField(String data" ;
        // parameters
        connections = (JSONArray) model.get("parameters");
        index= 0;
        for(Object o: connections) {
            if(index!=connections.size())
                code += " , " ;
            JSONObject param = (JSONObject) o;
            String name = param.get("name").toString();
            String type = param.get("paramType").toString();
            code += type + " " + name  + " " ;
            index++;
        }

        code +=               ")" + result;
        code += "\n  void sendData(JSONObject model){\n" +
                "        writer.write(consumerID + '_' + JSONObject.toJSONString(model) + \"$#$\");\n" +
                "        writer.flush();\n" +
                "\n" +
                "    }\n" +
                "\n}";

        saveFile(filename, code);
    }

}
