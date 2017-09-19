package cn.luo.yuan.maze.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.model.OwnedAble;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsProperties;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SpecialSkill;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.persistence.database.Sqlite;
import cn.luo.yuan.maze.persistence.serialize.SerializeLoader;
import cn.luo.yuan.maze.serialize.ObjectTable;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyuan on 2017/3/19.
 */

/**
 * 多存档机制设计：
 * 存档用index区分，将存档存入数据库中，存放的是Hero对象
 * 需要额外增加保存的是创建时间，最后更新时间，在开始页面的时候查询这些数据（名字，层数，时间相关）然后显示
 * 用户选择了对应的存档编号后，使用对应的编号创建DataManager
 * 所以的相关数据在存入数据库的时候需要带上存档编号信息
 */
public class DataManager implements DataManagerInterface {
    //多存档，所以需要一个index来区分当前使用的是哪一个存档
    private int index;
    private SerializeLoader<Accessory> accessoryLoader;
    private SerializeLoader<Maze> mazeLoader;
    private SerializeLoader<Hero> heroLoader;
    private SerializeLoader<Pet> petLoader;
    private SerializeLoader<Goods> goodsLoader;
    private SerializeLoader<Skill> skillLoader;
    private SerializeLoader<ClickSkill> clickSkillLoader;
    private ObjectTable<NeverEndConfig> configDB;
    private ObjectTable<Hero> defenderDB;
    private List<ObjectTable> tables = new ArrayList<>();

    private Sqlite database;
    private Context context;
    private ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();

    public DataManager(int index, Context context) {
        this.index = index;
        this.database = Sqlite.getSqlite(context);
        this.context = context;
        accessoryLoader = new SerializeLoader<>(Accessory.class, context, index);
        mazeLoader = new SerializeLoader<>(Maze.class, context, index);
        heroLoader = new SerializeLoader<>(Hero.class, context, index);
        petLoader = new SerializeLoader<>(Pet.class, context, index);
        goodsLoader = new SerializeLoader<>(Goods.class, context, index);
        skillLoader = new SerializeLoader<>(Skill.class, context, index);
        clickSkillLoader = new SerializeLoader<>(ClickSkill.class, context, index);
        configDB = new ObjectTable<>(NeverEndConfig.class, context.getDir(String.valueOf(index), Context.MODE_PRIVATE));
        defenderDB = new ObjectTable<>(Hero.class, context.getDir("defend", Context.MODE_PRIVATE));
        this.context = context;
        registerTable(accessoryLoader.getDb());
        registerTable(petLoader.getDb());
        registerTable(goodsLoader.getDb());
        registerTable(skillLoader.getDb());
        registerTable(clickSkillLoader.getDb());
        registerTable(configDB);
        registerTable(defenderDB);
        tables.add(heroLoader.getDb());
        tables.add(mazeLoader.getDb());
    }

    public int getIndex() {
        return index;
    }

    public Hero loadHero() {
        String query = "select * from hero where hero_index = '" + index + "'";
        try (Cursor cursor = database.excuseSOL(query)) {
            if (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                Hero hero = heroLoader.load(id);
                if (hero != null) {
                    hero.setId(id);
                    return hero;
                }
            }
        }
        Hero hero = new Hero();
        hero.setIndex(index);
        return hero;
    }

    public Accessory loadAccessory(String id) {
        return accessoryLoader.load(id);
    }

    public List<Accessory> loadMountedAccessory(Hero hero) {
        List<Accessory> accessories = new ArrayList<>();
        for (Accessory accessory : accessoryLoader.loadAll()) {
            if (accessory != null && accessory.getHeroIndex() == index && accessory.isMounted()) {
                accessories.add(accessory);
            }
        }
        return accessories;
    }

    public void saveHero(Hero hero) {
        ContentValues values = new ContentValues();
        values.put("name", hero.getName());
        values.put("hero_index", hero.getIndex());
        values.put("element", hero.getElement() != null ? hero.getElement().name() : Element.NONE.name());
        values.put("reincarnate", hero.getReincarnate());
        values.put("last_update", System.currentTimeMillis());
        if (hero.getGift() != null)
            values.put("gift", hero.getGift().name());
        values.put("race", hero.getRace().name());
        if (StringUtils.isNotEmpty(hero.getId())) {
            heroLoader.update(hero);
            database.updateById("hero", values, hero.getId());
        } else {
            heroLoader.save(hero);
            values.put("id", hero.getId());
            values.put("created", System.currentTimeMillis());
            database.insert("hero", values);
        }
    }

    public void saveAccessory(Accessory accessory) {
        accessory.setHeroIndex(index);
        if (StringUtils.isNotEmpty(accessory.getId())) {
            //Already existed, 我们会进行update操作
            accessoryLoader.update(accessory);
        } else {
            //先添加一个新的记录
            accessoryLoader.save(accessory);
        }
    }

    public void saveMaze(Maze maze) {
        ContentValues values = new ContentValues();
        values.put("hero_index", index);
        values.put("level", maze.getLevel());
        values.put("max_level", maze.getMaxLevel());
        if (StringUtils.isNotEmpty(maze.getId())) {
            database.updateById("maze", values, maze.getId());
            mazeLoader.update(maze);
        } else {
            mazeLoader.save(maze);
            values.put("id", maze.getId());
            database.insert("maze", values);
        }
    }

    public Maze loadMaze() {
        try (Cursor cursor = database.excuseSOL("select id from maze where hero_index = " + index)) {
            if (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                Maze maze = mazeLoader.load(id);
                if (maze == null) {
                    maze = newMaze();
                    maze.setId(id);
                } else {
                    maze.setId(id);
                }
                return maze;
            } else {
                return newMaze();
            }
        }
    }

    public int getPetCount() {
        return petLoader.size();
    }

    /**
     * 删除当前存档的数据
     */
    public void clean() {

        try (Cursor cursor = database.excuseSOL("select id from maze where hero_index = " + index)) {
            while (!cursor.isAfterLast()) {
                mazeLoader.delete(cursor.getString(cursor.getColumnIndex("id")));
                cursor.moveToNext();
            }
        }
        try (Cursor cursor = database.excuseSOL("select id from hero where hero_index = " + index)) {
            while (!cursor.isAfterLast()) {
                heroLoader.delete(cursor.getString(cursor.getColumnIndex("id")));
                cursor.moveToNext();
            }
        }
        cleanPets();
        cleanAccessories();
        for (Skill skill : loadAllSkill()) {
            delete(skill);
        }
        cleanGoods();
        for (ClickSkill clickSkill : loadClickSkill()) {
            deleteClickSkill(clickSkill);
        }
        database.excuseSQLWithoutResult("delete from maze where hero_index = " + index);
        database.excuseSQLWithoutResult("delete from hero where hero_index = " + index);
    }

    public void cleanPets() {
        for (Pet pet : new ArrayList<>(petLoader.loadAll())) {
            if (pet.getHeroIndex() == index) {
                petLoader.delete(pet.getId());
            }
        }
    }

    public void cleanGoods() {
        for (Goods goods : loadAllGoods(true)) {
            goods.markDelete();
            goodsLoader.delete(goods.getId());
        }
    }

    public void cleanAccessories() {
        for (Accessory accessory : new ArrayList<>(accessoryLoader.loadAll())) {
            if (accessory.getHeroIndex() == index) {
                delete(accessory.getId());
            }
        }
    }

    public Pet loadPet(String id) {
        return petLoader.load(id);
    }

    public void savePet(Pet pet) {
        pet.setHeroIndex(index);
        if (StringUtils.isNotEmpty(pet.getId())) {
            petLoader.update(pet);
        } else {
            petLoader.save(pet);
        }
    }

    public void deletePet(Pet pet) {
        pet.markDelete();
        petLoader.delete(pet.getId());
    }

    @Override
    public Goods loadGoods(String classSimpleName) {
        return goodsLoader.load(buildIdWithIndex(classSimpleName));
    }

    @Override
    public List<Pet> loadPets(int start, int rows, final String keyWord, Comparator<Pet> comparator) {
        return petLoader.loadLimit(start, rows, new Index<Pet>() {
            @Override
            public boolean match(Pet pet) {
                return !pet.isDelete() && pet.getHeroIndex() == index && (pet.getName().contains(keyWord) || (StringUtils.isNotEmpty(pet.getTag()) && pet.getTag().contains(keyWord)));
            }
        }, comparator);
    }

    public void saveGoods(Goods goods) {
        goods.setHeroIndex(index);
        goodsLoader.save(goods, buildIdWithIndex(goods.getClass().getSimpleName()));
    }

    public void addGoods(Goods newGoods) {
        Goods d = goodsLoader.load(buildIdWithIndex(newGoods.getClass().getSimpleName()));
        boolean load = false;
        if (d == null || d.isDelete()) {
            saveGoods(newGoods);
            d = newGoods;
            load = true;
        } else {
            long org = d.getCount();
            d.setCount(d.getCount() + newGoods.getCount());
            saveGoods(d);
            if (org <= 0 && d.getCount() > 0) {
                load = true;
            }
        }
        if (load) {
            d.load(new GoodsProperties(loadHero()));
        }

    }

    public Skill loadSkill(String name) {
        return skillLoader.load(buildIdWithIndex(name));
    }

    public List<Skill> loadSpecialSkills(){
        return skillLoader.loadLimit(0, -1, new Index<Skill>() {
            @Override
            public boolean match(Skill skill) {
                return skill instanceof SpecialSkill;
            }
        }, null);
    }

    @Override
    public Accessory findAccessoryByName(@NotNull final String name) {
        List<Accessory> accessories = accessoryLoader.loadLimit(0, 1, new Index<Accessory>() {
            @Override
            public boolean match(Accessory accessory) {
                return accessory != null && accessory.getName().equals(name);
            }
        }, null);
        if (!accessories.isEmpty()) {
            return accessories.get(0);
        }
        return null;
    }

    @NotNull
    @Override
    public List<Pet> findPetByType(@NotNull final String type) {
        return petLoader.loadLimit(0, 1, new Index<Pet>() {
            @Override
            public boolean match(Pet pet) {
                return pet.getType().equals(type);
            }
        }, null);
    }

    public void saveSkill(Skill skill) {
        skill.setId(buildIdWithIndex(skill.getClass().getSimpleName()));
        skillLoader.save(skill, buildIdWithIndex(skill.getClass().getSimpleName()));
    }

    public List<Skill> loadAllSkill() {
        List<Skill> skills = new ArrayList<>();
        for (Skill skill : skillLoader.loadAll()) {
            if (skill.getId().endsWith("@" + index)) {
                skills.add(skill);
            }
        }
        skills.addAll(loadSpecialSkills());
        return skills;
    }

    public List<Accessory> loadAccessories(int start, int row, final String key, Comparator<Accessory> order) {
        return accessoryLoader.loadLimit(start, row, new Index<Accessory>() {
            @Override
            public boolean match(Accessory accessory) {
                return accessory != null && accessory.getHeroIndex() == index && accessory.getName().contains(key);
            }
        }, order);
    }

    public List<ClickSkill> loadClickSkill() {
        List<ClickSkill> clickSkills = clickSkillLoader.loadAll();
        for (ClickSkill skill : new ArrayList<>(clickSkills)) {
            if (skill != null && !skill.getId().endsWith("@" + index)) {
                clickSkills.remove(skill);
            }
        }
        return clickSkills;
    }

    public void saveClickSkill(ClickSkill clickSkill) {
        clickSkill.setId(buildIdWithIndex(clickSkill.getName()));
        clickSkillLoader.save(clickSkill, buildIdWithIndex(clickSkill.getName()));
    }

    public void deleteClickSkill(ClickSkill clickSkill) {
        clickSkill.markDelete();
        clickSkillLoader.delete(buildIdWithIndex(clickSkill.getName()));
    }

    public SerializeLoader<ClickSkill> getClickSkillLoader() {
        return clickSkillLoader;
    }

    public void fuseCache() {
        accessoryLoader.fuse();
        petLoader.fuse();
        skillLoader.fuse();
        clickSkillLoader.fuse();
    }

    public List<Pet> loadMountPets() {
        List<Pet> pets = new ArrayList<>();
        for (Pet pet : petLoader.loadAll()) {
            if (pet != null && pet.getHeroIndex() == index && pet.isMounted()) {
                pets.add(pet);
            }
        }
        return pets;
    }

    public void delete(Serializable object) {
        if (object instanceof IDModel) {
            ((IDModel) object).markDelete();
        }
        if (object instanceof Pet) {
            deletePet((Pet) object);
        } else if (object instanceof Accessory) {
            accessoryLoader.delete(((Accessory) object).getId());
        } else if (object instanceof Goods) {
            ((Goods) object).setCount(0);
            goodsLoader.save((Goods) object);
        } else if (object instanceof Skill) {
            skillLoader.delete(((Skill) object).getId());
        } else if (object instanceof ClickSkill) {
            deleteClickSkill((ClickSkill) object);
        }

    }

    public void close() {
        e.shutdown();
        try {
            e.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LogHelper.logException(e, "Close Data manager");
        }
        fuseCache();
    }

    public void save(IDModel object) {
        if (object instanceof Maze) {
            saveMaze((Maze) object);
        }
        if (object instanceof OwnedAble) {
            Hero hero = loadHero();
            ((OwnedAble) object).setKeeperName(hero.getName());
            ((OwnedAble) object).setKeeperId(hero.getId());
        }
        if (object instanceof ClickSkill) {
            saveClickSkill((ClickSkill) object);
        }
        if (object instanceof Skill) {
            saveSkill((Skill) object);
        }
        if (object instanceof Pet) {
            savePet((Pet) object);
        } else if (object instanceof Accessory) {
            saveAccessory((Accessory) object);
        } else if (object instanceof Goods) {
            saveGoods((Goods) object);
        } else if (object instanceof NeverEndConfig) {
            try {
                configDB.save((NeverEndConfig) object, object.getId());
            } catch (IOException e1) {
                LogHelper.logException(e1, "save config");
            }
        }
    }

    public void add(IDModel object) {
        if (object instanceof Goods) {
            addGoods((Goods) object);
        } else if(object instanceof Skill){
            if(skillLoader.load(object.getId())==null){
                saveSkill((Skill) object);
            }
        }else {
            save(object);
        }
    }

    public List<Goods> loadAllGoods(final boolean all) {
        return goodsLoader.loadLimit(0, -1, new Index<Goods>() {
            @Override
            public boolean match(Goods goods) {
                return goods.getHeroIndex() == index && (all || goods.getCount() > 0);
            }
        }, null);
    }

    public NeverEndConfig loadConfig() {
        NeverEndConfig config = null;
        config = configDB.loadObject(String.valueOf(index));
        if (config == null) {
            config = new NeverEndConfig();
            config.setId(String.valueOf(index));
            try {
                configDB.save(config);
            } catch (IOException e1) {
                LogHelper.logException(e1, "Save Config while get");
            }
        }
        try {
            if (StringUtils.isEmpty(config.getSign())) {
                config.setSign(Resource.getSingInfo());
            }
            if (StringUtils.isEmpty(config.getVersion())) {
                config.setVersion(Resource.getVersion());
            }
        } catch (Exception e) {
            LogHelper.logException(e, "load config");
        }
        config.load();
        return config;
    }

    public Hero loadDefender(long level) {
        try {
            return defenderDB.loadObject(String.valueOf(level));
        } catch (Exception e) {
            LogHelper.logException(e, "DataManager->loadDefender");
        }
        return null;
    }

    public void addDefender(Hero hero, long level) {
        try {
            defenderDB.save(hero, hero.getId());
        } catch (IOException e1) {
            LogHelper.logException(e1, "addDefender");
        }
    }

    public void registerTable(ObjectTable table) {
        e.scheduleAtFixedRate(table, 10, 50, TimeUnit.SECONDS);
        tables.add(table);
    }

    public List<File> retrieveAllSaveFile() {
        List<File> files = new ArrayList<>();
        List<ObjectTable> tables = new ArrayList<>(this.tables);
        tables.remove(defenderDB);
        for (ObjectTable table : tables) {
            List<File> listFile = table.listFile();
            if (listFile != null) {
                files.addAll(listFile);
            }
        }
        return files;
    }

    public void overrideCurrentSaveFile(List<Serializable> objs) {
        clean();
        for (Serializable obj : objs) {
            if (obj instanceof IDModel) {
                add((IDModel) obj);
            }
        }
    }

    public void cleanDefender() {
        for (String h : heroLoader.getAllID()) {
            if (h.matches("\\d+")) {
                heroLoader.delete(h);
            }
        }
    }

    @NotNull
    private String buildIdWithIndex(String name) {
        return name + "@" + index;
    }

    private Maze newMaze() {
        Maze maze = new Maze();
        maze.setMaxLevel(1);
        maze.setLevel(1);
        maze.setMeetRate(99.9f);
        return maze;
    }

}
