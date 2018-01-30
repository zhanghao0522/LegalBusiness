package dohandle;

import entity.FileElement;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import util.FileUtil;
import util.WriterExcelFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangHao on 2017/10/12.
 */
public class SqwtsHandler implements FileHandler{

    //保存授权委托书中的名称
    private static List<String> listElement = new ArrayList<String>();
    static {
        listElement.add("委托方");
        listElement.add("住所地");
        listElement.add("法定代表人");
        listElement.add("职务");
        listElement.add("受托方");
        listElement.add("工作单位");
        listElement.add("地址");
        listElement.add("邮编");
        listElement.add("电话");
        listElement.add("受托方的代理权限包括");
        listElement.add("委托综述");
    }

    public Map<String, Object> handle(String str) throws Exception {
        String regexSecond = "[\u4e00-\u9fa5]+：";
        Pattern p2 = Pattern.compile(regexSecond);
        Matcher m2 = p2.matcher(str);
        List<FileElement> listFileElement = new ArrayList<FileElement>();
        while(m2.find()) {
            String strName = m2.group().substring(0,m2.group().indexOf("："));
            if (listElement.contains(strName)){
                FileElement fileElement = new FileElement();
                fileElement.setStrName(strName);
                fileElement.setStrStartIndex(m2.start());
                fileElement.setStrEndIndex(m2.end());
                listFileElement.add(fileElement);
            }
        }
        FileElement fileElementWTZS = new FileElement();
        for (int i = 0;i < listFileElement.size()-1; i++){
            int start = listFileElement.get(i).getStrEndIndex();
            int end = listFileElement.get(i+1).getStrStartIndex();
            String content = str.substring(start,end);
            if (listFileElement.get(i).getStrName().equals("电话")){
                /*特殊处理，将委托综述的内容提取出来*/
                String wtzsContent = content.substring(content.indexOf("\r"));
                wtzsContent = handleStr(wtzsContent);
                fileElementWTZS.setStrName("委托综述");
                fileElementWTZS.setStrContent(wtzsContent);
                content = content.substring(0,content.indexOf("\r"));
            }
            content = handleStr(content);
            listFileElement.get(i).setStrContent(content);
        }
        /*将文书末尾署名部分去掉*/
//        FileElement fileElementLast = listFileElement.get(listFileElement.size()-1);
//        int endLast = fileElementLast.getStrEndIndex();
//        fileElementLast.setStrContent(handleStr(str.substring(endLast)));
        listFileElement.remove(listFileElement.size()-1);
        /*标题直接按“授权委托书”处理*/
        FileElement fileElementTitle = new FileElement();
        fileElementTitle.setStrName("标题");
        fileElementTitle.setStrContent("授权委托书");
        listFileElement.add(fileElementTitle);
        /*添加“委托综述”元素，顺序放到了最后*/
        listFileElement.add(fileElementWTZS);

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
        String regexSecond = "[\u4e00-\u9fa5]+：";
        Pattern p2 = Pattern.compile(regexSecond);
        Matcher m2 = p2.matcher(str);
        List<FileElement> listFileElement = new ArrayList<FileElement>();
        while(m2.find()) {
            String strName = m2.group().substring(0,m2.group().indexOf("："));
            if (listElement.contains(strName)){
                FileElement fileElement = new FileElement();
                fileElement.setStrName(strName);
                fileElement.setStrStartIndex(m2.start());
                fileElement.setStrEndIndex(m2.end());
                listFileElement.add(fileElement);
            }
        }
        FileElement fileElementWTZS = new FileElement();
        for (int i = 0;i < listFileElement.size()-1; i++){
            int start = listFileElement.get(i).getStrEndIndex();
            int end = listFileElement.get(i+1).getStrStartIndex();
            String content = str.substring(start,end);
            if (listFileElement.get(i).getStrName().equals("电话")){
                /*特殊处理，将委托综述的内容提取出来*/
                String wtzsContent = content.substring(content.indexOf("\r"));
                wtzsContent = handleStr(wtzsContent);
                fileElementWTZS.setStrName("委托综述");
                fileElementWTZS.setStrContent(wtzsContent);
                content = content.substring(0,content.indexOf("\r"));
            }
            content = handleStr(content);
            listFileElement.get(i).setStrContent(content);
        }
        /*将文书末尾署名部分去掉*/
//        FileElement fileElementLast = listFileElement.get(listFileElement.size()-1);
//        int endLast = fileElementLast.getStrEndIndex();
//        fileElementLast.setStrContent(handleStr(str.substring(endLast)));
        listFileElement.remove(listFileElement.size()-1);
        /*标题直接按“授权委托书”处理*/
        FileElement fileElementTitle = new FileElement();
        fileElementTitle.setStrName("标题");
        fileElementTitle.setStrContent("授权委托书");
        listFileElement.add(fileElementTitle);
        /*添加“委托综述”元素，顺序放到了最后*/
        listFileElement.add(fileElementWTZS);

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
        /*SqwtsHandler m = new SqwtsHandler();
        List<String> listFilePath = new ArrayList<String>();
        listFilePath.add("F:\\OCR_output\\2016苏8602民初00052号\\Z\\0018.txt");
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
            WriterExcelFile.generateWorkbook("F:\\files1111\\excel\\" + "0018" + ".XLS","0018","XLS",titleList,excelList);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        SqwtsHandler tsblHandler = new SqwtsHandler();
//      List<String> listFilePath = new ArrayList<String>();

        String strFilePathOutput = "C:\\Users\\GuoGang\\Desktop\\sqwts.xls";
        List<FileElement> list = null;
        OutputStream out = null;
        try {
            List<String> listFilePath = new ArrayList<String>();
            String[] arr = "91_93".split("_");
            if (arr[0].equals(arr[1]))
                listFilePath.add(FileUtil.getFilePath(arr[0],"F:\\soft\\司法文档\\2016苏8602民初00370号\\Z1"));
            else {
                for (int begin = Integer.parseInt(arr[0]);begin <= Integer.parseInt(arr[1]); begin++ ){
                    listFilePath.add(FileUtil.getFilePath(arr[0],"F:\\soft\\司法文档\\2016苏8602民初00370号\\Z1"));
                }
            }
//            listFilePath.add(list1);
            out = new FileOutputStream(strFilePathOutput);
            HSSFWorkbook workbook = new HSSFWorkbook();
            list = tsblHandler.handleNew2(listFilePath);
             /*将解析结果转化为输出excel所需参数格式*/
            List<List<String>> data = new ArrayList<List<String>>();
            List<String> data2 = new ArrayList<String>();
            List<String> header = new ArrayList<String>();
            for (FileElement fileElement : list){
                header.add(fileElement.getStrName());
                data2.add(fileElement.getStrContent());
            }
            data.add(data2);

                /*输出excel*/
            WriterExcelFile.exportExcel(workbook,0,"庭审笔录",header,data);

            /*输出流输出*/
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
