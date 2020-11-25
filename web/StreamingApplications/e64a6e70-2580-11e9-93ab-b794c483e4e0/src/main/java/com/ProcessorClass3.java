package com;

import org.json.simple.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.sql.Timestamp;
import org.json.simple.parser.JSONParser;
import java.text.ParseException;
import twitter4j.*;
import twitter4j.conf.Configuration;
import java.text.DateFormat;
import twitter4j.conf.ConfigurationBuilder;


public class ProcessorClass3 extends ProcessorClass{
    String consumerID="e64a6e70-2580-11e9-93ab-b794c483e4e0-3";
    public  ProcessorClass3 (){
    }

    public JSONObject processData(JSONObject data) {
        String field = (String) data.get("1: Dioxido de Nitrogeno");
        if(field == null){
            //field = ((JSONObject) data.get("data")).get("1: Dioxido de Nitrogeno").toString();
            field =  data.get("data").toString();

            JSONParser parser = new JSONParser();
            try {
                JSONObject json = (JSONObject) parser.parse(field);
                field=null;
                String name = (String) json.get("name");
                if(name.equals("1: Dioxido de Nitrogeno")){
                    field =   json.get("value").toString();
                }
                if(field==null){
                    return null;
                }

            } catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }
        }
        String response = this.processField(field ,  "Usw6RrcjrszXzzhukeVtQbpJt"  ,  "GxzfBb0OkLPXtRGi5n9qIDPB8lct1zUk9mmLIfMu8ae7u2eWPF"  ,  "1135539195919949824-ajOIi3lnspiN9dW3IsLkLzCxpoiVTs"  ,  "UEHfuLHZlkXaeN7ZgFMHG9p8tyXGmlYFr6KAw3HRP8JcS"  ,  "Arturo Soria" );
        if(response==null)
            return null;
        data.put("1: Dioxido de Nitrogeno", response);
        if(data!=null)              sendData(data);
        return data;
    }
    public String processField(String data , String CONSUMER_KEY  , String CONSUMER_SECRET  , String ACCESS_TOKEN  , String ACCESS_TOKEN_SECRET  , String nombreEstacion ){


        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        String msg = "El dioxido de Nitrogeno en la estacion " + nombreEstacion + " es " + data + "  a las " + formattedDate;
        StatusUpdate status;
        Twitter twitter;


        // String CONSUMER_KEY = "Usw6RrcjrszXzzhukeVtQbpJt";
        // String CONSUMER_SECRET = "GxzfBb0OkLPXtRGi5n9qIDPB8lct1zUk9mmLIfMu8ae7u2eWPF";
        // String ACCESS_TOKEN = "1135539195919949824-ajOIi3lnspiN9dW3IsLkLzCxpoiVTs";
        // String ACCESS_TOKEN_SECRET = "UEHfuLHZlkXaeN7ZgFMHG9p8tyXGmlYFr6KAw3HRP8JcS";
        String key = CONSUMER_KEY;
        String secret = CONSUMER_SECRET;
        String token = ACCESS_TOKEN;
        String token_secret = ACCESS_TOKEN_SECRET;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(key).setOAuthConsumerSecret(secret).setOAuthAccessToken(token).setOAuthAccessTokenSecret(token_secret);
        Configuration conf = cb.build();
        try {
            TwitterFactory tf = new TwitterFactory(conf);
            twitter = tf.getInstance();
            twitter.verifyCredentials();
            int id = 0;
            if (id != -1) {
                if (msg.length() > 280) {
                    msg = msg.substring(0, 275) + "...";
                }
                status = new StatusUpdate(msg);
                status.setInReplyToStatusId(id);
            } else {
                if (msg.length() > 280) {
                    msg = msg.substring(0, 275) + "...";
                }
                status = new StatusUpdate(msg);
            }
            twitter.updateStatus(status);
        } catch (TwitterException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return data;
    }
    void sendData(JSONObject model){
        writer.write(consumerID + '_' + JSONObject.toJSONString(model) + "$#$");
        writer.flush();

    }

}