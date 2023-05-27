package com.learndm.androidlearnings.handler;

public class MessageQueue {

    private Message firstMessage;

    void enqueueMessage(Message msg, long when) {
        synchronized (this) {
            Message p = firstMessage;
            //表头为空或则when为0或者对头的when大于msg的时间戳
            if (when == 0 || p == null || when < p.when) {
                msg.next = p;
                p = msg;
            } else {
                //从头遍历,找到时间戳大于when的第一个Message,然后添加进去
                Message prev;
                for (; ; ) {
                     prev = p;
                     p = prev.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                }
                msg.next = p;
                prev.next = msg;
            }
        }
    }
}
