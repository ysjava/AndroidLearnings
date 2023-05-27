package com.learndm.androidlearnings.handler;

public class TestFile {
    public static void main(String[] args) {

        new Thread("t1"){
            @Override
            public void run() {
                //while (true){
                    System.out.println("ex  =");
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                }
                System.out.println("endx  =");
            }
        }.start();
    }
}
