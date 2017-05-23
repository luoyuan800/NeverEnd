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
   default Monster randomMonster(long level){
       if (getMonsterCache().size() == 0) {
           init();
       }
       for (Map.Entry<MonsterKey, WeakReference<Monster>> entry : getMonsterCache().entrySet()) {
           MonsterKey key = entry.getKey();
           Monster monster = entry.getValue().get();
           if (key.min_level < level && getRandom().nextInt(100 + key.count) < key.meet_rate) {
               key.count++;
               if (monster == null) {
                   monster = loadMonsterByIndex(key.index);
               }
               if (monster != null) {
                   Monster clone = monster.clone();
                   if (clone != null) {
                       if (clone.getSex() < 0) {
                           clone.setSex(getRandom().nextInt(1));
                       }
                       clone.setElement(Element.values()[getRandom().nextInt(Element.values().length)]);
                       clone.setMaterial(Data.getMonsterMaterial(monster.getMaxHp(), monster.getAtk(), level, getRandom()));
                       clone.setFirstName(FirstName.getRandom(level, getRandom()));
                       clone.setSecondName(SecondName.getRandom(level, getRandom()));
                   }
                   return clone;
               }
           } else {
               key.count--;
           }
       }
       return null;
   }

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
