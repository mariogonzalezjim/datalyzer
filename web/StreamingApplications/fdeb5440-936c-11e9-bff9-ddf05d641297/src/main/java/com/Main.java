package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.*;

public class Main {
    
    public static void main(String[] argv) throws Exception {

        String serverAddress = "127.0.0.1";
        Socket s = null;
        try {

            s = new Socket(serverAddress, 443);
            ConsumerClass.sock=s;
            ConsumerClass.writer = new PrintWriter(ConsumerClass.sock.getOutputStream(), true);
            ProcessorClass.writer = ConsumerClass.writer;
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //send appid
            ConsumerClass.writer.write("fdeb5440-936c-11e9-bff9-ddf05d641297"+"$#$\n");
            ConsumerClass.writer.flush();


        ArrayList<String> listTopic = new ArrayList<String>();
        listTopic.add("fdeb5440-936c-11e9-bff9-ddf05d641297.2");
        listTopic.add("fdeb5440-936c-11e9-bff9-ddf05d641297.7");
        KafkaManager kafkaManager= new KafkaManager(listTopic);
        kafkaManager.openKafkaChannels();

        /* PRODUCERS and CONSUMERS*/
        listTopic = new ArrayList<String>();
        listTopic.add("fdeb5440-936c-11e9-bff9-ddf05d641297.2");
        ProducerXml1 threadProducerXml1 = new ProducerXml1(listTopic);
        threadProducerXml1.start();

        listTopic = new ArrayList<String>();
        listTopic.add("fdeb5440-936c-11e9-bff9-ddf05d641297.2");
        ConsumerSimple2 threadConsumerSimple2 = new ConsumerSimple2(listTopic);
        threadConsumerSimple2.start();

        listTopic = new ArrayList<String>();
        listTopic.add("fdeb5440-936c-11e9-bff9-ddf05d641297.2");
        ProducerAireMadrid5 threadProducerAireMadrid5 = new ProducerAireMadrid5(listTopic);
        threadProducerAireMadrid5.start();

        listTopic = new ArrayList<String>();
        listTopic.add("fdeb5440-936c-11e9-bff9-ddf05d641297.7");
        ProducerXml6 threadProducerXml6 = new ProducerXml6(listTopic);
        threadProducerXml6.start();

        listTopic = new ArrayList<String>();
        listTopic.add("fdeb5440-936c-11e9-bff9-ddf05d641297.7");
        ConsumerSimple7 threadConsumerSimple7 = new ConsumerSimple7(listTopic);
        threadConsumerSimple7.start();

        //wait for initialization
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            //Handle exception
        }

            //receive data
            int flagExit=0;
            while(flagExit==0) {

                while (input.ready() == false && s.isConnected() == true) {
                }

                String message= input.readLine();

                if(message.equals("exit")){
                    flagExit=1;
                }else if(message.equals("resume")){
                    System.out.println("He renaudado");
     
               /* RESUME THREADS */
            threadProducerXml1.resumeThread();
            threadConsumerSimple2.resumeThread();
            threadProducerAireMadrid5.resumeThread();
            threadProducerXml6.resumeThread();
            threadConsumerSimple7.resumeThread();
                }else if(message.equals("pause")){
                    System.out.println("He pausado");
     
               /* RESUME THREADS */
            threadProducerXml1.pauseThread();
            threadConsumerSimple2.pauseThread();
            threadProducerAireMadrid5.pauseThread();
            threadProducerXml6.pauseThread();
            threadConsumerSimple7.pauseThread();
                }

            }
     
           /* CLOSE THREADS */
         threadProducerXml1.stopThread();
         threadProducerXml1.join();

         threadConsumerSimple2.stopThread();
         threadConsumerSimple2.join();

         threadProducerAireMadrid5.stopThread();
         threadProducerAireMadrid5.join();

         threadProducerXml6.stopThread();
         threadProducerXml6.join();

         threadConsumerSimple7.stopThread();
         threadConsumerSimple7.join();

           // close socket
            s.close();
           // close kafkamanager
           // kafkaManager.closeKafkaChannels();

        System.out.print("Fin");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}