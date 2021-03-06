package cn.luo.yuan.maze.client.service;

import android.util.Log;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.GameActivity;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.listener.LostListener;
import cn.luo.yuan.maze.listener.PetCatchListener;
import cn.luo.yuan.maze.listener.WinListener;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Egg;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillFactory;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.BattleService;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.service.RunningServiceInterface;
import cn.luo.yuan.maze.service.SkillHelper;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;

import static cn.luo.yuan.maze.service.ListenerService.lostListeners;
import static cn.luo.yuan.maze.service.ListenerService.petCatchListeners;
import static cn.luo.yuan.maze.service.ListenerService.winListeners;


/**
 * Created by luoyuan on 2017/3/28.
 */
public class RunningService implements RunningServiceInterface {
    private Hero hero;
    private NeverEnd gameContext;
    Runnable eggWarn = new Runnable() {
        @Override
        public void run() {

            for (Pet pet : new ArrayList<>(hero.getPets())) {
                if (pet instanceof Egg && ((Egg) pet).step > 0) {
                    ((Egg) pet).step--;
                    if (((Egg) pet).step <= 0) {
                        pet.setHp(pet.getMaxHp());
                        gameContext.getViewHandler().refreshPets(hero);
                        gameContext.addMessage(pet.getDisplayNameWithLevel() + "出生了！");
                    }
                }
            }
        }
    };
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
    private boolean invincible;
    private boolean weakling;

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

    public NeverEnd getContext() {
        return gameContext;
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
                    if (random.nextLong(10000) > 9985 || random.nextLong(maze.getStep()) > 5 + random.nextLong(5) || (maze.getStep() > 3 && random.nextLong(maze.getStreaking() + 1) > maze.getLevel())) {
                        maze.setStep(0);
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
                        if (point <= 0 || (maze.getLevel() <= maze.getMaxLevel() && !random.nextBoolean())) {
                            point = 1;
                        }
                        if(gameContext.getContext() instanceof GameActivity && ((GameActivity) gameContext.getContext()).isBirthDay()){
                            point *= 2;
                        }
                        msg = String.format(gameContext.getContext().getString(R.string.move_to_next_level),
                                hero.getDisplayName(), StringUtils.formatNumber(maze.getLevel(), true), StringUtils.formatNumber(point, false));
                        long heroHp = hero.getHp();
                        long maxHp = (long) (hero.getMaxHp() * (1 + EffectHandler.getEffectAdditionFloatValue(EffectHandler.RESTORE_RATE, hero.getEffects()) / 100));
                        if (heroHp < maxHp && random.nextLong(hero.getAgi()) > random.nextLong(hero.getStr())) {
                            long restore = random.nextLong((maxHp - heroHp) / 5);
                            hero.setHp(heroHp + restore);
                            gameContext.addMessage(String.format(Resource.getString(R.string.restore_hp), hero.getDisplayName(), StringUtils.formatNumber(restore, false)));
                        }
                        if(maze.getStreaking() > 0){
                            hero.setMaterial(hero.getMaterial() + maze.getStreaking());
                            gameContext.addMessage(String.format(Resource.getString(R.string.add_mate_for_streaking), StringUtils.formatNumber(maze.getStreaking())));
                        }
                        mazeLevelCalculate();
                        hero.setPoint(hero.getPoint() + point);
                        gameContext.addMessage(msg);

                    } else {
                        boolean meet = false;
                        if (random.nextFloat(100f) < maze.getMeetRate()) {
                            HarmAble monster = null;
                            if (maze.getStep() > 10 && random.nextInt(100) < 35) {
                                Log.d("maze", "Find defender");
                                monster = dataManager.loadDefender(maze.getLevel());
                            } else {
                                Log.d("maze", "Try to find target battle");
                                monster = monsterHelper.randomMonster(maze.getLevel());
                            }
                            this.target = monster;
                            if (monster != null) {
                                if(!isInvincible() && monster instanceof Monster && random.nextLong(maze.getStreaking()) > Data.STREAK_LIMIT){
                                    if(monster.getAtk() < hero.getUpperDef()){
                                        ((Monster) monster).setFirstName(FirstName.tire);
                                        ((Monster) monster).setAtk(hero.getUpperDef() + random.nextLong(monster.getUpperAtk()));
                                        if(monster.getUpperAtk() <= 0){
                                            ((Monster) monster).setAtk(hero.getUpperDef() + hero.getUpperHp()/10);
                                        }
                                        ((Monster) monster).setColor(Data.ORANGE_COLOR);
                                        ((Monster) monster).setMaterial(((Monster) monster).getMaterial() * 10);
                                    }
                                    if(monster.getHp() < hero.getUpperAtk()){
                                        ((Monster) monster).setFirstName(FirstName.tire);
                                        monster.setMaxHp(hero.getUpperAtk() * random.randomRange(3, 10));
                                        if(monster.getMaxHp() <= 0){
                                            monster.setMaxHp(hero.getUpperAtk());
                                        }
                                        ((Monster) monster).setColor(Data.ORANGE_COLOR);
                                        ((Monster) monster).setMaterial(((Monster) monster).getMaterial() * 10);
                                    }
                                    if(((Monster) monster).getFirstName() == FirstName.tire){
                                        gameContext.addMessage("天劫怪物降临！");
                                    }
                                }
                                monster.setHp(monster.getMaxHp());
                                meet = true;
                                gameContext.addMessage("遇见了 " + ((NameObject) monster).getDisplayName());
                                BattleService battleService = new BattleService(hero, monster, gameContext.getRandom(), this);
                                BattleMessageImp battleMessage = new BattleMessageImp(gameContext);
                                battleService.setBattleMessage(battleMessage);
                                long material = monster instanceof Monster ? ((Monster) monster).getMaterial() : maze.getLevel();
                                if(gameContext.getContext() instanceof GameActivity && ((GameActivity) gameContext.getContext()).isBirthDay()){
                                    material *= 2;
                                }
                                if(!isInvincible() && hero.getCurrentHp() <= 0){
                                    battleMessage.rowMessage(hero.getDisplayName() + "被吓傻了！");
                                }

                                if (isInvincible() || (hero.getCurrentHp() > 0 && battleService.battle(gameContext.getMaze().getLevel()))) {
                                    if(isInvincible()){
                                        gameContext.addMessage(String.format(Resource.getString(R.string.invincible_win), ((NameObject) monster).getDisplayName()));
                                    }
                                    Log.d("maze", "Battle win " + ((NameObject) monster).getDisplayName());
                                    maze.setStreaking(maze.getStreaking() + 1);
                                    hero.setMaterial(hero.getMaterial() + material);
                                    gameContext.addMessage(String.format(gameContext.getContext().getString(R.string.add_mate), StringUtils.formatNumber(material, false)));
                                    if (monster instanceof Monster) {
                                        Pet pet = tryCatch((Monster) monster, dataManager.getPetCount(), maze.getLevel());
                                        if (pet != null) {
                                            gameContext.addMessage(String.format(Resource.getString(R.string.pet_catch), pet.getDisplayName()));
                                            for (PetCatchListener listener : petCatchListeners.values()) {
                                                listener.catchPet(pet);
                                            }
                                            if (hero.getPets().size() < 1) {
                                                gameContext.getPetMonsterHelper().mountPet(pet, hero);
                                                gameContext.getViewHandler().refreshPets(hero);
                                            }
                                            dataManager.savePet(pet);
                                        }
                                    }
                                    for (WinListener listener : winListeners.values()) {
                                        listener.win(hero, monster);
                                    }
                                } else {
                                    Log.d("maze", "Battle failed with " + ((NameObject) monster).getDisplayName());
                                    maze.setStreaking(0);
                                    for (LostListener lostListener : lostListeners.values()) {
                                        lostListener.lost(hero, monster, gameContext);
                                    }
                                    if(monster instanceof Monster && ((Monster) monster).getFirstName()==FirstName.tire){
                                        hero.setMaterial(hero.getMaterial()-((Monster) monster).getMaterial()/5);
                                        gameContext.addMessage("渡劫失败，损失锻造: " + ((Monster) monster).getMaterial()/5);
                                    }
                                    if (hero.getCurrentHp() <= 0) {
                                        gameContext.addMessage(String.format(gameContext.getContext().getString(R.string.lost), hero.getDisplayName()));
                                        gameContext.getViewHandler().addDieMessage(battleMessage.getMessageCache());
                                        hero.setHp(hero.getMaxHp());
                                        for (Pet pet : new ArrayList<>(hero.getPets())) {
                                            pet.setHp(pet.getMaxHp());
                                        }
                                        maze.setLevel(1);
                                        maze.setDie(maze.getDie() + 1);
                                    }
                                    Log.d("maze", "Battle failed restore");
                                }
                                hero.setMaterial(battleService.round + hero.getMaterial());
                                gameContext.addMessage("战斗回合数奖励！" + String.format(Resource.getString(R.string.add_mate), StringUtils.formatNumber(battleService.round)));
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
        } catch (Exception e) {
            LogHelper.logException(e, "Running service run");
        }
    }

    @Override
    public boolean isPause() {
        return pause;
    }

    public HarmAble getTarget() {
        return target;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public boolean isWeakling() {
        return weakling;
    }

    public void setWeakling(boolean weakling) {
        this.weakling = weakling;
    }

    private void mazeLevelCalculate() {
        if (maze.getMaxLevel() < maze.getLevel()) {
            maze.setMaxLevel(maze.getLevel());
        }
        if (maze.getLevel() >= 100 && maze.getLevel() % 100 == 0) {
            Skill skill = SkillFactory.geSkillByName("EatHarm", gameContext.getDataManager());
            if (skill.isEnable()) {
                if (random.nextInt(4) == 0) {
                    if (skill instanceof MountAble) {
                        if (((MountAble) skill).isMounted()) {
                            SkillHelper.unMountSkill((MountAble) skill, hero);
                        }
                    }
                    skill.disable();
                }
            } else {
                if (random.nextBoolean()) {
                    SkillParameter sp = new SkillParameter(hero);
                    sp.set(SkillParameter.RANDOM, random);
                    sp.set(SkillParameter.CONTEXT, gameContext);
                    skill.enable(sp);
                }
            }
        }
    }

    private Pet tryCatch(Monster monster, int petCount, long level) {
        try {
            if (gameContext.getPetMonsterHelper().isCatchAble(monster, hero, random, petCount)) {
                NeverEndConfig config = dataManager.loadConfig();
                config.addMonsterCatch(monster.getIndex());
                dataManager.save(config);
                return gameContext.getPetMonsterHelper().monsterToPet(monster, hero, level);
            } else {
                return null;
            }
        } catch (MonsterToPetException e) {
            LogHelper.logException(e, "RunningService->tryCatch{" + monster + "}");
        }
        return null;
    }
}
