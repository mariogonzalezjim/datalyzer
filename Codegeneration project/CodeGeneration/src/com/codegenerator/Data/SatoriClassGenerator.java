package com.codegenerator.Data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by taray on 27/06/2017.
 */
public class SatoriClassGenerator extends DataClass {

    public SatoriClassGenerator(String json, String path, String app) {
        super(json);
        name= "ProducerSatori"+ model.get("id");
        filename = path + "ProducerSatori"+ model.get("id") + ".java";
        appid=app;
        JSONArray connections= (JSONArray) model.get("connections");
        for (Object val : connections) {
            JSONObject aux = (JSONObject) val;
            kafkaChannelId.add( appid + '.' + aux.get("id")) ;
        }
    }

    @Override
    public void generateCode() {
        String code= getImportsCode();

        code += "\n\n\n" +
                "public class " + name +" extends ProducerClass {\n" +
                "\n" +
                "    static final String endpoint = \"" + model.get("url") + "\";\n" +
                "    static final String appkey = \"" + getValueFromKey ((JSONArray)(model.get("auth")), "appkey" ) +"\";\n" +
                "    static final String channel = \""+ getValueFromKey ((JSONArray)(model.get("auth")), "channel" ) +"\";\n" +
                "    private volatile boolean _run;\n" +
                "\n" +
                "public " + name +"(List<String> topic) {\n" +
                "        this.topic = topic;\n" +
                "        _run=true;\n" +
                "    }\n\n" +
                "@Override\n" +
                "    public void run() {\n" +
                "        System.out.println(\"ProducerReddit is running\");\n" +
                "\n" +
                "        //Configure the ProducerReddit\n" +
                "        Properties configProperties = new Properties();\n" +
                "        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,\"localhost:9092\");\n" +
                "        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,\"org.apache.kafka.common.serialization.ByteArraySerializer\");\n" +
                "        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,\"org.apache.kafka.common.serialization.StringSerializer\");\n" +
                "\n" +
                "        final org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);\n" +
                "\n" +
                "        final RtmClient client = new RtmClientBuilder(endpoint, appkey)\n" +
                "                .setListener(new RtmClientAdapter() {\n" +
                "                    @Override\n" +
                "                    public void onEnterConnected(RtmClient client) {\n" +
                "                        System.out.println(\"Connected to Satori RTM!\");\n" +
                "                    }\n" +
                "                })\n" +
                "                .build();\n" +
                "\n" +
                "        //final CountDownLatch success = new CountDownLatch(1);\n" +
                "\n" +
                "        SubscriptionAdapter listener = new SubscriptionAdapter() {\n" +
                "            @Override\n" +
                "            public void onSubscriptionData(SubscriptionData data) {\n" +
                "                for (AnyJson msg : data.getMessages()) {\n" +
                "                        // System.out.println(\"Got message: \" + msg.toString());\n" +
                "                    try {\n" +
                "                        //read object\n" +
                "                        JSONParser parser = new JSONParser();\n" +
                "                        Object obj = parser.parse(msg.toString());\n" +
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

        //close class
        code +="\n" +
                "\n" +
                "                        if(_pause==false) {\n" +
                "                            for(String channel : topic){\n" +
                "                               ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());\n" +
                "                               producer.send(rec);\n" +
                "                            }\n" +
                "                        }\n" +
                "                    } catch (ParseException e) {\n" +
                "                        e.printStackTrace();\n" +
                "                    } catch (JSONException e) {\n" +
                "                        e.printStackTrace();\n" +
                "                    }" +
                "\n" +
                "                }\n" +
                "                //success.countDown();\n" +
                "            }\n" +
                "        };\n" +
                "\n" +
                "        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);\n" +
                "\n" +
                "        client.start();\n" +
                "\n" +
                "        while(_run){\n" +
                "            try {\n" +
                "                Thread.sleep(2000);\n" +
                "            } catch (InterruptedException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        client.shutdown();\n" +
                "\n" +
                "        producer.close();\n" +
                "    }\n" +
                "\n" +
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
                "}\n";


        //System.out.println(code);
        saveFile(filename , code);
    }



}
