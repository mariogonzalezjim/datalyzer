package com.codegenerator.Pipeline;

import com.codegenerator.CodeClassGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by taray on 18/03/2019.
 */
public class BasicPipelineGenerator extends PipelineClass {

    String appid;

    public BasicPipelineGenerator(String json, String path, List<CodeClassGenerator> array, String app) {
        super(json, array);
        name= "ConsumerSimple"+ model.get("id");
        filename = path + "ConsumerSimple"+ model.get("id") + ".java";
        kafkaChannelId.add( app + "." + getId()) ;
        appid=app;
    }

    @Override
    public void generateCode() {
        String code = getImportsCode();

        code += "import org.json.simple.JSONObject;\n" +
                "import org.json.simple.parser.JSONParser;\n" +
                "import org.json.simple.parser.ParseException;\n" +
                "public class " + name + " extends ConsumerClass{\n" +
                "    private String groupId;\n" +
                "    private String consumerID=\"" +appid + "-" + model.get("id") + "\"; \n" +
                "    private KafkaConsumer<String,String> kafkaConsumer;\n" +
                "    Map<String, JSONObject> datos = new HashMap<String, JSONObject>();\n" ;
        JSONArray connections = (JSONArray) model.get("connectionsOut");
        for(Object o: connections) {
            JSONObject port = (JSONObject) o;
            String id = port.get("id").toString();
            code +=     "    ProcessorClass" +id +" processor"+id +" = new ProcessorClass"+ id + "();\n" ;
        }
        code +=        "\n" +
                "    public " + name +"(List<String> topicName){\n" +
                "        this.topic = topicName;\n" +
                "        this.groupId = \"0\";\n" +
                "        this._pause=true;\n" +
                "        this._run=true;\n" +
                "    }\n" +
                "\n" +
                "    public void run() {\n" +
                "        System.out.println(\"Consumer is running\");\n" +
                "        Properties configProperties = new Properties();\n" +
                "        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, \"localhost:9092\");\n" +
                "        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, \"org.apache.kafka.common.serialization.ByteArrayDeserializer\");\n" +
                "        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, \"org.apache.kafka.common.serialization.StringDeserializer\");\n" +
                "        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);\n" +
                "        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, \"simple\");\n" +
                "\n";


        code +=
                "        //Figure out where to start processing messages from\n" +
                        "        kafkaConsumer = new KafkaConsumer<String, String>(configProperties);\n" +
                        "        kafkaConsumer.subscribe(topic);\n" +
                        "        \n" +
                        "        //Start processing messages\n" +
                        "        try {\n";

        code +=         "            JSONObject model;\n" +
                "            JSONObject aux;\n" +
                "            JSONParser parser;\n" +
                "            JSONObject jsonInput;\n" +
                "            JSONObject dataOut;\n" +
                "            Map<String, Object> data;\n" +
                "            while (_run) {\n" +
                "     " +
                "\n" +
                "                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);\n" +
                "                for (ConsumerRecord<String, String> record : records) {\n" +
                "                    data = new HashMap<String, Object>();\n" +
                "                    data.put(\"partition\", record.partition());\n" +
                "                    data.put(\"offset\", record.offset());\n" +
                "                    data.put(\"value\", record.value());\n" +
                "\n" +
                "                    parser = new JSONParser();\n" +
                "                    jsonInput = (JSONObject) parser.parse(record.value());\n" +
                "\n" +
                "\n" +
                "                    datos.put(jsonInput.get(\"nameProducer\").toString(), jsonInput);\n" +
                "\n" +
                "                    model= new JSONObject();\n\n" ;

        /* BUILD MODEL */
        // array connections with this pipeline
        int numFieldsModel=0;
        connections = (JSONArray) model.get("connections");
        for(Object o: connections){
            JSONObject port = (JSONObject) o;
            CodeClassGenerator connection = getModelById(port.get("id").toString(), arrayModels);
            if(connection==null) break;
            code += "                    aux = datos.get(\"" + connection.getName() + "\");\n" +
                    "                    if(aux!=null){\n";
            // search the fields selected by the user
            JSONObject aux = (JSONObject) connection.getJSON();
            JSONArray fields = (JSONArray) aux.get("values");
            for(Object o2: fields){
                JSONObject field = (JSONObject) o2;
                if(field.get("check").equals("true")){
                    numFieldsModel++;
                    String name = aux.get("nameId").toString() + ": " + field.get("name").toString();
                    code += "                        dataOut = new JSONObject();\n" +
                            "                        dataOut.put(\"name\", \"" + name + "\");\n" +
                            "                        dataOut.put(\"value\", aux.get(\"" + name + "\"));\n" +
                            "                        model.put(\"data\", JSONObject.toJSONString(dataOut));\n" +
                            "                        sendData(model);\n" +
                            "                        //delete data\n" +
                            "                        datos.remove(\"" + connection.getName() + "\");\n";

                }
            }
            code += "                    }\n";
        }


        code +=
                "\n                    System.out.println(model);\n" +

                "\n" +
                "\n" +
                "                }\n" +
                "\n" +
                "            }\n" +
                "        }catch(WakeupException ex){\n" +
                "            System.out.println(\"Exception caught \" + ex.getMessage());\n" +
                "        } catch (ParseException e) {\n" +
                "            e.printStackTrace();\n" +
                "        } finally{\n" +
                "            kafkaConsumer.close();\n" +
                "            System.out.println(\"After closing KafkaConsumer\");\n" +
                "        }\n" +
                "    }\n" +
                "    public KafkaConsumer<String,String> getKafkaConsumer(){\n" +
                "        return this.kafkaConsumer;\n" +
                "    }\n" +
                "\n" +
                "    public void stopThread(){\n" +
                "        _run=false;\n" +
                "    }\n" +
                "\n" +
                "    public void pauseThread() {\n" +
                "        //it is not necessary to pause this thread\n"  +
                "    }\n" +
                "\n" +
                "    public void resumeThread() {\n" +
                "        //it is not necessary to resume this thread\n"  +
                "    }\n" +
                "    void sendData(JSONObject data){\n" ;
        numFieldsModel=0;
        code += "                        //Data Processors\n" ;
        connections = (JSONArray) model.get("connectionsOut");
        for(Object o: connections) {
            JSONObject port = (JSONObject) o;
            //CodeClassGenerator connection = getModelById(port.get("id").toString(), arrayModels);
            //if(connection==null) break;
            String id = port.get("id").toString();
            code += "                        processor"+id + ".processData(data);\n";

        }
        code +=
                "       if(data!=null) {\n" +
                "            writer.write(consumerID + '_' + JSONObject.toJSONString(data) + \"$#$\");\n" +
                "            writer.flush();\n" +
                "        }\n" +
                "    }\n" +
                "}\n";



        saveFile(filename, code);
    }


}
