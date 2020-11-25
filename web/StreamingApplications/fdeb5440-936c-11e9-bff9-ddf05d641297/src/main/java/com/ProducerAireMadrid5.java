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
import org.apache.http.impl.client.DefaultHttpClient;import java.net.URL; 
import java.util.HashMap;
import org.json.XML;


public class ProducerAireMadrid5 extends ProducerClass{

    private volatile boolean _run;
    private String url="";
    private String topicKafka="";
    HashMap<String, String> magnitudesCodigos = new HashMap<String, String>();
    HashMap<String, JSONObject> estaciones = new HashMap<String, JSONObject>();
    private String estacion="";


    public ProducerAireMadrid5 (List<String> topic){
        this.topic=topic;
        _pause=false;
        _run=true;
        magnitudesCodigos.put("01", "Dioxido de Azufre");
        magnitudesCodigos.put("06", "Monoxido de Carbono");
        magnitudesCodigos.put("07", "Monoxido de Nitrogeno");
        magnitudesCodigos.put("08", "Dioxido de Nitrogeno");
        magnitudesCodigos.put("09", "Particulas < 2.5 um");
        magnitudesCodigos.put("10", "Particulas < 10 um");
        magnitudesCodigos.put("12", "Oxidos de Nitrogeno");
        magnitudesCodigos.put("14", "Ozono");
        magnitudesCodigos.put("20", "Tolueno");
        magnitudesCodigos.put("30", "Benceno");
        magnitudesCodigos.put("35", "Etilbenceno");
        magnitudesCodigos.put("37", "Metaxileno");
        magnitudesCodigos.put("38", "Paraxileno");
        magnitudesCodigos.put("39", "Ortoxileno");
        magnitudesCodigos.put("42", "Hidrocarburos totales(hexano)");
        magnitudesCodigos.put("43", "Metano");
        magnitudesCodigos.put("44", "Hidrocarburos no metanicos (hexano)");
        url="http://www.mambiente.madrid.es/opendata/horario.txt";
        estacion = "28079016";
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

                String dataInput = new Scanner(new URL("http://www.mambiente.madrid.es/opendata/horario.txt").openStream(), "UTF-8").useDelimiter("\\A").next();

                 while (dataInput != null && _run==true) {
                    String[] lines = dataInput.split("\n");
                    for (String line: lines) {
                        String[] parsed = line.split(",");
                        String estacion = parsed[0] + parsed[1] + parsed[2];
                        String magnitud =  parsed[3];
                        String valor = "";
                        int index=0;

                        for(index=0; index<parsed.length ; index++){
                            if(parsed[index].equals("N")){
                                valor = parsed[index-3];
                                break;
                            }
                        }
                        if(estaciones.get(estacion)==null){
                            JSONObject json = new JSONObject();
                            estaciones.put(estacion, json);
                        }
                        JSONObject jsonValores = estaciones.get(estacion);
                        jsonValores.put(magnitud, valor);
                    }
                    JSONObject valores ;
                    twitter4j.JSONObject info = new twitter4j.JSONObject();
;
                    info.put("nameProducer", "ProducerAireMadrid5");
                    valores = estaciones.get(estacion);
                    if(valores.has("08"))
                    info.put("5: Dioxido de Nitrogeno", valores.get("08"));
                    else
                        info.put("5: Dioxido de Nitrogeno", "data not found");

                    for(String channel : topic){
                        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(channel, info.toString());
                        producer.send(rec);
                    }
                    dataInput= null;
                }

                Thread.sleep(600);
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