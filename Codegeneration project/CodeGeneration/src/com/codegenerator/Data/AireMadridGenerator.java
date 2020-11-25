package com.codegenerator.Data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by taray on 03/04/2019.
 */
public class AireMadridGenerator extends DataClass {

    public AireMadridGenerator(String json, String path, String appId) {
        super(json);
        name= "ProducerAireMadrid"+ model.get("id");
        filename = path + "ProducerAireMadrid"+ model.get("id") + ".java";
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
        code += "import java.net.URL; \nimport java.util.HashMap;\n"+
                "import org.json.XML;\n";
        code += "\n\npublic class " + name +" extends ProducerClass{\n" +
                "\n" +
                "    private volatile boolean _run;\n" +
                "    private String url=\"\";\n" +
                "    private String topicKafka=\"\";\n" +
                "    HashMap<String, String> magnitudesCodigos = new HashMap<String, String>();\n" +
                "    HashMap<String, JSONObject> estaciones = new HashMap<String, JSONObject>();\n" +
                "    private String estacion=\"\";\n\n" +
                "\n" +
                "    public " + name +" (List<String> topic){\n" +
                "        this.topic=topic;\n" +
                "        _pause=false;\n"  +
                "        _run=true;\n" +
                "        magnitudesCodigos.put(\"01\", \"Dioxido de Azufre\");\n" +
                "        magnitudesCodigos.put(\"06\", \"Monoxido de Carbono\");\n" +
                "        magnitudesCodigos.put(\"07\", \"Monoxido de Nitrogeno\");\n" +
                "        magnitudesCodigos.put(\"08\", \"Dioxido de Nitrogeno\");\n" +
                "        magnitudesCodigos.put(\"09\", \"Particulas < 2.5 um\");\n" +
                "        magnitudesCodigos.put(\"10\", \"Particulas < 10 um\");\n" +
                "        magnitudesCodigos.put(\"12\", \"Oxidos de Nitrogeno\");\n" +
                "        magnitudesCodigos.put(\"14\", \"Ozono\");\n" +
                "        magnitudesCodigos.put(\"20\", \"Tolueno\");\n" +
                "        magnitudesCodigos.put(\"30\", \"Benceno\");\n" +
                "        magnitudesCodigos.put(\"35\", \"Etilbenceno\");\n" +
                "        magnitudesCodigos.put(\"37\", \"Metaxileno\");\n" +
                "        magnitudesCodigos.put(\"38\", \"Paraxileno\");\n" +
                "        magnitudesCodigos.put(\"39\", \"Ortoxileno\");\n" +
                "        magnitudesCodigos.put(\"42\", \"Hidrocarburos totales(hexano)\");\n" +
                "        magnitudesCodigos.put(\"43\", \"Metano\");\n" +
                "        magnitudesCodigos.put(\"44\", \"Hidrocarburos no metanicos (hexano)\");\n";

        // write url

        if(model.get("auth")=="[]"){
            code +=
                    "        url=\"" + model.get("url") + "\";\n";
        }else {
            JSONArray arrayvaluesAuth = (JSONArray) model.get("auth");
            JSONObject jsonAuth = (JSONObject) arrayvaluesAuth.get(0);
            code +=
                    "        url=\"" + model.get("url") + "\";\n";
        }
        //write params in url
        JSONArray arrayParams= (JSONArray) model.get("params");
        for (Object val : arrayParams) {
            JSONObject aux = (JSONObject) val;
            if(aux.get("value")!=""){
                code += "        estacion = \"" + aux.get("value") + "\";";
            }
        }

        code += "\n" +
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
                "                String dataInput = new Scanner(new URL(\"" + model.get("url")+ "\").openStream(), \"UTF-8\").useDelimiter(\"\\\\A\").next();\n" +
                "\n" +
                "                 while (dataInput != null && _run==true) {\n" +
                "                    String[] lines = dataInput.split(\"\\n\");\n" +
                "                    for (String line: lines) {\n" +
                "                        String[] parsed = line.split(\",\");\n" +
                "                        String estacion = parsed[0] + parsed[1] + parsed[2];\n" +
                "                        String magnitud =  parsed[3];\n" +
                "                        String valor = \"\";\n" +
                "                        int index=0;\n" +
                "\n" +
                "                        for(index=0; index<parsed.length ; index++){\n" +
                "                            if(parsed[index].equals(\"N\")){\n" +
                "                                valor = parsed[index-3];\n" +
                "                                break;\n" +
                "                            }\n" +
                "                        }\n" +
                "                        if(estaciones.get(estacion)==null){\n" +
                "                            JSONObject json = new JSONObject();\n" +
                "                            estaciones.put(estacion, json);\n" +
                "                        }\n" +
                "                        JSONObject jsonValores = estaciones.get(estacion);\n" +
                "                        jsonValores.put(magnitud, valor);\n" +
                "                    }\n" ;

        code += "                    JSONObject valores ;\n" +
                "                    twitter4j.JSONObject info = new twitter4j.JSONObject();\n;\n" +
                "                    info.put(\"nameProducer\", \""+ name+"\");\n" +
                "                    valores = estaciones.get(estacion);\n" ;


        //read the fields selected by the user
        JSONArray arrayvalues= (JSONArray) model.get("values");
        for (Object val : arrayvalues) {
            JSONObject aux = (JSONObject) val;
            if(aux.get("check").equals("true")){
                // code += "                    info.put(\""+ aux.get("name") +"\", " + generatePathJson((String) aux.get("ruta")) + ");\n";
                String nameField= model.get("nameId") + ": " +  aux.get("name");
                //code += this.generatePathJsonXml(name,(String) aux.get("ruta"));
                code +=
                        "                    if(valores.has(\"" + aux.get("ruta") + "\"))\n" +
                        "                    info.put(\"" + nameField + "\", valores.get(\"" +  aux.get("ruta")+ "\"));\n" +
                        "                    else\n" +
                        "                        info.put(\"" + nameField + "\", \"data not found\");\n\n" ;
            }
        }


        code += "                    for(String channel : topic){\n" +
                "                        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());\n" +
                "                        producer.send(rec);\n" +
                "                    }\n" +
                "                    dataInput= null;\n" +
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
