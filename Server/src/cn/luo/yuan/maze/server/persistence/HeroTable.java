package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable;
import cn.luo.yuan.maze.service.AccessoryHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by gluo on 5/22/2017.
 */
public class HeroTable {
    private ObjectTable<Hero> heroDb;
    private ObjectTable<Maze> mazeDb;
    private ObjectTable<Accessory> accDb;
    private ObjectTable<Skill> skillDb;
    private ObjectTable<Pet> petDb;
    private ObjectTable<ServerRecord> recordDb;
    private long maxLevel = 1;
    private File root;

    public HeroTable(File root) throws IOException, ClassNotFoundException {
        this.root = root;
        heroDb = new ObjectTable<>(Hero.class, root);
        mazeDb = new ObjectTable<>(Maze.class, root);
        accDb = new ObjectTable<>(Accessory.class, root);
        petDb = new ObjectTable<>(Pet.class, root);
        skillDb = new ObjectTable<>(Skill.class, root);
        recordDb = new ObjectTable<>(ServerRecord.class, root);

    }

    public Hero getHero(String id) throws IOException, ClassNotFoundException {
        return loadHero(id);
    }

    public long getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(long maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void clear() {
        heroDb.clear();
        mazeDb.clear();
    }

    public void save(Object obj) throws IOException {
        if (obj instanceof Accessory) {
            accDb.save((Accessory) obj);
        } else if (obj instanceof Pet) {
            petDb.save((Pet) obj);
        } else if (obj instanceof Skill) {
            skillDb.save((Skill) obj);
        } else if (obj instanceof ServerRecord) {
            recordDb.save((ServerRecord) obj);
        }

    }

    public Maze getMaze(String id) throws IOException, ClassNotFoundException {
        return mazeDb.loadObject(id);
    }

    public void saveHero(Hero hero) {
        try {
            heroDb.save(hero, hero.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMaze(Maze maze, String id) {
        try {
            mazeDb.save(maze, id);
            if (maze.getLevel() > maxLevel) {
                maxLevel = maze.getLevel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMaze(Maze maze) {
        try {
            mazeDb.save(maze, maze.getId());
            if (maze.getLevel() > maxLevel) {
                maxLevel = maze.getLevel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerRecord getRecord(String id) {
        return recordDb.loadObject(id);
    }

    public void delete() {
        heroDb.clear();
        mazeDb.clear();
        accDb.clear();
        petDb.clear();
    }

    private Hero loadHero(String id) throws IOException, ClassNotFoundException {
        Hero hero = heroDb.loadObject(id);
        for (Accessory accessory : accDb.loadAll()) {
            AccessoryHelper.mountAccessory(accessory, hero);
        }
        for (Pet pet : petDb.loadAll()) {
            hero.getPets().add(pet);
        }
        return hero;
    }

    public void save() throws IOException {
        heroDb.fuse();
        mazeDb.fuse();
        recordDb.fuse();
    }

}
