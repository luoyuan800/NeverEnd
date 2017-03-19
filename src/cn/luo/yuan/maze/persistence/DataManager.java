package cn.luo.yuan.maze.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.persistence.database.Sqlite;
import cn.luo.yuan.maze.persistence.serialize.SerializeLoader;
import cn.luo.yuan.maze.utils.SecureRAMReader;
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
        String query = "select * from hero where index = '" + index + "'";
        Cursor cursor = database.excuseSOL(query);
        try {
            if (!cursor.isAfterLast()) {
                Hero hero = new Hero();
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
                hero.setRamReader(new SecureRAMReader(database.getKey(index)));
                return hero;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public Accessory loadAccessory(String id) {
        return accessoryLoader.load(id);
    }

    public List<Accessory> loadMountedAccessory(Hero hero) {
        List<Accessory> accessories = new ArrayList<>();
        Cursor cursor = database.excuseSOL("select * from accessory where index = '" + index + "'");
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
        values.put("index", hero.getIndex());
        values.put("birthDay", hero.getBirthDay());
        values.put("element", hero.getElement().name());
        values.put("hp", hero.getEncodeHp());
        values.put("atk", hero.getEncodeAtk());
        values.put("def", hero.getEncodeDef());
        values.put("agi", hero.getEncodeAtk());
        values.put("maxHp", hero.getEncodeMaxHp());
        values.put("material", hero.getEncodeMaterial());
        values.put("reincarnate", hero.getReincarnate());
        values.put("hpGrow", hero.getEncodeHpGrow());
        values.put("defGrow", hero.getEncodeDefGrow());
        values.put("atkGrow", hero.getEncodeAtkGrow());
        values.put("last_update", System.currentTimeMillis());
        if(StringUtils.isNotEmpty(hero.getId())) {
            database.updateById("hero", values, hero.getId());
        }else {
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
        values.put("index", index);
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
        values.put("index", index);
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

}
