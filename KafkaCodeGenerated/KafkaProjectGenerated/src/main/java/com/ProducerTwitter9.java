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

public class ProducerTwitter9 extends ProducerClass{ 
    private String consumerKey="KdeuzUGWHSvoww4zC1vW23nHP";
    private String consumerSecret="I6YLqS9rG0uwBiY5TA4Ip0fQucwx8QN8GsvtKeZ4o5N5NYxydo";
    private String token="398781676-3O1kF5AJee0K0qsiOWhp0lCZaoSqcNq2XU0UV44Y";
    private String secret="Y9ltlmw0G2u1T5HYJT3d0EYagjaSCLIwXoAIBkOeQ1D2I";


    public ProducerTwitter9(List<String> topic) {
        this.topic = topic;
        _run=true;
    }
    @Override
    public void run() {
        System.out.println("Producer is running");

        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");final org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(secret);

        TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
        twitterStream.addListener(new StatusListener () {
            public void onException(Exception e) {

            }

            public void onStatus(Status status) {

                try {
                    JSONObject info = new JSONObject();
                    info.put("nameProducer", "ProducerTwitter9");
                    info.put("1: Tweet", status.getText()); 
                    for(String channel : topic){
                        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());
                        producer.send(rec);
                    }
            } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            public void onTrackLimitationNotice(int i) {

            }

            public void onScrubGeo(long l, long l1) {

            }

            public void onStallWarning(StallWarning stallWarning) {

            }
        });
        FilterQuery tweetFilterQuery = new FilterQuery();
        tweetFilterQuery.track(new String[]{"madrid"});
        twitterStream.filter(tweetFilterQuery);

        int flagPause=0;
        while(_run) {
            try {
                Thread.sleep(2000);

                if(_pause==true){
                    twitterStream.shutdown();
                    flagPause=1;
                }

                while (_pause==true && _run==true){
                    //pause thread, 500 ms
                    Thread.sleep(500);
                }

                if(flagPause==1 && _run==true){
                    twitterStream.filter(tweetFilterQuery);
                    flagPause=0;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        twitterStream.shutdown();
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