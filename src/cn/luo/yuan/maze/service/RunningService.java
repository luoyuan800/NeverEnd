package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.listener.LostListener;
import cn.luo.yuan.maze.listener.PetCatchListener;
import cn.luo.yuan.maze.listener.WinListener;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import static cn.luo.yuan.maze.service.ListenerService.lostListeners;
import static cn.luo.yuan.maze.service.ListenerService.petCatchListeners;
import static cn.luo.yuan.maze.service.ListenerService.winListeners;


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
    private Monster monster;
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
        PetMonsterHelper monsterHelper = PetMonsterHelper.getOrCreate(infoControl);
        startTime = System.currentTimeMillis();
        if (running){
            try {
                if (pause) {
                    return;
                }
                maze.setStep(maze.getStep() + 1);
                if (random.nextLong(10000) > 9985 || random.nextLong(maze.getStep()) > 10 + random.nextLong(22) || random.nextLong(maze.getStreaking() + 1) > 50 + maze.getLevel()) {
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
                    if(maze.getMaxLevel() < maze.getLevel()){
                        maze.setMaxLevel(maze.getLevel());
                    }
                    hero.setPoint(hero.getPoint() + point);
                    infoControl.addMessage(msg);
                    if ((System.currentTimeMillis() - startTime)%1000 == 5*60) {//每隔五分钟自动存储一次
                        infoControl.save();
                    }
                    maze.setStep(0);
                }else{
                    Monster monster = monsterHelper.randomMonster();
                    if(monster!=null){
                        BattleService battleService = new BattleService(infoControl, monster);
                        if(battleService.battle()){
                            maze.setStreaking(maze.getStreaking() + 1);
                            hero.setMaterial(hero.getMaterial() + monster.getMaterial());
                            infoControl.addMessage(String.format(infoControl.getContext().getString(R.string.add_mate), StringUtils.formatNumber(monster.getMaterial())));
                            Pet pet = tryCatch(monster, dataManager.getPetCount());
                            if(pet!=null){
                                infoControl.addMessage(String.format(Resource.getString(R.string.pet_catch), pet.getDisplayName()));
                                dataManager.savePet(pet);
                                for(PetCatchListener listener : petCatchListeners.values()){
                                    listener.catchPet(pet);
                                }
                            }
                            for(WinListener listener : winListeners.values()){
                                listener.win(hero, monster);
                            }
                        }else{
                            maze.setStreaking(0);
                            for(LostListener lostListener : lostListeners.values()){
                                lostListener.lost(hero, monster);
                            }
                            if(hero.getHp() <= 0){
                                infoControl.addMessage(String.format(infoControl.getContext().getString(R.string.lost), hero.getDisplayName()));
                                hero.setHp(hero.getMaxHp());
                                for(Pet pet : hero.getPets()){
                                    pet.setHp(pet.getMaxHP());
                                }
                                maze.setLevel(1);
                            }
                        }
                    }
                }
            }catch (Exception e){
                LogHelper.logException(e, false, "Error while running game thread.");
            }
        }
    }

    private Pet tryCatch(Monster monster, int petCount){
        if(PetMonsterHelper.isCatchAble(monster, hero, random, petCount)){
            return PetMonsterHelper.monsterToPet(monster, hero);
        }else{
            return null;
        }
    }

    public Monster getMonster() {
        return monster;
    }
}
