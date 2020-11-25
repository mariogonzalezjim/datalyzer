package com.codegenerator.Data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by taray on 22/06/2017.
 */
public class TwitterClassGenerator extends DataClass {


    public TwitterClassGenerator(String json, String path, String appId) {
        super(json);
        name= "ProducerTwitter"+ model.get("id");
        filename = path + "ProducerTwitter"+ model.get("id") + ".java";
        appid=appId;
        JSONArray connections= (JSONArray) model.get("connections");
        for (Object val : connections) {
            JSONObject aux = (JSONObject) val;
            kafkaChannelId.add( appid + '.' + aux.get("id") );
        }

    }

    @Override
    public void generateCode() {
        String code= getImportsCode();

        code += "\n\npublic class ProducerTwitter"+ model.get("id") +" extends ProducerClass{ \n";
        code += "    private String consumerKey=\""+   getValueFromKey ((JSONArray)(model.get("auth")), "consumerKey" )  +"\";\n" +
                "    private String consumerSecret=\"" + getValueFromKey ((JSONArray)(model.get("auth")), "consumerSecret" ) +"\";\n" +
                "    private String token=\"" + getValueFromKey ((JSONArray)(model.get("auth")), "token" ) + "\";\n" +
                "    private String secret=\"" + getValueFromKey ((JSONArray)(model.get("auth")), "secret" ) + "\";\n" +
                "\n" +
                "\n" +
                "    public ProducerTwitter"+ model.get("id") +"(List<String> topic) {\n" +
                "        this.topic = topic;\n" +
                "        _run=true;\n" +
                "    }" +
                "\n" +
                "    @Override\n" +
                "    public void run() {\n" +
                "        System.out.println(\"Producer is running\");\n" +
                "\n" +
                "        //Configure the Producer\n" +
                "        Properties configProperties = new Properties();\n" +
                "        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,\"localhost:9092\");\n" +
                "        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,\"org.apache.kafka.common.serialization.ByteArraySerializer\");\n" +
                "        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,\"org.apache.kafka.common.serialization.StringSerializer\");" +
                "final org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);\n" +
                "\n" +
                "        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();\n" +
                "        configurationBuilder.setOAuthConsumerKey(consumerKey)\n" +
                "                .setOAuthConsumerSecret(consumerSecret)\n" +
                "                .setOAuthAccessToken(token)\n" +
                "                .setOAuthAccessTokenSecret(secret);\n" +
                "\n" +
                "        TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();" +
                "\n" +
                "        twitterStream.addListener(new StatusListener () {\n" +
                "            public void onException(Exception e) {\n" +
                "\n" +
                "            }\n" +
                "\n" +
                "            public void onStatus(Status status) {\n" +
                "\n" +
                "                try {\n" +
                "                    JSONObject info = new JSONObject();\n" +
                "                    info.put(\"nameProducer\", \"" + name + "\");\n";

        // read fields selected
        JSONArray arrayvalues= (JSONArray) model.get("values");
        for (Object val : arrayvalues) {
            JSONObject aux = (JSONObject) val;
            if(aux.get("check").equals("true")){
                if(aux.get("name").equals("Date")) {
                    code += "                    info.put(\""+model.get("nameId") +": Date\", status.getCreatedAt().toString()); \n";
                }
                else if(aux.get("name").equals("Name User")){
                    code += "                    info.put(\""+model.get("nameId") +": Name User\", status.getUser().getName()); \n";
                }else if(aux.get("name").equals("Tweet")){
                    code += "                    info.put(\""+model.get("nameId") +": Tweet\", status.getText()); \n";
                }else if(aux.get("name").equals("Location")){
                    code += "                    String location=\"Not Found\";\n" +
                            "                    if(status.getPlace()!=null){\n" +
                            "                        location= status.getPlace().getName();\n" +
                            "                    }else if(status.getGeoLocation()!=null){\n" +
                            "                        location=status.getGeoLocation().toString();\n" +
                            "                    }\n" +
                            "                    info.put(\""+model.get("nameId") +": Location\", location);\n";
                }
            }
        }

        code += "                    for(String channel : topic){\n" +
                "                        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());\n" +
                "                        producer.send(rec);\n" +
                "                    }\n" +
                "            } catch (JSONException e) {\n" +
                "                    e.printStackTrace();\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {\n" +
                "\n" +
                "            }\n" +
                "\n" +
                "            public void onTrackLimitationNotice(int i) {\n" +
                "\n" +
                "            }\n" +
                "\n" +
                "            public void onScrubGeo(long l, long l1) {\n" +
                "\n" +
                "            }\n" +
                "\n" +
                "            public void onStallWarning(StallWarning stallWarning) {\n" +
                "\n" +
                "            }\n" +
                "        });\n";
        code += "        FilterQuery tweetFilterQuery = new FilterQuery();\n";

        //read filter array
        arrayvalues= (JSONArray) model.get("params");

        for (Object val : arrayvalues) {
            JSONObject aux = (JSONObject) val;
            if(!aux.get("value").equals("") && aux.get("name").equals("track")) {
                code += "        tweetFilterQuery.track(new String[]{\"" + aux.get("value") + "\"});\n";
            }else if(aux.get("value").equals("") && aux.get("name").equals("track")){
                code += "        tweetFilterQuery.track(new String[]{\"*\"});\n";
            }else if(!aux.get("value").equals("") && aux.get("name").equals("locations")){
                code += "        tweetFilterQuery.locations(new double[][]{new double[]{" + aux.get("value").toString().split("/")[0];
                code += "}, new double[]{" + aux.get("value").toString().split("/")[1] + "}});\n";
            }else if(!aux.get("value").equals("") && aux.get("name").equals("language")){
                code += "        tweetFilterQuery.language(new String[]{\"" + aux.get("value") +"\"});";
            }
        }
        code +="        twitterStream.filter(tweetFilterQuery);\n" +
                "\n" +
                "        int flagPause=0;\n" +
                "        while(_run) {\n" +
                "            try {\n" +
                "                Thread.sleep(2000);\n" +
                "\n" +
                "                if(_pause==true){\n" +
                "                    twitterStream.shutdown();\n" +
                "                    flagPause=1;\n" +
                "                }\n" +
                "\n" +
                "                while (_pause==true && _run==true){\n" +
                "                    //pause thread, 500 ms\n" +
                "                    Thread.sleep(500);\n" +
                "                }\n" +
                "\n" +
                "                if(flagPause==1 && _run==true){\n" +
                "                    twitterStream.filter(tweetFilterQuery);\n" +
                "                    flagPause=0;\n" +
                "                }\n" +
                "\n" +
                "            } catch (InterruptedException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "        twitterStream.shutdown();\n" +
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
                "}";


        saveFile(filename, code);
    }



}
