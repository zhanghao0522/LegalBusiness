package entity;

/**
 * 对应庭审笔录中对话的封装，
 * 每一个发言人及其所发言内容对应一个该类对象
 * @author: ZhangHao
 * @date: 2017/9/26 13:27
 */
public class KeyValue {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
