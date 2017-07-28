package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.GroupHolder;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.SellItem;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.types.Grill;
import cn.luo.yuan.maze.model.goods.types.Medallion;
import cn.luo.yuan.maze.model.goods.types.Omelet;
import cn.luo.yuan.maze.model.goods.types.ResetSkill;
import cn.luo.yuan.maze.serialize.ObjectTable;
import cn.luo.yuan.maze.server.model.User;
import cn.luo.yuan.maze.server.persistence.ExchangeTable;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import cn.luo.yuan.maze.server.persistence.NPCTable;
import cn.luo.yuan.maze.server.persistence.ShopTable;
import cn.luo.yuan.maze.server.persistence.WarehouseTable;
import cn.luo.yuan.maze.server.persistence.db.DatabaseConnection;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.task.Task;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.luo.yuan.maze.utils.Field.RESPONSE_RESULT_SUCCESS;

/**
 * Created by gluo on 7/6/2017.
 */
public class MainProcess {
    private final static String sign_match = "308202f9308201e1a003020102020413c56148300d06092a864886f70d01010b0500302d310b3009060355040813024744310b3009060355040713025a483111300f060355040313084c756f205975616e301e170d3135303930313037353531315a170d3430303832353037353531315a302d310b3009060355040813024744310b3009060355040713025a483111300f060355040313084c756f205975616e30820122300d06092a864886f70d01010105000382010f003082010a028201010098f398450e3cf13f8f7106c59f157be54eac63a0237ae11596f5b5cefd2228e8befd012c4673bec4c1cc90f21a585c0d4006726c0f0056f6bdb2ddead630227918318e0d6432e4b8cc6b65d0193afcd42c6a9f85b549f66bea8f1297ca374e437a7da338b234bc2e1a4bb2860f7fca1699d1c6e34ed897784d4a728c511241d3e0fe3879ea24460bac0b07010bef3c61d868c2c65cd458e6f79e032d845134a0da3009f9f687d4917ffeeab701d2b933d68580e6e9e47c110afc6633867d74b93836a43d31b824f83f7b0f7b70abda65bfd7a673d7ae0cf2d4b30481d09a51ba3e8f6d8175d6425e3c6b37dc9463e098ac549e5cfda8b1e35de0a2d188ab9bcd0203010001a321301f301d0603551d0e041604147ab8e6bfe5f22df19046f038eff017784c04c694300d06092a864886f70d01010b050003820101005b780aeee5909829165b51dda614d7a73c6caeab3ac784d730a98ef0d98e55095bf9d9fe95fa435bdf4d1cd939b0f1285141431686906c6d9547ac798a076d5da36cfad51be641b3e020d2b6bc391d1532f9c48b9f0575a4e8e4c6b525eb343e501efa4ad263e8ba12dfd08090aa27bd69cb43937075fd7fbb038f574e4e3f801b6d93a45b6fdebb94b79c0acbe9e9f901d0b518c9efe18939144f0942163b994c63a0bdab2627f5ffc16859feca9df40a57b6841ed9593a3a0aab57d0db10f529fc399163fa2ce5e62070eecff2b09678d43bcf7b66b6c84b94f102311d64d6e7910cfadcf7bf52963824055276907329b84130a847820aff9fb3f4852203c3";
    public static boolean debug = true;
    public static MainProcess process;
    public String sing = StringUtils.EMPTY_STRING;
    public ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    public User user;
    public String heroRange = StringUtils.EMPTY_STRING;
    public WarehouseTable warehouseTable;
    public ExchangeTable exchangeTable;
    public ObjectTable<Task> taskTable;
    public Set<GroupHolder> groups = Collections.synchronizedSet(new HashSet<>());
    public HeroTable heroTable;
    public GameContext context = new GameContext();
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
        taskTable = new ObjectTable<>(Task.class, new File(this.root, Task.class.getName()));
        heroTable = new HeroTable(heroDir);
        userDb = new ObjectTable<User>(User.class, new File(this.root, Task.class.getName()));
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

    //Only use for unit test
    static void clear() throws IOException, ClassNotFoundException {
        File root = new File("data");
        root.delete();
    }

    public boolean isSignVerify(String sign) {
        return debug || !StringUtils.isNotEmpty(sign_match) || sign_match.equalsIgnoreCase(sign);
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
                    accessory.getEffects().add(EffectHandler.buildEffect(ev[0], ev[1]));
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
                    case 4:
                        record.setRestoreLimit(record.getRestoreLimit() + 2);
                        record.getMessages().add("重生次数增加了！");
                        return "战斗塔重生次数增加两次";
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
                new HeroBattleService(heroTable, MainProcess.this).run();
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

    public boolean submitExchange(String ownerId, String limit, Object eo, int expectType) {
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
        if (StringUtils.isNotEmpty(id)) {
            for (GroupHolder holder1 : groups) {
                if (holder1.isInGroup(id)) {
                    holder = holder1;
                    break;
                }
            }
        }
        if (holder != null) {
            StringBuilder builder = new StringBuilder();
            for (String hid : holder.getHeroIds()) {
                ServerRecord record = queryRecord(hid);
                if (record != null && record.getData() != null && record.getData().getHero() != null) {
                    builder.append(record.getData().getHero().getDisplayName())
                            .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：").append(record.winRate())
                            .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;剩余复活次数：").append(StringUtils.formatNumber(record.getRestoreLimit() - record.getDieCount()))
                            .append("<br>");
                }
            }
            return builder.toString();
        } else {
            ServerRecord record = queryRecord(id);
            if (record != null && record.getData() != null && record.getData().getHero() != null) {
                return record.getData().getHero().getDisplayName() +
                        "<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：" + record.winRate() +
                        "<br>&nbsp;&nbsp;&nbsp;&nbsp;剩余复活次数：" + StringUtils.formatNumber(record.getRestoreLimit() - record.getDieCount());
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

    public void addAccessory(String name, String tag, String type, String author, String... effects) {
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
    }

    public void addOnlineGift(String id, int count) {
        ServerRecord record = heroTable.getRecord(id);
        if (record != null) {
            record.setGift(record.getGift() + count);
        }
    }

    public String getOnlineHeroList() {
        StringBuilder builder = new StringBuilder("<html><meta charset=\"utf-8\"><title>Heros</title><body>List Hero：<br>");
        for (String id : heroTable.getAllHeroIds()) {
            ServerRecord record = heroTable.getRecord(id);
            builder.append(id).append(": ");
            if (record != null) {
                builder.append("Range: ").append(record.getRange()).append("<br>");
                builder.append("Submit: ").append(StringUtils.formatData(record.getSubmitDate())).append("<br>");
                if (record.getData() != null && record.getData().getHero() != null) {
                    builder.append(record.getData().getHero());
                } else {
                    builder.append("NAN");
                }
            } else {
                builder.append("NAN");
            }
            builder.append("<br><hr/>");
        }
        builder.append("</body></html>");
        return builder.toString();
    }

    public String uploadFile(String name, InputStream stream) {
        SaveService.root = root;
        return SaveService.instance.saveFile(name, stream);
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
