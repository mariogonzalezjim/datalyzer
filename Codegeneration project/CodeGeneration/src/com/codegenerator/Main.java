package com.codegenerator;

import com.codegenerator.Data.*;
import com.codegenerator.Pipeline.BasicPipelineGenerator;
import com.codegenerator.Pipeline.ConsumerClassGenerator;
import com.codegenerator.Pipeline.JoinSimpleGenerator;
import com.codegenerator.Processors.CustomProcessorClassGenerator;
import com.codegenerator.Processors.ProcessorClassGenerator;
import com.codegenerator.Processors.StorageClassGenerator;
import com.codegenerator.Utils.KafkaManagerGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Main {


    static String path="C:\\Users\\taray\\Desktop\\KafkaProjectGenerated\\src\\main\\java\\com\\";
    static String appid;

    public static void main(String[] args) {

        //  args[0] is the project path
        //  args[1] is the appid of the project
        System.out.println(System.getProperty("os.name"));
        String OS = System.getProperty("os.name").toLowerCase();
        String filename = "";
        if (isWindows(OS)) {
            //old
            //String filename="C:\\Users\\taray\\Documents\\keystone\\datalyzer\\files\\";
            //new with args[0]
             filename= args[0] + "\\files\\";

            if(args[0]==null)
                return;
            else
                filename += args[1] + ".txt";

            //old
            //path = "C:\\Users\\taray\\Documents\\keystone\\datalyzer\\StreamingApplications\\"+ args[0] +"\\src\\main\\java\\com\\";
            //path = "C:\\Users\\taray\\Documents\\keystone\\datalyzer\\StreamingApplications\\"+ "a9f6b070-b11b-11e8-b850-0bde4e1af2be" +"\\src\\main\\java\\com\\";

            //new with project path
            path = args[0] +  "\\StreamingApplications\\"+ args[1] +"\\src\\main\\java\\com\\";
        }else{
             filename= args[0] + "/files/";

            if(args[0]==null)
                return;
            else
                filename += args[1] + ".txt";

            path = args[0] +  "/StreamingApplications/"+ args[1] +"/src/main/java/com/";
        }




        //read config file


        BufferedReader br = null;
        FileReader fr = null;
        String model="";
        List<CodeClassGenerator> arrayModels= new ArrayList<>();
        List<String> kafkaChannels= new ArrayList<>();

        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String sCurrentLine="";

            while ((sCurrentLine = br.readLine()) != null) {
                    model += sCurrentLine;
            }

            //parse file with xml
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //Document doc = dBuilder.parse(filename);
            Document doc = dBuilder.parse((new InputSource(new StringReader(model))));

            System.out.println("Root element : " + doc.getDocumentElement().getNodeName());
            appid= doc.getDocumentElement().getAttribute("appid");

            NodeList nList = doc.getElementsByTagName("Data");

            System.out.println("----------------------------\n");


            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String type = eElement.getElementsByTagName("type").item(0).getTextContent();
                    System.out.println('-' + type + "-");
                    if(type.matches("Twitter")){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        TwitterClassGenerator data = new TwitterClassGenerator(json, path, appid);
                        arrayModels.add(data);
                    }else if(type.matches("Satori")){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        SatoriClassGenerator data = new SatoriClassGenerator(json, path, appid);
                        arrayModels.add(data);
                    }else if(type.matches("APIRest")){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        APIRestClassGenerator data = new APIRestClassGenerator(json, path, appid);
                        arrayModels.add(data);
                    }else if(type.matches("Socket")){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        SocketClassGenerator data = new SocketClassGenerator(json, path, appid);
                        arrayModels.add(data);
                    }else if(type.matches("AireMadrid")){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        AireMadridGenerator data = new AireMadridGenerator(json, path, appid);
                        arrayModels.add(data);
                    }else if(type.matches("Storage") ){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        StorageClassGenerator data = new StorageClassGenerator(json, path);
                        //arrayModels.add(data);
                        data.generateCode();
                    }
                    else if(type.matches("CustomProcessor") ){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        CustomProcessorClassGenerator data = new CustomProcessorClassGenerator(json, path, appid);
                        //arrayModels.add(data);
                        data.generateCode();
                    }else if(type.matches("Xml") ){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        XmlClassGenerator data = new XmlClassGenerator(json, path, appid);
                        arrayModels.add(data);
                    }
                    else if(type.matches("PipelineSimple")){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        JoinSimpleGenerator data = new JoinSimpleGenerator(json, path, arrayModels, appid);
                        for(String chnl : data.getKafkaChannels()){
                            kafkaChannels.add(chnl);
                        }

                        arrayModels.add(data);
                    }
                    else if(type.matches("PipelineBasic")){
                        String json= eElement.getElementsByTagName("json").item(0).getTextContent();
                        BasicPipelineGenerator data = new BasicPipelineGenerator(json, path, arrayModels, appid);
                        for(String chnl : data.getKafkaChannels()){
                            kafkaChannels.add(chnl);
                        }

                        arrayModels.add(data);
                    }


                }
            } // end for


            //generate code
            for (CodeClassGenerator item : arrayModels) {
                item.generateCode();
            }

            // utils
            KafkaManagerGenerator kafkaManager = new KafkaManagerGenerator(path);
            kafkaManager.generateCode();

            //generate main
            MainGenerator mainGenerator = new MainGenerator(arrayModels, path, kafkaChannels, appid);
            mainGenerator.generateCode();

            //abstracts class
            ProducerClassGenerator producerClassGenerator = new ProducerClassGenerator(null, path);
            producerClassGenerator.generateCode();
            ConsumerClassGenerator consumerClassGenerator = new ConsumerClassGenerator(null, path);
            consumerClassGenerator.generateCode();
            ProcessorClassGenerator processorClassGenerator = new ProcessorClassGenerator( path);
            processorClassGenerator.generateCode();



        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


        System.out.println("Fin");

    }


    public static boolean isWindows(String OS) {

        return (OS.indexOf("win") >= 0);

    }



}
