package com.himmel.graduate.code.Management;

/**
 * Created by Lyaro on 28.01.2016.
 */
public class Manager implements Runnable {
    Thread thread;

    public Manager (){
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        //5000 = 5 сек.
        System.out.println(new Connect(5000).getAddress());
    }
}
