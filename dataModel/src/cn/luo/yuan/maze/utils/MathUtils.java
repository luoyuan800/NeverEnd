package cn.luo.yuan.maze.utils;

/**
 * Created by gluo on 7/3/2017.
 */
public class MathUtils {
    public static long getPercentAdditionReduceValue(long value, float percent) {
        return (long) (value / (percent / 100 + 1));
    }
}
