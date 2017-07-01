package cn.luo.yuan.maze.client.service;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import cn.luo.yuan.maze.client.display.handler.GameActivityViewHandler;
import cn.luo.yuan.maze.client.display.view.RollTextView;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.model.goods.GoodsProperties;
import cn.luo.yuan.maze.model.goods.GoodsType;
import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.service.SkillHelper;
import cn.luo.yuan.maze.utils.Random;

import java.io.Serializable;
import java.util.ArrayList;
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

    public ScheduledExecutorService getExecutor(){
        return executor;
    }

    public NeverEnd(){
    }
    public void setContext(Context context,DataManager dataManager){
        this.context = context;
        executor = Executors.newScheduledThreadPool(5);
        petMonsterHelper = PetMonsterHelper.instance;
        this.setDataManager(dataManager);
        accessoryHelper = AccessoryHelper.getOrCreate(this);
        petMonsterHelper.setRandom(random);
        petMonsterHelper.setMonsterLoader(PetMonsterLoder.getOrCreate(this));
        taskManager= new TaskManagerImp(this);
        handlerData(dataManager);
    }
    public void setContext(Context context){
        this.context = context;
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
                if (!runningService.getPause())
                    viewHandler.refreshFreqProperties();
            }
        }, 0, Data.REFRESH_SPEED, TimeUnit.MILLISECONDS);
        Log.i("maze", "Game started");
    }

    public Hero getHero() {
        return hero;
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

    void setHero(Hero hero) {
        this.hero = hero;
        random = new Random(hero.getBirthDay());
    }

    public void save() {
        Gift gift = hero.getGift();
        if (gift != null) {
            try {
                gift.unHandler(this);
            } catch (Exception e) {
                LogHelper.logException(e, "NeverEnd ->save->gift.un handler("+ gift+ ")");
            }
        }
        dataManager.saveHero(hero);
        dataManager.saveMaze(maze);
        dataManager.fuseCache();
        if (gift != null) {
            try {
                gift.handler(this);
            } catch (Exception e) {
                LogHelper.logException(e, "NeverEnd ->save->gift.handler("+ gift+ ")");
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

    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public TaskManagerImp getTaskManager() {
        return taskManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        setHero(dataManager.loadHero());
        setMaze(dataManager.loadMaze());
    }

    public void handlerData(DataManager dataManager) {
        //Gift handle
        Gift gift =hero.getGift();
        if (gift != null) {
            try {
                gift.handler(this);
            } catch (Exception e) {
                LogHelper.logException(e, "NeverEnd ->handlerData->gift.handler("+ gift+ ")");
            }
        }

        //Accessory handle
        for (Accessory accessory : dataManager.loadMountedAccessory(hero)) {
            mountAccessory(accessory);
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
        for (GoodsType type : GoodsType.values()) {
            if (type.getNeedLoad()) {
                dataManager.loadGoods(type).load(goodsProperties);
            }
        }

        //Pet Handler
        for(Pet pet : dataManager.loadMountPets()){
            if(pet.isMounted()){
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

    public void mountAccessory(Accessory accessory) {
        Accessory uMount = accessoryHelper.mountAccessory(accessory, hero);
        if (uMount != null) {
            dataManager.saveAccessory(uMount);
        }
        dataManager.saveAccessory(accessory);
    }

    public Object getCurrentBattleTarget() {
        return runningService.getTarget();
    }

    public Accessory covertAccessoryToLocal(Accessory a){
        List<Effect> effects = new ArrayList<>(a.getEffects());
        a.getEffects().clear();
        for(Effect effect : effects){
            a.getEffects().add(ClientEffectHandler.buildEffect(effect.getName(), effect.getValue().toString()));
        }
        return a;
    }

    public Serializable convertToServerObject(Serializable object) {
        if(object instanceof Accessory) {
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
}
