package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.skill.Skill;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class ServerData implements Serializable {
    public Hero hero;
    public Maze maze;
    public List<Accessory> accessories;
    public List<Pet> pets;
    public List<Skill> skills;
    public long material;
 }
