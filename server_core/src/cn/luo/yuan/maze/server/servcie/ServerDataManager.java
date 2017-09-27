package cn.luo.yuan.maze.server.servcie;

import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.persistence.DataManagerInterface;
import cn.luo.yuan.object.serializable.ObjectTable;
import cn.luo.yuan.object.IDModel;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by luoyuan on 2017/9/19.
 */
public class ServerDataManager implements DataManagerInterface {
    private Hero hero;
    private NeverEndConfig config;
    public ServerDataManager(Hero hero, NeverEndConfig config){
        this.hero = hero;
        this.config = config;
    }

    @Override
    public ObjectTable<NPCLevelRecord> getNPCTable() {
        return null;
    }

    @Override
    public Goods loadGoods(String name) {
        return null;
    }

    @Override
    public List<Skill> loadAllSkill() {
        return Arrays.asList(hero.getSkills());
    }

    @Override
    public void save(IDModel o) {

    }

    @Override
    public void add(IDModel o) {

    }

    @Override
    public List<Pet> loadPets(int start, int rows, String keyWord, Comparator<Pet> comparator) {
        return new ArrayList<>(hero.getPets());
    }

    @Override
    public void saveSkill(Skill skill) {

    }

    @Override
    public Skill loadSkill(String name) {
        for(Skill s : hero.getSkills()){
            if(s.getClass().getSimpleName().equals(name)){
                return s;
            }
        }
        return null;
    }

    @Override
    public Accessory findAccessoryByName(@NotNull String name) {
        return null;
    }

    @NotNull
    @Override
    public List<Pet> findPetByType(@NotNull String type) {
        return Collections.emptyList();
    }

    @Override
    public void saveAccessory(Accessory accessory) {

    }

    @Override
    public void savePet(@NotNull Pet pet) {

    }

    @Override
    public NeverEndConfig loadConfig() {
        return config;
    }
}
