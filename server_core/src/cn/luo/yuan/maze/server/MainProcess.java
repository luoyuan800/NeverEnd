package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.GroupHolder;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.original.AgiEffect;
import cn.luo.yuan.maze.model.effect.original.AtkEffect;
import cn.luo.yuan.maze.model.effect.original.DefEffect;
import cn.luo.yuan.maze.model.effect.original.HpEffect;
import cn.luo.yuan.maze.model.effect.original.MeetRateEffect;
import cn.luo.yuan.maze.model.effect.original.PetRateEffect;
import cn.luo.yuan.maze.model.effect.original.SkillRateEffect;
import cn.luo.yuan.maze.model.effect.original.StrEffect;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.types.Medallion;
import cn.luo.yuan.maze.server.model.User;
import cn.luo.yuan.maze.server.persistence.ExchangeTable;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import cn.luo.yuan.maze.server.persistence.NPCTable;
import cn.luo.yuan.maze.server.persistence.WarehouseTable;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable;
import cn.luo.yuan.maze.task.Task;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.luo.yuan.maze.utils.Field.RESPONSE_RESULT_SUCCESS;

/**
 * Created by gluo on 7/6/2017.
 */
public class MainProcess {
    public static MainProcess process;
    public String sing = StringUtils.EMPTY_STRING;
    public ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
    public User user;
    public String heroRange = StringUtils.EMPTY_STRING;
    private File root = new File("data");
    public WarehouseTable warehouseTable = new WarehouseTable(root);
    public ExchangeTable exchangeTable = new ExchangeTable(root);
    public ObjectTable<Task> taskTable = new ObjectTable<>(Task.class, root);
    private File heroDir = new File(root, "hero");
    public Map<String, HeroTable> heroTableCache = initHeroTableCache(heroDir);
    public List<GroupHolder> groups = new ArrayList<>();
    ObjectTable<User> userDb = new ObjectTable<>(User.class, root);
    public MainProcess() {
        process = this;
        user = userDb.loadObject("root");
        if (user == null) {
            user = new User();
            user.setPass(111);
            user.setName("luo");
            try {
                userDb.save(user);
            } catch (IOException e) {
                LogHelper.error(e);
            }
        }

    }

    public boolean login(int pass){
        if(user.getLogin()){
           return true;
        }else{
            if(user.getPass() == pass){
                user.setLogin(true);
                return true;
            }
        }
        return false;
    }

    public static String buildHeroRange(Map<String, HeroTable> heroTableCache) {
        StringBuilder sb = new StringBuilder("<b>排行榜</b><br>");
        try {
            List<ServerRecord> records = new ArrayList<>();
            for (Map.Entry<String, HeroTable> entry : heroTableCache.entrySet()) {
                try {
                    HeroTable table = entry.getValue();
                    String name = entry.getKey();
                    ServerRecord record = table.getRecord(name);
                    if (record.getData() != null && record.getData().getHero() != null) {
                        records.add(record);
                    }
                } catch (Exception e) {
                    LogHelper.error(e);
                }
            }
            records.sort(new Comparator<ServerRecord>() {
                @Override
                public int compare(ServerRecord r1, ServerRecord r2) {
                    return Integer.compare(r1.getRange(), r2.getRange());
                }
            });
            for (int i = 0; i < records.size() && i < 5; i++) {
                ServerData data = records.get(i).getData();
                if (data != null)
                    sb.append(data.getHero().getDisplayName()).append("<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：").append(records.get(i).winRate()).append("<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static Effect buildEffect(String effectName, String value) {
        switch (effectName) {
            case "SkillRateEffect":
                SkillRateEffect skillRateEffect = new SkillRateEffect();
                skillRateEffect.setSkillRate(Float.parseFloat(value));
                return skillRateEffect;
            case "AgiEffect":
                AgiEffect agiEffect = new AgiEffect();
                agiEffect.setAgi(Long.parseLong(value));
                return agiEffect;
            case "AtkEffect":
                AtkEffect atkEffect = new AtkEffect();
                atkEffect.setAtk(Long.parseLong(value));
                return atkEffect;
            case "DefEffect":
                DefEffect defEffect = new DefEffect();
                defEffect.setDef(Long.parseLong(value));
                return defEffect;
            case "HpEffect":
                HpEffect hpEffect = new HpEffect();
                hpEffect.setHp(Long.parseLong(value));
                return hpEffect;
            case "StrEffect":
                StrEffect strEffect = new StrEffect();
                strEffect.setStr(Long.parseLong(value));
                return strEffect;
            case "MeetRateEffect":
                MeetRateEffect meetRateEffect = new MeetRateEffect();
                meetRateEffect.setMeetRate(Float.parseFloat(value));
                return meetRateEffect;
            case "PetRateEffect":
                PetRateEffect petRateEffect = new PetRateEffect();
                petRateEffect.setPetRate(Float.parseFloat(value));
                return petRateEffect;
        }
        return null;
    }

    //Only use for unit test
    static void clear() throws IOException, ClassNotFoundException {
        File root = new File("data");
        root.delete();
    }

    public void submitHero(ServerData data) throws IOException, ClassNotFoundException {
        data.getMaze().setId(data.getHero().getId());
        HeroTable table = heroTableCache.get(data.getHero().getId());
        if (table == null) {
            table = new HeroTable(heroDir);
            heroTableCache.put(data.getHero().getId(), table);
        }
        table.submitHero(data);
    }

    public ServerData getBackHero(String id) throws IOException {
        HeroTable table = heroTableCache.get(id);
        if (table != null) {
            heroTableCache.remove(id);
            return table.getBackHero(id);
        } else {
            return null;
        }
    }

    public void addTask(String name, String desc, String material, String accessory_name, String point, String accessory_color, String accessory_level, String accessory_element, String accessory_type, String accessory_effects) throws IOException {
        Task task = new Task(name, desc);
        task.setId(task.getName());
        task.setMaterial(Integer.parseInt(material));
        task.setPoint(Integer.parseInt(point));
        if (StringUtils.isNotEmpty(accessory_name)) {
            Accessory accessory = new Accessory();
            accessory.setName(accessory_name);
            accessory.setColor(accessory_color);
            for (String effect : accessory_effects.split(";")) {
                String[] ev = effect.split(":");
                if (ev.length > 1) {
                    accessory.getEffects().add(buildEffect(ev[0], ev[1]));
                }
            }
            accessory.setLevel(Long.parseLong(accessory_level));
            accessory.setElement(Element.getByName(accessory_element));
            accessory.setType(accessory_type);
        }
        //TODO Pet
        taskTable.save(task, task.getId());
    }

    public Serializable openOnlineGift(String ownerId) {
        Medallion medallion = new Medallion();
        medallion.setCount(1);
        return medallion;
    }

    public boolean requestExchange(Object objMy, ExchangeObject exServer, String id) throws Exception {
        ExchangeObject exMy = createExchangeObject(id, objMy);
        if (exServer.change(exMy)) {
            exMy.setChanged(exServer);
            exMy.setChangedTime(System.currentTimeMillis());
            exMy.setAcknowledge(true);
            exchangeTable.addExchange(exMy);
            return true;
        }
        return false;
    }

    @NotNull
    public Object get_back_exchange(String id) throws IOException {
        ExchangeObject exchangeMy = exchangeTable.loadObject(id);
        if (exchangeMy == null) {
            return 1;
        }
        try {
            exchangeMy.getLock().tryLock();
            if (exchangeMy.getChanged() != null) {
                return exchangeMy;
            }
            exchangeTable.removeObject(exchangeMy);
            return RESPONSE_RESULT_SUCCESS;
        } finally {
            exchangeMy.getLock().unlock();
        }
    }

    @NotNull
    public boolean acknowledge(String id) {
        ExchangeObject exchangeMy = exchangeTable.loadObject(id);
        if (exchangeMy != null) {
            exchangeMy.setAcknowledge(true);
            if (exchangeMy.getChanged() != null && exchangeMy.getAcknowledge()) {
                exchangeTable.removeObject(exchangeMy);
            }
            return true;
        } else {
            return false;
        }
    }

    public void stop() {
        executor.shutdown();
        for (HeroTable table : heroTableCache.values()) {
            try {
                table.save();
            } catch (IOException e) {
                LogHelper.error(e);
            }
        }
    }

    public void start() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                HashMap<String, HeroTable> tableCache = new HashMap<>(heroTableCache);
                new HeroBattleService(tableCache, new ArrayList<>(groups), MainProcess.this).run();
                heroRange = buildHeroRange(tableCache);
            }
        }, 0, user.getBattleInterval(), TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(warehouseTable, 0, 1, TimeUnit.DAYS);
    }

    public ServerData queryHeroData(String id) {
        if (StringUtils.isNotEmpty(id)) {
            HeroTable table = heroTableCache.get(id);
            if (table != null) {
                ServerRecord record = table.getRecord(id);
                if (record != null && record.getData() != null) {
                    return record.getData();
                }
            }
        }
        return null;
    }

    public String queryBattleMessages(String id, int count){
        if (StringUtils.isNotEmpty(id)) {
            HeroTable table = heroTableCache.get(id);
            if (table != null) {
                return table.pollBattleMsg(id, count);
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    private Map<String, HeroTable> initHeroTableCache(File root) {
        Map<String, HeroTable> cache = new HashMap<>();
        if (root.exists()) {
            for (String name : root.list()) {
                try {
                    HeroTable table = new HeroTable(root);
                    if (table.getHero(name, 0) != null) {
                        cache.put(name, table);
                    }
                } catch (Exception e) {
                    LogHelper.error(e);
                }
            }
        } else {
            LogHelper.info("create root file " + root.getAbsolutePath() + ", result: " + root.mkdirs());
        }
        return cache;
    }

    @NotNull
    private ExchangeObject createExchangeObject(String ownerId, Object objMy) throws Exception {
        ExchangeObject exMy = new ExchangeObject((IDModel) objMy, ownerId);
        if (objMy instanceof Pet) {
            exMy.setType(1);
        } else if (objMy instanceof Accessory) {
            exMy.setType(2);
        } else if (objMy instanceof Goods) {
            exMy.setType(3);
        } else {
            throw new Exception("Wrong exchange type!");
        }
        exMy.setSubmitTime(System.currentTimeMillis());
        return exMy;
    }
    public String queryBattleAward(String id){
        if (StringUtils.isNotEmpty(id)) {
            HeroTable table = heroTableCache.get(id);
            if (table != null) {
                return table.queryBattleAward(id);
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public int getGiftCount(String id){
        HeroTable table = heroTableCache.get(id);
        if(table!=null) {
            ServerRecord record = table.getRecord(id);
            if (record != null) {
                return record.getGift();
            } else {
                return 0;
            }
        }return 0;
    }

    public void addGroup(String h1, String h2){
        GroupHolder holder = new GroupHolder();
        holder.getHeroIds().add(h1);
        holder.getHeroIds().add(h2);
        groups.add(holder);
    }

    public void removeGroup(String heroId){
        for(GroupHolder holder : new ArrayList<>(groups)){
            if(holder.isInGroup(heroId)){
                groups.remove(holder);
            }
        }
    }

    public void submitBoss(String name, String element, String race, String atk, String def, String hp, String hpG, String atkG, String defG){
        Hero hero = new Hero();
        hero.setDef(Long.parseLong(def));
        hero.setAtk(Long.parseLong(atk));
        hero.setMaxHp(Long.parseLong(hp));
        hero.setHp(hero.getMaxHp());
        hero.setName(name);
        hero.setRace(Integer.parseInt(race));
        hero.setElement(Element.valueOf(element));
        hero.setHpGrow(Integer.parseInt(hpG));
        hero.setDefGrow(Integer.parseInt(defG));
        hero.setAtkGrow(Integer.parseInt(atkG));
        try {
            NPCTable table = new NPCTable(new File("data/npc"));
            table.save(hero);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveUserConfig(String name, int pass, String sing, int battlInterval){
        if(user!=null){
            user.setName(name);
            user.setPass(pass);
            user.setSing(sing);
            user.setBattleInterval(battlInterval);
            try {
                userDb.save(user);
            } catch (IOException e) {
                LogHelper.error(e);
            }
        }
    }

    public List<ServerRecord> queryRecords(int start, int row, String key){
        List<ServerRecord> srs = new ArrayList<>();
        ArrayList<Map.Entry<String, HeroTable>> tables = new ArrayList<>(heroTableCache.entrySet());
        for(int i = start; i< tables.size() && srs.size() < row; i++){
            HeroTable table = tables.get(i).getValue();
            String id = tables.get(i).getKey();
            ServerRecord record = table.getRecord(id);
            if(record.getData()!=null) {
                if(record.getData().getHero()!=null){
                    if(record.getData().getHero().getDisplayName().contains(key) || record.getData().getHero().getId().contains(key)){
                        srs.add(record);
                    }
                }
            }
        }
        return srs;
    }

    public boolean updateRecord(ServerRecord record){
        ServerData data = record.getData();
        if(data!=null){
            HeroTable table = heroTableCache.get(data.getHero().getId());
            if(table != null){
                try {
                    table.save(record);
                    return true;
                } catch (IOException e) {
                    LogHelper.error(e);

                }
            }
        }
        return false;
    }

    public String getGroupMessage(String id){
        GroupHolder holder = null;
        for(GroupHolder holder1 : groups){
            if(holder1.isInGroup(id)){
                holder = holder1;
                break;
            }
        }
        if(holder!=null){
            StringBuilder builder = new StringBuilder();
            for(String hid : holder.getHeroIds()){
                ServerRecord record = queryRecord(hid);
                if(record!=null && record.getData()!=null && record.getData().getHero()!=null) {
                        builder.append(record.getData().getHero().getDisplayName()).append("<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：").append(record.winRate()).append("<br>");
                }
            }
            return builder.toString();
        }else {
            ServerRecord record = queryRecord(id);
            if(record!=null && record.getData()!=null && record.getData().getHero()!=null) {
                return record.getData().getHero().getDisplayName()+ "<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：" + record.winRate();
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public ServerRecord queryRecord(String id){
        HeroTable table = heroTableCache.get(id);
        if(table!=null){
            return table.getRecord(id);
        }
        return null;
    }


    public Hero postHeroByLevel(long level){
        for(Map.Entry<String, HeroTable> entry : heroTableCache.entrySet()){
            try {
                Hero hero = entry.getValue().getHero(entry.getKey());
                if(hero!=null){
                    Maze maze = entry.getValue().getMaze(entry.getKey(), level);
                    if(maze!=null){
                        if(Math.abs(maze.getMaxLevel() - level) < 50){
                            return hero;
                        }
                    }
                }
            } catch (Exception e) {
                LogHelper.error(e);
            }
        }
        return null;
    }
}
