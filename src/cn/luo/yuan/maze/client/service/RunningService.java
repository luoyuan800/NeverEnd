package cn.luo.yuan.maze.client.service;

import android.util.Log;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.listener.LostListener;
import cn.luo.yuan.maze.listener.PetCatchListener;
import cn.luo.yuan.maze.listener.WinListener;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillFactory;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.*;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;

import static cn.luo.yuan.maze.service.ListenerService.*;


/**
 * Created by luoyuan on 2017/3/28.
 */
public class RunningService implements RunningServiceInterface {
    private Hero hero;
    private NeverEnd gameContext;
    private Maze maze;
    private boolean running;
    private boolean pause;
    private long fps;
    private Random random;
    private long startTime;
    private DataManager dataManager;
    private HarmAble target;
    private RandomEventService randomEventService;
    private PetMonsterHelper monsterHelper;
    private long saveTime = 0L;
    public NeverEnd getContext(){
        return gameContext;
    }
    public RunningService(Hero hero, Maze maze, NeverEnd gameContext, DataManager dataManager, long fps) {
        startTime = System.currentTimeMillis();
        saveTime = startTime;
        this.hero = hero;
        this.gameContext = gameContext;
        this.maze = maze;
        running = true;
        this.fps = fps;
        this.dataManager = dataManager;
        random = gameContext.getRandom();
        randomEventService = new RandomEventService(gameContext);
        monsterHelper = gameContext.getPetMonsterHelper();
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

    Runnable eggWarn = new Runnable() {
        @Override
        public void run() {

            for (Pet pet : new ArrayList<>(hero.getPets())) {
                if (pet instanceof Egg) {
                    ((Egg) pet).step--;
                    if (((Egg) pet).step <= 0) {
                            gameContext.getViewHandler().refreshPets(hero);
                            gameContext.addMessage(pet.getDisplayNameWithLevel() + "出生了！");
                    }
                }
            }
        }
    };

    @Override
    public void run() {
        try {
            if (running) {
                try {
                    if (pause) {
                        return;
                    }

                    gameContext.getExecutor().submit(eggWarn);
                    if ((System.currentTimeMillis() - saveTime) >= 300000) {//每隔五分钟自动存储一次
                        gameContext.save(false);
                        saveTime = System.currentTimeMillis();
                    }
                    maze.setStep(maze.getStep() + 1);
                    if (maze.getStep() > 50 || random.nextLong(10000) > 9985 || random.nextLong(maze.getStep()) > 10 + random.nextLong(22) || random.nextLong(maze.getStreaking() + 1) > 10 + maze.getLevel()) {
                        maze.setLevel(maze.getLevel() + 1);
                        Log.d("maze", "End to next level");
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
                                    hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel(), true), StringUtils.formatNumber(point, false));
                        } else {
                            point = 1;
                            msg = String.format(gameContext.getContext().getString(R.string.move_to_next_level),
                                    hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel(), true), StringUtils.formatNumber(point, false));
                        }
                        long heroHp = hero.getHp();
                        long maxHp = (long)(hero.getMaxHp() * (1 + EffectHandler.getEffectAdditionFloatValue(EffectHandler.RESTORE_RATE, hero.getEffects())/100));
                        if (heroHp < maxHp && random.nextLong(hero.getAgi()) > random.nextLong(hero.getStr())) {
                            long restore = random.nextLong((maxHp - heroHp) / 5);
                            hero.setHp(heroHp + restore);
                            gameContext.addMessage(String.format(Resource.getString(R.string.restore_hp), hero.getDisplayName(), StringUtils.formatNumber(restore, false)));
                        }
                        mazeLevelCalculate();
                        hero.setPoint(hero.getPoint() + point);
                        gameContext.addMessage(msg);

                        maze.setStep(0);
                    } else {
                        boolean meet = false;
                        if (random.nextFloat(100f) < maze.getMeetRate()) {
                            HarmAble monster = null;
                            if (maze.getStep() > 10 && random.nextInt(100) < 35) {
                                Log.d("maze", "Find defender");
                                monster = dataManager.loadDefender(maze.getLevel());
                            }else {
                                Log.d("maze", "Try to find target battle");
                                monster = monsterHelper.randomMonster(maze.getLevel());
                            }
                            this.target = monster;
                            if (monster != null) {
                                monster.setHp(monster.getMaxHp());
                                meet = true;
                                gameContext.addMessage("遇见了 " + ((NameObject)monster).getDisplayName());
                                BattleService battleService = new BattleService(hero, monster, gameContext.getRandom(), this);
                                BattleMessage battleMessage = new BattleMessageImp(gameContext);
                                battleService.setBattleMessage(battleMessage);
                                long material = monster instanceof Monster ? ((Monster)monster).getMaterial() : maze.getLevel();
                                if (battleService.battle(gameContext.getMaze().getLevel())) {
                                    Log.d("maze", "Battle win " + ((NameObject)monster).getDisplayName());
                                    maze.setStreaking(maze.getStreaking() + 1);
                                    hero.setMaterial(hero.getMaterial() + material);
                                    gameContext.addMessage(String.format(gameContext.getContext().getString(R.string.add_mate), StringUtils.formatNumber(material, false)));
                                    if(monster instanceof Monster) {
                                        Pet pet = tryCatch((Monster) monster, dataManager.getPetCount(), maze.getLevel());
                                        if (pet != null) {
                                            gameContext.addMessage(String.format(Resource.getString(R.string.pet_catch), pet.getDisplayName()));
                                            for (PetCatchListener listener : petCatchListeners.values()) {
                                                listener.catchPet(pet);
                                            }
                                            if (hero.getPets().size() < 1) {
                                                hero.getPets().add(pet);
                                                pet.setMounted(true);
                                                gameContext.getViewHandler().refreshPets(hero);
                                            }
                                            dataManager.savePet(pet);
                                        }
                                    }
                                    for (WinListener listener : winListeners.values()) {
                                        listener.win(hero, monster);
                                    }
                                } else {
                                    Log.d("maze", "Battle failed with " + ((NameObject)monster).getDisplayName());
                                    maze.setStreaking(0);
                                    for (LostListener lostListener : lostListeners.values()) {
                                        lostListener.lost(hero, monster, gameContext);
                                    }
                                    if (hero.getHp() <= 0) {
                                        gameContext.addMessage(String.format(gameContext.getContext().getString(R.string.lost), hero.getDisplayName()));
                                        hero.setHp(hero.getMaxHp());
                                        for (Pet pet : new ArrayList<>(hero.getPets())) {
                                            pet.setHp(pet.getMaxHp());
                                        }
                                        maze.setLevel(1);
                                        maze.setStep(0);
                                    }
                                    Log.d("maze", "Battle failed restore");
                                }
                                gameContext.getViewHandler().refreshPets(hero);
                            }
                            target = null;
                        }
                        if (!meet) {
                            Log.d("maze", "Process random events");
                            randomEventService.random();
                        }
                    }
                    mazeLevelCalculate();
                } catch (Exception e) {
                    LogHelper.logException(e, "Error while running game thread.");
                }
            }
        }catch (Exception e){
            LogHelper.logException(e, "Running service run");
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
        if(maze.getLevel()>= 100 && maze.getLevel()%100 == 0){
            Skill skill = SkillFactory.geSkillByName("EatHarm",gameContext.getDataManager());
            if(skill.isEnable()){
                if(random.nextInt(4) == 0){
                    if(skill instanceof MountAble){
                        if(((MountAble) skill).isMounted()){
                            SkillHelper.unMountSkill((MountAble) skill, hero);
                        }
                    }
                    skill.disable();
                }
            }else{
                if(random.nextBoolean()){
                    SkillParameter sp = new SkillParameter(hero);
                    sp.set(SkillParameter.RANDOM, random);
                    sp.set(SkillParameter.CONTEXT, gameContext);
                    skill.enable(sp);
                }
            }
        }
    }

    public HarmAble getTarget() {
        return target;
    }

    private Pet tryCatch(Monster monster, int petCount, long level) {
        try {
            if (gameContext.getPetMonsterHelper().isCatchAble(monster, hero, random, petCount)) {
                return gameContext.getPetMonsterHelper().monsterToPet(monster, hero, level);
            } else {
                return null;
            }
        } catch (MonsterToPetException e) {
            LogHelper.logException(e,"RunningService->tryCatch{" + monster + "}");
        }
        return null;
    }
}
