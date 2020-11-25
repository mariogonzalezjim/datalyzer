package com;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;


public class ConsumerSimple2 extends ConsumerClass{
    private String groupId;
    private String consumerID="e64a6e70-2580-11e9-93ab-b794c483e4e0-2"; 
    private KafkaConsumer<String,String> kafkaConsumer;
    Map<String, JSONObject> datos = new HashMap<String, JSONObject>();

    public ConsumerSimple2(List<String> topicName){
        this.topic = topicName;
        this.groupId = "0";
        this._pause=true;
        this._run=true;
    }

    public void run() {
        System.out.println("Consumer is running");
        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

        //Figure out where to start processing messages from
        kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
        kafkaConsumer.subscribe(topic);
        
        //Start processing messages
        try {
            ProcessorClass3 processor3 = new ProcessorClass3();
            JSONObject model;
            JSONObject aux;
            while (_run) {
     
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());

                    JSONParser parser = new JSONParser();
                    JSONObject jsonInput = (JSONObject) parser.parse(record.value());


                    datos.put(jsonInput.get("nameProducer").toString(), jsonInput);

                    model= new JSONObject();

                    aux = datos.get("ProducerAireMadrid1");
                    if(aux!=null){
                        model.put("1: Dioxido de Nitrogeno", aux.get("1: Dioxido de Nitrogeno"));
                    }

                    System.out.println(model);
                    if(ConsumerClass.writer!=null && model.values().size()==1){
                        if(model!=null) {
                            writer.write(consumerID + '_' + JSONObject.toJSONString(model) + "$#$");
                            writer.flush();
                        }
                        //Data Processors
                        processor3.processData(model);
                    }


                }

            }
        }catch(WakeupException ex){
            System.out.println("Exception caught " + ex.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        } finally{
            kafkaConsumer.close();
            System.out.println("After closing KafkaConsumer");
        }
    }
    public KafkaConsumer<String,String> getKafkaConsumer(){
        return this.kafkaConsumer;
    }

    public void stopThread(){
        _run=false;
    }

    public void pauseThread() {
        //it is not necessary to pause this thread
    }

    public void resumeThread() {
        //it is not necessary to resume this thread
    }
}
