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
import cn.luo.yuan.maze.task.Task;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
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
    private SerializeLoader<Task> taskLoader;

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
        taskLoader = new SerializeLoader<Task>(Task.class, context);
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
        for(Accessory accessory : accessoryLoader.loadAll()){
            if(accessory.isMounted()){
                accessories.add(accessory);
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

        try (Cursor cursor = database.excuseSOL("select id from maze where hero_index = " + index)) {
            while (!cursor.isAfterLast()) {
                mazeLoader.delete(cursor.getString(cursor.getColumnIndex("id")));
                cursor.moveToNext();
            }
        }
        try(Cursor cursor = database.excuseSOL("select id from hero where hero_index = " + index)){
            while (!cursor.isAfterLast()){
                heroLoader.delete(cursor.getString(cursor.getColumnIndex("id")));
                cursor.moveToNext();
            }
        }
        for(Pet pet : new ArrayList<>(petLoader.loadAll())){
            petLoader.delete(pet.getId());
        }
        for(Accessory accessory : new ArrayList<>(accessoryLoader.loadAll())){
            accessoryLoader.delete(accessory.getId());
        }
        database.excuseSQLWithoutResult("delete from maze where hero_index = " + index);
        database.excuseSQLWithoutResult("delete from hero where hero_index = " + index);
    }

    public Pet loadPet(String id) {
        return petLoader.load(id);
    }

    public void savePet(Pet pet) {
        if (StringUtils.isNotEmpty(pet.getId())) {
            petLoader.update(pet);
        } else {
            petLoader.save(pet);
        }
    }

    public void deletePet(Pet pet) {
        petLoader.delete(pet.getId());
    }

    public Goods loadGoods(GoodsType type) {
        return goodsLoader.load(type.name() + "@" + index);
    }

    @Override
    public List<Pet> loadPets(int start, int rows, String keyWord, Comparator<Pet> comparator) {
        return petLoader.loadLimit(start, rows, new Index<Pet>() {
            @Override
            public boolean match(Pet pet) {
                return pet.getName().contains(keyWord) || pet.getTag().contains(keyWord);
            }
        }, comparator);
    }

    public void saveGoods(Goods goods) {
        goodsLoader.save(goods, goods.getClass().getSimpleName() + "@" + index);
    }

    public Skill loadSkill(String name) {
        return skillLoader.load(name + "@" + index);
    }

    @Override
    public Accessory findAccessoryByName(@NotNull String name) {
        List<Accessory> accessories = accessoryLoader.loadLimit(0, 1, new Index<Accessory>() {
            @Override
            public boolean match(Accessory accessory) {
                return accessory.getName().equals(name);
            }
        }, null);
        if(!accessories.isEmpty()){
            return accessories.get(0);
        }
        return null;
    }

    @NotNull
    @Override
    public List<Pet> findPetByType(@NotNull String type) {
        return petLoader.loadLimit(0, 1, new Index<Pet>() {
            @Override
            public boolean match(Pet pet) {
                return pet.getType().equals(type);
            }
        }, null);
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
        return accessoryLoader.loadLimit(start, row, new Index<Accessory>() {
            @Override
            public boolean match(Accessory accessory) {
                return accessory.getName().contains(key);
            }
        }, null);
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

    public void fuseCache() {
        accessoryLoader.fuse();
        petLoader.fuse();
        skillLoader.fuse();
        clickSkillLoader.fuse();
        taskLoader.fuse();
    }

    public List<Pet> loadMountPets() {
        List<Pet> pets = new ArrayList<>();
        for(Pet pet : petLoader.loadAll()){
            if(pet.isMounted()){
                pets.add(pet);
            }
        }
        return pets;
    }

    public void delete(Serializable object) {
        if(object instanceof Pet){
            deletePet((Pet)object);
        } else if(object instanceof Accessory){
            accessoryLoader.delete(((Accessory)object).getId());
        } else if( object instanceof Goods){
            ((Goods) object).setCount(((Goods) object).getCount()  - 1);
        } else{
            //TODO
        }

    }

    public Task loadTask(String taskId) {
        return taskLoader.load(taskId);
    }

    public List<Task> loadTask(int start, int row, Index<Task> filter, Comparator<Task> order) {
        return taskLoader.loadLimit(start, row, filter, order);
    }

    public int taskCount() {
        return taskLoader.size();
    }

    public void addTask(Task task) {
        taskLoader.save(task,task.getId());
    }

    private Maze newMaze() {
        Maze maze = new Maze();
        maze.setMaxLevel(1);
        maze.setLevel(1);
        maze.setMeetRate(99.9f);
        return maze;
    }
}
