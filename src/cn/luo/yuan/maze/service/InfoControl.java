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
import cn.luo.yuan.maze.utils.Random;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private Random random;
    RunningService runningService;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public InfoControl(Context context) {
        this.context = context;
    }

    public void addMessage(String msg) {
        textView.addMessage(msg);
    }

    public boolean pauseGame(){
        return runningService.pause();
    }

    public void startGame() {
        viewHandler.refreshProperties(hero);
        viewHandler.refreshAccessory(hero);
        viewHandler.refreshSkill(hero);
        runningService = new RunningService(hero, maze, this, dataManager, Data.REFRESH_SPEED);
        executor.scheduleAtFixedRate(runningService,0, Data.REFRESH_SPEED, TimeUnit.MICROSECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(!runningService.getPause())
                    viewHandler.refreshFreqProperties();
            }
        }, 0, Data.REFRESH_SPEED, TimeUnit.MICROSECONDS);

    }

    public Hero getHero() {
        return hero;
    }

    void setHero(Hero hero) {
        this.hero = hero;
        //Gift handle
        Gift gift = Gift.getByName(hero.getGift());
        if (gift != null) {
            gift.handler(this);
        }
        //Accessory handle
        for(Accessory accessory : dataManager.loadMountedAccessory(hero)){
            mountAccessory(accessory);
        }
        random = new Random(hero.getBirthDay());
    }

    public void save() {
        Gift gift = Gift.getByName(hero.getGift());
        if (gift != null) {
            gift.unHandler(this);
        }
        dataManager.saveHero(hero);
        dataManager.saveMaze(maze);
        for (Accessory accessory : hero.getAccessories()) {
            dataManager.saveAccessory(accessory);
        }
        if(gift!=null) {
            gift.handler(this);
        }
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setTextView(RollTextView textView) {
        this.textView = textView;
    }

    public Context getContext() {
        return context;
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

    public void mountAccessory(Accessory accessory){
        Accessory uMount = hero.mountAccessory(accessory);
        if(uMount!=null){
            uMount.setMounted(false);
            dataManager.saveAccessory(uMount);
        }
        accessory.setMounted(true);
        dataManager.saveAccessory(accessory);
    }
}
