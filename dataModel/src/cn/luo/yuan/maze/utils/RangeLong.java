package cn.luo.yuan.maze.utils;

import cn.luo.yuan.encode.number.EncodeLong;

/**
 * Created by luoyuan on 2017/6/17.
 */
public class RangeLong extends EncodeLong {
    private byte[] minValue;
    private byte[] minKey;
    private boolean minNegative;
    public RangeLong(long min, long max) {
        super(max);

    }

    public void setMaxValue(long value){
        setValue(value);
    }



}
