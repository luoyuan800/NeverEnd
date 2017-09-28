package cn.luo.yuan.maze.client.service;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.client.display.handler.CrashHandler;
import cn.luo.yuan.maze.client.display.handler.GameActivityViewHandler;
import cn.luo.yuan.maze.client.display.view.RollTextView;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.exception.MountLimitException;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.original.AtkPercentEffect;
import cn.luo.yuan.maze.model.effect.original.DefPercentEffect;
import cn.luo.yuan.maze.model.effect.original.HPPercentEffect;
import cn.luo.yuan.maze.model.gift.*;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsProperties;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.service.SkillHelper;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

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
    private CrashHandler crashHandler;
    private ServerService serverService;
    private TaskManager taskManager;
    private boolean started = false;

    @Override
    public void postTaskInUIThread(Runnable task) {
        getViewHandler().post(task);
    }

    public NeverEnd() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        crashHandler = new CrashHandler(this);
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void refreshHead(){
        viewHandler.refreshHeadImage(getDataManager().loadConfig());
    }

    @Override
    public void showEmptyAccessoryDialog() {
        viewHandler.post(new Runnable() {
            @Override
            public void run() {
                final EditText title = new EditText(context);
                final EditText tag = new EditText(context);
                title.setHint("输入装备的名字");
                tag.setHint("装备描述");
                LinearLayout ly = new LinearLayout(context);
                ly.setOrientation(LinearLayout.VERTICAL);
                ly.addView(title);
                ly.addView(tag);
                SimplerDialogBuilder.build(ly, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(StringUtils.isNotEmpty(title.getText().toString())){
                            Accessory accessory = new Accessory();
                            accessory.setName(title.getText().toString());
                            accessory.setDesc(tag.getText().toString());
                            accessory.setType(random.randomItem(new String[]{Field.HAT_TYPE, Field.RING_TYPE, Field.ARMOR_TYPR, Field.SWORD_TYPE, Field.NECKLACE_TYPE}));
                            accessory.setAuthor(getHero().getName());
                            accessory.setElement(random.randomItem(Element.values()));
                            dataManager.add(accessory);
                            dialog.dismiss();
                            showToast("获得了%S", accessory.getDisplayName());
                        }
                    }
                }, context, false);
            }
        });
    }

    @Override
    public void showInputPopup(final InputListener listener, String hint) {
        final EditText et = new EditText(context);
        et.setHint(hint);
        SimplerDialogBuilder.build(et, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ip = et.getText().toString();
                if(listener!=null){
                    listener.input(ip, NeverEnd.this);
                }
                dialog.dismiss();
            }
        }, context, false);
    }

    @Override
    public void setRandomNPC(NPCLevelRecord npc) {
        viewHandler.showNPCIcon(npc);
    }

    public void setContext(Context context, DataManager dataManager) {
        this.context = context;
        executor = Executors.newScheduledThreadPool(5);
        this.setDataManager(dataManager);
        accessoryHelper = AccessoryHelper.getOrCreate(this);
        petMonsterHelper = ClientPetMonsterHelper.getOrCreate(this);
        taskManager = new TaskManager(this);
        handlerData(dataManager);
    }

    public void addMessage(String msg) {
        textView.addMessage(msg);
    }

    @Override
    public void stopGame() {
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            executor = null;
            dataManager.close();
            started = false;
        } catch (Exception e) {
            LogHelper.logException(e, "stopGame");
        }

    }

    public boolean pauseGame() {
        return runningService.pause();
    }

    public void startGame() {
        if(!started) {
            Log.i("maze", "Starting game");
            viewHandler.refreshProperties(hero);
            viewHandler.refreshPets(hero);
            runningService = new RunningService(hero, maze, this, dataManager, Data.REFRESH_SPEED);
            executor.scheduleAtFixedRate(runningService, 1, Data.REFRESH_SPEED, TimeUnit.MILLISECONDS);
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!runningService.getPause())
                            viewHandler.refreshFreqProperties();
                    } catch (Exception e) {
                        LogHelper.logException(e, "refreshfreqProperties_runnable");
                    }
                }
            }, 0, Data.REFRESH_SPEED, TimeUnit.MILLISECONDS);
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!runningService.getPause())
                            viewHandler.refreshSkill();
                    } catch (Exception e) {
                        LogHelper.logException(e, "refreshfreqProperties_runnable");
                    }
                }
            }, 0, Data.REFRESH_SPEED * 2, TimeUnit.MILLISECONDS);
            started = true;
            Log.i("maze", "Game started");
        }
    }

    public Hero getHero() {
        return hero;
    }

    void setHero(Hero hero) {
        this.hero = hero;
        random = new Random(hero.getBirthDay());
    }

    public int getIndex() {
        return dataManager.getIndex();
    }

    public ServerService getServerService() {
        if (serverService == null) {
            serverService = new ServerService(getVersion());
        }
        return serverService;
    }

    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
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

    public synchronized void save(boolean showTip) {
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
        if (showTip) {
            showToast("保存成功！");
        }
    }

    public void showToast(final String format, final Object... args) {
        getViewHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, Html.fromHtml(String.format(format, args)), Toast.LENGTH_SHORT).show();
            }
        });
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
        for (Goods goods : dataManager.loadAllGoods(true)) {
            goods.load(goodsProperties);
        }

        //Pet Handler
        for (Pet pet : dataManager.loadMountPets()) {
            if (pet.isMounted()) {
                petMonsterHelper.mountPet(pet, hero);
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
        Accessory uMount = cn.luo.yuan.maze.service.AccessoryHelper.mountAccessory(accessory, hero, check, this);
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

    public Object convertToLocalObject(Object o){
        if(o instanceof Accessory){
            return covertAccessoryToLocal((Accessory) o);
        }else{
            return o;
        }
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

    public long reincarnate() {
        long mate = (hero.getReincarnate() * 2 + 1) * Data.REINCARNATE_COST;
        if (hero.getMaterial() < mate) {
            Toast.makeText(context, Resource.getString(R.string.need_mate, mate), Toast.LENGTH_SHORT).show();
            return hero.getReincarnate();
        }
        long level = Data.REINCARNATE_LEVEL + hero.getReincarnate() * Data.REINCARNATE_LEVEL * 3;
        if (maze.getMaxLevel() < level) {
            Toast.makeText(context, Resource.getString(R.string.need_level, level), Toast.LENGTH_SHORT).show();
            return hero.getReincarnate();
        }
        dataManager.addDefender(hero, maze.getMaxLevel());
        hero.setMaterial(hero.getMaterial() - mate);
        hero.setReincarnate(hero.getReincarnate() + 1);
        if (hero.getGift() != null) {
            try {
                hero.getGift().unHandler(this);
            } catch (Exception e) {
                LogHelper.logException(e, "NeverEnd -> reincarnate -> unHandler");
            }
        }
        SkillHelper.detectSkillCount(hero);
        for (Accessory accessory : new ArrayList<>(hero.getAccessories())) {
            AccessoryHelper.unMountAccessory(accessory, hero, this);
        }
        getDataManager().cleanAccessories();
        hero.getEffects().clear();
        hero.getPets().clear();
        getDataManager().cleanPets();
        getDataManager().cleanGoods();
        getDataManager().cleanPets();
        SkillParameter sp = new SkillParameter(hero);
        sp.set(SkillParameter.CONTEXT, this);
        long totalPoint = resetSkill(sp);
        Log.d("maze-reincarnate", "After unhandler gift, AtkGrow: " + hero.getAtkGrow() + ", HpGrow: " + hero.getHpGrow() + ", DefGrow: " + hero.getDefGrow());
        hero.setAtkGrow(hero.getReincarnate() + Data.GROW_INCRESE + hero.getAtkGrow());
        hero.setDefGrow(hero.getReincarnate() + Data.GROW_INCRESE + hero.getDefGrow());
        hero.setHpGrow(hero.getReincarnate() + Data.GROW_INCRESE + hero.getHpGrow());
        hero.setMaxHp(hero.getReincarnate() * 20);
        hero.setHp(hero.getMaxHp());
        hero.setDef(hero.getReincarnate() * 5);
        hero.setAtk(hero.getReincarnate() * 15);
        hero.setAgi(0);
        hero.setStr(0);
        hero.setPoint(0);
        maze.setLevel(1);
        maze.setMaxLevel(1);
        save(false);
        viewHandler.refreshProperties(hero);
        showToast("第%d次转生！", hero.getReincarnate());
        Log.d("maze-reincarnate", "After reincarnate gift, AtkGrow: " + hero.getAtkGrow() + ", HpGrow: " + hero.getHpGrow() + ", DefGrow: " + hero.getDefGrow());
        return hero.getReincarnate();
    }

    public void showPopup(final String msg) {
        viewHandler.post(new Runnable() {
            @Override
            public void run() {
                SimplerDialogBuilder.build(msg, Resource.getString(R.string.close), context, random);
            }
        });
    }

    public long resetSkill(SkillParameter sp) {
        long totalPoint = 0L;
        sp.set(SkillParameter.CONTEXT, this);
        for (Skill skill : dataManager.loadAllSkill()) {
            if (skill instanceof MountAble) {
                if (((MountAble) skill).isMounted()) {
                    SkillHelper.unMountSkill((MountAble) skill, hero);
                }
            }
            if (skill.isEnable()) {
                if (skill instanceof UpgradeAble) {
                    totalPoint += ((UpgradeAble) skill).getLevel() * Data.SKILL_ENABLE_COST;
                } else {
                    totalPoint += Data.SKILL_ENABLE_COST;
                }
                if (skill instanceof PropertySkill) {
                    ((PropertySkill) skill).disable(sp);
                } else {
                    skill.disable();
                }
            }
            dataManager.delete(skill);
        }
        return totalPoint;
    }

    public boolean dieCountClear(){
        if(runningService.isWeakling()){
            showToast(Resource.getString(R.string.weaking));
            return false;
        }
        long t = maze.getDie();
        if(t > 0 && t > 1000){
            if(t > 60000){
                t = 30000;
            }
            startInvincible(t, -1);
            maze.setDie(0);
            return true;
        }
        return false;
    }

    public boolean streakingClear(){
        long t = maze.getStreaking();
        if(t > 0 && t > 1000){
            if(t > 30000){
                t = 30000;
            }
            startInvincible(t, -1);
            maze.setStreaking(0);
            return true;
        }
        return false;
    }

     public void startInvincible(long mill, final long weakling){
        runningService.setInvincible(true);
        showToast(Resource.getString(R.string.invincible), mill/1000);
         viewHandler.showInvincible();
         executor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    runningService.setInvincible(false);
                    showToast(Resource.getString(R.string.quit_invincible));
                    viewHandler.disableInvincible();
                    if (weakling > 0) {
                        runningService.setWeakling(true);
                        viewHandler.showWeakling();
                        final AtkPercentEffect atkE = new AtkPercentEffect();
                        atkE.setTag("weakling");
                        atkE.setValue(-50);
                        hero.getEffects().add(atkE);
                        final DefPercentEffect defE = new DefPercentEffect();
                        defE.setTag("weakling");
                        defE.setValue(-50);
                        hero.getEffects().add(defE);
                        final HPPercentEffect hpE = new HPPercentEffect();
                        hpE.setTag("weakling");
                        hpE.setValue(-50);
                        hero.getEffects().add(hpE);
                        executor.schedule(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    runningService.setWeakling(false);
                                    viewHandler.disableWeakling();
                                    hero.getEffects().removeAll(Arrays.asList(atkE, hpE, defE));
                                }catch (Exception e){
                                    LogHelper.logException(e, "Remove weakling");
                                }
                            }
                        }, weakling, TimeUnit.MILLISECONDS);
                    }
                }catch (Exception e){
                    LogHelper.logException(e, "Cancel Invincible");
                }
            }
        }, mill, TimeUnit.MILLISECONDS);
    }

    public boolean isWeakling(){
         return runningService.isWeakling();
    }
}
