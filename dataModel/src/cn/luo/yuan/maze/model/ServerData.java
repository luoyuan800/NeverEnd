package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

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
    public List<Accessory> awardAccessories;
    public List<Pet> pets;
    public List<Pet> awardPets;
    public List<Skill> skills;
    public long material;

    public String toString(){
        StringBuilder acces = new StringBuilder();
        if(awardAccessories!=null && awardAccessories.size() > 0){
            for(Accessory accessory : awardAccessories){
                acces.append(accessory.getDisplayName()).append("<br>");
            }
        }else{
            acces.append("无");
        }
        StringBuilder petb = new StringBuilder();
        if(awardPets!=null && awardPets.size() > 0){
            for(Pet pet : awardPets){
                petb.append(pet.getDisplayName()).append("<br>");
            }
        }else{
            petb.append("无");
        }
        return "可以获得锻造点：" + StringUtils.formatNumber(material) + "<br>"
                + "装备：<br>"
                + petb
                + "宠物："
                + petb;
    }
 }
