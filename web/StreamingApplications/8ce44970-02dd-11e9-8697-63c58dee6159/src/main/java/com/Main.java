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



        ArrayList<String> listTopic = new ArrayList<String>();
        listTopic.add("8ce44970-02dd-11e9-8697-63c58dee6159.2");
        listTopic.add("8ce44970-02dd-11e9-8697-63c58dee6159.7");
        KafkaManager kafkaManager= new KafkaManager(listTopic);
        kafkaManager.openKafkaChannels();

        /* PRODUCERS and CONSUMERS*/
        listTopic = new ArrayList<String>();
        listTopic.add("8ce44970-02dd-11e9-8697-63c58dee6159.2");
        ConsumerSimple2 threadConsumerSimple2 = new ConsumerSimple2(listTopic);
        threadConsumerSimple2.start();

        listTopic = new ArrayList<String>();
        listTopic.add("8ce44970-02dd-11e9-8697-63c58dee6159.2");
        ProducerTwitter4 threadProducerTwitter4 = new ProducerTwitter4(listTopic);
        threadProducerTwitter4.start();

        listTopic = new ArrayList<String>();
        listTopic.add("8ce44970-02dd-11e9-8697-63c58dee6159.7");
        listTopic.add("8ce44970-02dd-11e9-8697-63c58dee6159.7");
        ProducerXml6 threadProducerXml6 = new ProducerXml6(listTopic);
        threadProducerXml6.start();

        listTopic = new ArrayList<String>();
        listTopic.add("8ce44970-02dd-11e9-8697-63c58dee6159.7");
        ConsumerSimple7 threadConsumerSimple7 = new ConsumerSimple7(listTopic);
        threadConsumerSimple7.start();

        listTopic = new ArrayList<String>();
        listTopic.add("8ce44970-02dd-11e9-8697-63c58dee6159.2");
        ProducerTwitter9 threadProducerTwitter9 = new ProducerTwitter9(listTopic);
        threadProducerTwitter9.start();

        //wait for initialization
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            //Handle exception
        }

        String serverAddress = "127.0.0.1";
        Socket s = null;
        try {

            s = new Socket(serverAddress, 443);
            ConsumerClass.sock=s;
            ConsumerClass.writer = new PrintWriter(ConsumerClass.sock.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //send appid
            ConsumerClass.writer.write("8ce44970-02dd-11e9-8697-63c58dee6159"+"$#$\n");
            ConsumerClass.writer.flush();

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
            threadConsumerSimple2.resumeThread();
            threadProducerTwitter4.resumeThread();
            threadProducerXml6.resumeThread();
            threadConsumerSimple7.resumeThread();
            threadProducerTwitter9.resumeThread();
                }else if(message.equals("pause")){
                    System.out.println("He pausado");
     
               /* RESUME THREADS */
            threadConsumerSimple2.pauseThread();
            threadProducerTwitter4.pauseThread();
            threadProducerXml6.pauseThread();
            threadConsumerSimple7.pauseThread();
            threadProducerTwitter9.pauseThread();
                }

            }
     
           /* CLOSE THREADS */
         threadConsumerSimple2.stopThread();
         threadConsumerSimple2.join();

         threadProducerTwitter4.stopThread();
         threadProducerTwitter4.join();

         threadProducerXml6.stopThread();
         threadProducerXml6.join();

         threadConsumerSimple7.stopThread();
         threadConsumerSimple7.join();

         threadProducerTwitter9.stopThread();
         threadProducerTwitter9.join();

           // close socket
            s.close();
           // close kafkamanager
           // kafkaManager.closeKafkaChannels();
           System.exit(0);

        System.out.print("Fin");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}