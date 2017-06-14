package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.task.Task;
import org.jetbrains.annotations.NotNull;

/**
 * Created by gluo on 6/12/2017.
 */
public interface TaskManager {
    Task findTaskById(@NotNull String taskId);

}
