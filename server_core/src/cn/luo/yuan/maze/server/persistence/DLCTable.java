package cn.luo.yuan.maze.server.persistence;

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
    private ObjectTable<MonsterDLC> monsterDLCTable;
    private MainProcess process;

    public DLCTable(MainProcess process) {
        this.process = process;
        this.monsterDLCTable = new ObjectTable<>(MonsterDLC.class, process.root);
    }

    public ObjectTable<MonsterDLC> getMonsterDLCTable() {
        return monsterDLCTable;
    }

    public List<DLCKey> queryKeys(Set<String> filterOut) {
        List<DLCKey> keys = new ArrayList<>();
        for (String title : monsterDLCTable.loadIds()) {
            MonsterDLC dlc = monsterDLCTable.loadObject(title);
            if (dlc != null) {
                DLCKey key = new DLCKey();
                key.setId(dlc.getId());
                key.setCost(filterOut.contains(dlc.getId()) ? 0 : dlc.getDebrisCost());
                key.setDesc(dlc.getDesc());
                keys.add(key);
            }
        }
        return keys;
    }

    public MonsterDLC getMonsterDLC(String id) {
        return monsterDLCTable.loadObject(id);
    }
}
