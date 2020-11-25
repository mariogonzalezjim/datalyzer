package com;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import twitter4j.*;
import twitter4j.JSONObject;import twitter4j.conf.ConfigurationBuilder;
import java.util.List;
import java.util.Properties;import java.util.Scanner;
import com.satori.rtm.*;
import com.satori.rtm.model.*;
import java.util.concurrent.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;import org.json.XML;
import java.net.URL;

public class ProducerXml1 extends ProducerClass{

    private volatile boolean _run;
    private String url="";
    private String topicKafka="";

    public ProducerXml1 (List<String> topic){
        this.topic=topic;
        _pause=false;
        _run=true;
        url="http://www.mc30.es/images/xml/DatosTrafico.xml";
    }

    @Override
    public void run() {
        System.out.println("Producer is running");

        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        final org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);

        while(_run) {

            try {

                String xmlTrafico = new Scanner(new URL("http://www.mc30.es/images/xml/DatosTrafico.xml").openStream(), "UTF-8").useDelimiter("\\A").next();

                 while (xmlTrafico != null && _run==true) {
                    twitter4j.JSONObject info = new twitter4j.JSONObject();
                    org.json.JSONObject json =  XML.toJSONObject(xmlTrafico);
                    org.json.JSONObject jsonChild;
                    info.put("nameProducer", "ProducerXml1");
                    jsonChild= json;
                    jsonChild = (org.json.JSONObject) jsonChild.get("DatosTrafico");
                    jsonChild = (org.json.JSONObject)((org.json.JSONArray)jsonChild.get("DatoGlobal")).get(0);
                    info.put("1: Total Vehiculos Tunel", jsonChild.get("VALOR"));
                    jsonChild= json;
                    jsonChild = (org.json.JSONObject) jsonChild.get("DatosTrafico");
                    jsonChild = (org.json.JSONObject)((org.json.JSONArray)jsonChild.get("DatoGlobal")).get(2);
                    info.put("1: Velocidad Media Tunel", jsonChild.get("VALOR"));
                    jsonChild= json;
                    jsonChild = (org.json.JSONObject) jsonChild.get("DatosTrafico");
                    jsonChild = (org.json.JSONObject)((org.json.JSONArray)jsonChild.get("DatoGlobal")).get(3);
                    info.put("1: Velocidad Media M30", jsonChild.get("VALOR"));
                    jsonChild= json;
                    jsonChild = (org.json.JSONObject) jsonChild.get("DatosTrafico");
                    jsonChild = (org.json.JSONObject)((org.json.JSONArray)jsonChild.get("DatoGlobal")).get(4);
                    info.put("1: Fecha actualizacion", jsonChild.get("FECHA"));
                    for(String channel : topic){
                        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());
                        producer.send(rec);
                    }
                    xmlTrafico= null;
                }

                Thread.sleep(500);
                while (_pause==true && _run==true){
                    //pause thread, 500 ms
                    Thread.sleep(500);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void stopThread(){
        _run=false;
    }

    public void pauseThread() {
        _pause=true;
    }

    public void resumeThread() {
        _pause=false;
    }

}