package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.utils.Random;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by gluo on 5/23/2017.
 */
public class MonsterLoader implements cn.luo.yuan.maze.service.MonsterLoader {
    @Override
    public Map<MonsterKey, WeakReference<Monster>> getMonsterCache() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public Random getRandom() {
        return null;
    }

    @Override
    public Monster randomMonster(long level, boolean addKey) {
        return null;
    }

    @Override
    public String getDescription(int index, String type) {
        return null;
    }

    @Override
    public int getEvolutionIndex(int index) {
        return 0;
    }

    @Override
    public Monster loadMonsterByIndex(int index) {
        return null;
    }
}
