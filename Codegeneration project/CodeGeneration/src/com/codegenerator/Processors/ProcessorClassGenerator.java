package com.codegenerator.Processors;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by taray on 31/08/2018.
 */
public class ProcessorClassGenerator  {

    String name;
    String filename;

    public ProcessorClassGenerator ( String path){

        name= "ProcessorClass";
        filename = path + "ProcessorClass.java";

    }


    public void generateCode() {

        String code = "package com;\n" +
                "\n" +
                "import java.io.PrintWriter;\n" +
                "import org.json.simple.JSONObject;\n\n\n" +
                "public abstract class ProcessorClass {\n" +
                "\n" +
                "   static PrintWriter writer=null;\n" +
                "\n" +
                "\n" +
                "   abstract public JSONObject processData(JSONObject data);\n" +
                "\n" +
                "}" ;

        saveFile(filename, code);
    }

    public void saveFile(String filename, String code) {
        // write file on disk
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(code);  //Replace with the string
            //you are trying to write
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
