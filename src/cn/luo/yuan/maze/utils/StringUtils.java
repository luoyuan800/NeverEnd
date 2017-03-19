package cn.luo.yuan.maze.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by gluo on 9/8/2015.
 * Use for handle string input type
 */
public class StringUtils {
    public static String formatNumber(long num){
        Double value;
        if(num > 100000000){
            value = num/100000000d;
            return String.format("%.1f", value) + "亿";
        }
        if(num > 10000000){
            value = num/10000000d;
            return String.format("%.1f", value) + "千万";
        }
        if(num > 10000){
            value = num/10000d;
            return String.format("%.1f", value) + "万";
        }
        return num + "";
    }

    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return "0x" + str;//0x表示十六进制
    }

    //转换十六进制编码为字符串
    public static String toStringHex(String s) {
        if ("0x".equals(s.substring(0, 2))) {
            s = s.substring(2);
        }
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            s = new String(baKeyword, "utf-16");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static boolean isNotEmpty(String countStr) {
        return countStr !=null && !countStr.trim().isEmpty() && !"null".equalsIgnoreCase(countStr);
    }

    public static String[] split(String str,String regularExpression){
        if(isNotEmpty(str)){
            return str.split(regularExpression);
        }else{
            return new String[]{""};
        }
    }

    public static Long toLong(String number){
        try {
            number = number.replaceFirst("~","-");
            return Long.parseLong(number);
        }catch (Exception e){
            try {
                return Double.valueOf(number).longValue();
            }catch (Exception e1){
                return 1l;
            }
        }
    }

    public static Float toFloat(String number){
        try {
            return Float.parseFloat(number);
        }catch (Exception e){
            try {
                return Double.valueOf(number).floatValue();
            }catch (Exception e1){
                return 0.1f;
            }
        }
    }

    public static void main(String...args){
        System.out.print(toStringHex("0x6c81739f"));
    }

    public static Integer toInt(String type) {
        try {
            return Integer.parseInt(type);
        }catch (Exception e){
            try {
                return Double.valueOf(type).intValue();
            }catch (Exception exp){
                return 1;
            }
        }
    }

    public static String readHelp(String name, Context context){
        StringBuilder controlHelp = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(name)));
            String line = reader.readLine();
            while(StringUtils.isNotEmpty(line)){
                controlHelp.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return controlHelp.toString();
    }

    /**
     * 使用DecimalFormat，保留小数点后n位数
     *
     * @param value 需要进行四舍五入的数字
     * @param scale 精度（保留小数点后几位）
     * @return 数字文本
     */
    public static String DecimalFormatRound(float value, int scale) {
        StringBuilder sb = new StringBuilder("0");
        if (scale > 0) {
            sb.append(".");
            while (scale > 0) {
                sb.append("0");
                scale--;
            }
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value);

    }
}
