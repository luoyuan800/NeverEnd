package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.original.*;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.types.Grill;
import cn.luo.yuan.maze.model.goods.types.Medallion;
import cn.luo.yuan.maze.model.goods.types.Omelet;
import cn.luo.yuan.maze.model.goods.types.ResetSkill;
import cn.luo.yuan.maze.server.model.User;
import cn.luo.yuan.maze.server.persistence.*;
import cn.luo.yuan.maze.server.persistence.db.DatabaseConnection;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable;
import cn.luo.yuan.maze.task.Task;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
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
    public ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    public User user;
    public String heroRange = StringUtils.EMPTY_STRING;
    public WarehouseTable warehouseTable;
    public ExchangeTable exchangeTable;
    public ObjectTable<Task> taskTable;
    public List<GroupHolder> groups = new ArrayList<>();
    public HeroTable heroTable;
    private ObjectTable<User> userDb;
    private File root;
    private File heroDir;
    private DatabaseConnection database;
    private ShopTable shop;
    private cn.luo.yuan.maze.utils.Random random = new Random(System.currentTimeMillis());

    public MainProcess(String root) throws IOException, ClassNotFoundException {
        this.root = new File(root);
        this.heroDir = new File(root, "hero");
        warehouseTable = new WarehouseTable(this.root);
        exchangeTable = new ExchangeTable(this.root);
        taskTable = new ObjectTable<>(Task.class, this.root);
        heroTable = new HeroTable(heroDir);
        userDb = new ObjectTable<User>(User.class, this.root);
        process = this;
        user = userDb.loadObject("root");
        if (user == null) {
            user = new User();
            user.setPass(111);
            user.setName("luo");
            try {
                userDb.save(user, "root");
            } catch (IOException e) {
                LogHelper.error(e);
            }
        }

    }

    public String buildHeroRange() {
        StringBuilder sb = new StringBuilder("<b>排行榜</b><br>");
        try {
            List<ServerRecord> records = new ArrayList<>();
            for (String heroId : heroTable.getAllHeroIds()) {
                try {
                    ServerRecord record = heroTable.getRecord(heroId);
                    if (record != null && record.getData() != null && record.getData().getHero() != null) {
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
                if (data != null && data.getHero() != null)
                    sb.append(data.getHero().getDisplayName())
                            .append(data.getMaze() != null ? ("&nbsp;" + StringUtils.formatNumber(data.getMaze().getMaxLevel()) + "层") : "")
                            .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：").append(records.get(i).winRate()).append("<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public boolean login(int pass) {
        if (user.getLogin()) {
            return true;
        } else {
            if (user.getPass() == pass) {
                user.setLogin(true);
                return true;
            }
        }
        return false;
    }

    public void submitHero(ServerData data) throws IOException, ClassNotFoundException {
        data.getMaze().setId(data.getHero().getId());
        heroTable.submitHero(data);
    }

    public ServerData getBackHero(String id) throws IOException {
        return heroTable.getBackHero(id);
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
        ServerRecord record = heroTable.getRecord(ownerId);
        if (record != null && record.getGift() > 0) {
            try {
                switch (random.nextInt(5)) {
                    case 0:
                        Medallion medallion = new Medallion();
                        medallion.setCount(1);
                        return medallion;
                    case 1:
                        ResetSkill resetSkill = new ResetSkill();
                        resetSkill.setCount(1);
                        return resetSkill;
                    case 2:
                        Grill grill = new Grill();
                        grill.setCount(1);
                        return grill;
                    case 3:
                        Omelet omelet = new Omelet();
                        omelet.setCount(1);
                        return omelet;
                    default:
                        return null;
                }
            } finally {
                record.setGift(record.getGift() - 1);
            }
        } else {
            return null;
        }
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
        save();
        executor.shutdown();

    }

    public void save() {
        try {
            heroTable.save();
        } catch (IOException e) {
            LogHelper.error(e);
        }
    }

    public void start() {
        if (database != null) {
            shop = new ShopTable(database, root);
        }
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                LogHelper.info("Preparing battle info");
                new HeroBattleService(heroTable, new ArrayList<>(groups), MainProcess.this).run();
                LogHelper.info("updating range message");
                heroRange = buildHeroRange();
                LogHelper.info("updated range message");
            }
        }, 0, user.getBattleInterval(), TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(warehouseTable, 1, 1, TimeUnit.DAYS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                save();
            }
        }, 4, 4, TimeUnit.HOURS);
    }

    public ServerData queryHeroData(String id) {
        if (StringUtils.isNotEmpty(id)) {
            ServerRecord record = heroTable.getRecord(id);
            if (record != null && record.getData() != null) {
                return record.getData();
            }
        }
        return null;
    }

    public String queryBattleMessages(String id, int count) {
        if (StringUtils.isNotEmpty(id)) {
            return heroTable.pollBattleMsg(id, count);
        }
        return StringUtils.EMPTY_STRING;
    }


    public String queryBattleAward(String id) {
        if (StringUtils.isNotEmpty(id)) {
            return heroTable.queryBattleAward(id);
        } else {
            return StringUtils.EMPTY_STRING;
        }
    }


    public int getGiftCount(String id) {
        ServerRecord record = heroTable.getRecord(id);
        if (record != null) {
            return record.getGift();
        } else {
            return 0;
        }
    }

    public void addGroup(String h1, String h2) {
        GroupHolder holder = new GroupHolder();
        holder.getHeroIds().add(h1);
        holder.getHeroIds().add(h2);
        groups.add(holder);
    }

    public void removeGroup(String heroId) {
        for (GroupHolder holder : new ArrayList<>(groups)) {
            if (holder.isInGroup(heroId)) {
                groups.remove(holder);
            }
        }
    }

    public void submitBoss(String name, String element, String race, String atk, String def, String hp, String hpG, String atkG, String defG) {
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

    public void saveUserConfig(String name, int pass, String sing, int battlInterval) {
        if (user != null) {
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

    public boolean submitExchange(String ownerId, String limit, ExchangeObject eo, int expectType) {
        if (process.exchangeTable.addExchange(eo, ownerId, limit, expectType)) {
            LogHelper.info(eo + " submitted!");
            return true;
        } else {
            return false;
        }
    }

    public List<ServerRecord> queryRecords(int start, int row, String key) {
        List<ServerRecord> srs = new ArrayList<>();
        /*ArrayList<Map.Entry<String, HeroTable>> tables = new ArrayList<>(heroTableCache.entrySet());
        for (int i = start; i < tables.size() && srs.size() < row; i++) {
            HeroTable table = tables.get(i).getValue();
            String id = tables.get(i).getKey();
            ServerRecord record = table.getRecord(id);
            if (record.getData() != null) {
                if (record.getData().getHero() != null) {
                    if (record.getData().getHero().getDisplayName().contains(key) || record.getData().getHero().getId().contains(key)) {
                        srs.add(record);
                    }
                }
            }
        }*/
        return srs;
    }

    public boolean updateRecord(ServerRecord record) {
        ServerData data = record.getData();
        /*if (data != null) {
            HeroTable table = heroTableCache.get(data.getHero().getId());
            if (table != null) {
                try {
                    table.save(record);
                    return true;
                } catch (IOException e) {
                    LogHelper.error(e);

                }
            }
        }*/
        return false;
    }

    public String getGroupMessage(String id) {
        GroupHolder holder = null;
        for (GroupHolder holder1 : groups) {
            if (holder1.isInGroup(id)) {
                holder = holder1;
                break;
            }
        }
        if (holder != null) {
            StringBuilder builder = new StringBuilder();
            for (String hid : holder.getHeroIds()) {
                ServerRecord record = queryRecord(hid);
                if (record != null && record.getData() != null && record.getData().getHero() != null) {
                    builder.append(record.getData().getHero().getDisplayName())
                            .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：").append(record.winRate()).append("<br>");
                }
            }
            return builder.toString();
        } else {
            ServerRecord record = queryRecord(id);
            if (record != null && record.getData() != null && record.getData().getHero() != null) {
                return record.getData().getHero().getDisplayName() + "<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：" + record.winRate();
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public ServerRecord queryRecord(String id) {
        return heroTable.getRecord(id);

    }

    public Hero postHeroByLevel(long level) {
        for (String heroId : heroTable.getAllHeroIds()) {
            try {
                Hero hero = heroTable.getHero(heroId);
                if (hero != null) {
                    Maze maze = heroTable.getMaze(heroId, level);
                    if (maze != null) {
                        if (Math.abs(maze.getMaxLevel() - level) < 50) {
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

    public DatabaseConnection getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConnection database) {
        this.database = database;
    }

    public List<SellItem> getOnlineSell() {
        if (shop != null) {
            return shop.getAllSelling();
        } else {
            return Collections.emptyList();
        }
    }

    public void buy(String id, int count) {
        if (shop != null) {
            SellItem item = new SellItem();
            item.id = id;
            item.count = count;
            shop.sell(item);
        }
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
}
