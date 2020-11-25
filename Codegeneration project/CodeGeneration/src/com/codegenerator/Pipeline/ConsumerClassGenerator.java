package com.codegenerator.Pipeline;

/**
 * Created by taray on 03/07/2017.
 */
public class ConsumerClassGenerator extends PipelineClass {

    public ConsumerClassGenerator(String json, String path) {
        super(null, null);
        name= "ConsumerClass";
        filename = path + "ConsumerClass.java";
    }

    @Override
    public void generateCode() {

        String code = "package com;\n" +
                "\n" +
                "\n" +
                "\n" +
                "import java.io.PrintWriter;\n" +
                "import java.net.Socket;\n" +
                "import java.util.List;\n\n" +
                "public abstract class ConsumerClass extends Thread {\n" +
                "\n" +
                "     volatile boolean _run;\n" +
                "     volatile boolean _pause;\n" +
                "     List<String> topic;\n" +
                "     static Socket sock=null;\n" +
                "     static PrintWriter writer=null;\n\n" +
                "     abstract public void pauseThread();\n" +
                "\n" +
                "     abstract public void resumeThread();\n" +
                "\n}";

        saveFile(filename, code);

    }
}
