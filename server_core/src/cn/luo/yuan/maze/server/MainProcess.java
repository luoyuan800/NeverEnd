package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.exception.CribberException;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.dlc.DLC;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.types.ChangeHead;
import cn.luo.yuan.maze.model.goods.types.Evolution;
import cn.luo.yuan.maze.model.goods.types.Filter;
import cn.luo.yuan.maze.model.goods.types.Grill;
import cn.luo.yuan.maze.model.goods.types.HPM;
import cn.luo.yuan.maze.model.goods.types.HalfSafe;
import cn.luo.yuan.maze.model.goods.types.Invincible;
import cn.luo.yuan.maze.model.goods.types.Mirror;
import cn.luo.yuan.maze.model.goods.types.Omelet;
import cn.luo.yuan.maze.model.goods.types.ResetSkill;
import cn.luo.yuan.maze.model.goods.types.Restrictor;
import cn.luo.yuan.maze.model.goods.types.Scalpel;
import cn.luo.yuan.maze.model.real.RealState;
import cn.luo.yuan.maze.model.real.action.RealTimeAction;
import cn.luo.yuan.maze.model.task.Scene;
import cn.luo.yuan.maze.model.task.Task;
import cn.luo.yuan.maze.server.persistence.*;
import cn.luo.yuan.serialize.FileObjectTable;
import cn.luo.yuan.serialize.ObjectTable;
import cn.luo.yuan.maze.server.bomb.BombRestConnection;
import cn.luo.yuan.maze.server.bomb.json.MyJSON;
import cn.luo.yuan.maze.server.bomb.json.MyJSONValue;
import cn.luo.yuan.maze.server.bomb.json.SimpleToken;
import cn.luo.yuan.maze.server.level.RealService;
import cn.luo.yuan.maze.server.model.User;
import cn.luo.yuan.maze.persistence.DatabaseConnection;
import cn.luo.yuan.maze.server.servcie.ServerDataManager;
import cn.luo.yuan.maze.server.servcie.ServerGameContext;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gluo on 7/6/2017.
 */
public class MainProcess {
    public static boolean debug = false;
    public static MainProcess process;
    public ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    public User user;
    public String heroRange = StringUtils.EMPTY_STRING;
    public WarehouseTable warehouseTable;
    public ExchangeTable exchangeTable;
    public ObjectTable<Task> taskTable;
    public ObjectTable<Scene> sceneTable;
    public MonsterTable monsterTable;
    public HeroTable heroTable;
    public GroupTable groups;
    public GameContext context = new GameContext();
    public File root;
    private String sign_match = StringUtils.EMPTY_STRING;
    private String version = StringUtils.EMPTY_STRING;
    private ObjectTable<User> userDb;
    private File heroDir;
    private DatabaseConnection database;
    private ShopTable shop;
    private CribberTable cribber;
    private DLCTable dlcTable;
    private cn.luo.yuan.maze.utils.Random random = new Random(System.currentTimeMillis());
    private ReleaseManager releaseManager;
    private CDKEYTable cdkeyTable;
    private SaveService saveService;
    private RealService realService;

    public MainProcess(String root) throws IOException, ClassNotFoundException {
        this.root = new File(root);
        this.heroDir = new File(root, "hero");
        warehouseTable = new WarehouseTable(this.root);
        exchangeTable = new ExchangeTable(this.root);
        taskTable = new FileObjectTable<>(Task.class, this.root);
        sceneTable = new FileObjectTable<>(Scene.class, this.root);
        heroTable = new HeroTable(heroDir);
        userDb = new FileObjectTable<User>(User.class, this.root);
        monsterTable = new MonsterTable(this.root);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SaveService.setRoot(this.root);
        saveService = SaveService.instance();
        realService = new RealService(this);
    }

    //Only use for unit test
    static void clear() throws IOException, ClassNotFoundException {
        File root = new File("data");
        root.delete();
    }

    public int isSignVerify(String sign, String version) {
//        return debug || !StringUtils.isNotEmpty(sign_match) || (sign_match.equalsIgnoreCase(sign) && (MainProcess.version.equals(version)||version_Dot.equals(version)));
        boolean verify = true;
        if (StringUtils.isNotEmpty(version) && StringUtils.isNotEmpty(this.version)) {
            try {
                int vi = Integer.parseInt(this.version);
                int vm = Integer.parseInt(version);
                if (vm < vi) {
                    //LogHelper.info("Verify version: " + version + " not match " + this.version);
                    return -1;
                }
            } catch (Exception e) {
                LogHelper.error(e);
                verify = this.version.equalsIgnoreCase(version);
                if (!verify) {
                    //LogHelper.info("Verify version: " + version + " not match " + this.version);
                    return -1;
                }
            }
        }
        return ((StringUtils.isEmpty(sign) && StringUtils.isEmpty(version)) || debug || !StringUtils.isNotEmpty(sign_match) || (sign_match.equalsIgnoreCase(sign))) ? 1 : 0;
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
                            .append(data.getMaze() != null ? ("&nbsp;" + StringUtils.formatNumber(data.getMaze().getMaxLevel(), false) + "层") : "")
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

    public String newCdKey() {
        return cdkeyTable.newCdKey();
    }

    public boolean submitHero(ServerData data) throws IOException, ClassNotFoundException, CribberException {
        checkCribber(data);
        data.getMaze().setId(data.getHero().getId());
        heroTable.submitHero(data);
        return true;
    }

    public ServerData getBackHero(String id) throws IOException {
        removeGroup(id);
        return heroTable.getBackHero(id);
    }

    public void addTask(String name, String desc, String material, String accessory_name, String point, String accessory_color, String accessory_level, String accessory_element, String accessory_type, String accessory_effects) throws IOException {
        /*Task task = new Task(name, desc);
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
                    accessory.getContainsEffects().add(EffectHandler.buildEffect(ev[0], ev[1]));
                }
            }
            accessory.setLevel(Long.parseLong(accessory_level));
            accessory.setElement(Element.getByName(accessory_element));
            accessory.setType(accessory_type);
        }
        //TODO Pet
        taskTable.save(task, task.getId());*/
    }

    public Serializable openOnlineGift(String ownerId) {
        ServerRecord record = heroTable.getRecord(ownerId);
        if (record != null && record.getGift() > 0) {
            try {
                switch (random.nextInt(16)) {
                    case 0:
                        HalfSafe medallion = new HalfSafe();
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
                    case 4:
                        record.setRestoreLimit(record.getRestoreLimit() + 2);
                        record.getMessages().add("重生次数增加了！");
                        return "战斗塔重生次数增加两次";
                    case 6:
                        record.setRestoreLimit(record.getRestoreLimit() + 1);
                        record.getMessages().add("重生次数增加了！");
                        return "战斗塔重生次数增加一次";
                    case 5:
                        ServerData data = record.getData();
                        if (data != null) {
                            data.setMaterial(data.getMaterial() + 2000);
                            return "获得2000点锻造";
                        }
                        return null;
                    case 7:
                        HPM hpm = new HPM();
                        hpm.setCount(1);
                        return hpm;
                    case 8:
                        ChangeHead ch = new ChangeHead();
                        ch.setCount(1);
                        return ch;
                    case 9:
                        record.setDebris(record.getDebris() + 1);
                        return "获得一块碎片";
                    case 10:
                        Evolution evolution = new Evolution();
                        evolution.setCount(1);
                        return evolution;
                    case 11:
                        if(random.nextInt(3) == 0) {
                            Filter filter = new Filter();
                            filter.setCount(1);
                            return filter;
                        } else if(random.nextInt(2) == 0){
                            Restrictor restrictor = new Restrictor();
                            restrictor.setCount(1);
                            return restrictor;
                        } else return null;
                    case 12:
                        int mi = random.nextInt(100);
                        if(mi == 1){
                            Mirror mirror = new Mirror();
                            mirror.setCount(0);
                            return mirror;
                        }else if(mi <=5){
                            Scalpel scalpel = new Scalpel();
                            scalpel.setCount(1);
                            return scalpel;
                        } else return null;
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
        if (checkCribber(id)) return false;
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

    public boolean checkCribber(String id) {
        if (cribber != null) {
            if (cribber.isCribber(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCribber(ServerData data) throws CribberException {
        if (cribber != null) {
            if (cribber.isCribber(data)) {
                throw new CribberException(data.getId(), data.getMac(), data.getHero() != null ? data.getHero().getName() : " ");
            }
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
            exchangeTable.removeObject(exchangeMy);
            return exchangeMy;
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
                if (exchangeMy.getChanged() != null && exchangeMy.getChanged().getAcknowledge()) {
                    exchangeTable.removeObject(exchangeMy.getChanged());
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void stop() {
        save();
        executor.shutdown();
        realService.stop();
        database.close();
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
            groups = new GroupTable(database);
            shop = new ShopTable(database, root);
            cribber = new CribberTable(database);
            cdkeyTable = new CDKEYTable(database);
            try (Connection connection = database.getConnection()) {
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE IF NOT EXISTS `verify` ( `id` INTEGER UNSIGNED NOT NULL DEFAULT 0, `sign` TEXT, `version` VARCHAR(10), PRIMARY KEY (`id`)) ENGINE = InnoDB;");
                ResultSet rs = statement.executeQuery("select sign,version from verify");
                if (rs.next()) {
                    sign_match = rs.getString("sign");
                    version = rs.getString("version");
                }
                rs.close();
                statement.execute("CREATE TABLE IF NOT EXISTS rang_award (" +
                        "  `range` INTEGER UNSIGNED NOT NULL," +
                        "  debris INTEGER UNSIGNED DEFAULT 0," +
                        "  gift INTEGER UNSIGNED DEFAULT 0," +
                        "  mate INTEGER UNSIGNED DEFAULT 0," +
                        "  PRIMARY KEY (`range`)" +
                        ")");
                statement.close();
            } catch (Exception e) {
                LogHelper.error(e);
            }
            releaseManager = new ReleaseManager(database, new File(root, "apk"));
        }
        realService.run();
        dlcTable = new DLCTable(this);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                LogHelper.info("Preparing battle info");
                new HeroBattleService(heroTable, groups, executor,user.getBattleInterval()).run();
                LogHelper.info("updating range message");
                heroRange = buildHeroRange();
                LogHelper.info("updated range message");
            }
        }, 0, user.getBattleInterval(), TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(warehouseTable, 1, 1, TimeUnit.DAYS);
        executor.scheduleAtFixedRate(warehouseTable.getAccessoryWH(), 100, 3000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(warehouseTable.getPetWH(), 100, 3000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(warehouseTable.getGoodsWH(), 100, 3000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                save();
            }
        }, 4, 4, TimeUnit.HOURS);
        executor.scheduleAtFixedRate(taskTable, 100, 3000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(sceneTable, 111, 3000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(userDb, 111, 300, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(heroTable.getDb(), 99, 200, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(heroTable, 20, 20, TimeUnit.HOURS);
        executor.scheduleAtFixedRate(exchangeTable.getExchangeDb(), 120, 200, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(monsterTable.getMonsterTable(), 120, 500, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(dlcTable.getDLCTable(), 525, 1500, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                for (String id : new ArrayList<String>(heroTable.getAllHeroIds())) {
                    ServerRecord record = heroTable.getRecord(id);
                    if (record != null) {
                        if (record.getRange() < 150 && record.getData()!=null) {
                            record.setAward(true);
                        } else {
                            record.setAward(false);
                        }
                    }
                }
            }
        }, 0, 1, TimeUnit.DAYS);
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

    public GroupHolder addGroup(String h1, String h2) {

        return groups.add(h1, h2);
    }

    public void removeGroup(String heroId) {
        groups.remove(heroId);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean submitExchange(String ownerId, String limit, Object eo, int expectType) {
        if (checkCribber(ownerId)) return false;
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
       return groups.getGroupMessage(id, heroTable);
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
                            Hero clone = hero.clone();
                            clone.setId(String.valueOf(maze.getMaxLevel()));
                            return clone;
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

    public Accessory addAccessory(String name, String tag, String type, String author, String... effects) {
        LogHelper.info("Add accessory: " + name);
        Accessory accessory = new Accessory();
        accessory.setName(name);
        accessory.setDesc(tag);
        accessory.setAuthor(author);
        accessory.setType(type);
        for (String effect : effects) {
            if (StringUtils.isNotEmpty(effect)) {
                String[] ss = StringUtils.split(effect, ",|，");
                if (ss.length >= 2) {
                    Effect e = EffectHandler.buildEffect(ss[0].trim(), ss[1].trim());
                    if (e != null) {
                        if (ss.length > 2 && StringUtils.isNotEmpty(ss[2])) {
                            e.setElementControl(true);
                        }
                        accessory.getEffects().add(e);
                    }
                }
            }
        }
        shop.add(accessory);
        return accessory;
    }

    public String updateShopAccessory(int start) {
        List<Accessory> accessories = new ArrayList<>(10);
        BombRestConnection connection = new BombRestConnection();
        for (int i = start; i < start + 5; i++) {
            try {
                MyJSON json = connection.queryObjects("SelfAccessory", "createAt", i, 1);
                json.parse();
                List<SimpleToken> tokens = json.getTokens();
                SimpleToken nameToken;
                if (tokens.size() == 3) {
                    nameToken = tokens.get(2);
                } else if (tokens.size() == 2) {
                    nameToken = tokens.get(1);
                } else {
                    continue;
                }
                List<String> effectString = new ArrayList<>();
                SimpleToken aeffectToken = tokens.get(0);
                for (Map.Entry<String, MyJSONValue> entry : aeffectToken.getData().entrySet()) {
                    String effect = buildEffectString(entry, true);
                    if (StringUtils.isNotEmpty(effect)) {
                        effectString.add(effect);
                    }
                }
                if (tokens.size() == 3) {
                    SimpleToken effectToken = tokens.get(1);
                    for (Map.Entry<String, MyJSONValue> entry : effectToken.getData().entrySet()) {
                        String effect = buildEffectString(entry, false);
                        if (StringUtils.isNotEmpty(effect)) {
                            effectString.add(effect);
                        }
                    }
                }
                int typenumber = Integer.parseInt(nameToken.getValue("type").toString());
                String type = "";
                switch (typenumber) {
                    case 1:
                        type = Field.RING_TYPE;
                        break;
                    case 2:
                        type = Field.NECKLACE_TYPE;
                        break;
                    case 0:
                        type = Field.HAT_TYPE;
                        break;
                }
                if (Boolean.parseBoolean(nameToken.<String>getValue("isConform"))) {
                    accessories.add(addAccessory(nameToken.<String>getValue("name"), nameToken.<String>getValue("desc"), type, nameToken.<String>getValue("userName"), effectString.toArray(new String[effectString.size()])));
                }
            } catch (Exception e) {
                LogHelper.error(e);
            }
        }
        StringBuilder builder = new StringBuilder();
        for (Accessory accessory : accessories) {
            builder.append(accessory.getName()).append("<br>");
        }
        return builder.toString();
    }

    public void addOnlineGift(String id, int count) {
        ServerRecord record = heroTable.getRecord(id);
        if (record != null) {
            record.setGift(record.getGift() + count);
        }
    }

    public String getOnlineHeroList() {
        List<String> allHeroIds = heroTable.getAllHeroIds();
        StringBuilder builder = new StringBuilder("{\"total\":").append(allHeroIds.size()).append(",\"rows\":[");
        List<String> sortedByDescending = heroTable.getAllHeroIds();
        sortedByDescending.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                ServerRecord record1 = heroTable.getRecord(o1);
                ServerRecord record2 = heroTable.getRecord(o2);
                if (record1 != null && record2 != null) {
                    return Integer.compare(record1.getRange(), record2.getRange());
                }
                return 0;
            }
        });
        for (String id : sortedByDescending) {
            ServerRecord record = heroTable.getRecord(id);
            if (record != null && record.getData() != null) {
                String string = formatJson(record);
                if (StringUtils.isNotEmpty(string)) {
                    builder.append(string).append(",");
                }
            }
        }
        builder.replace(builder.lastIndexOf(","), builder.length(), "");
        builder.append("]}");
        return builder.toString();
    }

    public String uploadFile(String name, InputStream stream) {
        return saveService.saveFile(name, stream);
    }

    public String uploadSaveFile(InputStream is, String ownerId) {
        ServerRecord record = heroTable.getRecord(ownerId);
        if (record != null && record.getDebris() >= Data.UPLOAD_SAVE_DEBRIS) {
            record.setDebris(record.getDebris() - Data.UPLOAD_SAVE_DEBRIS);
            try {
                heroTable.save(record);
            } catch (IOException e) {
                LogHelper.error(e);
            }
            return saveService.saveSave(is);
        } else {
            return StringUtils.EMPTY_STRING;
        }
    }

    public void deleteSaveFile(String id) {
        saveService.delete(id);
    }

    public List<Task> queryTask(int start, int row, Set<String> filter) {
        try {
            return taskTable.loadLimit(start, row, new Index<Task>() {
                @Override
                public boolean match(Task task) {
                    return !filter.contains(task.getId());
                }
            }, null);
        } catch (Exception e) {
            LogHelper.error(e);
        }
        return Collections.emptyList();
    }

    public List<Scene> queryScenes(String taskId) {
        try {
            sceneTable.loadLimit(0, -1, new Index<Scene>() {
                @Override
                public boolean match(Scene scene) {
                    return scene.getTaskId().equals(taskId);
                }
            }, new Comparator<Scene>() {
                @Override
                public int compare(Scene o1, Scene o2) {
                    return Integer.compare(o1.getOrder(), o2.getOrder());
                }
            });
        } catch (Exception e) {
            LogHelper.error(e);
        }
        return Collections.emptyList();
    }

    public List<Monster> listMonster(int start, int count) {
        return monsterTable.listMonster(start, count);
    }

    public void addCribber(String id) {
        if (StringUtils.isNotEmpty(id) && cribber != null) {
            LogHelper.info("add cribber: " + id);
            ServerRecord record;
            record = heroTable.getRecord(id);
            String mac = record.getMac();
            LogHelper.info("delete cribber: " + id);
            heroTable.delete(id);
            cribber.addToCribber(id, StringUtils.isEmpty(mac) ? "NAN" : mac, record.getData() != null && record.getData().getHero() != null ? record.getData().getHero().getName() : "NAN");
        }
    }

    public String exchangeJson() {
        StringBuilder builder = new StringBuilder("{\"total\":").append(exchangeTable.getExchangeDb().size()).append(",\"rows\":[");
        for (String id : exchangeTable.getExchangeDb().loadIds()) {
            String str = formatJson(exchangeTable.loadObject(id));
            if (StringUtils.isNotEmpty(str)) {
                builder.append(str).append(",");
            }
        }
        builder.replace(builder.lastIndexOf(","), builder.length(), "");
        builder.append("]}");
        return builder.toString();
    }

    public int getDebris(String ownerId) {
        ServerRecord record = heroTable.getRecord(ownerId);
        if (record != null) {
            return record.getDebris();
        }
        return 0;
    }

    public void addDebris(String ownerId, int count) {
        ServerRecord record = heroTable.getRecord(ownerId);
        if (record != null) {
            record.setDebris(record.getDebris() + count);
        }
    }

    public List<DLCKey> queryDLCKeys(String ownerId) {
        ServerRecord record = heroTable.getRecord(ownerId);
        if (record != null) {
            return dlcTable.queryKeys(record.getDlcs(), ownerId);
        } else {
            return Collections.emptyList();
        }
    }

    public DLC getDlc(String ownerId, String id) {
        DLC dlc = dlcTable.getDLC(id);
        if (dlc != null) {
            dlc = dlc.clone();
            ServerRecord record = heroTable.getRecord(ownerId);
            if (dlc instanceof MonsterDLC && record.getDlcs().contains(id)) {
                dlc.setDebrisCost(dlc.getDebrisCost() / 2);
            }
            return dlc;
        }
        return null;
    }

    public boolean buyDlc(String ownerId, String id) {
        DLC dlc = dlcTable.getDLC(id);
        if (dlc != null) {
            ServerRecord record = heroTable.getRecord(ownerId);
            if (dlc instanceof MonsterDLC && record.getDlcs().contains(id)) {
                dlc.setDebrisCost(dlc.getDebrisCost() / 2);
            }
            if (record.getDebris() >= dlc.getDebrisCost()) {
                record.setDebris(record.getDebris() - dlc.getDebrisCost());
                record.getDlcs().add(dlc.getId());
            } else {
                return false;
            }

            return true;
        }
        return false;
    }

    public boolean storeIntoWarehouse(OwnedAble object) {
        ServerRecord record = heroTable.getRecord(object.getKeeperId());
        if (record != null && record.getDebris() > Data.WAREHOUSE_DEBRIS) {
            warehouseTable.store(object);
            record.setDebris(record.getDebris() - Data.WAREHOUSE_DEBRIS);
            return true;
        }
        return false;
    }

    public OwnedAble deleteFromWarehouse(String id, int type, String ownerId) {
        OwnedAble object = warehouseTable.retrieve(id, type);
        if (object != null) {
            if (object.getKeeperId().equals(ownerId)) {
                warehouseTable.delete(object);
                return object;
            }
        }
        return null;
    }

    public List<OwnedAble> warehouseList(String ownerId, int type) {
        return warehouseTable.retrieveAll(ownerId, type);
    }

    public String getLatestReleaseNotes() {
        if (releaseManager != null)
            return releaseManager.getReleaseNotes();
        else
            return StringUtils.EMPTY_STRING;
    }

    public int getReleaseVersion() {
        if (releaseManager != null)
            return releaseManager.getReleaseVersion();
        else return 0;
    }

    public byte[] downloadApk() {
        if (releaseManager != null) {
            return releaseManager.getApk(getReleaseVersion());
        } else {
            return new byte[0];
        }
    }

    public byte[] downloadSaveZip(String id) {
        return saveService.getSaveFile(id);
    }

    public Object useCdkey(String cdId, String userId) {
        ServerRecord record = heroTable.getRecord(userId);
        if (record != null && record.getCdkdys().contains(cdId)) {
            return "已经使用过该兑换码了";
        } else {
            KeyResult us = cdkeyTable.use(cdId);
            if (us.getVerify() && record != null) {
                record.setDebris(record.getDebris() + us.getDebris());
                record.setGift(record.getGift() + us.getGift());
                record.getCdkdys().add(cdId);
            } else {
                if(record == null) {
                    return "至少进入战斗塔一次之后才能兑换";
                }else{
                    return "该兑换码已经使用过了";
                }
            }
            return us;
        }
    }

    public RangeAward getRangeAward(String userId) {
        ServerRecord record = heroTable.getRecord(userId);
        try (Connection con = database.getConnection()) {
            try (Statement sta = con.createStatement()) {
                if (record != null && record.getAward()) {
                    RangeAward ra = new RangeAward();
                    int range = record.getRange();
                    ra.setRange(range);
                    ra.setId(userId);
                    if (range < 10) {
                        try (ResultSet rs = sta.executeQuery("select * from rang_award where `range` = " + range)) {
                            if (rs.next()) {
                                ra.setDebris(rs.getInt("debris"));
                                ra.setGift(rs.getInt("gift"));
                                ra.setMate(rs.getInt("mate"));
                            } else {
                                return null;
                            }
                        }
                    } else {
                        if(range < 30) {
                            ra.setDebris(1);
                            ra.setGift(5);
                            ra.setMate(8000);
                        }else{
                            Invincible invincible = new Invincible();
                            invincible.setCount(1);
                            ra.getGoods().add(invincible);
                        }
                    }
                    record.setDebris(record.getDebris() + ra.getDebris());
                    record.setGift(record.getGift() + ra.getGift());
                    if (record.getData() != null) {
                        record.getData().setMaterial(record.getData().getMaterial() + ra.getMate());
                    }
                    record.setAward(false);
                    heroTable.save(record);
                    return ra;
                }
            }
        } catch (Exception e) {
            LogHelper.error(e);
        }
        return null;
    }

    public String pollTopNPalaceRecords(int n){
        return realService.pollTopNRecord(n);
    }

    public RealState pollCurrentState(String id, int msgIndex, String battleId, RealState cstate) {
        return realService.pollState(id, msgIndex, battleId, cstate);
    }

    public void updateRealData(LevelRecord record) {
        realService.newOrUpdateRecord(record);
    }

    public LevelRecord pollRealRecord(String id){
        LevelRecord levelRecord = realService.queryRecord(id);
        //Clone it and clean not use data to save bandwidth
        levelRecord = levelRecord.clone();
        levelRecord.setHero(null);
        levelRecord.getPets().clear();
        levelRecord.getAccessories().clear();
        levelRecord.getSkills().clear();
        return levelRecord;
    }

    public void realBattleReady(String id){
        realService.ready(id);
    }

    public boolean realBattleAction(RealTimeAction action){
        if(action!=null) {
            return realService.action(action);
        }else{
            return false;
        }
    }

    public RealState quitRealBattle(String id, RealState cstate){
        return realService.singleQuit(id, cstate);
    }

    public void updateRealRecordPriorPoint(String id) {
        realService.updateRealRecordPriorPoint(id);
    }

    private String buildEffectString(Map.Entry<String, MyJSONValue> entry, boolean isElement) {
        String key = entry.getKey();
        Object value = entry.getValue().getValue();
        switch (key) {
            case "ADD_ATK":
                key = "AtkEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 50;
                }
                break;
            case "ADD_UPPER_HP":
                key = "HpEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 30;
                }
                break;
            case "ADD_DEF":
                key = "DefEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 30;
                }
                break;
            case "ADD_AGI":
                key = "AgiEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 10;
                }
                break;
            case "ADD_STR":
                key = "StrEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 20;
                }
                break;
            case "ADD_DODGE_RATE":
                key = "DogeRateEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 5;
                }
                break;
            case "ADD_PARRY":
                key = "ParryEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 5;
                }
                break;
            case "ADD_PER_ATK":
                key = "AtkPercentEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 7;
                }
                break;
            case "ADD_PER_UPPER_HP":
                key = "HPPercentEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 7;
                }
                break;
            case "ADD_PER_DEF":
                key = "DefPercentEffect";
                if (value instanceof Number) {
                    value = ((Number) value).longValue() / 7;
                }
                break;
            default:
                value = 0;
        }
        if (value instanceof Number && ((Number) value).longValue() > 0) {
            return key + "," + value.toString() + (isElement ? ",e" : ",");
        } else return StringUtils.EMPTY_STRING;
    }

    private String formatJson(ServerRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (record != null) {
            builder.append("\"id\":\"").append(record.getId()).append("\",");
            builder.append("\"Range\":").append(record.getRange()).append(",");
            builder.append("\"limit\":").append(record.getRestoreLimit() - record.getDieCount()).append(",");
            builder.append("\"Submit\":").append(record.getSubmitDate()).append(",");
            builder.append("\"mac\":\"").append(record.getMac()).append("\",");
            if (record.getData() != null && record.getData().getHero() != null) {
                builder.append("\"data\":\"").append(record.getData().getHero().toString().replaceAll("\"", "'")).append("\"");
                if(record.getData().getMaze()!=null) {
                    builder.append(",");
                    builder.append("\"level\":").append(record.getData().getMaze().getMaxLevel());
                }
            } else {
                builder.append("\"data\":\"").append("NAN").append("\"");
            }
        } else {
            return StringUtils.EMPTY_STRING;
        }
        builder.append("}");
        return builder.toString();
    }

    private String formatJson(ExchangeObject exchange) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (exchange != null) {
            builder.append("\"id\":\"").append(exchange.getId()).append("\",");
            builder.append("\"name\":\"").append(exchange.toString().replaceAll("\"", "'")).append("\",");
            builder.append("\"Submit\":").append(exchange.getSubmitTime()).append(",");
            if (exchange.getExchange() instanceof Accessory) {
                builder.append("\"data\":\"").append(StringUtils.formatEffectsAsHtml(((Accessory) exchange.getExchange()).getEffects()).replaceAll("\"", "'")).append("\"");
            } else if (exchange.getExchange() instanceof Pet) {
                builder.append("\"data\":\"").append(exchange.getExchange().toString()).append("\"");
            } else {
                builder.append("\"data\":\"").append("").append("\"");
            }
        } else {
            return StringUtils.EMPTY_STRING;
        }
        builder.append("}");
        return builder.toString();
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

    public long pollRealBattleTurn(String myId){
        return realService.pollBattleTurn(myId);
    }

    public LevelRecord pollBattleTargetRecord(String myId){
        return realService.pollTargetRecord(myId);
    }

    public static ServerGameContext buildGameContext(ServerRecord record, ScheduledExecutorService executor)  {
        NeverEndConfig heroConfig = new NeverEndConfig();
        heroConfig.setElementer(record.getData().isElementer());
        heroConfig.setLongKiller(record.getData().isLong());
        ServerDataManager hdm = new ServerDataManager(record.getData().getHero(), heroConfig);
        ServerGameContext hContext = new ServerGameContext(record.getData().getHero(), hdm, record.getData().getMaze());
        hContext.setExecutor(executor);
        return hContext;
    }

    public String changeDebris(int debris, String ownerId){
        ServerRecord record = heroTable.getRecord(ownerId);
        if(record!=null && record.getDebris() >= debris){
            record.setDebris(record.getDebris() - debris);
            String key = cdkeyTable.newCdKey(debris, 0, 0);
            record.getMyKeys().add(key);
            try {
                heroTable.save(record);
            } catch (IOException e) {
                LogHelper.error(e);
            }
            return key;
        }
        return null;
    }

    public ArrayList<String> queryMyKeys(String id){
        ServerRecord record = heroTable.getRecord(id);
        if(record!=null){
            return new ArrayList<>(record.getMyKeys());
        }
        return new ArrayList<>(0);
    }
}
