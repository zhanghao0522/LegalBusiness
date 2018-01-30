package entity;

/**
 * Created by ZhangHao on 2017/10/10.
 */
public class JuanZongInfo {
    //文书序号
    private String strSeq;
    //文书名称
    private String strName;
    //文书页码
    private String strThisPagination;
    //是否匹配成功
    private boolean isSuccess;
    //匹配到的文书类型
    private String strType;
    //该文书起始页码
    private String strBeginPagination;
    //该文书结束页码(多一页，使用是需要减一)
    private String strEndPagination;
    //是否匹配首尾页成功
    private boolean isSuccessBeginEnd;

    public String getStrSeq() {
        return strSeq;
    }

    public void setStrSeq(String strSeq) {
        this.strSeq = strSeq;
    }

    public boolean getIsSuccessBeginEnd() {
        return isSuccessBeginEnd;
    }

    public void setIsSuccessBeginEnd(boolean successBeginEnd) {
        isSuccessBeginEnd = successBeginEnd;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrThisPagination() {
        return strThisPagination;
    }

    public void setStrThisPagination(String strThisPagination) {
        this.strThisPagination = strThisPagination;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean success) {
        isSuccess = success;
    }

    public String getStrType() {
        return strType;
    }

    public void setStrType(String strType) {
        this.strType = strType;
    }

    public String getStrBeginPagination() {
        return strBeginPagination;
    }

    public void setStrBeginPagination(String strBeginPagination) {
        this.strBeginPagination = strBeginPagination;
    }

    public String getStrEndPagination() {
        return strEndPagination;
    }

    public void setStrEndPagination(String strEndPagination) {
        this.strEndPagination = strEndPagination;
    }
}
