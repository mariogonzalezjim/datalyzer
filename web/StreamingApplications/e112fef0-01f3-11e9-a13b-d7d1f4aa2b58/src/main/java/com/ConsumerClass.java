package com;



import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public abstract class ConsumerClass extends Thread {

     volatile boolean _run;
     volatile boolean _pause;
     List<String> topic;
     static Socket sock=null;
     static PrintWriter writer=null;

     abstract public void pauseThread();

     abstract public void resumeThread();

}