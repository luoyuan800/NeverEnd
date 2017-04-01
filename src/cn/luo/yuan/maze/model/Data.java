package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.Random;

/**
 * 定义一些基本的游戏数据，这些数据只是根据经验和个人喜欢设计的，所以特意抽取出来放到一起方便修改
 * Created by luoyuan on 2017/3/28.
 */
public class Data {
    public static float MONSTER_MEET_RATE = 99f;//遇怪的概率
    public static float PORTAL_RATE = 0.1f;//传送门的概率
    public static int LEVEL_BASE_POINT_REDUCE = 1000;//爬楼奖励的点数基数分母（random（level/reduce））
    public static long REFRESH_SPEED = 500;//刷新间隔
    public static long MONSTER_ATK_RISE_PRE_LEVEL = 100;//怪物每层的攻击成长
    public static long MONSTER_HP_RISE_PRE_LEVEL = 500;//怪物每层的生命成长
    public static long MONSTER_DEF_RISE_PRE_LEVEL = 50;//怪物每层的防御成长

    /**
     * 计算击败一个怪物可以获得的锻造点
     * @param hp 怪物的最高血量
     * @param atk 怪物的攻击
     * @param level 当前层数
     * @return 返回可以获得的基础锻造数量
     */
    public static long getMonsterMaterial(long hp, long atk, long level, Random random){
        long m1 = random.nextLong(hp + 1) / 181 + 3;
        long m2 = random.nextLong(atk + 1) / 411 + 5;
        long material = random.nextLong((m1 + m2) / 1832 + 1) + 10 + random.nextLong(level / 100 + 1);
        if (material > 1000) {
            material = 300 + random.nextInt(700);
        }
        return material;
    }
}
