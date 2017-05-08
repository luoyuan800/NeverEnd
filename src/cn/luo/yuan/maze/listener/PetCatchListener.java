package cn.luo.yuan.maze.listener;

import cn.luo.yuan.maze.model.Pet;

/**
 * Created by gluo on 5/8/2017.
 */
public interface PetCatchListener extends Listener {
    void catchPet(Pet pet);
}
