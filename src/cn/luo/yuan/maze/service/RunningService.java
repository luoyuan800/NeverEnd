package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;


/**
 * Created by luoyuan on 2017/3/28.
 */
public class RunningService implements Runnable {
    private Hero hero;
    private InfoControl infoControl;
    private Maze maze;
    private boolean running;
    private boolean pause;
    private long fps;
    private Random random;
    private long startTime;
    private DataManager dataManager;
    public RunningService(Hero hero, Maze maze, InfoControl infoControl,DataManager dataManager, long fps){
        this.hero = hero;
        this.infoControl = infoControl;
        this.maze = maze;
        running = true;
        this.fps = fps;
        this.dataManager = dataManager;
        random = infoControl.getRandom();
    }
    public void close(){
        this.running = false;
    }
    public boolean getPause(){
        return pause;
    }
    public boolean pause(){
        this.pause = !this.pause;
        return pause;
    }
    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        if (running){
            try {
                if (pause) {
                    return;
                }
                maze.setStep(maze.getStep() + 1);
                if (random.nextLong(10000) > 9985 || maze.getStep() > random.nextLong(22) || random.nextLong(maze.getStreaking() + 1) > 50 + maze.getLevel()) {
                    maze.setLevel(maze.getLevel()+1);
                    long point = 1;
                    long add = random.nextLong(maze.getLevel() / 1000);
                    if (add < 10) {
                        point += add;
                    }
                    if (maze.getMaxLevel() < 500) {
                        point *= 4;
                    }
                    if (maze.getLevel() < maze.getMaxLevel()) {
                        point /= 2;
                    }
                    String msg;
                    if (point > 0 && (maze.getLevel() > maze.getMaxLevel()|| random.nextBoolean())) {
                        msg = String.format(infoControl.getContext().getString(R.string.move_to_next_level),
                                hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel()),StringUtils.formatNumber(point));
                    } else {
                        point = 1;
                        msg = String.format(infoControl.getContext().getString(R.string.move_to_next_level),
                                hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel()), StringUtils.formatNumber(point));
                    }
                    hero.setPoint(hero.getPoint() + point);
                    infoControl.addMessage(msg);
                    if ((System.currentTimeMillis() - startTime)%1000 == 5*60) {//每隔五分钟自动存储一次
                        infoControl.save();
                    }
                }else{
                    Monster monster = dataManager.buildRandomMonster(infoControl);
                    if(monster!=null){
                        BattleService battleService = new BattleService(infoControl, monster);
                        if(battleService.battle()){
                            maze.setStreaking(maze.getStreaking() + 1);
                            hero.setMaterial(hero.getMaterial() + monster.getMaterial());
                            infoControl.addMessage(String.format(infoControl.getContext().getString(R.string.add_mate), StringUtils.formatNumber(monster.getMaterial())));
                        }else{
                            maze.setStreaking(0);
                        }
                    }
                }
            }catch (Exception e){
                LogHelper.logException(e, false, "Error while running game thread.");
            }
        }
    }
}
