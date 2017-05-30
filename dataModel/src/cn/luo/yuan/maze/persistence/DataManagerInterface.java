package cn.luo.yuan.maze.persistence;

import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsType;
import cn.luo.yuan.maze.model.skill.Skill;

import java.util.List;

/**
 * Created by gluo on 5/12/2017.
 */
public interface DataManagerInterface {
    Goods loadGoods(GoodsType type);

    List<Pet> loadPets(int start, int rows, String keyWord);

    void saveSkill(Skill skill);

    Skill loadSkill(String name);
}
