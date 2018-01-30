package dohandle;

import entity.FileElement;
import entity.KeyValue;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import util.FileUtil;
import util.WriterExcelFile;

/**
 * 处理庭审笔录
 * Created by ZhangHao on 2017/9/22.
 */
public class TsblHandler implements FileHandler{
    //保存庭审中发言人的身份
    private static List<String> listShenFen = new ArrayList<String>();
    static {
        listShenFen.add("书记员");
        listShenFen.add("书");
        listShenFen.add("原代");
        listShenFen.add("被代");
        listShenFen.add("审");
        listShenFen.add("委托代理人");
        listShenFen.add("审判长");
        listShenFen.add("均");
        listShenFen.add("原");
    }


    public static void main(String[] args){
        TsblHandler tsblHandler = new TsblHandler();
//      List<String> listFilePath = new ArrayList<String>();

        String strFilePathOutput = "C:\\Users\\GuoGang\\Desktop\\tsbl1.xls";
        List<FileElement> list = null;
        OutputStream out = null;
        try {
            List<String> listFilePath = new ArrayList<String>();
            String[] arr = "123_130".split("_");
            if (arr[0].equals(arr[1]))
                listFilePath.add(FileUtil.getFilePath(arr[0],"F:\\OCR_output\\2016苏8602民初00370号_1\\Z"));
            else {
                for (int begin = Integer.parseInt(arr[0]);begin <= Integer.parseInt(arr[1]); begin++ ){
                    listFilePath.add(FileUtil.getFilePath(arr[0],"F:\\司法文档\\tsbl\\2016苏8602民初00974号_output"));
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

    public Map<String,Object> handleNew(List<String> listFilePath) throws Exception{
        StringBuilder doc1 = new StringBuilder();
        /*循环读取文件路径，读取其中内容输出到变量doc1中*/
        for(String strFilePath : listFilePath){
            File file = new File(strFilePath);
            /*FileInputStream fis = null;
            HWPFDocument doc = null;
            try {
                fis = new FileInputStream(file);
                doc = new HWPFDocument(fis);
                doc1 = doc1.append(doc.getDocumentText());
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file),"Unicode"));//构造一个BufferedReader类来读取文件
            String s = "";
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                doc1.append("\r"+s);
            }
            br.close();
        }
        /*连续很多页的文档内容已经全部拼接起来到了doc1中，下来开始进行解析工作*/
        Map<String,Object> mapResult = handle(doc1.toString());

        return mapResult;
    }

    public List<FileElement> handle2(String str) throws Exception {
        List<FileElement> list = new ArrayList<FileElement>();
        Map<String,Object> resultMap = new HashMap<String , Object>();

        /*匹配标题和版本描述*/
        String regexFirst = "\\r";
        Pattern p1 = Pattern.compile(regexFirst);
        Matcher m1 = p1.matcher(str);
        int end1 = 0,startText = 0;
        String strVersion = "",title = "";
        while(m1.find()) {
            int start1 = m1.start();
            String substring = str.substring(end1,start1);
            if (substring.endsWith("号")){
                strVersion = substring.substring(1).replaceAll("\\s*","");
                title = str.substring(0,end1).replaceAll("\\s*","").replaceAll("\\r","");
            }
            if (substring.contains("：")){
                startText = end1;
                break;
            }
            end1 = start1;
        }
//        resultMap.put("题目",title);
//        resultMap.put("版本号",strVersion);

        /*匹配剩余正文*/
        String regexSecond = "[\u4e00-\u9fa5]+：";
        Pattern p2 = Pattern.compile(regexSecond);
        String text = str.substring(startText);
        Matcher m2 = p2.matcher(text);
        String keyLast = "";
        int end2 = 0, k = 0, startDialog =0;
        List<KeyValue> dialogList = new ArrayList<KeyValue>();
        while(m2.find()) {
            boolean flag = true;
            int start2 = m2.start();
            String keyThis = m2.group();
            keyThis = keyThis.substring(0,keyThis.indexOf("："));
            if (keyThis.equals("书记员") || keyThis.equals("书"))
                k++;
            if (k > 2){
                if (!listShenFen.contains(keyThis)){
                    flag = false;
                }else{
                    start2 = m2.start();
                    dialogList.get(dialogList.size()-1).setValue(text.substring(end2,start2).trim().replaceAll("\\s*", "").replaceAll("\\r",""));
                    KeyValue kv = new KeyValue();
                    kv.setKey(keyThis);
                    dialogList.add(kv);
                }
            }
//            if (keyThis.equals("原告签名处")){
//                end2 = 0; k = 0;
//            }
            if (k <= 2){
                if (end2 > 0)
                    resultMap.put(keyLast,text.substring(end2,start2).trim().replaceAll("\\s*", "").replaceAll("\\r",""));
                if (k < 2)
                    resultMap.put(keyThis,null);
            }
            if (k == 2){
                KeyValue kv = new KeyValue();
                kv.setKey(keyThis);
                dialogList.add(kv);
                k++;
            }
            if (flag == true){
                end2 = m2.end();
                keyLast = keyThis;
            }
        }
        dialogList.remove(dialogList.size()-1);
        String strDialog = JSON.toJSONString(dialogList);
        resultMap.put("庭审发言",strDialog);

        for (Map.Entry entry : resultMap.entrySet()){
            FileElement fileElement = new FileElement();
            fileElement.setStrName(entry.getKey().toString());
            fileElement.setStrContent(entry.getValue().toString());
            list.add(fileElement);
        }

        return list;
    }

    public List<FileElement> handleNew2(List<String> listFilePath) throws Exception {
        StringBuilder doc1 = new StringBuilder();
        /*循环读取文件路径，读取其中内容输出到变量doc1中*/
        for(String strFilePath : listFilePath){
            File file = new File(strFilePath);
            /*FileInputStream fis = null;
            HWPFDocument doc = null;
            try {
                fis = new FileInputStream(file);
                doc = new HWPFDocument(fis);
                doc1 = doc1.append(doc.getDocumentText());
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file),"Unicode"));//构造一个BufferedReader类来读取文件
            String s = "";
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                doc1.append("\r"+s);
            }
            br.close();
        }
        /*连续很多页的文档内容已经全部拼接起来到了doc1中，下来开始进行解析工作*/
        List<FileElement> list = handle2(doc1.toString());

        return list;
    }

    public Map<String,Object> handle(String str) throws Exception{
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String,Object> resultMap = new HashMap<String , Object>();

        /*匹配标题和版本描述*/
        String regexFirst = "\\r";
        Pattern p1 = Pattern.compile(regexFirst);
        Matcher m1 = p1.matcher(str);
        int end1 = 0,startText = 0;
        String strVersion = "",title = "";
        while(m1.find()) {
            int start1 = m1.start();
            String substring = str.substring(end1,start1);
            if (substring.endsWith("号")){
                strVersion = substring.substring(1).replaceAll("\\s*","");
                title = str.substring(0,end1).replaceAll("\\s*","").replaceAll("\\r","");
            }
            if (substring.contains("：")){
                startText = end1;
                break;
            }
            end1 = start1;
        }
//        resultMap.put("题目",title);
//        resultMap.put("版本号",strVersion);

        /*匹配剩余正文*/
        String regexSecond = "[\u4e00-\u9fa5]+：";
        Pattern p2 = Pattern.compile(regexSecond);
        String text = str.substring(startText);
        Matcher m2 = p2.matcher(text);
        String keyLast = "";
        int end2 = 0, k = 0, startDialog =0;
        List<KeyValue> dialogList = new ArrayList<KeyValue>();
        while(m2.find()) {
            boolean flag = true;
            int start2 = m2.start();
            String keyThis = m2.group();
            keyThis = keyThis.substring(0,keyThis.indexOf("："));
            if (keyThis.equals("书记员") || keyThis.equals("书"))
                k++;
            if (k > 2){
                if (!listShenFen.contains(keyThis)){
                    flag = false;
                }else{
                    start2 = m2.start();
                    dialogList.get(dialogList.size()-1).setValue(text.substring(end2,start2).trim().replaceAll("\\s*", "").replaceAll("\\r",""));
                    KeyValue kv = new KeyValue();
                    kv.setKey(keyThis);
                    dialogList.add(kv);
                }
            }
//            if (keyThis.equals("原告签名处")){
//                end2 = 0; k = 0;
//            }
            if (k <= 2){
                if (end2 > 0)
                    resultMap.put(keyLast,text.substring(end2,start2).trim().replaceAll("\\s*", "").replaceAll("\\r",""));
                if (k < 2)
                    resultMap.put(keyThis,null);
            }
            if (k == 2){
                KeyValue kv = new KeyValue();
                kv.setKey(keyThis);
                dialogList.add(kv);
                k++;
            }
            if (flag == true){
                end2 = m2.end();
                keyLast = keyThis;
            }
        }
        dialogList.remove(dialogList.size()-1);
        String strDialog = JSON.toJSONString(dialogList);
        resultMap.put("庭审发言",strDialog);

        return resultMap;
    }

}
