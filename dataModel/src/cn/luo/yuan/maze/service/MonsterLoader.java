package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.utils.Random;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by gluo on 5/19/2017.
 */
public interface MonsterLoader {
    Map<MonsterKey, WeakReference<Monster>> getMonsterCache();
    void addSpecialMonster(Monster monster);
    void init();

    Random getRandom();
    Monster randomMonster(long level, boolean addKey);
    String getDescription(int index, String type);

    int getEvolutionIndex(int index);

    Monster loadMonsterByIndex(int index);

    static class MonsterKey {
        public int count;
        public float meet_rate;
        public float min_level;
        public int index;

        public int hashCode() {
            return index;
        }

        public boolean equals(Object o) {
            return o instanceof MonsterKey && ((MonsterKey) o).index == index;
        }
    }

}
