package cn.luo.yuan.maze.persistence;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsType;
import cn.luo.yuan.maze.model.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Created by gluo on 5/12/2017.
 */
public interface DataManagerInterface {
    Goods loadGoods(GoodsType type);

    List<Pet> loadPets(int start, int rows, String keyWord, Comparator<Pet> comparator);

    void saveSkill(Skill skill);

    Skill loadSkill(String name);

    Accessory findAccessoryByName(@NotNull String name);

    @NotNull
    List<Pet> findPetByType(@NotNull String type);
}
