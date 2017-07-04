package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.utils.Random;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gluo on 6/30/2017.
 */
public class MockMonsterLoader implements MonsterLoader {
    private Map<MonsterKey, WeakReference<Monster>> cache;
    private ArrayList<Monster> monsterList;

    public MockMonsterLoader() {
    }

    @Override
    public Map<MonsterKey, WeakReference<Monster>> getMonsterCache() {
        return cache;
    }

    @Override
    public void init() {

    }

    @Override
    public Random getRandom() {
        return new Random(System.currentTimeMillis());
    }

    @Override
    public Monster randomMonster(long level, boolean addKey) {
        return getRandom().randomItem(monsterList);
    }

    @Override
    public String getDescription(int index, String type) {
        return "";
    }

    @Override
    public int getEvolutionIndex(int index) {
        return 0;
    }

    @Override
    public Monster loadMonsterByIndex(int index) {
        return getMonsterList().get(index);
    }

    public void setCache(Map<MonsterKey, WeakReference<Monster>> cache) {
        this.cache = cache;
    }

    public List<Monster> getMonsterList() {
        return monsterList;
    }

    public void setMonsterList(ArrayList<Monster> monsterList) {
        this.monsterList = monsterList;
    }
}
