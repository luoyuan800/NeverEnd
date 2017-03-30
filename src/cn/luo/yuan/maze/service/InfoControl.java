package cn.luo.yuan.maze.service;

import android.content.Context;
import cn.luo.yuan.maze.display.activity.GameActivity;
import cn.luo.yuan.maze.display.view.RollTextView;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.persistence.DataManager;

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

    public void addMessage(String msg) {
        textView.addMessage(msg);
    }

    public void startGame() {
        RunningService runningService = new RunningService(hero, maze, this, dataManager, Data.REFRESH_SPEED);
    }

    public Hero getHero() {
        return hero;
    }

    public void save() {
        Gift gift = Gift.getByName(hero.getGift());
        if(gift!=null){
            gift.unHandler(this);
        }
        dataManager.saveHero(hero);
        dataManager.saveMaze(maze);
        for(Accessory accessory : hero.getAccessories()){
            dataManager.saveAccessory(accessory);
        }
        gift.handler(this);
    }

    void setHero(Hero hero) {
        this.hero = hero;
        Gift gift = Gift.getByName(hero.getGift());
        if(gift!=null){
            gift.handler(this);
        }
    }

    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        setHero(dataManager.loadHero());
        setMaze(dataManager.loadMaze());
    }

    public GameActivity.ViewHandler getViewHandler() {
        return viewHandler;
    }

    public void setViewHandler(GameActivity.ViewHandler viewHandler) {
        this.viewHandler = viewHandler;
    }
}
