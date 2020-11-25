import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.lang.reflect.Parameter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;


/**
 * Created by taray on 06/02/2019.
 */
public class Main {

    public static void main(String[] args) {

        JSONObject model= new JSONObject();


        CustomProcessor aux = new CustomProcessor();
        Test test = new Test();
        test.testProcessor(aux);


        // array parameters
        JSONArray parameters = new JSONArray();

        try {
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader("./src/main/java/CustomProcessor.java"));
            String file = "" ;
            String currentLine = reader.readLine() ;
            while (currentLine!=null){
                file += currentLine + '\n';
                currentLine = reader.readLine();
            }
            //System.out.println(file);

            reader.close();


            CompilationUnit compilationUnit = JavaParser.parse(file);
            Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("CustomProcessor");
            //System.out.println(classA.get().getMembers());
            NodeList<BodyDeclaration<?>> parts= classA.get().getMembers();


            AnnotationExpr annotation =  classA.get().getAnnotation(0);
            //annotation.getChildNodes()
            for (Node nod :  annotation.getChildNodes()) {
                if(nod.getClass().toString().equals("class com.github.javaparser.ast.expr.MemberValuePair")) {
                    MemberValuePair pair = (MemberValuePair) nod;
                    System.out.println(pair.getName() + " " +pair.getValue().toString().replace("\"", "") );
                    model.put(pair.getName().toString(), pair.getValue().toString().replace("\"", ""));

                }

            }

            for (BodyDeclaration body : parts) {
                System.out.println("----------------------------");
               // System.out.println(body.getClass());
                if(body.getClass().toString().equals("class com.github.javaparser.ast.body.FieldDeclaration")) {
                    FieldDeclaration field = (FieldDeclaration) body;
                    //System.out.println(field.getVariable(0).getName());
                    //model.put("name", field.getVariable(0).getInitializer().get());
                    //model.put("description", field.getVariable(0).getInitializer().get());
                }else if(body.getClass().toString().equals("class com.github.javaparser.ast.body.MethodDeclaration")){
                    MethodDeclaration method = (MethodDeclaration) body;
                    if(method.getName().toString().equals("process")) {
                        model.put("codeProcess", method.getBody().get().toString());

                        for (com.github.javaparser.ast.body.Parameter parameter : method.getParameters()) {

                            String name= parameter.getName().toString();
                            String type = parameter.getType().toString();
                            if(!name.equals("data")){
                                JSONObject jsonAux = new JSONObject();
                                jsonAux.put("name", name);
                                jsonAux.put("paramType", type);
                                jsonAux.put("value", "");
                                parameters.add(jsonAux);
                            }

                        }

                    }

                }

            }


            model.put("parameters", parameters);
            System.out.println(model.toJSONString());


            URL url = new URL("http://localhost:8080/services/createcustomprocessor?json=" + encodeURIComponent(model.toJSONString()));
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine;
            String response="";
            while ((inputLine = br.readLine()) != null) {
               response += inputLine;
            }

            System.out.println(response);

            System.out.println("Fin");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String encodeURIComponent(String s)
    {
        String result = null;

        try
        {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = s;
        }

        return result;
    }

}
