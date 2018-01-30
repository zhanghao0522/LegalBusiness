package dohandle;

import entity.JuanZongInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangHao on 2017/10/16.
 */
public class ContentsHandler {

    public List<JuanZongInfo> handleNew2(String strFilePath){
        BufferedReader br = null;
        StringBuilder doc1 = new StringBuilder();
        try {
            File file = new File(strFilePath);

            br = new BufferedReader( new InputStreamReader(new FileInputStream(file),"utf-8"));//构造一个BufferedReader类来读取文件
            String s = "";
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
//              doc1.append("\r"+s);
                doc1.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*下来开始进行解析工作*/
        List<JuanZongInfo> list = handle2(doc1.toString());

        return list;
    }

    public List<JuanZongInfo> handle2(String str) {

        String strNew = str.substring(2,str.lastIndexOf("$"));
        String[] arrStr =  strNew.split("§");
        List<JuanZongInfo> list = new ArrayList<JuanZongInfo>();
        for (String s : arrStr){
            String[] as = s.split("\\|");
            JuanZongInfo juanZongInfo = new JuanZongInfo();
            juanZongInfo.setStrSeq(as[0]);
            juanZongInfo.setStrName(as[1]);
            juanZongInfo.setStrThisPagination(as[2]);
            list.add(juanZongInfo);
        }

        return list;
    }

    public static void main(String[] args){
        ContentsHandler contentsHandler = new ContentsHandler();
        contentsHandler.handleNew2("F:\\OCR_output2\\0000.txt");
    }
}
