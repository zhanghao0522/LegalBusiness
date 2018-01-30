package entity;

/**
 * Created by ZhangHao on 2017/10/11.
 */
public class FileElement {
    //文书名称
    private String strName;
    //该文书起始页码
    private int strStartIndex;
    //该文书结束页码(多一页，使用是需要减一)
    private int strEndIndex;
    //该文书结束页码(多一页，使用是需要减一)
    private String strContent;

    public String getStrContent() {
        return strContent;
    }

    public void setStrContent(String strContent) {
        this.strContent = strContent;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public int getStrStartIndex() {
        return strStartIndex;
    }

    public void setStrStartIndex(int strStartIndex) {
        this.strStartIndex = strStartIndex;
    }

    public int getStrEndIndex() {
        return strEndIndex;
    }

    public void setStrEndIndex(int strEndIndex) {
        this.strEndIndex = strEndIndex;
    }
}
