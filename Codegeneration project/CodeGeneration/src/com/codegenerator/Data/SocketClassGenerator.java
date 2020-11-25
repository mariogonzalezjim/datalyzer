package com.codegenerator.Data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by taray on 15/02/2018.
 */
public class SocketClassGenerator extends DataClass {

    public SocketClassGenerator(String json, String path, String appId) {
        super(json);
        name= "ProducerSocket"+ model.get("id");
        filename = path + "ProducerSocket"+ model.get("id") + ".java";
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
        code += "import java.net.ServerSocket;\n" +
                "import java.net.Socket;\n";
        code += "\n\npublic class " + name +" extends ProducerClass{\n" +
                "\n" +
                "    private volatile boolean _run;\n" +
                "    private int port;\n" +
                "    ServerSocket socket ;\n" +
                "    String message;\n" +
                "\n" +
                "    public " + name +" (List<String> topic) throws IOException {\n" +
                "        this.topic=topic;\n" +
                "        _pause=false;\n"  +
                "        _run=true;\n" ;

        //write params
        JSONArray arrayParams= (JSONArray) model.get("params");
        for (Object val : arrayParams) {
            JSONObject aux = (JSONObject) val;
            if(aux.get("value")!=""){
                code += "         " + aux.get("name") + "=" + aux.get("value");
            }
        }

        code += ";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void run() {\n" +
                "        System.out.println(\"Producer Socket is running\");\n" +
                "\n" +
                "        //Configure the ProducerReddit\n" +
                "        Properties configProperties = new Properties();\n" +
                "        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,\"localhost:9092\");\n" +
                "        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,\"org.apache.kafka.common.serialization.ByteArraySerializer\");\n" +
                "        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,\"org.apache.kafka.common.serialization.StringSerializer\");\n" +
                "\n" +
                "        final org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);\n" +
                "        String data = null;\n" +
                "        try {\n" +
                "            socket = new ServerSocket(port);\n" +
                "\n" +
                "            while(_run) {\n" +
                "                final Socket client = socket.accept();\n" +
                "                //open thread\n" +
                "                Thread clientThread = new Thread() {\n" +
                "                    public void run() {\n" +
                "                        String clientAddress = client.getInetAddress().getHostAddress();\n" +
                "                        System.out.println(\"\\r\\nNew connection from \" + clientAddress);\n" +
                "                        String data ;\n" +
                "                        BufferedReader in = null;\n" +
                "                        try {\n" +
                "                            in = new BufferedReader(\n" +
                "                                    new InputStreamReader(client.getInputStream()));\n" +
                "\n" ;


        code += "                            while ( (data = in.readLine()) != null ) {\n" +
                "                                System.out.println(\"\\r\\nMessage from \" + clientAddress + \": \" + data);\n" ;
        code+="                        //read object\n" +
                "                        JSONParser parser = new JSONParser();\n" +
                "                        Object obj = parser.parse(data.toString());\n" +
                "                        org.json.simple.JSONObject json = (org.json.simple.JSONObject) obj;\n" +
                "                        org.json.simple.JSONObject jsonChild;\n" +
                "\n";
        // read the fields selected by the user
        code += "                        //create a new object that we are going to send\n" +
                "                        twitter4j.JSONObject info = new twitter4j.JSONObject();\n"+
                "                        info.put(\"nameProducer\", \""+ name+"\");\n" ;


        //read the fields selected by the user
        JSONArray arrayvalues= (JSONArray) model.get("values");
        for (Object val : arrayvalues) {
            JSONObject aux = (JSONObject) val;
            if(aux.get("check").equals("true")){
                String name= model.get("nameId") + ": " +  aux.get("name");
                code += generatePathJson(name,(String) aux.get("ruta"));
            }
        }
        code+=  "                                for(String channel : topic){\n" +
                "                                   ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());\n" +
                "                                   producer.send(rec);\n" +
                "                                }\n" +
                "                            }\n" +
                "                        } catch (IOException e) {\n" +
                "                            e.printStackTrace();\n" +
                "                        } catch (JSONException e) {\n" +
                "                            e.printStackTrace(); \n" +
                "                         } catch (ParseException e) {\n" +
                "                            e.printStackTrace();\n" +
                "                        }\n" +
                "                    }\n" +
                "                };\n" +
                "                clientThread.start();\n" +
                "\n" +
                "            }\n" +
                "            socket.close();\n" +
                "        } catch (IOException e) {\n" +
                "            e.printStackTrace();\n" +
                "        }" +
                "\n" ;
        code +=   "    }\n" +
                "\n" +
                "\n" +
                "    public void stopThread(){\n" +
                "        _run=false;\n" +
                "       try {\n" +
                "            socket.close();\n" +
                "        } catch (IOException e) {\n" +
                "            e.printStackTrace();\n" +
                "        }" +
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
                "}\n";
        saveFile(filename , code);
    }

}
