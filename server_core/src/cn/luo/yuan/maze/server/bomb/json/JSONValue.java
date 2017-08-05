package cn.luo.yuan.maze.server.bomb.json;

/**
 * Created by luoyuan on 2016/6/18.
 */
public class JSONValue<T> {
    T value;
    public T getValue(){
        return value;
    }
    public void setValue(T value){
        this.value = value;
    }
    public String toString(){
        if(value instanceof String ){
            return "\"" + value + "\"";
        }else{
            return String.format("%s", value);
        }
    }
}
