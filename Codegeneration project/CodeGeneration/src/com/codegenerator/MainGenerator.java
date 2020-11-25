package com.codegenerator;

import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taray on 26/06/2017.
 */
public class MainGenerator implements CodeClassGenerator {


    List<CodeClassGenerator> arrayModels;
    String filename;
    List<String> kafkaChannels;
    String appid="";

    public MainGenerator (List<CodeClassGenerator> list , String path, List<String> kafkaList, String app){
        arrayModels = list;
        filename = path + "Main.java";
        kafkaChannels=kafkaList;
        appid=app;
    }

    @Override
    public void generateCode() {

        String code = "package com;\n\n" +
                "import java.io.BufferedReader;\n" +
                "import java.io.IOException;\n" +
                "import java.io.InputStreamReader;\n" +
                "import java.net.Socket;\n" +
                "import java.io.PrintWriter;\n" +
                "import java.util.ArrayList;\n"+
                "import com.*;\n" +
                "\n" +
                "public class Main {\n" +
                "    \n" +
                "    public static void main(String[] argv) throws Exception {\n" +
                "\n" ;

        code +=
                        "        String serverAddress = \"127.0.0.1\";\n" +
                        "        Socket s = null;\n" +
                        "        try {\n" +
                        "\n" +
                        "            s = new Socket(serverAddress, 443);\n" +
                        "            ConsumerClass.sock=s;\n" +
                        "            ConsumerClass.writer = new PrintWriter(ConsumerClass.sock.getOutputStream(), true);\n" +
                        "            ProcessorClass.writer = ConsumerClass.writer;\n" +
                        "            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));\n" +
                        "            //send appid\n" +
                        "            ConsumerClass.writer.write(\"" + appid +"\"+\"$#$\\n\");\n" +
                        "            ConsumerClass.writer.flush();\n" ;



                         /* KAFKA MANAGER */
        code += "\n\n        ArrayList<String> listTopic = new ArrayList<String>();\n";
        for (String channel : kafkaChannels) {
            code += "        listTopic.add(\""+ channel + "\");\n";
        }

        code += "        KafkaManager kafkaManager= new KafkaManager(listTopic);\n" +
                "        kafkaManager.openKafkaChannels();\n\n";


         /* PRODUCERS (DATA) AND CONSUMERS (PIPELINE)*/
        code+=      "        /* PRODUCERS and CONSUMERS*/\n";
        for (Object o : arrayModels){
            CodeClassGenerator producer = (CodeClassGenerator) o;
            code += "        " + "listTopic = new ArrayList<String>();\n";
            for(String chnl : producer.getKafkaChannels()){
                code += "        " + "listTopic.add(\""+ chnl +"\");\n";
            }
            code += "        " + producer.getName() + " thread" + producer.getName() + " = new " + producer.getName()+"(listTopic);\n";
            code += "        " + "thread" + producer.getName() + ".start();\n\n";

        }


        /* OPEN SOCKET TO COMUNNICATE WITH THE SERVER */
        code+=  "        //wait for initialization\n" +
                "        try {\n" +
                "            Thread.sleep(1000);\n" +
                "        } catch (InterruptedException ie) {\n" +
                "            //Handle exception\n" +
                "        }\n\n" ;



        code+=
                "            //receive data\n" +
                        "            int flagExit=0;\n" +
                        "            while(flagExit==0) {\n" +
                        "\n" +
                        "                while (input.ready() == false && s.isConnected() == true) {\n" +
                        "                }\n" +
                        "\n" +
                        "                String message= input.readLine();\n" +
                        "\n" +
                        "                if(message.equals(\"exit\")){\n" +
                        "                    flagExit=1;\n" +
                        "                }else if(message.equals(\"resume\")){\n" +
                        "                    System.out.println(\"He renaudado\");\n" ;
        //resume threads
        code+="     \n               /* RESUME THREADS */\n";
        for (Object o : arrayModels) {
            CodeClassGenerator producer = (CodeClassGenerator) o;
            code +="            "+  "thread" + producer.getName() +".resumeThread();\n";
        }
        code+=
                "                }else if(message.equals(\"pause\")){\n" +
                        "                    System.out.println(\"He pausado\");\n" ;
        //pause threads
        code+="     \n               /* RESUME THREADS */\n";
        for (Object o : arrayModels) {
            CodeClassGenerator producer = (CodeClassGenerator) o;
            code +="            "+  "thread" + producer.getName() +".pauseThread();\n";
        }
        code+=
                "                }\n" +
                        "\n" +
                        "            }\n" ;


        //close threads
        code+="     \n           /* CLOSE THREADS */\n";
        for (Object o : arrayModels) {
            CodeClassGenerator producer = (CodeClassGenerator) o;
            code +="         "+  "thread" + producer.getName() +".stopThread();\n";
            code +="         "+  "thread" + producer.getName() +".join();\n\n";
        }


        //close socket
        code += "           // close socket\n" +
                "            s.close();\n";

        //close kafkamanager
        code += "           // close kafkamanager\n" +
                "           // kafkaManager.closeKafkaChannels();\n";

        // close class
        code += "\n        System.out.print(\"Fin\");\n" +
                "\n" +
                "        } catch (IOException e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";


        saveFile(getFilename(), code);
    }

    @Override
    public void saveFile(String filename, String code) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(code);  //Replace with the string
            //you are trying to write
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getName() {
        return "Main";
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public JSONObject getJSON(){
        return null;
    }

    @Override
    public ArrayList<String> getKafkaChannels() {
        return null;
    }
}
