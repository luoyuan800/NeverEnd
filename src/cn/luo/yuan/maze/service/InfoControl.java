package cn.luo.yuan.maze.service;

import android.content.Context;
import android.view.View;
import cn.luo.yuan.maze.display.activity.GameActivity;
import cn.luo.yuan.maze.display.view.RollTextView;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.persistence.DataManager;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by luoyuan on 2017/3/28.
 */
public class InfoControl {
    private RollTextView textView;
    private Context context;
    private Hero hero;
    private Maze maze;
    private DataManager dataManager;
    private GameActivity.ViewHandler viewHandler;

    public InfoControl(Context context) {
        this.context = context;
    }

    public void addMessage(String msg){
        textView.addMessage(msg);
    }

    public void startGame(){
        RunningService runningService = new RunningService(hero,maze,this, dataManager, Data.REFRESH_SPEED);

    }


    public Hero getHero() {
        return hero;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public Maze getMaze() {
        return maze;
    }

    void setHero(Hero hero) {
        this.hero = hero;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        setHero(dataManager.loadHero());
        setMaze(dataManager.loadMaze());
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setViewHandler(GameActivity.ViewHandler viewHandler) {
        this.viewHandler = viewHandler;
    }

    public GameActivity.ViewHandler getViewHandler() {
        return viewHandler;
    }
}
