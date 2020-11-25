package com.codegenerator.Data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by taray on 13/12/2018.
 */
public class XmlClassGenerator extends DataClass  {


    public XmlClassGenerator(String json, String path, String appId) {
        super(json);
        name= "ProducerXml"+ model.get("id");
        filename = path + "ProducerXml"+ model.get("id") + ".java";
        appid=appId;
        JSONArray connections= (JSONArray) model.get("connections");
        for (Object val : connections) {
            JSONObject aux = (JSONObject) val;
            kafkaChannelId.add(appid + '.' + aux.get("id")) ;
        }
    }

    @Override
    public void generateCode() {
        String code =getImportsCode();
        code += "\nimport java.net.URL;import org.json.XML;\n";
        code += "\n\npublic class " + name +" extends ProducerClass{\n" +
                "\n" +
                "    private volatile boolean _run;\n" +
                "    private String url=\"\";\n" +
                "    private String topicKafka=\"\";\n" +
                "\n" +
                "    public " + name +" (List<String> topic){\n" +
                "        this.topic=topic;\n" +
                "        _pause=false;\n"  +
                "        _run=true;\n" ;

        // write url + auth
        if(model.get("auth")=="[]"){
            code +=
                    "        url=\"" + model.get("url") + "\";\n";
        }else {
            JSONArray arrayvaluesAuth = (JSONArray) model.get("auth");
            JSONObject jsonAuth = (JSONObject) arrayvaluesAuth.get(0);
            code +=
                    "        url=\"" + model.get("url") ;
        }
        System.out.println(model);
        //write params in url
        JSONArray arrayParams= (JSONArray) model.get("params");
        for (Object val : arrayParams) {
            JSONObject aux = (JSONObject) val;
            if(aux.get("value")!=""){
                code += "&" + aux.get("name") + "=" + aux.get("value");
            }
        }

        code += "\";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void run() {\n" +
                "        System.out.println(\"Producer is running\");\n" +
                "\n" +
                "        //Configure the Producer\n" +
                "        Properties configProperties = new Properties();\n" +
                "        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,\"localhost:9092\");\n" +
                "        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,\"org.apache.kafka.common.serialization.ByteArraySerializer\");\n" +
                "        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,\"org.apache.kafka.common.serialization.StringSerializer\");\n" +
                "\n" +
                "        final org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);\n" +
                "\n" +
                "        while(_run) {\n" +
                "\n" +
                "            try {\n" +
                "\n" +
                "                String xmlTrafico = new Scanner(new URL(\"" + model.get("url")+ "\").openStream(), \"UTF-8\").useDelimiter(\"\\\\A\").next();\n" +
                "\n" +
                "                 while (xmlTrafico != null && _run==true) {\n" +
                "                    twitter4j.JSONObject info = new twitter4j.JSONObject();\n" +
                "                    org.json.JSONObject json =  XML.toJSONObject(xmlTrafico);\n" +
                "                    org.json.JSONObject jsonChild;\n" +
                "                    info.put(\"nameProducer\", \""+ name+"\");\n";

        //read the fields selected by the user
        JSONArray arrayvalues= (JSONArray) model.get("values");
        for (Object val : arrayvalues) {
            JSONObject aux = (JSONObject) val;
            if(aux.get("check").equals("true")){
                // code += "                    info.put(\""+ aux.get("name") +"\", " + generatePathJson((String) aux.get("ruta")) + ");\n";
                String name= model.get("nameId") + ": " +  aux.get("name");
                code += this.generatePathJsonXml(name,(String) aux.get("ruta"));
            }
        }


        code += "                    for(String channel : topic){\n" +
                "                        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());\n" +
                "                        producer.send(rec);\n" +
                "                    }\n" +
                "                    xmlTrafico= null;\n" +
                "                }\n" +
                "\n" +
                "                Thread.sleep("+ model.get("rate") + ");\n" +
                "                while (_pause==true && _run==true){\n" +
                "                    //pause thread, 500 ms\n" +
                "                    Thread.sleep(500);\n" +
                "                }\n" +
                "\n" +
                "            } catch (IOException e) {\n" +
                "                e.printStackTrace();\n" +

                "            } catch (InterruptedException e) {\n" +
                "                e.printStackTrace();\n" +
                "            } catch (JSONException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "            catch (org.json.JSONException e){\n" +
                "                System.out.println(e);\n" +
                "            }\n" +
                "\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public void stopThread(){\n" +
                "        _run=false;\n" +
                "    }\n" +
                "\n" +
                "    public void pauseThread() {\n" +
                "        _pause=true;\n" +
                "    }\n" +
                "\n" +
                "    public void resumeThread() {\n" +
                "        _pause=false;\n" +
                "    }\n" +
                "\n" +
                "}";


        saveFile(filename , code);
    }
}
