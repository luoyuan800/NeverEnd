package cn.luo.yuan.maze.utils;

/**
 * Created by gluo on 5/17/2017.
 */
public class TestRandom {
    public static void main(String...args){
        Random random = new Random(System.currentTimeMillis());
        /*for(int i = 0; i< 100; i++){
            System.out.println("Random Int in Long: " + random.nextLong(100000000));
            System.out.println("Random Long in Long: " + random.nextLong(10000000000000000L));
        }*/
        for(int i = 0; i< 100; i++){
            System.out.println("Random float: " + random.nextFloat(10.11f));
        }
        /*
        for(int i = 0; i< 100; i++){
            System.out.println("Random float range: " + random.randomRange(10.11f, 20.25f));
        }
        for(int i = 0; i< 100; i++){
            System.out.println("Random long range: " + random.randomRange(10L, 30L));
        }*/
    }
}
