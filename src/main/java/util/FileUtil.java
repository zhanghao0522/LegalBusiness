package util;

import java.io.File;

/**
 * Created by ZhangHao on 2017/9/30.
 */
public class FileUtil {

    /**
     *
     * @param pagination 卷宗中读取到的页码
     * @param filePath 当前文件路径（上级），如："F:\\soft\\司法文档\\2016苏8602民初00994号\\Z"
     * @return 返回该页码所代表的文件路径
     *
     * @author: ZhangHao
     * @date: 2017/10/9 10:19
     */
    public static String getFilePath(String pagination , String filePath){
        int k = pagination.length();
        for (int i = 0; i < 4 - k; i++){
            pagination = "0" + pagination;
        }
        return filePath + File.separator + pagination + ".txt";
    }

    public static void main(String[] args){
        System.out.println(getFilePath("44","F:\\soft\\司法文档\\2016苏8602民初00994号\\Z"));
    }
}
