package entity;

/**
 * 对卷宗中每条处理失败的记录的封装
 *
 * Created by zhanghao on 2017/10/2.0:25
 */
public class JuanZongDefeatInfo {
    //文书名称
    private String name;
    //文书页码
    private String thisPagination;
    //上次判断成功的页码
    private String lastPagination;
    //下次判断成功的页码
    private String nextPagination;

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

    public String getLastPagination() {
        return lastPagination;
    }

    public void setLastPagination(String lastPagination) {
        this.lastPagination = lastPagination;
    }

    public String getNextPagination() {
        return nextPagination;
    }

    public void setNextPagination(String nextPagination) {
        this.nextPagination = nextPagination;
    }
}
