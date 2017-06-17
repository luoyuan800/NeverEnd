package cn.luo.yuan.maze.service;

import android.content.Context;
import android.util.Log;
import cn.luo.yuan.maze.display.activity.GameActivity;
import cn.luo.yuan.maze.display.view.RollTextView;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.model.goods.GoodsProperties;
import cn.luo.yuan.maze.model.goods.GoodsType;
import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.utils.Random;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyuan on 2017/3/28.
 */
public class GameContext implements InfoControlInterface {
    private RunningService runningService;
    private RollTextView textView;
    private Context context;
    private Hero hero;
    private Maze maze;
    private DataManager dataManager;
    private GameActivity.ViewHandler viewHandler;
    private Random random;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private AccessoryHelper accessoryHelper;
    private PetMonsterHelper petMonsterHelper;
    private TaskManagerImp taskManager;

    public GameContext(Context context, DataManager dataManager) {
        this.context = context;
        this.setDataManager(dataManager);
        accessoryHelper = AccessoryHelper.getOrCreate(this);
        petMonsterHelper = PetMonsterHelper.instance;
        petMonsterHelper.setRandom(random);
        petMonsterHelper.setMonsterLoader(PetMonsterLoder.getOrCreate(this));
        taskManager= new TaskManagerImp(this);
    }

    public void addMessage(String msg) {
        textView.addMessage(msg);
    }

    @Override
    public void stopGame() {
        executor.shutdown();
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
            gift.unHandler(this);
        }
        dataManager.saveHero(hero);
        dataManager.saveMaze(maze);
        dataManager.fuseCache();
        if (gift != null) {
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

    @Override
    public TaskManagerImp getTaskManager() {
        return taskManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        setHero(dataManager.loadHero());
        setMaze(dataManager.loadMaze());
        //Gift handle
        Gift gift =hero.getGift();
        if (gift != null) {
            gift.handler(this);
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

    public GameActivity.ViewHandler getViewHandler() {
        return viewHandler;
    }

    public void setViewHandler(GameActivity.ViewHandler viewHandler) {
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
