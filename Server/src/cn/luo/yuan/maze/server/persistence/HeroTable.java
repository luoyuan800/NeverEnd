package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.server.LogHelper;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable;
import cn.luo.yuan.maze.service.AccessoryHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by gluo on 5/22/2017.
 */
public class HeroTable {
    private ObjectTable<ServerRecord> recordDb;
    private long maxLevel = 1;
    private File root;

    public HeroTable(File root) throws IOException, ClassNotFoundException {
        this.root = root;
        recordDb = new ObjectTable<>(ServerRecord.class, root);

    }

    public Hero getHero(String id, long level) throws IOException, ClassNotFoundException {
        return loadHero(id);
    }

    public long getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(long maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void clear() {
        for(ServerRecord record : recordDb.loadAll()){
            record.setData(null);
        }
        try {
            recordDb.fuse();
        } catch (IOException e) {
            LogHelper.error(e);
        }
    }

    public void save(Object obj) throws IOException {
        if (obj instanceof ServerRecord) {
            recordDb.save((ServerRecord) obj);
        }

    }

    public Maze getMaze(String id, long level) throws IOException, ClassNotFoundException {
        return getRecord(id).getData().maze;
    }

    public ServerRecord getRecord(String id) {
        return recordDb.loadObject(id);
    }

    private Hero loadHero(String id) throws IOException, ClassNotFoundException {
        ServerRecord record = getRecord(id);
        ServerData data = record.getData();
        if(data!=null) {
            Hero hero = data.hero;
            if(data.accessories!=null) {
                for (Accessory accessory : data.accessories) {
                    AccessoryHelper.mountAccessory(accessory, hero);
                }
            }
            if(data.pets!=null) {
                for (Pet pet : data.pets) {
                    hero.getPets().add(pet);
                }
            }
            return hero;
        }
        return null;
    }

    public void save() throws IOException {
        recordDb.fuse();
    }

}
