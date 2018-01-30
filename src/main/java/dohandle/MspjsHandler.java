package dohandle;

import entity.FileElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangHao on 2017/9/27.
 */
public class MspjsHandler implements FileHandler{

    public Map<String,Object> handle(String str) throws Exception{

        return new HashMap<String,Object>();
    }

    public Map<String,Object> handleNew(List<String> listFilePath) throws Exception{

        return new HashMap<String,Object>();
    }

    public List<FileElement> handle2(String str) throws Exception {
        return null;
    }

    public List<FileElement> handleNew2(List<String> listFilePath) throws Exception {
        return null;
    }
}