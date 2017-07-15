package cn.luo.yuan.maze.client.service;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.handler.GameActivityViewHandler;
import cn.luo.yuan.maze.client.display.view.RollTextView;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.exception.MountLimitException;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsProperties;
import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.model.skill.PropertySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.model.skill.UpgradeAble;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.service.SkillHelper;
import cn.luo.yuan.maze.utils.Random;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyuan on 2017/3/28.
 */
public class NeverEnd extends Application implements InfoControlInterface {
    private RunningService runningService;
    private RollTextView textView;
    private Context context;
    private Hero hero;
    private Maze maze;
    private DataManager dataManager;
    private GameActivityViewHandler viewHandler;
    private Random random;
    private ScheduledExecutorService executor;
    private AccessoryHelper accessoryHelper;
    private PetMonsterHelper petMonsterHelper;
    private TaskManagerImp taskManager;

    public NeverEnd() {
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void setContext(Context context, DataManager dataManager) {
        this.context = context;
        executor = Executors.newScheduledThreadPool(5);
        petMonsterHelper = PetMonsterHelper.instance;
        this.setDataManager(dataManager);
        accessoryHelper = AccessoryHelper.getOrCreate(this);
        petMonsterHelper.setRandom(random);
        petMonsterHelper.setMonsterLoader(PetMonsterLoder.getOrCreate(this));
        taskManager = new TaskManagerImp(this);
        handlerData(dataManager);
    }

    public void addMessage(String msg) {
            textView.addMessage(msg);
    }

    @Override
    public void stopGame() {
        executor.shutdown();
        executor = null;
        dataManager.fuseCache();
        dataManager = null;
        petMonsterHelper = null;
        accessoryHelper = null;
        taskManager = null;

    }

    public boolean pauseGame() {
        return runningService.pause();
    }

    public void startGame() {
        Log.i("maze", "Starting game");
        viewHandler.refreshProperties(hero);
        viewHandler.refreshAccessory(hero);
        viewHandler.refreshSkill(hero);
        viewHandler.refreshPets(hero);
        runningService = new RunningService(hero, maze, this, dataManager, Data.REFRESH_SPEED);
        executor.scheduleAtFixedRate(runningService, 1, Data.REFRESH_SPEED, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!runningService.getPause())
                        viewHandler.refreshFreqProperties();
                }catch (Exception e){
                    LogHelper.logException(e, "refreshfreqProperties_runnable");
                }
            }
        }, 0, Data.REFRESH_SPEED, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!runningService.getPause())
                        viewHandler.refreshClickSkill();
                }catch (Exception e){
                    LogHelper.logException(e, "refreshfreqProperties_runnable");
                }
            }
        }, 0, Data.REFRESH_SPEED * 2, TimeUnit.MILLISECONDS);
        Log.i("maze", "Game started");
    }

    public Hero getHero() {
        return hero;
    }

    public int getIndex() {
        return dataManager.getIndex();
    }

    void setHero(Hero hero) {
        this.hero = hero;
        random = new Random(hero.getBirthDay());
    }

    public String getVersion() {
        try {
            String pkName = getContext().getPackageName();
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return versionCode + "";
        } catch (Exception e) {
            return "0";
        }
    }

    public AccessoryHelper getAccessoryHelper() {
        return accessoryHelper;
    }

    public PetMonsterHelper getPetMonsterHelper() {
        return petMonsterHelper;
    }

    public void save() {
        Gift gift = hero.getGift();
        if (gift != null) {
            try {
                gift.unHandler(this);
            } catch (Exception e) {
                LogHelper.logException(e, "NeverEnd ->save->gift.un handler(" + gift + ")");
            }
        }
        dataManager.saveHero(hero);
        dataManager.saveMaze(maze);
        dataManager.fuseCache();
        if (gift != null) {
            try {
                gift.handler(this);
            } catch (Exception e) {
                LogHelper.logException(e, "NeverEnd ->save->gift.handler(" + gift + ")");
            }
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

    public void setContext(Context context) {
        this.context = context;
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

    @Override
    public TaskManagerImp getTaskManager() {
        return taskManager;
    }

    public void handlerData(DataManager dataManager) {
        //Gift handle
        Gift gift = hero.getGift();
        if (gift != null) {
            try {
                gift.handler(this);
            } catch (Exception e) {
                LogHelper.logException(e, "NeverEnd ->handlerData->gift.handler(" + gift + ")");
            }
        }

        //Accessory handle
        for (Accessory accessory : dataManager.loadMountedAccessory(hero)) {
            try {
                mountAccessory(accessory, false);
            } catch (MountLimitException e) {
                //LogHelper.logException(e, "NeverEnd->handler accessory");
            }
        }

        //Skill handle
        for (Skill skill : dataManager.loadAllSkill()) {
            if (skill instanceof MountAble && ((MountAble) skill).isMounted()) {
                SkillHelper.mountSkill(skill, hero);
            }
        }
        hero.getClickSkills().addAll(dataManager.loadClickSkill());

        //Goods handle
        GoodsProperties goodsProperties = new GoodsProperties(hero);
        for (Goods goods : dataManager.loadAllGoods()) {
            goods.load(goodsProperties);
        }

        //Pet Handler
        for (Pet pet : dataManager.loadMountPets()) {
            if (pet.isMounted()) {
                hero.getPets().add(pet);
            }
        }
    }

    public GameActivityViewHandler getViewHandler() {
        return viewHandler;
    }

    public void setViewHandler(GameActivityViewHandler viewHandler) {
        this.viewHandler = viewHandler;
    }

    public void mountAccessory(Accessory accessory, boolean check) throws MountLimitException {
        Accessory uMount = cn.luo.yuan.maze.service.AccessoryHelper.mountAccessory(accessory, hero, check);
        if (uMount != null) {
            dataManager.saveAccessory(uMount);
        }
        dataManager.saveAccessory(accessory);
    }

    public HarmAble getCurrentBattleTarget() {
        return runningService.getTarget();
    }

    public Accessory covertAccessoryToLocal(Accessory a) {
        List<Effect> effects = new ArrayList<>(a.getEffects());
        a.getEffects().clear();
        for (Effect effect : effects) {
            a.getEffects().add(ClientEffectHandler.buildClientEffect(effect));
        }
        return a;
    }

    public Serializable convertToServerObject(Serializable object) {
        if (object instanceof Accessory) {
            Accessory accessory = new Accessory();
            accessory.setName(((Accessory) object).getName());
            accessory.setId(((Accessory) object).getId());
            accessory.setColor(((Accessory) object).getColor());
            accessory.setElement(((Accessory) object).getElement());
            accessory.setLevel(((Accessory) object).getLevel());
            accessory.setType(((Accessory) object).getType());
            for (Effect effect : ((Accessory) object).getEffects()) {
                accessory.getEffects().add(effect.covertToOriginal());
            }
            object = accessory;
        }
        return object;
    }

    public long reincarnate(){
        long mate = (hero.getReincarnate()+1) * Data.REINCARNATE_COST;
        if(hero.getMaterial() < mate){
            Toast.makeText(context, Resource.getString(R.string.need_mate, mate), Toast.LENGTH_SHORT).show();
            return hero.getReincarnate();
        }
        long level = Data.REINCARNATE_LEVEL + hero.getReincarnate() * Data.REINCARNATE_LEVEL;
        if(maze.getMaxLevel() < level){
            Toast.makeText(context, Resource.getString(R.string.need_level, level), Toast.LENGTH_SHORT).show();
            return hero.getReincarnate();
        }
        hero.setMaterial(hero.getMaterial() - mate);
        hero.setReincarnate(hero.getReincarnate() + 1);
        if(hero.getGift()!=null){
            try {
                hero.getGift().unHandler(this);
            } catch (Exception e) {
                LogHelper.logException(e, "NeverEnd -> reincarnate -> unHandler");
            }
        }
        SkillHelper.detectSkillCount(hero);
        for(Accessory accessory : new ArrayList<>(hero.getAccessories())){
            AccessoryHelper.unMountAccessory(accessory, hero);
        }
        SkillParameter sp = new SkillParameter(hero);
        long totalPoint = resetSkill(sp);
        hero.setHpGrow(hero.getReincarnate()*Data.GROW_INCRESE  + hero.getHpGrow());
        hero.setDefGrow(hero.getReincarnate()*Data.GROW_INCRESE  + hero.getDefGrow());
        hero.setHpGrow(hero.getReincarnate()*Data.GROW_INCRESE  + hero.getHpGrow());
        hero.setMaxHp(hero.getReincarnate() * 20);
        hero.setHp(hero.getMaxHp());
        hero.setDef(hero.getReincarnate() * 5);
        hero.setAtk(hero.getReincarnate() * 15);
        hero.setAgi(0);
        hero.setStr(0);
        hero.setPoint(totalPoint);
        maze.setLevel(1);
        maze.setMaxLevel(1);
        viewHandler.refreshProperties(hero);
        viewHandler.refreshSkill(hero);
        viewHandler.refreshAccessory(hero);
        save();
        return hero.getReincarnate();
    }

    public long resetSkill(@NotNull SkillParameter sp) {
        long totalPoint = 0L;
        for( Skill skill : dataManager.loadAllSkill()){
            if(skill instanceof MountAble){
                if(((MountAble) skill).isMounted()){
                    SkillHelper.unMountSkill((MountAble) skill, hero);
                }
            }
            if(skill.isEnable()){
                if(skill instanceof UpgradeAble){
                    totalPoint += ((UpgradeAble) skill).getLevel() * Data.SKILL_ENABLE_COST;
                }else{
                    totalPoint += Data.SKILL_ENABLE_COST;
                }
                if(skill instanceof PropertySkill){
                    ((PropertySkill)skill).disable(sp);
                }else {
                    skill.disable();
                }
            }
        }
        return totalPoint;
    }
}
