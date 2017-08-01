package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.task.Task;
import cn.luo.yuan.maze.persistence.serialize.SerializeLoader;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/1/2017.
 */
public class TaskManager {
    private NeverEnd context;
    private SerializeLoader<Task> taskLoader;

    public TaskManager(NeverEnd context) {
        this.context = context;
        taskLoader = new SerializeLoader<>(Task.class, context.getContext(), context.getIndex());
    }

    public boolean canStart(String id) {
        Task task = taskLoader.load(id);
        if (task != null) {
            if (StringUtils.isNotEmpty(task.getPreTaskId())) {
                Task pre = taskLoader.load(task.getPreTaskId());
                if (pre == null || !pre.isFinished()) {
                    return false;
                }
            }
            for (IDModel item : task.predecessorItems()) {
                if (item instanceof Accessory) {
                    boolean notstart = true;
                    for (Accessory accessory : context.getHero().getAccessories()) {
                        if (accessory.getName().equals(((Accessory) item).getName())) {
                            notstart = false;
                            break;
                        }
                    }
                    if (notstart) {
                        return false;
                    }
                }
                if (item instanceof Goods) {
                    Goods goods = context.getDataManager().loadGoods(item.getClass().getSimpleName());
                    if (goods == null || goods.getCount() <= 0) {
                        return false;
                    }
                }
                if (item instanceof Pet) {
                    boolean notstart = true;
                    for (Pet pet : context.getHero().getPets()) {
                        if (pet.getName().equals(((Pet) item).getType())) {
                            notstart = false;
                            break;
                        }
                    }
                    if (notstart) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void saveTask(Task task) {
        taskLoader.save(task);
    }

    public List<Task> queryFinishedTask(int start, int row) {
        return taskLoader.loadLimit(start, row, new Index<Task>() {
            @Override
            public boolean match(Task task) {
                return task.isFinished();
            }
        }, null);
    }

    public List<Task> queryProgressTask(int start, int row) {
        return taskLoader.loadLimit(start, row, new Index<Task>() {
            @Override
            public boolean match(Task task) {
                return task.isStart();
            }
        }, null);
    }
}
