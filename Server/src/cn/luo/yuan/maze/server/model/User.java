package cn.luo.yuan.maze.server.model;

import cn.luo.yuan.maze.utils.EncodeLong;

import java.io.Serializable;

/**
 * Created by gluo on 7/4/2017.
 */
public class User implements Serializable{
    public EncodeLong pass = new EncodeLong(111);
    public String name;
    public boolean login;
}
