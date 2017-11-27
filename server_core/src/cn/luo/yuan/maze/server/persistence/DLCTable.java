package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.dlc.DLC;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.model.dlc.SkillDLC;
import cn.luo.yuan.serialize.FileObjectTable;
import cn.luo.yuan.serialize.ObjectTable;
import cn.luo.yuan.maze.server.MainProcess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/8/2017.
 */
public class DLCTable {
    private ObjectTable<DLC> dlcTable;
    private MainProcess process;

    public DLCTable(MainProcess process) {
        this.process = process;
        this.dlcTable = new FileObjectTable<>(DLC.class, process.root);
    }

    public ObjectTable<DLC> getDLCTable() {
        return dlcTable;
    }

    public List<DLCKey> queryKeys(String ownerId, int offset, int row) {
        List<DLCKey> keys = new ArrayList<>();
        ServerRecord record = process.heroTable.getRecord(ownerId);
        Set<String> dlcs = record.getDlcs();
        try {
            for(DLC dlc : dlcTable.loadLimit(offset, row, null, null)){
                if (dlc != null) {
                    DLCKey key = new DLCKey();
                    key.setId(dlc.getId());
                    key.setCost((dlc instanceof SkillDLC || dlc instanceof MonsterDLC) && dlcs!=null && dlcs.contains(dlc.getId()) ? dlc.getDebrisCost()/2 : dlc.getDebrisCost());
                    key.setType(dlc.getClass().getSimpleName());
                    if(dlcs !=null){
                        key.setBuy(dlcs.contains(dlc.getId()));
                    }
                    keys.add(key);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return keys;
    }

    public DLC getDLC(String id) {
        return dlcTable.loadObject(id);
    }
}
