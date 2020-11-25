package com.codegenerator.Utils;

import com.codegenerator.CodeClassGenerator;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by taray on 05/10/2017.
 */
public class KafkaManagerGenerator implements CodeClassGenerator {


    String filename;


    public KafkaManagerGenerator ( String path){
        filename = path + "KafkaManager.java";
    }

    @Override
    public void generateCode() {
        String code="package com;\n" +
                "\n" +
                "import java.io.BufferedReader;\n" +
                "import java.io.IOException;\n" +
                "import java.io.InputStreamReader;\n" +
                "import java.util.List;\n" +
                "\n" +
                "/**\n" +
                " * Created by taray on 06/10/2017.\n" +
                " */\n" +
                "public class KafkaManager {\n" +
                "\n" +
                "    List<String> channels;\n" +
                "    String host=\"localhost:2181\";\n" +
                "\n" +
                "    public KafkaManager(List<String> lst){\n" +
                "        channels=lst;\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    void openKafkaChannels(){\n" +
                "\n" +
                "        for (String topic : channels) {\n" +
                "            createTopic(host,topic);\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    void closeKafkaChannels(){\n" +
                "        for (String topic : channels) {\n" +
                "            deleteTopic(host,topic);\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    private void createTopic(String zookeeperConnect, String topicName)  {\n" +
                "\n" +
                "        try\n" +
                "        {\n" +
                "            String command=\"cmd /c  cd C:\\\\kafka_2.10-0.10.2.1 & .\\\\bin\\\\windows\\\\kafka-topics.bat \" +\n" +
                "                    \"--create --zookeeper \"+ host+\" --replication-factor 1 --partitions 1 --topic \" + topicName;\n" +
                "\n" +
                "            Process p=Runtime.getRuntime().exec(command);\n" +
                "            p.waitFor();\n" +
                "            BufferedReader reader=new BufferedReader(\n" +
                "                    new InputStreamReader(p.getInputStream())\n" +
                "            );\n" +
                "            String line;\n" +
                "            while((line = reader.readLine()) != null)\n" +
                "            {\n" +
                "                System.out.println(line);\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "        catch(IOException e1) {e1.printStackTrace();}\n" +
                "        catch(InterruptedException e2) {e2.printStackTrace();}\n" +
                "\n" +
                "        System.out.println(\"Topic \" + topicName + \" created\");\n" +
                "    }\n" +
                "\n" +
                "    private void deleteTopic(String zookeeperConnect, String topicName){\n" +
                "        try\n" +
                "        {\n" +
                "            String command=\"cmd /c  cd C:\\\\kafka_2.10-0.10.2.1 & .\\\\bin\\\\windows\\\\kafka-topics.bat \" +\n" +
                "                    \"--delete --zookeeper \"+ host +\" --topic \" + topicName;\n" +
                "\n" +
                "            Process p=Runtime.getRuntime().exec(command);\n" +
                "            p.waitFor();\n" +
                "            BufferedReader reader=new BufferedReader(\n" +
                "                    new InputStreamReader(p.getInputStream())\n" +
                "            );\n" +
                "            String line;\n" +
                "            while((line = reader.readLine()) != null)\n" +
                "            {\n" +
                "                System.out.println(line);\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "        catch(IOException e1) {e1.printStackTrace();}\n" +
                "        catch(InterruptedException e2) {e2.printStackTrace();}\n" +
                "\n" +
                "        System.out.println(\"Topic \" + topicName + \" deleted\");\n" +
                "\n" +
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
        return "KafkaManager";
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
    public JSONObject getJSON() {
        return null;
    }

    @Override
    public ArrayList<String> getKafkaChannels() {
        return null;
    }
}
