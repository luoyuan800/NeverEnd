package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.Path;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.task.Scene;
import cn.luo.yuan.maze.model.task.Task;
import cn.luo.yuan.maze.persistence.serialize.SerializeLoader;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/1/2017.
 */
public class TaskManager implements cn.luo.yuan.maze.service.TaskManager {
    private NeverEnd context;
    private SerializeLoader<Task> taskLoader;
    private RestConnection server;

    public TaskManager(NeverEnd context) {
        this.context = context;
        taskLoader = new SerializeLoader<>(Task.class, context.getContext(), context.getIndex());
        server = new RestConnection(Field.SERVER_URL,context.getVersion(), Resource.getSingInfo());
        context.getDataManager().registerTable(taskLoader.getDb());
    }

    public boolean canStart(String taskId) {
        Task task = taskLoader.load(taskId);
        return canStart(task);
    }

    private boolean canStart(Task task) {
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

    public List<Task> queryCanStartTask(List<Task> tasks){
        List<Task> canTask = new ArrayList<>(tasks.size());
        for(Task task : tasks){
            if(canStart(task)){
                canTask.add(task);
            }
        }
        return canTask;
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

    public List<Task> queryOnlineTask(int start, int row){
        try {
            HttpURLConnection connection = server.getHttpURLConnection(Path.QUERY_ONLINE_TASK, RestConnection.POST);
            connection.addRequestProperty(Field.COUNT, String.valueOf(row));
            connection.addRequestProperty(Field.INDEX, String.valueOf(start));
            HashSet<String> existingTaskIds = new HashSet<>(taskLoader.getDb().loadIds());
            Object rs = server.connect(existingTaskIds, connection);
            if(rs instanceof List){
                return (List<Task>) rs;
            }
        } catch (IOException e) {
            LogHelper.logException(e, "query online task: " + start + ", row: " + row);
        }
        return Collections.emptyList();
    }

    public List<Scene> queryTaskScenes(String taskId){
        try {
            HttpURLConnection connection = server.getHttpURLConnection(Path.QUERY_TASK_SCENES, RestConnection.POST);
            connection.addRequestProperty(Field.TASK_ID, taskId);
            Object rs = server.connect(connection);
            if(rs instanceof List){
                return (List<Scene>) rs;
            }
        } catch (Exception e) {
            LogHelper.logException(e, "queryTask for " + taskId);
        }
        return Collections.emptyList();
    }

    public void startTask(Task task){
        task.start(context);
        taskLoader.save(task);
    }

    public void discardTask(Task task){
        taskLoader.delete(task.getId());
    }

    public String finishedTask(Task task){
        task.finished(context);
        task.setFinished(true);
        taskLoader.save(task);
        return task.getAward();
    }
}
