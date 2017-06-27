package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class ServerData implements Serializable {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    public Hero hero;
    public Maze maze;
    public List<Accessory> accessories;
    public List<Pet> pets;
    public List<Skill> skills;
    public long material;
 }
