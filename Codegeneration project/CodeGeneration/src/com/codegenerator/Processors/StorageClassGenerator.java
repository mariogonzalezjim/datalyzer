package com.codegenerator.Processors;

/**
 * Created by taray on 03/09/2018.
 */
public class StorageClassGenerator extends Processor {

    public StorageClassGenerator (String json, String path) {
        super(json);
        name= "ProcessorClass"+ model.get("id");
        filename = path + "ProcessorClass"+ model.get("id") + ".java";
    }


    @Override
    public void generateCode() {
        String code = getImportsCode();
        code += "import java.sql.Timestamp;\n";
        code += "public class ProcessorClass" +  model.get("id") +" extends ProcessorClass{\n" +
                "\n" +
                "    String logName = \""+ model.get("nameId") + ".txt\";\n" +
                "    String path    = \"./logs/\";\n\n" +
                "    static int inialization = 0;\n" +
                "    public  ProcessorClass" + model.get("id") +" (){\n" +
                "        if (inialization==0) {\n" +
                "            try {\n" +
                "                // create empty file\n" +
                "                BufferedWriter bw = new BufferedWriter(new FileWriter(path + logName));\n" +
                "                bw.write(\"\");\n" +
                "                bw.close();\n" +
                "            } catch (IOException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "            inialization++;\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "    \n" +
                "\n" +
                "    public JSONObject processData(JSONObject data) {\n" +
                "        try {\n" +
                "            data.put(\"timestampFile\",  new SimpleDateFormat(\"dd-MM-yyyy HH:mm:ss.SSS\").format(new Date()));\n" +
               // "            File file = new File(path + logName);\n" +
               // "            file.createNewFile();\n" +
                "            BufferedWriter writer = new BufferedWriter(new FileWriter(path + logName, true));\n" +
                "            writer.write(data.toJSONString());\n" +
                "            writer.newLine();\n" +
                "            writer.flush();\n" +
                "            writer.close();\n" +
                "            data.remove(\"timestampFile\");\n" +
                "            return data;\n" +
                "        } catch (IOException ex) {\n" +
                "            System.out.println(ex);\n" +
                "            System.out.println(\"File could not be created\");\n" +
                "            return data;\n" +
                "        }\n" +
                "    }\n" +
                "}";

        saveFile(filename, code);
    }
}
