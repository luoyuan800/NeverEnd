package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Monster;

/**
 * Created by gluo on 5/19/2017.
 */
public interface MonsterLoader {
    Monster randomMonster();

    String getDescription(int index, String type);

    int getEvolutionIndex(int index);

    Monster loadMonsterByIndex(int index);
}
