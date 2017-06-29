package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Egg;
import cn.luo.yuan.maze.model.Pet;

/**
 * Created by luoyuan on 2017/6/29.
 */
public interface PetMonsterHelperInterface {
    Egg buildEgg(Pet p1, Pet p2, InfoControlInterface gameControl);
}
