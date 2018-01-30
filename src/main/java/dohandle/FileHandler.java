package dohandle;

import entity.FileElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangHao on 2017/9/25.
 */
public interface FileHandler {

    public Map<String,Object> handle(String str) throws Exception;

    public Map<String,Object> handleNew(List<String> listFilePath) throws Exception;

    public List<FileElement> handle2(String str) throws Exception;

    public List<FileElement> handleNew2(List<String> listFilePath) throws Exception;

}
