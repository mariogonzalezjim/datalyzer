package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by taray on 06/10/2017.
 */
public class KafkaManager {

    List<String> channels;
    String host="localhost:2181";

    public KafkaManager(List<String> lst){
        channels=lst;

    }

    void openKafkaChannels(){

        for (String topic : channels) {
            createTopic(host,topic);
        }

    }

    void closeKafkaChannels(){
        for (String topic : channels) {
            deleteTopic(host,topic);
        }

    }


    private void createTopic(String zookeeperConnect, String topicName)  {

        try
        {
            String command="cmd /c  cd C:\\kafka_2.10-0.10.2.1 & .\\bin\\windows\\kafka-topics.bat " +
                    "--create --zookeeper "+ host+" --replication-factor 1 --partitions 1 --topic " + topicName;

            Process p=Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader=new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );
            String line;
            while((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }

        }
        catch(IOException e1) {e1.printStackTrace();}
        catch(InterruptedException e2) {e2.printStackTrace();}

        System.out.println("Topic " + topicName + " created");
    }

    private void deleteTopic(String zookeeperConnect, String topicName){
        try
        {
            String command="cmd /c  cd C:\\kafka_2.10-0.10.2.1 & .\\bin\\windows\\kafka-topics.bat " +
                    "--delete --zookeeper "+ host +" --topic " + topicName;

            Process p=Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader=new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );
            String line;
            while((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }

        }
        catch(IOException e1) {e1.printStackTrace();}
        catch(InterruptedException e2) {e2.printStackTrace();}

        System.out.println("Topic " + topicName + " deleted");

    }

}