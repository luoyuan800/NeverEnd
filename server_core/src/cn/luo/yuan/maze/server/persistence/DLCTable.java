package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.dlc.DLC;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.serialize.ObjectTable;
import cn.luo.yuan.maze.server.MainProcess;

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
        this.dlcTable = new ObjectTable<>(DLC.class, process.root);
    }

    public ObjectTable<DLC> getDLCTable() {
        return dlcTable;
    }

    public List<DLCKey> queryKeys(Set<String> filterOut) {
        List<DLCKey> keys = new ArrayList<>();
        for (String title : dlcTable.loadIds()) {
            DLC dlc = dlcTable.loadObject(title);
            if (dlc != null) {
                DLCKey key = new DLCKey();
                key.setId(dlc.getId());
                key.setCost(dlc instanceof MonsterDLC && filterOut.contains(dlc.getId()) ? dlc.getDebrisCost()/2 : dlc.getDebrisCost());
                key.setDesc(dlc.getDesc());
                keys.add(key);
            }
        }
        return keys;
    }

    public DLC getDLC(String id) {
        return dlcTable.loadObject(id);
    }
}
