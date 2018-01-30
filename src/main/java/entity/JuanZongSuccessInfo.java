package entity;

/**
 * 对卷宗中每条处理成功的记录的封装
 *
 * Created by zhanghao on 2017/10/2.0:30
 */
public class JuanZongSuccessInfo {
    //文书名称
    private String name;
    //文书页码
    private String thisPagination;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThisPagination() {
        return thisPagination;
    }

    public void setThisPagination(String thisPagination) {
        this.thisPagination = thisPagination;
    }
}
