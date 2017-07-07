package cn.luo.yuan.maze.utils;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by luoyuan on 2017/7/7.
 */
public class LimitConcurrentLinkedQueue<T> extends ConcurrentLinkedQueue<T> {
    private T last;
    @Override
    public boolean add(T e) {
        if(size() > 100){
            poll();
        }
        if(last!=null && last.equals(e)){
            last = e;
            return false;
        }
        return super.add(e);
    }
}
