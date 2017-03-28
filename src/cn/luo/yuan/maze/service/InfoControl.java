package cn.luo.yuan.maze.service;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by luoyuan on 2017/3/28.
 */
public class InfoControl {
    public Queue<String> msgQueue = new LinkedList<>();
    public void addMessage(String msg){
        msgQueue.add(msg);
    }
}
