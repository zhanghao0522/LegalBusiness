package prehandle;

import com.wintone.ocr.JavaSDK;
import dohandle.*;
import entity.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import util.FileUtil;
import util.SimilarityRatioUtil;
import util.WriterExcelFile;

import java.io.*;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.*;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangHao on 2017/9/21.
 */
public class FileReaderUtil {
    //文件标题 到 文件类型的映射
    private static HashMap<String, String> mapFileTitle2Type = new HashMap<String, String>();
    //文件类型 到 处理类的映射
    private static HashMap<String, FileHandler> mapType2Handler = new HashMap<String, FileHandler>();
    //存放源路径下所有文件
    public static List<File> filelist = new ArrayList<File>();
    //源路径
    private static String inputUrl;
    //输出excel路径
    private static String outputUrl;
    //解析失败后放置路径
    private static String defeatUrl;
    //加载配置文件
    static {
        InputStream inputStream=FileReaderUtil.class.getClassLoader().getResourceAsStream("configuration.properties");
        Properties prop = new Properties();
        try {
            prop.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputUrl = prop.getProperty("inputUrl").trim();
        outputUrl = prop.getProperty("outputUrl").trim();
        defeatUrl = prop.getProperty("defeatUrl").trim();
        mapFileTitle2Type.put("南京铁路运输法院庭审笔录","tsbl");
        mapFileTitle2Type.put("南京铁路运输法院立案登记表","ladjb");
        mapFileTitle2Type.put("南京铁路运输法院民事判决书","mspjs");
        mapFileTitle2Type.put("撤诉申请书","cssqs");
        mapFileTitle2Type.put("民事起诉状","msqsz");
        mapType2Handler.put("tsbl",new TsblHandler());
        mapType2Handler.put("ladjb",new LadjbHandler());
        mapType2Handler.put("mspjs",new MspjsHandler());
        mapType2Handler.put("msqsz",new MspjsHandler());
    }

    //主方法
    public void start(){
        getAllPath(inputUrl);
        for (File file : filelist) {
            try {
                FileInputStream fis = new FileInputStream(file);
                HWPFDocument doc = new HWPFDocument(fis);
                String doc1 = doc.getDocumentText();

                /*读取文档前两行内容，赋给title*/
                String strRegex = "\\r";
                Pattern p = Pattern.compile(strRegex);
                Matcher m = p.matcher(doc1);
                int i = 0;
                while(m.find()) {
                    i++;
                    if (i==2)  break;
                }
                int index = m.start();
                String title = doc1.substring(0,index).replaceAll("\\s*", "").replaceAll("\\r","");

                /*由title与mapFileTitle2Type中的key进行匹配，得到对应的文档类型type*/
                float max = -1f;
                String key = "";
                Map<String,Object> resultMap = null;
                SimilarityRatioUtil similarityRatioUtil = new SimilarityRatioUtil();
                for (String titleExeample : mapFileTitle2Type.keySet()){
                    if (title.equals(titleExeample)){
                        key = titleExeample;
                        break;
                    } else {
                        float f = similarityRatioUtil.getSimilarityRatio(title,titleExeample);
                        if(f>max){
                            max = f;
                            key = titleExeample;
                        }
                    }
                }
                String type = mapFileTitle2Type.get(key);

                /*执行解析过程*/
                boolean isSuccessful = true;
                try{
                    resultMap = mapType2Handler.get(type).handle(doc1);
                }catch (Exception e){
                    /*捕获到异常则解析失败*/
                    isSuccessful = false;
                    System.out.println("---------------"+file.toString()+"解析失败！");
                    e.printStackTrace();
                }

                /*判断解析是否成功*/
                //resultMap中是否有超过40%的key为null，是则解析失败
                if (isSuccessful == true) {
                    if (resultMap == null) {
                        System.out.println("------------" + file.toString() + "解析失败！resultMap为null");
                        isSuccessful = false;
                    }
                    if (resultMap.size() == 0) {
                        System.out.println("------------" + file.toString() + "解析失败！resultMap.size()为0");
                        isSuccessful = false;
                    }
                }
                if (isSuccessful == true){
                    double n = 0.0;
                    double size = resultMap.keySet().size();
                    for (String key1 : resultMap.keySet()){
                        if (key1 == null)
                            n++;
                        if (n / size >=0.4 ){
                            System.out.println("----------超过40%内容未能解析，"+file.toString()+"解析失败！");
                            isSuccessful = false;
                            break;
                        }
                    }
                }

                /*解析成功和失败后续对应处理*/
                if (isSuccessful == false){
                    /*将解析失败的文件移动到default文件夹中*/
                    copyFile(file.toString(),defeatUrl + file.getName());
                    file.delete();
                    System.out.println("----------------"+file.toString()+"已移动到"+defeatUrl+"目录下");
                }else{
                    /*文件解析成功，输出到excel中*/
                    System.out.println("---------------"+file.toString()+"解析成功！");
                    List<Map<String,Object>> excelList = new ArrayList<Map<String, Object>>();
                    List<String> titleList = new ArrayList<String>();
                    for(Map.Entry entry : resultMap.entrySet()){
                        HashMap<String,Object> map = new HashMap<String, Object>();
                        map.put(entry.getKey().toString(),entry.getValue());
                        excelList.add(map);
                        titleList.add(entry.getKey().toString());
                    }

                    String fileName = file.getName();
                    WriterExcelFile.generateWorkbook(outputUrl + fileName.substring(0,fileName.lastIndexOf(".")) +".XLS",file.getName(),"XLS",titleList,excelList);
                    System.out.println("---------------"+file.toString()+"解析后数据已输出到"+outputUrl
                            + fileName.substring(0,fileName.lastIndexOf("."))+".XLS文件中");
                }

                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getContentsFilePath(String currentFilePath){

        return "";
    }

    public void handle22(){
        /*存放卷宗标题到页码的映射（由HashMap改为ArrayList存储映射关系）*/
        JuanZongInfo kv4 = new JuanZongInfo();
        kv4.setStrName("民事起诉状");
        kv4.setStrThisPagination("2");
        JuanZongInfo kv1 = new JuanZongInfo();
        kv1.setStrName("法庭审理笔录");
        kv1.setStrThisPagination("87");
        JuanZongInfo kv2 = new JuanZongInfo();
        kv2.setStrName("撤诉申请书");
        kv2.setStrThisPagination("102");
        JuanZongInfo kv3 = new JuanZongInfo();
        kv3.setStrName("谈话笔录（口头裁定）");
        kv3.setStrThisPagination("103");
        List<JuanZongInfo> listJZInfo = new ArrayList<JuanZongInfo>();
        listJZInfo.add(kv1);
        listJZInfo.add(kv2);
        listJZInfo.add(kv3);
        listJZInfo.add(kv4);

        /*遍历卷宗中读出的listNameAndPagination*/
        File file = new File("F:\\OCR_output\\2016苏8602民初00052号\\Z");
        String strThisName = "" , strThisPagination = "" , strCurrentPath = file.toString();
        for (JuanZongInfo juanZongInfo : listJZInfo){
            strThisName  = juanZongInfo.getStrName();
            strThisPagination = juanZongInfo.getStrThisPagination();

            /*对listNameAndPagination中的每条记录调用下面方法*/
            String type = findFileAndMatchFileType(strThisName,strThisPagination,strCurrentPath);
            boolean flagThis;
            if (type == null)
                flagThis = false;
            else
                flagThis = true;

            /*将每次匹配结果记录到*/
            juanZongInfo.setStrType(type);
            juanZongInfo.setIsSuccess(flagThis);
        }

        //最后一篇文书页码需要特殊处理，待做。
        /*给每篇处理成功的文书的strBeginPagination strEndPagination isSuccessBeginEnd赋值*/
        for (int i = 0; i < listJZInfo.size() - 1; i++){
            JuanZongInfo juanZongInfo = listJZInfo.get(i);
            JuanZongInfo juanZongInfoNext = listJZInfo.get(i+1);
            if (juanZongInfo.getIsSuccess() == true){
                juanZongInfo.setStrBeginPagination(juanZongInfo.getStrThisPagination());
                if (juanZongInfoNext.getIsSuccess() == true)
                    juanZongInfo.setStrEndPagination(juanZongInfoNext.getStrThisPagination());
                juanZongInfo.setIsSuccessBeginEnd(true);
            }else
                juanZongInfo.setIsSuccessBeginEnd(false);
        }

        /*读取isSuccessBeginEnd，为true的进行后续解析，并输出到excel*/
        for (JuanZongInfo juanZongInfo : listJZInfo){
            if (juanZongInfo.getIsSuccessBeginEnd() == true){
                List<String> listFilePath = new ArrayList<String>();
                int startPagination = Integer.parseInt(juanZongInfo.getStrBeginPagination()) ;
                int endPagination = Integer.parseInt(juanZongInfo.getStrEndPagination()) - 1;
                for (int pagination = startPagination;pagination <= endPagination; pagination++){
                    String strFilePath =  FileUtil.getFilePath(pagination + "",strCurrentPath);
                    listFilePath.add(strFilePath);
                }
                /*调用解析方法*/
                Map<String,Object> mapResult = null;
                try {
                    mapResult = mapType2Handler.get(juanZongInfo.getStrType()).handleNew(listFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*加入解析结果合理性分析*/
                boolean isSuccessful = checkAnalysisResult(mapResult);
                /*文件解析成功，输出到excel中*/
                if (isSuccessful == true){
                    /*通过卷宗中的页码和当前文件夹路径，获取目标文件路径*/
                    String strFilePath = FileUtil.getFilePath(juanZongInfo.getStrThisPagination(),file.toString());
                    System.out.println("---------------"+ strFilePath +"解析成功！");
                    //输出到excel中
                    outputExcel(mapResult,juanZongInfo);
                }
            }
        }
    }

    /**
     * 将解析结果输出到excel中
     * @param mapResult 解析结果
     * @param juanZongInfo 解析对应的某条目录内容
     * @author: ZhangHao
     * @date: 2017/10/10 13:54
     */
    private void outputExcel(Map<String,Object> mapResult , JuanZongInfo juanZongInfo){
        List<Map<String,Object>> excelList = new ArrayList<Map<String, Object>>();
        List<String> titleList = new ArrayList<String>();
        for(Map.Entry entry : mapResult.entrySet()){
            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put(entry.getKey().toString(),entry.getValue());
            excelList.add(map);
            titleList.add(entry.getKey().toString());
        }
        WriterExcelFile.generateWorkbook(outputUrl + juanZongInfo.getStrName() +".XLS",juanZongInfo.getStrName(),"XLS",titleList,excelList);
        System.out.println("---------------"+juanZongInfo.getStrName()+"解析后数据已输出到"+outputUrl+juanZongInfo.getStrName()+".XLS文件中");
    }

    /**
     * 判断解析结果是否合理
     *
     * @param mapResult 解析结果
     * @author: ZhangHao
     * @date: 2017/10/10 13:52
     */
    private boolean checkAnalysisResult(Map<String,Object> mapResult){
        double n = 0.0;
        double size = mapResult.keySet().size();
        boolean isSuccessful = true;
        for (String key1 : mapResult.keySet()){
            if (key1 == null)
                n++;
            if (n / size >=0.4 ){
                System.out.println("----------超过40%内容未能解析");
                isSuccessful = false;
                break;
            }
        }
        return isSuccessful;
    }


    /**
     * 根据卷宗中的每一条记录寻找对应文件路径，并判断改文件类型（解析办法）
     *
     * @author: ZhangHao
     * @date: 2017/9/30 15:20
     * @param strName 卷宗中的“文书名称”内容
     * @param strPagination  卷宗中的“页码”内容
     * @param strCurrentPath  当前文件夹路径
     */
    public String findFileAndMatchFileType(String strName , String strPagination , String strCurrentPath){
        /*通过卷宗中的页码和当前文件夹路径，获取目标文件路径*/
        String strFilePath = FileUtil.getFilePath(strPagination,strCurrentPath);
//      String strFilePath = "F:\\OCR_output\\2016苏8602民初00052号\\Z\\0087.txt";

        File file = new File(strFilePath);
        StringBuilder docSB = new StringBuilder();
        BufferedReader br = null;//构造一个BufferedReader类来读取文件
        try {
            br = new BufferedReader( new InputStreamReader(new FileInputStream(file),"Unicode"));
            String s = "";
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                docSB.append("\r"+s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String doc1 = docSB.toString();

        /*根据目标文件路径读取文件*//*
        File file = new File(strFilePath);
        FileInputStream fis = null;
        HWPFDocument doc = null;
        String doc1 = "";

        try {
            fis = new FileInputStream(file);
            doc = new HWPFDocument(fis);
            doc1 = doc.getDocumentText();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*读取文档前三行内容，赋给title*/
        String strRegex = "\\r";
        Pattern p = Pattern.compile(strRegex);
        Matcher m = p.matcher(doc1);
        int i = 0;
        while(m.find()) {
            i++;
            if (i==10)  break;
        }
        int index = m.start();
        //将文档中的标题去掉空格，去掉“\r“
        String strTitle = doc1.substring(0,index).replaceAll("\\s*", "").replaceAll("\\r","");

        /*若卷宗中的文书名称包含中文字符中的 左右括号，如“送达回证（应诉通知书等）”，截取左括号前的内容*/
        if (strName.contains("（") && strName.contains("）")){
            strName = strName.substring(0,strName.indexOf("（"));
        }

        /*判断 取出的文档前三行中，是否包含卷宗中的 文书名称*/
        if (strTitle.contains(strName)){
            /*包含，则让文书名称 与 样例名称进行算法匹配，寻找对应的文档类型*/
            /*由title与mapFileTitle2Type中的key进行匹配，得到对应的文档类型type*/
            float max = -1f;
            String key = "";
            Map<String,Object> resultMap = null;
            SimilarityRatioUtil similarityRatioUtil = new SimilarityRatioUtil();
            for (String titleExeample : mapFileTitle2Type.keySet()){
                if (strName.equals(titleExeample)){
                    key = titleExeample;
                    break;
                } else {
                    float f = similarityRatioUtil.getSimilarityRatio(strName,titleExeample);
                    if(f>max){
                        max = f;
                        key = titleExeample;
                    }
                }
            }
            String type = mapFileTitle2Type.get(key);
            System.out.println("----------------"+strFilePath+"匹配到的文档类型为"+type+"，即将进行后续解析处理");
            return type;
        }else {
            System.out.println("----------------"+strFilePath+"中的标题不包含"+strName+"，该页文档未能识别出标题，或者该页文档并非文档首页，或其它原因");
            return null;
        }

    }


    // 根据文件路径查找该路径下的所有文件并返回所有文件的路径
    public static List<File> getAllPath(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();//文件过滤。。。。。。。
            for (File file2 : files) {
                if (file2.isFile()){
                    filelist.add(file2);
                }else if (file2.isDirectory()){
                    String dirPath = file2.getPath();
                    getAllPath(dirPath);
                }
            }
        }
        return filelist;
    }


    /**
     * 复制单个文件
     * @author: ZhangHao
     * @date: 2017/9/27 14:44
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
//                  System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
//        copyFile("F:\\files1111\\error.doc","F:\\files1111\\defeat\\error.doc");
//        findFileAndMatchFileType("法庭审理笔录","87","F:\\OCR_output\\2016苏8602民初00052号\\Z");
        FileReaderUtil fileReaderUtil = new FileReaderUtil();
//        fileReaderUtil.handle23();
        fileReaderUtil.showAnalysisAndExcel();
    }

    public void showAnalysisAndExcel(){
        /*起始参数设置*/  /*可修改*/
        String strFilePathAccess = "F:\\OCR_output\\2016苏8602民初00052号\\Z";
        String strFilePathOutput = "C:\\Users\\GuoGang\\Desktop\\2016苏8602民初00052号_Z.xls";
        String strContents = "F:\\OCR_output2\\0000.txt";

        /*将页码批量转化为文书路径*/
        List<List<String>> listFilePathList = new ArrayList<List<String>>();
        String[] arrStrPagination = {"2_2","18_18","87_101","102_102"};
        FileHandler[] arrayFileHandler = {new MsqszHandler(),new SqwtsHandler(),new TsblHandler(),new CssqsHandler()};
        String[] arrStrName = {"民事起诉状","授权委托书","庭审笔录","撤诉申请书"};
        for (String str : arrStrPagination){
            List<String> list1 = new ArrayList<String>();
            String[] arr = str.split("_");
            if (arr[0].equals(arr[1]))
                list1.add(FileUtil.getFilePath(arr[0],strFilePathAccess));
            else {
                for (int begin = Integer.parseInt(arr[0]);begin <= Integer.parseInt(arr[1]); begin++ ){
                    list1.add(FileUtil.getFilePath(arr[0],strFilePathAccess));
                }
            }
            listFilePathList.add(list1);
        }

        /*解析，并输出excel*/
        OutputStream out = null;
        try {
            out = new FileOutputStream(strFilePathOutput);
            HSSFWorkbook workbook = new HSSFWorkbook();

            /*对目录处理*/
//            List<JuanZongInfo> listJZInfo = new ArrayList<JuanZongInfo>();
//            List<List<String>> dataContents = new ArrayList<List<String>>();
//            List<String> headerContents = new ArrayList<String>();
//            headerContents.add("序号");
//            headerContents.add("文书名称");
//            headerContents.add("页数");
            /*可修改*/
//            List<String> dataContents1 = new ArrayList<String>();
//            dataContents1.add("2");
//            dataContents1.add("民事起诉状");
//            dataContents1.add("2");
//            dataContents.add(dataContents1);
//            List<String> dataContents2 = new ArrayList<String>();
//            dataContents2.add("10");
//            dataContents2.add("原告身份证明");
//            dataContents2.add("13");
//            dataContents.add(dataContents2);
//            List<String> dataContents3 = new ArrayList<String>();
//            dataContents3.add("17");
//            dataContents3.add("法庭审理笔录");
//            dataContents3.add("87");
//            dataContents.add(dataContents3);
//            List<String> dataContents4 = new ArrayList<String>();
//            dataContents4.add("18");
//            dataContents4.add("撤诉申请书");
//            dataContents4.add("102");
//            dataContents.add(dataContents4);
//            /*输出excel*/
//            WriterExcelFile.exportExcel(workbook,0,"目录",headerContents,dataContents);
            ContentsHandler contentsHandler = new ContentsHandler();
            List<JuanZongInfo> listJZInfo = contentsHandler.handleNew2(strContents);
            List<List<String>> dataContents = new ArrayList<List<String>>();
            List<String> headerContents = new ArrayList<String>();
            for (JuanZongInfo juanZongInfo : listJZInfo){
                List<String> dataContents2 = new ArrayList<String>();
                dataContents2.add(juanZongInfo.getStrSeq());
                dataContents2.add(juanZongInfo.getStrName());
                dataContents2.add(juanZongInfo.getStrThisPagination());
                dataContents.add(dataContents2);
            }
            headerContents.add("序号");
            headerContents.add("文书名称");
            headerContents.add("页数");
            /*输出excel*/
            WriterExcelFile.exportExcel(workbook,0,"目录",headerContents,dataContents);



            /*对目录以外的文书处理*/
            for (int i = 0; i<arrayFileHandler.length; i++){
                /*解析*/
                List<FileElement> list = arrayFileHandler[i].handleNew2(listFilePathList.get(i));

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
                WriterExcelFile.exportExcel(workbook,i+1,arrStrName[i],header,data);
            }
            /*输出流输出*/
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }




}