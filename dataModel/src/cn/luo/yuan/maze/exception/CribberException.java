package cn.luo.yuan.maze.exception;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/7/2017.
 */
public class CribberException extends Exception {
    public CribberException(String id, String mac, String name){
        super(name + "(" + id + ") @ " + mac);
    }
}
