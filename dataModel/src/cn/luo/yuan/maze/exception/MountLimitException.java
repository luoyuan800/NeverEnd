package cn.luo.yuan.maze.exception;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/11/2017.
 */
public class MountLimitException extends Exception {
    public String word;
    public MountLimitException(String word){
        super(word);
        this.word = word;
    }
}
