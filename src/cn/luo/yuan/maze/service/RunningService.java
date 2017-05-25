package cn.luo.yuan.maze.service;

import android.util.Log;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.listener.LostListener;
import cn.luo.yuan.maze.listener.PetCatchListener;
import cn.luo.yuan.maze.listener.WinListener;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.gift.Gift;
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
    private GameContext gameContext;
    private Maze maze;
    private boolean running;
    private boolean pause;
    private long fps;
    private Random random;
    private long startTime;
    private DataManager dataManager;
    private Monster monster;
    private RandomEventService randomEventService;

    public RunningService(Hero hero, Maze maze, GameContext gameContext, DataManager dataManager, long fps) {
        this.hero = hero;
        this.gameContext = gameContext;
        this.maze = maze;
        running = true;
        this.fps = fps;
        this.dataManager = dataManager;
        random = gameContext.getRandom();
        randomEventService = new RandomEventService(gameContext);
    }

    public void close() {
        this.running = false;
    }

    public boolean getPause() {
        return pause;
    }

    public boolean pause() {
        this.pause = !this.pause;
        return pause;
    }

    @Override
    public void run() {
        PetMonsterHelper monsterHelper = gameContext.getPetMonsterHelper();
        startTime = System.currentTimeMillis();
        if (running) {
            try {
                if (pause) {
                    return;
                }
                maze.setStep(maze.getStep() + 1);
                if (random.nextLong(10000) > 9985 || random.nextLong(maze.getStep()) > 10 + random.nextLong(22) || random.nextLong(maze.getStreaking() + 1) > 50 + maze.getLevel()) {
                    maze.setLevel(maze.getLevel() + 1);
                    Log.i("maze", "End to next level");
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
                    if (point > 0 && (maze.getLevel() > maze.getMaxLevel() || random.nextBoolean())) {
                        msg = String.format(gameContext.getContext().getString(R.string.move_to_next_level),
                                hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel()), StringUtils.formatNumber(point));
                    } else {
                        point = 1;
                        msg = String.format(gameContext.getContext().getString(R.string.move_to_next_level),
                                hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel()), StringUtils.formatNumber(point));
                    }
                    mazeLevelCalculate();
                    hero.setPoint(hero.getPoint() + point);
                    gameContext.addMessage(msg);
                    if ((System.currentTimeMillis() - startTime) % 1000 == 5 * 60) {//每隔五分钟自动存储一次
                        gameContext.save();
                    }
                    maze.setStep(0);
                } else {
                    Log.i("maze", "Try to find monster battle");
                    monster = monsterHelper.randomMonster(maze.getLevel());
                    if (monster != null) {
                        Log.i("maze", "battle with " + monster.getDisplayName());
                        BattleService battleService = new BattleService(hero,monster, gameContext.getRandom());
                        BattleMessage battleMessage = new BattleMessageImp(gameContext);
                        battleService.setBattleMessage(battleMessage);
                        if (battleService.battle(gameContext.getMaze().getLevel())) {
                            Log.i("maze", "Battle win " + monster.getDisplayName());
                            maze.setStreaking(maze.getStreaking() + 1);
                            hero.setMaterial(hero.getMaterial() + monster.getMaterial());
                            gameContext.addMessage(String.format(gameContext.getContext().getString(R.string.add_mate), StringUtils.formatNumber(monster.getMaterial())));
                            Pet pet = tryCatch(monster, dataManager.getPetCount());
                            if (pet != null) {
                                gameContext.addMessage(String.format(Resource.getString(R.string.pet_catch), pet.getDisplayName()));
                                dataManager.savePet(pet);
                                for (PetCatchListener listener : petCatchListeners.values()) {
                                    listener.catchPet(pet);
                                }
                            }
                            for (WinListener listener : winListeners.values()) {
                                listener.win(hero, monster);
                            }
                        } else {
                            Log.i("maze", "Battle failed with " + monster.getDisplayName());
                            maze.setStreaking(0);
                            for (LostListener lostListener : lostListeners.values()) {
                                lostListener.lost(hero, monster);
                            }
                            if (hero.getHp() <= 0) {
                                gameContext.addMessage(String.format(gameContext.getContext().getString(R.string.lost), hero.getDisplayName()));
                                hero.setHp(hero.getMaxHp());
                                for (Pet pet : hero.getPets()) {
                                    pet.setHp(pet.getMaxHp());
                                }
                                maze.setLevel(1);
                            }
                            Log.i("maze", "Battle failed restore");
                        }
                        monster = null;
                    }else{
                        Log.i("maze", "not monster");
                        randomEventService.random();
                    }
                }
                mazeLevelCalculate();
            } catch (Exception e) {
                LogHelper.logException(e, false, "Error while running game thread.");
            }
        }
    }

    private void mazeLevelCalculate() {
        if (maze.getMaxLevel() < maze.getLevel()) {
            maze.setMaxLevel(maze.getLevel());
        }
    }

    public Monster getMonster() {
        return monster;
    }

    private Pet tryCatch(Monster monster, int petCount) {
        try {
            if (gameContext.getPetMonsterHelper().isCatchAble(monster, hero, random, petCount)) {
                return gameContext.getPetMonsterHelper().monsterToPet(monster, hero);
            } else {
                return null;
            }
        } catch (MonsterToPetException e) {

        }
        return null;
    }
}
