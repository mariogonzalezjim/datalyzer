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
import org.apache.http.impl.client.DefaultHttpClient;

public class ProducerAPI12 extends ProducerClass{

    private volatile boolean _run;
    private String url="";
    private String topicKafka="";

    public ProducerAPI12 (String topic){
        topicKafka=topic;
        _pause=false;
        _run=true;
        url="http://api.openweathermap.org/data/2.5/weather?&APPID=bc204dff9cd2a69b12d7a3bf6cd3878d&q=Madrid,es&units=";
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

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;

                while ((line = rd.readLine()) != null || _run==false) {
                    twitter4j.JSONObject info = new twitter4j.JSONObject();
                    JSONParser parser = new JSONParser();
                    org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(line);
                    org.json.simple.JSONObject jsonChild;
                    info.put("nameProducer", "ProducerAPI12");
                    jsonChild= json;
                    info.put("City Name", jsonChild.get("name"));
                    ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topicKafka, info.toString());
                    producer.send(rec);

                }

                Thread.sleep(10000);
                while (_pause==true && _run==true){
                    //pause thread, 500 ms
                    Thread.sleep(500);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
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