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
import org.apache.http.impl.client.DefaultHttpClient;import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.Socket;


public class ProducerSocket1 extends ProducerClass{

    private volatile boolean _run;
    private int port;
    ServerSocket socket ;
    String message;

    public ProducerSocket1 (List<String> topic) throws IOException {
        this.topic=topic;
        _pause=false;
        _run=true;
        port=3334;
    }

    @Override
    public void run() {
        System.out.println("Producer Socket is running");

        //Configure the ProducerReddit
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        final org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);
        String data = null;
        try {
            socket = new ServerSocket(port);

            while(_run) {
                final Socket client = socket.accept();
                //open thread
                Thread clientThread = new Thread() {
                    public void run() {
                        String clientAddress = client.getInetAddress().getHostAddress();
                        System.out.println("\r\nNew connection from " + clientAddress);
                        String data ;
                        BufferedReader in = null;
                        try {
                            in = new BufferedReader(
                                    new InputStreamReader(client.getInputStream()));

                            while ( (data = in.readLine()) != null ) {
                                //System.out.println("\r\nMessage from " + clientAddress + ": " + data);
                                //read object
                                JSONParser parser = new JSONParser();
                                Object obj = parser.parse(data.toString());
                                org.json.simple.JSONObject json = (org.json.simple.JSONObject) obj;
                                org.json.simple.JSONObject jsonChild;

                                //create a new object that we are going to send
                                twitter4j.JSONObject info = new twitter4j.JSONObject();
                                info.put("nameProducer", "ProducerSocket1");
                                jsonChild= json;
                                info.put("1: Timestamp", jsonChild.get("time"));
                                jsonChild= json;
                                info.put("1: Total Vehiculos Tunel", jsonChild.get("totalCochesTunel"));
                                info.put("timestampProducer",  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date()));
                                for(String channel : topic){
                                    ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());
                                    producer.send(rec);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                };
                clientThread.start();

            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopThread(){
        _run=false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }    }

    public void pauseThread() {
        _pause=true;
    }

    public void resumeThread() {
        _pause=false;
    }

}
