package cn.luo.yuan.maze.model;

/**
 * Created by gluo on 6/16/2016.
 */
public enum Race{
    Nonsr("无", ""),Elyosr("神族", "#EC9C07"),Orger("魔族", "#d40210"),Wizardsr("仙族", "#EB6DC7"),Eviler("妖族", "#7a31ff"),Ghosr("鬼族", "#2AB906");
    private String name;
    private String color;
    Race(String name, String color){
        this.name = name;
        this.color = color;
    }
    public static Race getByIndex(int index){
        if(index < values().length) {
            return values()[index];
        }else{
            return Nonsr;
        }
    }

    public String toString(){
        return name;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public boolean isRestriction(Race race){
        return race.ordinal() == ordinal() + 1 && race.ordinal() == ordinal() - 5;
    }
}
