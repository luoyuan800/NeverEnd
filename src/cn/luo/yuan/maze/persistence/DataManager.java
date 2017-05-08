package cn.luo.yuan.maze.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsType;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.persistence.database.Sqlite;
import cn.luo.yuan.maze.persistence.serialize.SerializeLoader;
import cn.luo.yuan.maze.service.InfoControl;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
public class DataManager {
    //多存档，所以需要一个index来区分当前使用的是哪一个存档
    private int index;
    private SerializeLoader<Accessory> accessoryLoader;
    private SerializeLoader<Maze> mazeLoader;
    private SerializeLoader<Hero> heroLoader;
    private SerializeLoader<Pet> petLoader;
    private SerializeLoader<Goods> goodsLoader;
    private Sqlite database;
    private Context context;

    public DataManager(int index, Context context) {
        this.index = index;
        this.database = Sqlite.getSqlite(context);
        this.context = context;
        accessoryLoader = new SerializeLoader<>(Accessory.class, context);
        mazeLoader = new SerializeLoader<>(Maze.class, context);
        heroLoader = new SerializeLoader<>(Hero.class, context);
        petLoader = new SerializeLoader<>(Pet.class, context);
        goodsLoader = new SerializeLoader<>(Goods.class, context);
        this.context = context;
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
        hero.setAtkGrow(1);
        hero.setHpGrow(2);
        hero.setDefGrow(3);
        hero.setHp(20);
        hero.setMaxHp(20);
        hero.setDef(1);
        hero.setAtk(5);
        hero.setIndex(index);
        hero.setMaterial(0);
        hero.setStr(0);
        hero.setAgi(0);
        hero.setPoint(0);
        return hero;
    }

    public Accessory loadAccessory(String id) {
        return accessoryLoader.load(id);
    }

    public List<Accessory> loadMountedAccessory(Hero hero) {
        List<Accessory> accessories = new ArrayList<>();

        try (Cursor cursor = database.excuseSOL("select * from accessory where hero_index = '" + index + "'")) {
            while (!cursor.isAfterLast()) {
                if (cursor.getInt(cursor.getColumnIndex("mounted")) == 1) {
                    Accessory accessory = loadAccessory(cursor.getString(cursor.getColumnIndex("id")));
                    if (accessory != null) {
                        accessories.add(accessory);
                    }
                }
                cursor.moveToNext();
            }
        }
        return accessories;
    }

    public void saveHero(Hero hero) {
        ContentValues values = new ContentValues();
        values.put("name", hero.getName());
        values.put("hero_index", hero.getIndex());
        values.put("element", hero.getElement().name());
        values.put("reincarnate", hero.getReincarnate());
        values.put("last_update", System.currentTimeMillis());
        values.put("gift", hero.getGift());
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
        ContentValues values = new ContentValues();
        values.put("name", accessory.getName());
        values.put("id", accessory.getId());
        values.put("desc", accessory.getDesc());
        values.put("mounted", accessory.isMounted());
        values.put("hero_index", index);
        values.put("level", accessory.getLevel());
        if (StringUtils.isNotEmpty(accessory.getId())) {
            //Already existed, 我们会进行update操作
            database.updateById("accessory", values, accessory.getId());
            accessoryLoader.update(accessory);
        } else {
            //先添加一个新的记录
            accessoryLoader.save(accessory);
            values.put("id", accessory.getId());
            database.insert("accessory", values);
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
        try (Cursor cursor = database.excuseSOL("select count(*) from pet")) {
            return cursor.getInt(1);
        }
    }

    /**
     * 删除当前存档的数据
     */
    public void clean() {
        try (Cursor cursor = database.excuseSOL("select id from accessory where hero_index = " + index)) {
            while (!cursor.isAfterLast()) {
                accessoryLoader.delete(cursor.getString(cursor.getColumnIndex("id")));
                cursor.moveToNext();
            }
        }
        try (Cursor cursor = database.excuseSOL("select id from maze where hero_index = " + index)) {
            while (!cursor.isAfterLast()) {
                mazeLoader.delete(cursor.getString(cursor.getColumnIndex("id")));
                cursor.moveToNext();
            }
        }
        database.excuseSQLWithoutResult("delete from maze where hero_index = " + index);
        database.excuseSQLWithoutResult("delete from hero where hero_index = " + index);
        database.excuseSQLWithoutResult("delete from accessory where hero_index = " + index);
    }

    public Monster buildRandomMonster(InfoControl control) {
        try (Cursor cursor = database.excuseSOL("select * from monster where min_level < " + control.getMaze().getLevel())) {
            while (!cursor.isAfterLast()) {
                float meetRate = cursor.getFloat(cursor.getColumnIndex("meet_rate"));
                Random random = control.getRandom();
                if (random.nextInt(100) + random.nextFloat() < meetRate) {
                    Monster monster = new Monster();
                    monster.setType(cursor.getString(cursor.getColumnIndex("type")));
                    monster.setRace(cursor.getInt(cursor.getColumnIndex("race")));
                    monster.setPetRate(cursor.getFloat(cursor.getColumnIndex("pet_rate")));
                    monster.setHitRate(cursor.getFloat(cursor.getColumnIndex("hit")));
                    monster.setSilent(cursor.getFloat(cursor.getColumnIndex("silent")));
                    int sex = cursor.getInt(cursor.getColumnIndex("sex"));
                    monster.setSex(sex >= 0 ? sex : random.nextInt(2));
                    monster.setAtk(cursor.getLong(cursor.getColumnIndex("atk")));
                    monster.setDef(cursor.getLong(cursor.getColumnIndex("def")));
                    monster.setMaxHP(cursor.getLong(cursor.getColumnIndex("hp")));
                    FirstName firstName = FirstName.getRandom(control.getMaze().getLevel(), random);
                    SecondName secondName = SecondName.getRandom(control.getMaze().getLevel(), random);
                    monster.setFirstName(firstName.getName());
                    monster.setSecondName(secondName.getName());
                    monster.setElement(Element.values()[random.nextInt(Element.values().length)]);
                    monster.setMaxHP(monster.getMaxHP() + firstName.getHPAddition(monster.getMaxHP()));
                    monster.setMaxHP(monster.getMaxHP() + secondName.getHpAddition(monster.getMaxHP()));
                    monster.setAtk(monster.getAtk() + firstName.getAtkAddition(monster.getAtk()));
                    monster.setAtk(monster.getAtk() + secondName.getAtkAddition(monster.getAtk()));
                    if (control.getMaze().getLevel() < 5000) {
                        monster.setMaxHP(control.getMaze().getLevel() * (monster.getMaxHP() + Data.MONSTER_HP_RISE_PRE_LEVEL) / 2);
                        monster.setAtk(control.getMaze().getLevel() * (monster.getAtk() + Data.MONSTER_ATK_RISE_PRE_LEVEL) / 2);
                        monster.setDef(control.getMaze().getLevel() * (monster.getDef() + Data.MONSTER_DEF_RISE_PRE_LEVEL) / 2);
                    } else {
                        monster.setMaxHP(control.getMaze().getLevel() * (monster.getMaxHP() + Data.MONSTER_HP_RISE_PRE_LEVEL));
                        monster.setAtk(control.getMaze().getLevel() * (monster.getAtk() + Data.MONSTER_ATK_RISE_PRE_LEVEL));
                        monster.setDef(control.getMaze().getLevel() * (monster.getDef() + Data.MONSTER_DEF_RISE_PRE_LEVEL));
                    }
                    long m1 = random.nextLong(monster.getHp() + 1) / 181 + 3;
                    long m2 = random.nextLong(monster.getAtk() + 1) / 411 + 5;
                    long material = random.nextLong((m1 + m2) / 1832 + 1) + 10 + random.nextLong(control.getMaze().getLevel() / 100 + 1);
                    if (control.getMaze().getMaxLevel() < 1000) {
                        material += 15;
                    }
                    if (control.getMaze().getMaxLevel() > 5000 && control.getMaze().getLevel() < control.getMaze().getMaxLevel()) {
                        material /= 2;
                    }
                    if (material > 1000) {
                        material = 300 + random.nextInt(700);
                    }
                    monster.setMaterial(material);
                    return monster;
                }
                cursor.moveToNext();
            }
        }
        return null;
    }

    public Pet loadPet(String id) {
        return petLoader.load(id);
    }

    public void savePet(Pet pet) {
        ContentValues values = new ContentValues();
        values.put("hero_index", index);
        values.put("sex", pet.getSex());
        values.put("name", pet.getDisplayName());
        values.put("element", pet.getElement().ordinal());
        values.put("last_update", System.currentTimeMillis());
        values.put("color", pet.getColor());
        values.put("level", pet.getLevel());
        values.put("tag", pet.getTag());
        values.put("mounted", pet.isMounted());
        if (StringUtils.isNotEmpty(pet.getId())) {
            petLoader.update(pet);
            database.updateById("pet", values, pet.getId());
        } else {
            petLoader.save(pet);
            values.put("create", System.currentTimeMillis());
            values.put("id", pet.getId());
            database.insert("pet", values);
        }
    }

    private Maze newMaze() {
        Maze maze = new Maze();
        maze.setMaxLevel(1);
        maze.setLevel(1);
        maze.setMeetRate(99.9f);
        return maze;
    }

    public Goods loadGoods(GoodsType type){
        return goodsLoader.load(type.name() + "@" + index);
    }

    public void saveGoods(Goods goods){
        goodsLoader.save(goods, goods.getClass().getSimpleName() + "@" + index);
    }
}
