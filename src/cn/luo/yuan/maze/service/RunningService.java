package cn.luo.yuan.maze.service;

import android.util.Log;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.listener.LostListener;
import cn.luo.yuan.maze.listener.PetCatchListener;
import cn.luo.yuan.maze.listener.WinListener;
import cn.luo.yuan.maze.model.*;
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
public class RunningService implements RunningServiceInterface {
    private Hero hero;
    private GameContext gameContext;
    private Maze maze;
    private boolean running;
    private boolean pause;
    private long fps;
    private Random random;
    private long startTime;
    private DataManager dataManager;
    private HarmAble target;
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
                if (maze.getStep() > 100 || random.nextLong(10000) > 9985 || random.nextLong(maze.getStep()) > 10 + random.nextLong(22) || random.nextLong(maze.getStreaking() + 1) > 50 + maze.getLevel()) {
                    maze.setLevel(maze.getLevel() + 1);
                    Log.i("maze", "End to next level");
                    long point = 1;
                    long add = random.nextLong(maze.getLevel() / Data.LEVEL_BASE_POINT_REDUCE);
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
                    long heroHp = hero.getHp();
                    long maxHp = hero.getMaxHp();
                    if(heroHp < maxHp && random.nextLong(hero.getAgi()) > random.nextLong(hero.getStr())){
                        long restore = random.nextLong((maxHp - heroHp) / 5);
                        hero.setHp(heroHp + restore);
                        gameContext.addMessage(String.format(Resource.getString(R.string.restore_hp), hero.getDisplayName(), StringUtils.formatNumber(restore)));
                    }
                    mazeLevelCalculate();
                    hero.setPoint(hero.getPoint() + point);
                    gameContext.addMessage(msg);
                    if ((System.currentTimeMillis() - startTime) % 1000 == 300) {//每隔五分钟自动存储一次
                        gameContext.save();
                    }
                    maze.setStep(0);
                } else {
                    boolean meet = false;
                    if (random.nextFloat(100f) < Data.MONSTER_MEET_RATE) {
                        if (maze.getStep() > 10 && random.nextInt(100) < 35) {
                            //Defender
                        }
                        Log.i("maze", "Try to find target battle");
                        Monster monster = monsterHelper.randomMonster(maze.getLevel());
                        this.target = monster;
                        if (monster != null) {
                            meet = true;
                            gameContext.addMessage("遇见了 " + monster.getDisplayName());
                            BattleService battleService = new BattleService(hero, monster, gameContext.getRandom(), this);
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
                                    if (hero.getPets().size() < 1) {
                                        hero.getPets().add(pet);
                                        pet.setMounted(true);
                                        gameContext.getViewHandler().refreshPets(hero);
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
                        }
                        target = null;
                    }
                    if(!meet) {
                        randomEventService.random();
                    }
                }
                mazeLevelCalculate();
            } catch (Exception e) {
                LogHelper.logException(e, "Error while running game thread.");
            }
        }
    }

    @Override
    public boolean isPause() {
        return pause;
    }

    private void mazeLevelCalculate() {
        if (maze.getMaxLevel() < maze.getLevel()) {
            maze.setMaxLevel(maze.getLevel());
        }
    }

    public HarmAble getTarget() {
        return target;
    }

    private Pet tryCatch(Monster monster, int petCount) {
        try {
            if (gameContext.getPetMonsterHelper().isCatchAble(monster, hero, random, petCount)) {
                return gameContext.getPetMonsterHelper().monsterToPet(monster, hero);
            } else {
                return null;
            }
        } catch (MonsterToPetException e) {
            LogHelper.logException(e,"RunningService->tryCatch{" + monster + "}");
        }
        return null;
    }
}
