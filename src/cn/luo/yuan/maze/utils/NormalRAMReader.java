package cn.luo.yuan.maze.utils;

/**
 * Created by luoyuan on 2017/4/23.
 */
public class NormalRAMReader {
    private int key;
    public NormalRAMReader(int key){
        this.key = key;
    }
    public static int generateKey(){
        return new Random(System.currentTimeMillis()).nextInt();
    }

    public long decodeLong(long value) {
        return (value - key) * key + key;
    }

    public long encodeLong(long value) {
        return ((value - key)/key) + key;
    }
}
