package dohandle;

import entity.FileElement;
import util.WriterExcelFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangHao on 2017/10/12.
 */
public class CssqsHandler implements FileHandler{

    //保存民事起诉状中的名称
    private static List<String> listElement = new ArrayList<String>();
    static {
        listElement.add("申请人");
        listElement.add("请求事项");
        listElement.add("事实理由");
        listElement.add("此致");
//      listElement.add("其余事项");
    }

    public Map<String, Object> handle(String str) throws Exception {
        List<FileElement> listFileElement = new ArrayList<FileElement>();
        for (String strName : listElement){
            int start = str.indexOf(strName);
            int end = start + strName.length();
            FileElement fileElement = new FileElement();
            fileElement.setStrName(strName);
            fileElement.setStrStartIndex(start);
            fileElement.setStrEndIndex(end);
            listFileElement.add(fileElement);
        }
        for (int i = 0;i < listFileElement.size()-1; i++){
            int start = listFileElement.get(i).getStrEndIndex();
            int end = listFileElement.get(i+1).getStrStartIndex();
            String content = str.substring(start,end);
            //申请人元素这里需要去掉冒号，所以再次截取
            if (listFileElement.get(i).getStrName().equals("申请人"))
                content = content.substring(1);
            content = handleStr(content);
            listFileElement.get(i).setStrContent(content);
        }
        /*处理“此致”*/
        FileElement fileElementLast = listFileElement.get(listFileElement.size()-1);
        int endLast = fileElementLast.getStrEndIndex();
        String content = str.substring(endLast+1);
        //将文书末尾的署名部分去掉
        String content2= content.substring(content.indexOf("\r"),content.indexOf("\r申请人："));
        content = content.substring(0,content.indexOf("\r"));
        fileElementLast.setStrContent(content);
        /*特殊处理，处理“其余事项”*/
        FileElement fileElementQysx = new FileElement();
        fileElementQysx.setStrName("其余事项");
        fileElementQysx.setStrContent(handleStr(content2));
        listFileElement.add(fileElementQysx);
        /*标题直接按“撤诉申请书”处理*/
        FileElement fileElementTitle = new FileElement();
        fileElementTitle.setStrName("标题");
        fileElementTitle.setStrContent("撤诉申请书");
        listFileElement.add(fileElementTitle);

        /*先暂时将结果转化为Map*/
        Map<String,Object> resultMap = new HashMap<String , Object>();
        for (FileElement fileElement : listFileElement){
            resultMap.put(fileElement.getStrName(),fileElement.getStrContent());
        }

        return resultMap;
    }

    public Map<String, Object> handleNew(List<String> listFilePath) throws Exception {
        StringBuilder doc1 = new StringBuilder();
        /*循环读取文件路径，读取其中内容输出到变量doc1中*/
        for(String strFilePath : listFilePath){
            File file = new File(strFilePath);

            BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file),"Unicode"));//构造一个BufferedReader类来读取文件
            String s = "";
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                doc1.append("\r"+s);
            }
            br.close();
        }
//        System.out.println(doc1);
        /*连续很多页的文档内容已经全部拼接起来到了doc1中，下来开始进行解析工作*/
        Map<String,Object> mapResult = handle(doc1.toString());

        return mapResult;
    }

    public List<FileElement> handle2(String str) throws Exception {
        List<FileElement> listFileElement = new ArrayList<FileElement>();
        for (String strName : listElement){
            int start = str.indexOf(strName);
            int end = start + strName.length();
            FileElement fileElement = new FileElement();
            fileElement.setStrName(strName);
            fileElement.setStrStartIndex(start);
            fileElement.setStrEndIndex(end);
            listFileElement.add(fileElement);
        }
        for (int i = 0;i < listFileElement.size()-1; i++){
            int start = listFileElement.get(i).getStrEndIndex();
            int end = listFileElement.get(i+1).getStrStartIndex();
            String content = str.substring(start,end);
            //申请人元素这里需要去掉冒号，所以再次截取
            if (listFileElement.get(i).getStrName().equals("申请人"))
                content = content.substring(1);
            content = handleStr(content);
            listFileElement.get(i).setStrContent(content);
        }
        /*处理“此致”*/
        FileElement fileElementLast = listFileElement.get(listFileElement.size()-1);
        int endLast = fileElementLast.getStrEndIndex();
        String content = str.substring(endLast+1);
        //将文书末尾的署名部分去掉
        String content2= content.substring(content.indexOf("\r"),content.indexOf("\r申请人："));
        content = content.substring(0,content.indexOf("\r"));
        fileElementLast.setStrContent(content);
        /*特殊处理，处理“其余事项”*/
        FileElement fileElementQysx = new FileElement();
        fileElementQysx.setStrName("其余事项");
        fileElementQysx.setStrContent(handleStr(content2));
        listFileElement.add(fileElementQysx);
        /*标题直接按“撤诉申请书”处理*/
        FileElement fileElementTitle = new FileElement();
        fileElementTitle.setStrName("标题");
        fileElementTitle.setStrContent("撤诉申请书");
        listFileElement.add(fileElementTitle);

        return listFileElement;
    }

    public List<FileElement> handleNew2(List<String> listFilePath) throws Exception {
        StringBuilder doc1 = new StringBuilder();
        /*循环读取文件路径，读取其中内容输出到变量doc1中*/
        for(String strFilePath : listFilePath){
            File file = new File(strFilePath);

            BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file),"Unicode"));//构造一个BufferedReader类来读取文件
            String s = "";
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                doc1.append("\r"+s);
            }
            br.close();
        }
//        System.out.println(doc1);
        /*连续很多页的文档内容已经全部拼接起来到了doc1中，下来开始进行解析工作*/
        List<FileElement> list = handle2(doc1.toString());

        return list;
    }

    /**
     * 去除字符串中所有空格和\r
     * @author: ZhangHao
     * @date: 2017/10/11 11:35
     */
    private String handleStr(String str){
        return str.replaceAll("\\s*", "").replaceAll("\\r","");
    }


    public static void main(String[] args){
        CssqsHandler m = new CssqsHandler();
        List<String> listFilePath = new ArrayList<String>();
        listFilePath.add("F:\\OCR_output\\2016苏8602民初00052号\\Z\\0102.txt");
        try {
            Map<String,Object> mapResult = m.handleNew(listFilePath);

            List<Map<String,Object>> excelList = new ArrayList<Map<String, Object>>();
            List<String> titleList = new ArrayList<String>();
            for(Map.Entry entry : mapResult.entrySet()){
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put(entry.getKey().toString(),entry.getValue());
                excelList.add(map);
                titleList.add(entry.getKey().toString());
            }
            WriterExcelFile.generateWorkbook("F:\\files1111\\excel\\" + "0102" + ".XLS","0102","XLS",titleList,excelList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
