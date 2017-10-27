package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.exception.MountLimitException;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.serialize.FileObjectTable;
import cn.luo.yuan.serialize.ObjectTable;
import cn.luo.yuan.maze.server.LogHelper;
import cn.luo.yuan.maze.server.MainProcess;
import cn.luo.yuan.maze.service.AccessoryHelper;
import cn.luo.yuan.maze.service.SkillHelper;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by gluo on 5/22/2017.
 */
public class HeroTable implements Runnable {
    private ObjectTable<ServerRecord> recordDb;
    private long maxLevel = 1;
    private File root;

    public HeroTable(File root) throws IOException, ClassNotFoundException {
        this.root = root;
        recordDb = new FileObjectTable<>(ServerRecord.class, root);
    }

    public String queryBattleAward(String id) {
        ServerRecord record = getRecord(id);
        if (record!=null && record.getData() != null) {
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
            try {
                recordDb.save((ServerRecord) obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public Maze getMaze(String id, long level) throws IOException, ClassNotFoundException {
        ServerRecord record = getRecord(id);
        if (record != null) {
            ServerData data = record.getData();
            if (data != null)
                return data.getMaze();
        }
        return null;
    }

    public ServerRecord getRecord(String id) {
        return recordDb.loadObject(id);
    }

    public void save() throws IOException {
        recordDb.fuse();
    }

    public String queryDataString(String id) {
        ServerRecord record = getRecord(id);
        if (record.getData() != null) {
            int t = record.getWinCount() + record.getLostCount();
            if (t <= 0) t++;
            return record.getData().getHero().getDisplayName() + "<br>"
                    + "排名：" + record.getRange() + "， 胜率：" + StringUtils.formatPercentage(record.getWinCount() * 100 / (t))
                    ;
        }
        return StringUtils.EMPTY_STRING;
    }

    public String pollBattleMsg(String id, int count) {
        ServerRecord record = getRecord(id);
        StringBuilder s = new StringBuilder();
        if(record!=null) {
            if (record.getMessages().size() > 10) {
                count = record.getMessages().size();
            }
            while (count-- > 0 && record.getMessages().size() > 0) {
                String poll = record.getMessages().poll();
                s.append(poll).append(count > 0 ? "<br>" : "");
            }
        }
        return s.toString();
    }

    public ServerData getBackHero(String id) throws IOException {
        ServerRecord record = getRecord(id);
        if (record != null && record.getData() != null && record.getData().getHero() != null) {
            LogHelper.info(record.getData().getHero().getDisplayName() + " Get back!");
            ServerData data = new ServerData(record.getData());
            record.setData(null);
            record.setDieCount(0);
            record.setDieTime(0);
            record.setCurrentWin(0);
            record.getMessages().clear();
            record.setRange(Integer.MAX_VALUE);
            record.setRestoreLimit(Data.RESTORE_LIMIT);
            save();
            return data;
        } else {
            return null;
        }
    }

    public void submitHero(ServerData data) throws IOException {
        ServerRecord record = getRecord(data.getHero().getId());
        if (record == null) {
            record = new ServerRecord();
            record.setId(data.getHero().getId());
        }
        record.setAward(false);
        record.setMac(data.getMac());
        record.setSubmitDate(System.currentTimeMillis());
        record.setRange(Integer.MAX_VALUE);
        record.setData(data);
        save(record);
        LogHelper.info(record.getData().getHero().getDisplayName() + " Submit!");
    }

    public Hero getHero(String name) throws IOException, ClassNotFoundException {
        return loadHero(name);
    }

    @Nullable
    public Object size() {
        return recordDb.size();
    }

    public List<String> getAllHeroIds() {
        return recordDb.loadIds();
    }


    public void delete(String id) {
        recordDb.delete(id);
    }

    private Hero loadHero(String id) throws IOException, ClassNotFoundException {
        ServerRecord record = getRecord(id);
        if (record != null) {
            ServerData data = record.getData();
            if (data != null) {
                Hero hero = data.getHero();
                if (data.getAccessories() != null && hero.getAccessories().isEmpty()) {
                    for (Accessory accessory : data.getAccessories()) {
                        try {
                            AccessoryHelper.mountAccessory(accessory, hero, false, MainProcess.buildGameContext(record, Executors.newSingleThreadScheduledExecutor()));
                        } catch (MountLimitException e) {
                            LogHelper.error(e);
                        }
                    }
                }
                if (data.getPets() != null && hero.getPets().isEmpty()) {
                    for (Pet pet : data.getPets()) {
                        hero.getPets().add(pet);
                    }
                }

                if(data.getSkills()!=null && hero.getSkills().length == 0){
                    for(Skill skill : data.getSkills()){
                        SkillHelper.mountSkill(skill, hero);
                    }
                }
                return hero;
            }
        }
        return null;
    }

    public ObjectTable<ServerRecord> getDb(){
        return recordDb;
    }
    private static final int DAYEXTIME = 3 * 24 * 60 * 60 * 1000;
    private static final int WEEKEXTIME = 7 * 24 * 60 * 60 * 1000;

    public void run(){
        for(String id : getAllHeroIds()){
            boolean delete = false;
            ServerRecord record = getRecord(id);
            if(record == null){
                delete = true;
            }else{
                if((record.getData() == null || record.getDieCount() >= record.getRestoreLimit()) && System.currentTimeMillis() - record.getSubmitDate() > DAYEXTIME){
                    delete = true;
                }else{
                    if(System.currentTimeMillis() - record.getSubmitDate() > WEEKEXTIME){
                        delete = true;
                    }
                }
            }
            if(delete){
                delete(id);
            }
        }
    }

}
