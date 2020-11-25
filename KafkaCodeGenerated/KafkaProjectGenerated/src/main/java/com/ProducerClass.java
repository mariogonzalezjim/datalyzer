package com;

import java.util.List;

 
public abstract class ProducerClass extends Thread {

     volatile boolean _run;
     volatile boolean _pause;
     List<String> topic;

     abstract public void pauseThread();

     abstract public void resumeThread();


}