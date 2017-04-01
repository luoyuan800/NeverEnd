package cn.luo.yuan.maze.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.FirstName;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.SecondName;
import cn.luo.yuan.maze.persistence.database.Sqlite;
import cn.luo.yuan.maze.persistence.serialize.SerializeLoader;
import cn.luo.yuan.maze.service.InfoControl;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.SecureRAMReader;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private SerializeLoader<Accessory> accessoryLoader = new SerializeLoader<>(Accessory.class);
    private SerializeLoader<Maze> mazeLoader = new SerializeLoader<>(Maze.class);
    private Sqlite database;
    private Context context;

    public DataManager(int index, Context context) {
        this.index = index;
        this.database = Sqlite.getSqlite(context);
        this.context = context;
    }

    public Hero loadHero() {
        String query = "select * from hero where hero_index = '" + index + "'";
        Cursor cursor = database.excuseSOL(query);
        try {
            if (!cursor.isAfterLast()) {
                Hero hero = new Hero(database.getKey(index));
                hero.setAgi(cursor.getBlob(cursor.getColumnIndex("agi")));
                hero.setAtk(cursor.getBlob(cursor.getColumnIndex("atk")));
                hero.setDef(cursor.getBlob(cursor.getColumnIndex("def")));
                hero.setHp(cursor.getBlob(cursor.getColumnIndex("hp")));
                hero.setMaxHp(cursor.getBlob(cursor.getColumnIndex("maxHp")));
                hero.setStr(cursor.getBlob(cursor.getColumnIndex("str")));
                hero.setMaterial(cursor.getBlob(cursor.getColumnIndex("material")));
                hero.setReincarnate(cursor.getLong(cursor.getColumnIndex("reincarnate")));
                hero.setName(cursor.getString(cursor.getColumnIndex("name")));
                hero.setIndex(index);
                hero.setBirthDay(cursor.getLong(cursor.getColumnIndex("birthDay")));
                String element = cursor.getString(cursor.getColumnIndex("element"));
                if (element != null && !element.isEmpty()) {
                    hero.setElement(Element.valueOf(element));
                } else {
                    hero.setElement(Element.NONE);
                }
                hero.setId(cursor.getString(cursor.getColumnIndex("uuid")));
                hero.setAtkGrow(cursor.getBlob(cursor.getColumnIndex("atkGrow")));
                hero.setDefGrow(cursor.getBlob(cursor.getColumnIndex("defGrow")));
                hero.setHpGrow(cursor.getBlob(cursor.getColumnIndex("hpGrow")));
                hero.setIndex(index);
                hero.setPoint(cursor.getBlob(cursor.getColumnIndex("point")));
                hero.setGift(cursor.getString(cursor.getColumnIndex("gift")));
                hero.setClick(cursor.getLong(cursor.getColumnIndex("click")));
                return hero;
            }
        } finally {
            cursor.close();
        }
        Hero hero = new Hero(database.getKey(index));
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
        Cursor cursor = database.excuseSOL("select * from accessory where hero_index = '" + index + "'");
        try {
            while (!cursor.isAfterLast()) {
                if (cursor.getInt(cursor.getColumnIndex("mounted")) == 1) {
                    Accessory accessory = loadAccessory(cursor.getString(cursor.getColumnIndex("id")));
                    if (accessory != null) {
                        hero.mountAccessory(accessory);
                        accessories.add(accessory);
                    }
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return accessories;
    }

    public void saveHero(Hero hero) {
        ContentValues values = new ContentValues();
        values.put("name", hero.getName());
        values.put("hero_index", hero.getIndex());
        values.put("birthDay", hero.getBirthDay());
        values.put("element", hero.getElement().name());
        values.put("hp", hero.getEncodeHp());
        values.put("atk", hero.getEncodeAtk());
        values.put("def", hero.getEncodeDef());
        values.put("agi", hero.getEncodeAtk());
        values.put("str", hero.getEncodeStr());
        values.put("maxHp", hero.getEncodeMaxHp());
        values.put("material", hero.getEncodeMaterial());
        values.put("reincarnate", hero.getReincarnate());
        values.put("hpGrow", hero.getEncodeHpGrow());
        values.put("defGrow", hero.getEncodeDefGrow());
        values.put("atkGrow", hero.getEncodeAtkGrow());
        values.put("last_update", System.currentTimeMillis());
        values.put("point", hero.getEncodePoint());
        values.put("gift", hero.getGift());
        values.put("click", hero.getClick());
        if(StringUtils.isNotEmpty(hero.getId())) {
            database.updateById("hero", values, hero.getId());
        }else {
            hero.setId(UUID.randomUUID().toString());
            values.put("id", hero.getId());
            values.put("created", System.currentTimeMillis());
            database.insert("hero", values);
        }
        for(Accessory accessory : hero.getAccessories()){
            saveAccessory(accessory);
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
        if(StringUtils.isNotEmpty(accessory.getId())) {
            //Already existed, 我们会进行update操作
            database.updateById("accessory", values, accessory.getId());
            accessoryLoader.update(accessory);
        }else{
            //先添加一个新的记录
            accessoryLoader.save(accessory);
            values.put("id", accessory.getId());
            database.insert("accessory", values);
        }
    }

    public void saveMaze(Maze maze){
        ContentValues values = new ContentValues();
        values.put("hero_index", index);
        values.put("level", maze.getLevel());
        values.put("max_level", maze.getMaxLevel());
        if(StringUtils.isNotEmpty(maze.getId())){
            database.updateById("maze", values, maze.getId());
            mazeLoader.update(maze);
        }else{
            mazeLoader.save(maze);
            values.put("id", maze.getId());
            database.insert("maze", values);
        }
    }

    public Maze loadMaze(){
        Cursor cursor = database.excuseSOL("select id from maze");
        try {
            if (!cursor.isAfterLast()) {
                return mazeLoader.load(cursor.getString(cursor.getColumnIndex("id")));
            }
        }finally {
            cursor.close();
        }
        return null;
    }

    /**
     * 删除当前存档的数据
     */
    public void clean() {
        Cursor cursor = database.excuseSOL("select id from accessory where hero_index = " + index);
        try {
            while (!cursor.isAfterLast()) {
                accessoryLoader.delete(cursor.getString(cursor.getColumnIndex("id")));
            }
        }finally {
            cursor.close();
        }
        try {
            cursor = database.excuseSOL("select id from maze where hero_index = " + index);
            while (!cursor.isAfterLast()) {
                mazeLoader.delete(cursor.getString(cursor.getColumnIndex("id")));
            }
        }finally {
            cursor.close();
        }
        database.excuseSOL("delete from maze where hero_index = " + index);
        database.excuseSOL("delete from hero where hero_index = " + index);
        database.excuseSOL("delete from accessory where hero_index = " + index);
    }
    public Monster buildRandomMonster(InfoControl control){
        Cursor cursor = database.excuseSOL("select * from monster where min_level < " + control.getMaze().getLevel());
        try {
            while (!cursor.isAfterLast()) {
                float meetRate = cursor.getFloat(cursor.getColumnIndex("meet_rate"));
                Random random = control.getRandom();
                if (random.nextInt(100) + random.nextFloat() < meetRate) {
                    Monster monster = new Monster();
                    monster.setType(cursor.getString(cursor.getColumnIndex("type")));
                    monster.setRace(cursor.getInt(cursor.getColumnIndex("race")));
                    monster.setImageId(cursor.getInt(cursor.getColumnIndex("image")));
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
                        monster.setMaxHP(control.getMaze().getLevel() * (monster.getMaxHP() + Data.MONSTER_HP_RISE_PRE_LEVEL)/2);
                        monster.setAtk(control.getMaze().getLevel() * (monster.getAtk() + Data.MONSTER_ATK_RISE_PRE_LEVEL)/2);
                        monster.setDef(control.getMaze().getLevel() * (monster.getDef() + Data.MONSTER_DEF_RISE_PRE_LEVEL)/2);
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
                cursor.moveToFirst();
            }
        }catch (Exception e) {
            cursor.close();
        }
        return null;
    }
}
