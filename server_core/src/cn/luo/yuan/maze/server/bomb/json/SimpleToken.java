package cn.luo.yuan.maze.server.bomb.json;


import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleToken {
    private String content;
    private Map<String, MyJSONValue> data = new HashMap<>();

    public SimpleToken(String content) {
        this.content = content;
    }

    public SimpleToken() {

    }

    public void parse() {
        String[] array = content.split(",(?=\")");
        for (int i =0 ; i< array.length; i++) {
            String[] entry = array[i].split("(?<=\"):");
            if (entry.length > 1) {
                if(entry[1].startsWith("[") && !entry[1].endsWith("]")){
                    while(i < array.length) {
                        entry[1] = entry[1] + "," + array[++i];
                        if(i < array.length && array[i].endsWith("]")){
                            break;
                        }
                    }
                }
                if (entry[1].matches("^\\[.*\\]$")) {
                    MyJSONValue<List<MyJSONValue>> value = new MyJSONValue<>();
                    entry[1] = entry[1].replaceAll("\\[|\\]", "");
                    String[] listArray = entry[1].split(",");
                    List<MyJSONValue> jsondata = new ArrayList<>();
                    for (String s : listArray) {
                        MyJSONValue value1 = buildJSONValue(s);
                        jsondata.add(value1);
                    }
                    value.setValue(jsondata);
                    data.put(entry[0].replaceAll("\"", ""), value);
                } else {
                    data.put(entry[0].replaceAll("\"", ""), buildJSONValue(entry[1]));
                }
            }
        }
    }

    private MyJSONValue buildJSONValue(String value) {
        if(value.matches("\\d+")) {
            //Number type
            if(value.indexOf(".") > 0){
                //Double
                MyJSONValue<Double> jsonValue = new MyJSONValue<>();
                jsonValue.setValue(StringUtils.toFloat(value).doubleValue());
                return jsonValue;
            }else{
                //Long
                MyJSONValue<Long> jsonValue = new MyJSONValue<>();
                jsonValue.setValue(StringUtils.toLong(value));
                return jsonValue;
            }

        }else{
            //String
            MyJSONValue<String> jsonValue = new MyJSONValue<>();
            jsonValue.setValue(value.replaceAll("\"", ""));
            return jsonValue;
        }
    }

    public String toString() {
        return data.toString();
    }

    public Map<String, MyJSONValue> getData() {
        return data;
    }

    public String toJSONString() {
        StringBuilder builder = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, MyJSONValue> entry : data.entrySet()) {
            builder.append("\"").append(entry.getKey()).append("\"").append(":").append(entry.getValue().toString());
            if (i < data.size() - 1) {
                builder.append(",");
            }
            i++;
        }
        builder.append("}");
        return builder.toString();
    }

    public <T> T getValue(String key) {
        MyJSONValue value = data.get(key);
        if (value != null && value.getValue() != null) {
            return (T) value.getValue();
        }
        return null;
    }

    public <T> void setValue(String key, T value) {
        MyJSONValue<T> jsonValue = new MyJSONValue<>();
        jsonValue.setValue(value);
        this.data.put(key, jsonValue);
    }

    public void removeValue(String key){
        data.remove(key);
    }
}

