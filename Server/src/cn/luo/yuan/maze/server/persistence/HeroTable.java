package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.server.LogHelper;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable;
import cn.luo.yuan.maze.service.AccessoryHelper;
import cn.luo.yuan.maze.utils.StringUtils;

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

    public String queryBattleAward(String id) {
        ServerRecord record = getRecord(id);
        if (record.getData() != null) {
            return record.getData().toString();
        }
        return StringUtils.EMPTY_STRING;
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
        for (ServerRecord record : recordDb.loadAll()) {
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

    public void save() throws IOException {
        recordDb.fuse();
    }

    public String queryDataString(String id) {
        ServerRecord record = getRecord(id);
        if(record.getData()!=null) {
            return record.getData().hero.getDisplayName() + "<br>"
                    + "排名：" + record.getRange() + "， 胜率：" +  StringUtils.formatPercentage(record.getWinCount() * 100/(record.getWinCount() + record.getLostCount() + 1))
                    + "<br>胜利：" + StringUtils.formatNumber(record.getWinCount()) + "， "
                    + "失败：" + StringUtils.formatNumber(record.getLostCount()) + "<br>";
        }
        return StringUtils.EMPTY_STRING;
    }

    public String pollBattleMsg(String id, int count) {
        ServerRecord record = getRecord(id);
        if(record.getMessages().size() > 50){
            count += 5;
        }
        String s = "";
        while (count-- > 0 && record.getMessages().size() > 0){
            s += record.getMessages().poll() + (count > 0 ? "<br>" : "");
        }
        return s;
    }

    public ServerData getBackHero(String id) throws IOException {
        ServerRecord record = getRecord(id);
        LogHelper.info(record.getData().hero.getDisplayName() + " Get back!");
        ServerData data = new ServerData(record.getData());
        record.setData(null);
        record.setDieCount(0);
        record.setDieTime(0);
        record.getMessages().clear();
        save();
        return data;
    }

    public void submitHero(ServerData data) throws IOException {
        ServerRecord record = getRecord(data.hero.getId());
        if(record == null){
            record = new ServerRecord();
            record.setId(data.hero.getId());
        }
        record.setRange(Integer.MAX_VALUE);
        record.setData(data);
        save(record);
        LogHelper.info(record.getData().hero.getDisplayName() + " Submit!");
    }

    private Hero loadHero(String id) throws IOException, ClassNotFoundException {
        ServerRecord record = getRecord(id);
        ServerData data = record.getData();
        if (data != null) {
            Hero hero = data.hero;
            if (data.accessories != null) {
                for (Accessory accessory : data.accessories) {
                    AccessoryHelper.mountAccessory(accessory, hero);
                }
            }
            if (data.pets != null) {
                for (Pet pet : data.pets) {
                    hero.getPets().add(pet);
                }
            }
            return hero;
        }
        return null;
    }

}
