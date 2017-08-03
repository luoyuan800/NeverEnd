package cn.luo.yuan.maze.service;

/**
 * Created by luoyuan on 2017/6/3.
 */
public interface RunningServiceInterface extends Runnable {
    boolean isPause();
    InfoControlInterface getContext();
}
