package com;

import java.io.PrintWriter;
import org.json.simple.JSONObject;


public abstract class ProcessorClass {

   static PrintWriter writer=null;


   abstract public JSONObject processData(JSONObject data);

}