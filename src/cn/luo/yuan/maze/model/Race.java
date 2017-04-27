package cn.luo.yuan.maze.model;

/**
 * Created by gluo on 6/16/2016.
 */
public enum Race {
    Nonsr("无"),Elyosr("神族"),Orger("魔族"),Wizardsr("仙族"),Eviler("妖族"),Ghosr("鬼族");
    private String name;
    Race(String name){
        this.name = name;
    }
    public static Race getByIndex(int index){
        if(index < values().length) {
            return values()[index];
        }else{
            return Nonsr;
        }
    }

    public String getName() {
        return name;
    }
}
