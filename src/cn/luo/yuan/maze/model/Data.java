package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.Random;

/**
 * 定义一些基本的游戏数据，这些数据只是根据经验和个人喜欢设计的，所以特意抽取出来放到一起方便修改
 * Created by luoyuan on 2017/3/28.
 */
public class Data {
    public static final int MAX_SELL_COUNT = 30;//商店最大的商品数量
    public static float MONSTER_MEET_RATE = 99f;//遇怪的概率
    public static float PORTAL_RATE = 0.1f;//传送门的概率
    public static int LEVEL_BASE_POINT_REDUCE = 1000;//爬楼奖励的点数基数分母（random（level/reduce））
    public static long REFRESH_SPEED = 500;//刷新间隔
    public static long MONSTER_ATK_RISE_PRE_LEVEL = 100;//怪物每层的攻击成长
    public static long MONSTER_HP_RISE_PRE_LEVEL = 500;//怪物每层的生命成长
    public static long MONSTER_DEF_RISE_PRE_LEVEL = 50;//怪物每层的防御成长
    public static float DODGE_AGI_RATE = 0.0001f;//敏捷转换成闪避率加成
    public static float DODGE_STR_RATE = 0.0001f;//力量转换成闪避率衰减
    public static float PARRY_STR_RATE = 0.001f;//力量转化格挡率加成
    public static float PARRY_AGI_RATE = 0.0001f;//敏捷转化格挡率衰减
    public static float HIT_AGI_RATE = 0.001f;//敏捷转化重击率衰减
    public static float HIT_STR_RATE = 0.002f;//力量转化重击率加成
    public static int SKILL_ENABLE_COST = 50;//激活技能消耗的能力点
    public static final String BLUE = "";
    public static final String RED = "red";
    public static final int ACCESSORY_FLUSE_LIMIT = 15;

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
