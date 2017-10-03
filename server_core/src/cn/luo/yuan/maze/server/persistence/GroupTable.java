package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.GroupHolder;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.persistence.DatabaseConnection;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GroupTable {
    public Set<GroupHolder> groups = Collections.synchronizedSet(new HashSet<GroupHolder>());
    private DatabaseConnection db;
    public GroupTable(DatabaseConnection db){
        this.db = db;
    }

    public GroupHolder add(String h1, String h2) {
        GroupHolder holder = new GroupHolder();
        holder.getHeroIds().add(h1);
        holder.getHeroIds().add(h2);
        groups.add(holder);
        return holder;
    }

    public Set<GroupHolder> getGroups() {
        return groups;
    }

    public void remove(String heroId) {
        for (GroupHolder holder : new ArrayList<>(groups)) {
            if (holder.isInGroup(heroId)) {
                groups.remove(holder);
            }
        }
    }

    public void remove(GroupHolder group){
        groups.remove(group);
    }

    public String getGroupMessage(String heroId, HeroTable heroTable) {
        GroupHolder holder = null;
        if (StringUtils.isNotEmpty(heroId)) {
            for (GroupHolder holder1 : groups) {
                if (holder1.isInGroup(heroId)) {
                    holder = holder1;
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        ServerRecord record = heroTable.getRecord(heroId);
        if (record != null && record.getData() != null && record.getData().getHero() != null) {
            builder.append(record.getData().getHero().getDisplayName()).append("<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：").
                    append(record.winRate()).append("<br>&nbsp;&nbsp;&nbsp;&nbsp;剩余复活次数：").
                    append(StringUtils.formatNumber(record.getRestoreLimit() - record.getDieCount(), false));
        }
        if (holder != null) {
            for (String hid : holder.getHeroIds()) {
                if (!hid.equals(heroId)) {
                    if (hid.equals("npc")) {
                        builder.append("<br>").append(holder.getNpc().getDisplayName());
                    } else {
                        ServerRecord orecord = heroTable.getRecord(hid);
                        if (orecord != null && orecord.getData() != null && orecord.getData().getHero() != null) {
                            builder.append("<br>").append(orecord.getData().getHero().getDisplayName())
                                    .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;胜率：").append(orecord.winRate())
                                    .append("<br>&nbsp;&nbsp;&nbsp;&nbsp;剩余复活次数：").
                                    append(StringUtils.formatNumber(orecord.getRestoreLimit() - orecord.getDieCount(), false))
                                    .append("<br>");
                        }
                    }
                }
            }
        }
        return builder.toString();
    }

    public GroupHolder find(String id){
        for (GroupHolder holder : groups) {
            if (holder.isInGroup(id)) {
                return holder;
            }
        }
        return null;
    }
}
