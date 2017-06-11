package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.utils.Random;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by gluo on 5/19/2017.
 */
public interface MonsterLoader {
    Map<MonsterKey, WeakReference<Monster>> getMonsterCache();

    void init();

    Random getRandom();
    Monster randomMonster(long level, boolean addKey);
    String getDescription(int index, String type);

    int getEvolutionIndex(int index);

    Monster loadMonsterByIndex(int index);

    static class MonsterKey {
        int count;
        float meet_rate;
        float min_level;
        int index;

        public int hashCode() {
            return index;
        }

        public boolean equals(Object o) {
            return o instanceof MonsterKey && ((MonsterKey) o).index == index;
        }
    }

}
