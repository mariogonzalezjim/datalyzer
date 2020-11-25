package com.codegenerator.Data;

/**
 * Created by taray on 29/06/2017.
 */
public class ProducerClassGenerator extends DataClass {

    public ProducerClassGenerator(String json, String path) {
        super(null);
        name= "ProducerClass";
        filename = path + "ProducerClass.java";
    }

    @Override
    public void generateCode() {

        String code = "package com;\n" +
                "\n" +
                "import java.util.List;\n\n" +
                " \n" +
                "public abstract class ProducerClass extends Thread {\n" +
                "\n" +
                "     volatile boolean _run;\n" +
                "     volatile boolean _pause;\n" +
                "     List<String> topic;\n\n" +
                "     abstract public void pauseThread();\n" +
                "\n" +
                "     abstract public void resumeThread();\n" +
                "\n" +
                "\n}";

        saveFile(filename, code);
    }


}
