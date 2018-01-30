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
 * Created by Li Xiaoqiang on 2017/10/13.
 */
public class GzsHandler implements FileHandler {

//    保存公证书中的各标签名称
    private static List<String> listElement= new ArrayList<String>();
    static{
        listElement.add("申请人");
        listElement.add("法定代表人");
        listElement.add("地址");
        listElement.add("代理人");
        listElement.add("公民身份号码");
        listElement.add("公证事项");
    }

//    实现接口中的handle方法
    public Map<String, Object> handle(String str) throws Exception {
        String regex = "[\u4e00-\u9fa5]+：";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        List<FileElement> listFileElement = new ArrayList<FileElement>();
        while(matcher.find()) {
            String strName = matcher.group().substring(0,matcher.group().indexOf("："));
            if (listElement.contains(strName)){
                FileElement fileElement = new FileElement();
                fileElement.setStrName(strName);
                fileElement.setStrStartIndex(matcher.start());
                fileElement.setStrEndIndex(matcher.end());
                listFileElement.add(fileElement);
            }
        }

        FileElement fileElementZMNR = new FileElement();
        FileElement fileElementGZSX = new FileElement();
        for (int i = 0;i < listFileElement.size()-1; i++){
            int start = listFileElement.get(i).getStrEndIndex();
            int end = listFileElement.get(i+1).getStrStartIndex();
            String content = str.substring(start,end-1);
                /*特殊处理，将公证书的证明内容提取出来*/
            if (listFileElement.get(i+1).getStrName().equals("公证事项")){
                start = str.indexOf("公证事项");
                String strContent = str.substring(start);
                String gzsxContent = strContent.substring(0, strContent.indexOf("\r"));
                String zmnrContent = strContent.substring(strContent.indexOf("\r"), strContent.lastIndexOf("中华人民共和国"));
                zmnrContent = handleStr(zmnrContent);
                gzsxContent = handleStr(gzsxContent);
                fileElementGZSX.setStrName("公证事项");
                fileElementGZSX.setStrContent(gzsxContent);
                fileElementZMNR.setStrName("证明内容");
                fileElementZMNR.setStrContent(zmnrContent);
            }
            content = handleStr(content);
            listFileElement.get(i).setStrContent(content);
        }

//        FileElement fileElementLast = listFileElement.get(listFileElement.size()-1);
//        int endLast = fileElementLast.getStrEndIndex();
//        fileElementLast.setStrContent(handleStr(str.substring(endLast)));
        /*标题直接按“公证书”处理*/
        FileElement fileElementTitle = new FileElement();
        fileElementTitle.setStrName("标题");
        fileElementTitle.setStrContent("公证书");
        listFileElement.add(fileElementTitle);
        /*对版本号进行处理*/
        FileElement fileElementVersion = new FileElement();
        String versionContent = str.substring(str.indexOf("公证书"));
        versionContent = versionContent.substring(versionContent.indexOf("\r"), versionContent.indexOf("申请人"));
        versionContent = handleStr(versionContent);
        fileElementVersion.setStrName("版本号");
        fileElementVersion.setStrContent(versionContent);
        listFileElement.add(fileElementVersion);
        /*添加“公证事项”元素*/
        listFileElement.add(fileElementGZSX);
        /*添加“证明内容”元素*/
        listFileElement.add(fileElementZMNR);
        /*对公证处单位进行处理*/
        FileElement fileElementGZC = new FileElement();
        String gzcContent = str.substring(str.lastIndexOf("中华人民共和国"));
        int end = gzcContent.indexOf("\r");
        gzcContent = gzcContent.substring(0, end - 1);
        gzcContent = handleStr(gzcContent);
        fileElementGZC.setStrName("公证处");
        fileElementGZC.setStrContent(gzcContent);
        listFileElement.add(fileElementGZC);

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
        /*连续很多页的文档内容已经全部拼接起来到了doc1中，下来开始进行解析工作*/
        Map<String,Object> mapResult = handle(doc1.toString());

        return mapResult;

    }

    public List<FileElement> handle2(String str) throws Exception {
        return null;
    }

    public List<FileElement> handleNew2(List<String> listFilePath) throws Exception {
        return null;
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
        /*GzsHandler m = new GzsHandler();
        List<String> listFilePath = new ArrayList<String>();
//        listFilePath.add("F:\\OCR_output\\2016苏8602民初00052号\\Z\\0018.txt");
        listFilePath.add("D:\\input_txt\\0038.txt");
        try {
            Map<String,Object> mapResult = m.handleNew(listFilePath);

            List<Map<String,Object>> excelList = new ArrayList<Map<String, Object>>();
            List<String> titleList = new ArrayList<String>();
            for(Map.Entry entry : mapResult.entrySet()){
                if (entry.getKey() != null && entry.getValue() != null) {
                    HashMap<String,Object> map = new HashMap<String, Object>();
                    map.put(entry.getKey().toString(), entry.getValue());
                    excelList.add(map);
                    titleList.add(entry.getKey().toString());
                }
            }
//            WriterExcelFile.generateWorkbook("F:\\files1111\\excel\\" + "0018" + ".XLS","0018","XLS",titleList,excelList);
            WriterExcelFile.generateWorkbook("D:\\output_excel\\" + "0038" + ".XLS","0038","XLS",titleList,excelList);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        GzsHandler tsblHandler = new GzsHandler();
//      List<String> listFilePath = new ArrayList<String>();

        String strFilePathOutput = "C:\\Users\\GuoGang\\Desktop\\tsbl.xls";
        List<FileElement> list = null;
        OutputStream out = null;
        try {
            List<String> listFilePath = new ArrayList<String>();
            String[] arr = "49_49".split("_");
            if (arr[0].equals(arr[1]))
                listFilePath.add(FileUtil.getFilePath(arr[0],"F:\\OCR_output2"));
            else {
                for (int begin = Integer.parseInt(arr[0]);begin <= Integer.parseInt(arr[1]); begin++ ){
                    listFilePath.add(FileUtil.getFilePath(arr[0],"F:\\OCR_output2"));
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
            WriterExcelFile.exportExcel(workbook,0,"公证书",header,data);

            /*输出流输出*/
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
