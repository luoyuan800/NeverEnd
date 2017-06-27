package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.service.TaskManager;
import cn.luo.yuan.maze.task.Task;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.client.utils.RestConnection;
import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by gluo on 6/13/2017.
 */
public class TaskManagerImp implements TaskManager {
    private NeverEnd context;

    public TaskManagerImp(NeverEnd context) {
        this.context = context;
    }

    @Override
    public Task findTaskById(@NotNull String taskId) {
        return context.getDataManager().loadTask(taskId);
    }

    public List<Task> canStartTasks() {
        return context.getDataManager().loadTask(0, -1, new Index<Task>() {

            @Override
            public boolean match(Task t) {
                return !t.getFinished() && !t.getStart() && t.canStart(context);
            }
        }, null);
    }

    public List<Task> finishedTasks() {
        return context.getDataManager().loadTask(0, -1, new Index<Task>() {

            @Override
            public boolean match(Task t) {
                return t.getFinished();
            }
        }, null);
    }

    public List<Task> startedTasks() {
        return context.getDataManager().loadTask(0, -1, new Index<Task>() {

            @Override
            public boolean match(Task t) {
                return !t.getFinished() && t.getStart();
            }
        }, null);
    }

    public void updateNewTasks(){
        RestConnection server = new RestConnection(Field.SERVER_URL, context.getVersion());
        try {
            HttpURLConnection connection = server.getHttpURLConnection("task_version", RestConnection.POST);
            int version = Integer.parseInt(server.connect(connection).toString());
            if(version > context.getDataManager().taskCount()){
                connection = server.getHttpURLConnection("retrieve_new_task", RestConnection.POST);
                connection.addRequestProperty(Field.LOCAL_TASK_VERSION, context.getDataManager().taskCount() + "");
                List<Task> tasks = (List<Task>) server.connect(connection);
                for(Task task : tasks){
                    context.getDataManager().addTask(task);
                }
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
