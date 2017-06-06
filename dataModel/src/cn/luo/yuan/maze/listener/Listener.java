package cn.luo.yuan.maze.listener;

import java.io.Serializable;

/**
 * Created by gluo on 5/8/2017.
 */
public interface Listener extends Serializable {
    static final long serialVersionUID = 1L;
    String getKey();
}
