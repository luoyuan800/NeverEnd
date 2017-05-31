package cn.luo.yuan.maze.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsType;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.persistence.database.Sqlite;
import cn.luo.yuan.maze.persistence.serialize.SerializeLoader;
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
        skillLoader = new SerializeLoader<>(Skill.class, context);
        clickSkillLoader = new SerializeLoader<>(ClickSkill.class, context);
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
            if (!cursor.isAfterLast())
                return cursor.getInt(0);
            else
                return 0;
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

    public Pet loadPet(String id) {
        return petLoader.load(id);
    }

    public void savePet(Pet pet) {
        ContentValues values = new ContentValues();
        values.put("hero_index", index);
        values.put("sex", pet.getSex());
        values.put("name", pet.getName());
        values.put("element", pet.getElement().ordinal());
        values.put("last_update", System.currentTimeMillis());
        values.put("color", pet.getColor());
        values.put("level", pet.getLevel());
        values.put("tag", pet.getTag());
        values.put("mounted", pet.isMounted());
        values.put("race", pet.getRace().ordinal());
        if (StringUtils.isNotEmpty(pet.getId())) {
            petLoader.update(pet);
            database.updateById("pet", values, pet.getId());
        } else {
            petLoader.save(pet);
            values.put("created", System.currentTimeMillis());
            values.put("id", pet.getId());
            database.insert("pet", values);
        }
    }

    public void deletePet(Pet pet) {
        petLoader.delete(pet.getId());
    }

    public Goods loadGoods(GoodsType type) {
        return goodsLoader.load(type.name() + "@" + index);
    }

    @Override
    public List<Pet> loadPets(int start, int rows, String keyWord) {
        List<Pet> pets = new ArrayList<>();
        try(Cursor cursor = database.excuseSOL("select id from pet where index = '" + index + "' and ")) {
            while (!cursor.isAfterLast()){
                Pet pet = petLoader.load(cursor.getString(cursor.getColumnIndex("id")));
                if(pet!=null){
                    pets.add(pet);
                }
                cursor.moveToNext();
            }
        }
        return pets;
    }

    public void saveGoods(Goods goods) {
        goodsLoader.save(goods, goods.getClass().getSimpleName() + "@" + index);
    }

    public Skill loadSkill(String name) {
        return skillLoader.load(name + "@" + index);
    }

    public void saveSkill(Skill skill) {
        skill.setId(skill.getClass().getSimpleName() + "@" + index);
        skillLoader.save(skill, skill.getClass().getSimpleName() + "@" + index);
    }

    public List<Skill> loadAllSkill() {
        List<Skill> skills = new ArrayList<>();
        for(Skill skill : skillLoader.loadAll()){
            if(skill.getId().endsWith("@" + index)){
                skills.add(skill);
            }
        }
        return skills;
    }

    public List<Accessory> loadAccessories(int start, int row, String key) {
        List<Accessory> all = new ArrayList<>(row);
        try(Cursor cursor = database.excuseSOL("select id from accessory where index = '" + index + "' and name like '%" + key + "%' limit " + row + " offset " + start)) {
            while (!cursor.isAfterLast()) {
                Accessory accessory = accessoryLoader.load(cursor.getString(cursor.getColumnIndex("id")));
                if (accessory != null) {
                    all.add(accessory);
                }
                cursor.moveToNext();
            }
        }
        return all;
    }

    public List<ClickSkill> loadClickSkill(){
        List<ClickSkill> clickSkills = clickSkillLoader.loadAll();
        for(ClickSkill skill : new ArrayList<>(clickSkills)){
            if(!skill.getId().endsWith("@" + index)){
                clickSkills.remove(skill);
            }
        }
        return clickSkills;
    }

    public void saveClickSkill(ClickSkill clickSkill){
        clickSkill.setId(clickSkill.getName() + "@" + index);
        clickSkillLoader.save(clickSkill, clickSkill.getName() + "@" + index);
    }

    public void deleteClickSkill(ClickSkill clickSkill){
        clickSkillLoader.delete(clickSkill.getName() + "@" + index);
    }

    public SerializeLoader<ClickSkill> getClickSkillLoader() {
        return clickSkillLoader;
    }

    public void fluseCache() {
        accessoryLoader.fluse();
        petLoader.fluse();
        skillLoader.fluse();
        clickSkillLoader.fluse();
    }

    private Maze newMaze() {
        Maze maze = new Maze();
        maze.setMaxLevel(1);
        maze.setLevel(1);
        maze.setMeetRate(99.9f);
        return maze;
    }
}
