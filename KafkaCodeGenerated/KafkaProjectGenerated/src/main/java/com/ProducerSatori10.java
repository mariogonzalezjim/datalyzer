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


public class ProducerSatori10 extends ProducerClass {

    static final String endpoint = "wss://open-data.api.satori.com";
    static final String appkey = "0cdB10bB22DEd81f54bdFD0773E61031";
    static final String channel = "bitcoin-transactions";
    private volatile boolean _run;

public ProducerSatori10(List<String> topic) {
        this.topic = topic;
        _run=true;
    }

@Override
    public void run() {
        System.out.println("ProducerReddit is running");

        //Configure the ProducerReddit
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        final org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);

        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                    }
                })
                .build();

        //final CountDownLatch success = new CountDownLatch(1);

        SubscriptionAdapter listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for (AnyJson msg : data.getMessages()) {
                        // System.out.println("Got message: " + msg.toString());
                    try {
                        //read object
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(msg.toString());
                        org.json.simple.JSONObject json = (org.json.simple.JSONObject) obj;
                        org.json.simple.JSONObject jsonChild;

                        //create a new object that we are going to send
                        twitter4j.JSONObject info = new twitter4j.JSONObject();
                        info.put("nameProducer", "ProducerSatori10");
                    jsonChild= json;
                    jsonChild = (org.json.simple.JSONObject) jsonChild.get("x");
                    jsonChild = (org.json.simple.JSONObject)((org.json.simple.JSONArray)jsonChild.get("out")).get(0);
                    info.put("2: Value transaction (Satoshi)", jsonChild.get("value"));


                        if(_pause==false) {
                            for(String channel : topic){
                               ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());
                               producer.send(rec);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //success.countDown();
            }
        };

        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);

        client.start();

        while(_run){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        client.shutdown();

        producer.close();
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
