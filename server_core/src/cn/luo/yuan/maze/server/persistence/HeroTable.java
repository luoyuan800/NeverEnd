package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
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
        return getRecord(id).getData().getMaze();
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
            int t = record.getWinCount() + record.getLostCount();
            if(t<= 0) t++;
            return record.getData().getHero().getDisplayName() + "<br>"
                    + "排名：" + record.getRange() + "， 胜率：" +  StringUtils.formatPercentage(record.getWinCount() * 100/(t))
                    ;
        }
        return StringUtils.EMPTY_STRING;
    }

    public String pollBattleMsg(String id, int count) {
        ServerRecord record = getRecord(id);
        if(record.getMessages().size() > 50){
            count += 5;
        }
        StringBuilder s = new StringBuilder();
        while (count-- > 0 && record.getMessages().size() > 0){
            s.append(record.getMessages().poll()).append(count > 0 ? "<br>" : "");
        }
        return s.toString();
    }

    public ServerData getBackHero(String id) throws IOException {
        ServerRecord record = getRecord(id);
        LogHelper.info(record.getData().getHero().getDisplayName() + " Get back!");
        ServerData data = new ServerData(record.getData());
        record.setData(null);
        record.setDieCount(0);
        record.setDieTime(0);
        record.getMessages().clear();
        save();
        return data;
    }

    public void submitHero(ServerData data) throws IOException {
        ServerRecord record = getRecord(data.getHero().getId());
        if(record == null){
            record = new ServerRecord();
            record.setId(data.getHero().getId());
        }
        record.setRange(Integer.MAX_VALUE);
        record.setData(data);
        save(record);
        LogHelper.info(record.getData().getHero().getDisplayName() + " Submit!");
    }

    public Hero getHero(String name) throws IOException, ClassNotFoundException {
        return loadHero(name);
    }

    private Hero loadHero(String id) throws IOException, ClassNotFoundException {
        ServerRecord record = getRecord(id);
        ServerData data = record.getData();
        if (data != null) {
            Hero hero = data.getHero();
            if (data.getAccessories() != null) {
                for (Accessory accessory : data.getAccessories()) {
                    AccessoryHelper.mountAccessory(accessory, hero);
                }
            }
            if (data.getPets() != null) {
                for (Pet pet : data.getPets()) {
                    hero.getPets().add(pet);
                }
            }
            return hero;
        }
        return null;
    }

}
