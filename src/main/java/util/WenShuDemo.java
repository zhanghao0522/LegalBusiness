package util;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangHao on 2017/10/31.
 *
 * 最后列了一个网上down的样例代码，用于生成word文档
 */
public class WenShuDemo {

    /**
     * 流程：
     * 1.确定文档输入路径
     * 2.获取文档通篇String    getStr()
     * 3.提取文档要素    getList()
     * 4.生成word   generateWordFile()
     *
     * @author: ZhangHao
     * @date: 2017/11/1 11:06
     */
    public static void main(String[] args){
        WenShuDemo demo = new WenShuDemo();
        String filepath = "F:\\一键文书生成资料 (1)\\行政庭信息公开案件文书生成材料\\2016-712笔录.doc";
        String str = demo.getStr(filepath);
        List<String> list = demo.getList(str);
//        demo.generateWord(list);
        try {
            demo.generateWordFile(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件路径读取文件，返回String
     * @author: ZhangHao
     * @date: 2017/11/1 10:53
     */
    public String getStr(String strFilePath){
        StringBuilder doc1 = new StringBuilder();
        File file = new File(strFilePath);
        FileInputStream fis = null;
        HWPFDocument doc = null;
        try {
            fis = new FileInputStream(file);
            doc = new HWPFDocument(fis);
            doc1 = doc1.append(doc.getDocumentText());
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc1.toString();
    }

    /**
     * 提取文书中的对应内容
     * @author: ZhangHao
     * @date: 2017/11/1 10:54
     */
    public List<String> getList(String str){
        List<String> list = new ArrayList<String>();
        String str1 = "书记员：原告，你的姓名，出生年月、居民身份证号码、户籍地等自然情况？";
        String str2 = "（编号2）";
        String content1 = getContent(str,str1,str2);
        content1 = content1.substring(1);
        content1 = content1.replace("\r","\r    ");
//        content1 = content1.replaceAll("\\s*", "").replaceAll("\\r","");
//        content1 = "    " + content1;
        list.add(content1);

        String str11 = "审：被告，请陈述你的单位名称、单位住所地、定代表人姓名及职务？委托代理人的委托权限。";
        String str22 = "（编号3）";
        String content2 = getContent(str,str11,str22);
        content2 = content2.substring(1);
        content2 = content2.replace("\r","\r    ");
//        content2 = content2.replaceAll("\\s*", "").replaceAll("\\r","");
//        content2 = "    " + content2;
        list.add(content2);

        String str111 = "3、必须遵守诉讼秩序。";
        String str222 = "（编号4）";
        String content3 = getContent(str,str111,str222);
        content3 = content3.replaceAll("\\s*", "").replaceAll("\\r","");
        content3 = content3.substring(2);
//        content3 = "    " + content3;
        list.add(content3);

        String strDateBef = "开庭时间：";
        String strDateAft = "（编号5）";
        String dateContent = getContent(str,strDateBef,strDateAft);
        dateContent = dateContent.replaceAll("\\s*", "").replaceAll("\\r","");
//        content3 = "    " + content3;
        list.add(dateContent);

        String str5_1Bef = "原告艾某及其委托代理人是否到庭？";
        String str5_1Aft = "（编号6）";
        String str5_1 = getContent(str,str5_1Bef,str5_1Aft);
        str5_1 = str5_1.replaceAll("\\s*", "").replaceAll("\\r","");
        str5_1 = str5_1.substring(3);
//        content3 = "    " + content3;
        String str5_2Bef = "被告南京市住房保障和房产局的出庭负责人及委托代理人是否到庭？";
        String str5_2Aft = "（编号7）";
        String str5_2 = getContent(str,str5_2Bef,str5_2Aft);
        str5_2 = str5_2.replaceAll("\\s*", "").replaceAll("\\r","");
        str5_2 = str5_2.substring(3);
//        content3 = "    " + content3;
        list.add(str5_1 + "  " + str5_2);

        return list;
    }

    /**
     *
     *
     * 根据段前、段后的固定语句，截取出需要的内容
     * @author: ZhangHao
     * @date: 2017/11/1 10:55
     *
     * @param str:整篇文书内容
     * @param strStart:段前的固定内容
     * @param strEnd:段后的固定内容
     *
     * @return 需要提取出的内容(String)
     */
    public String getContent(String str, String strStart, String strEnd){
        int index1 = str.indexOf(strStart);
        int index2 = str.indexOf(strEnd);
        int len = strStart.length();
        String content1 = str.substring(index1 + len, index2);
        return content1;
    }


    /**
     *
     * 模板方式实现word输出
     * 输入、输出均为.doc格式
     * 使用HWPFDocument
     *
     * @author: ZhangHao
     * @date: 2017/11/1 19:55
     */
    public void generateWordFile(List<String> list) throws Exception {
        String templatePath = "C:\\Users\\GuoGang\\Desktop\\1.doc";
        InputStream is = new FileInputStream(templatePath);
        HWPFDocument doc = new HWPFDocument(is);
        Range range = doc.getRange();
        //把range范围内的${reportDate}替换为当前的日期
        range.replaceText("${str1}", list.get(0));
        range.replaceText("${str2}", list.get(1));
        range.replaceText("${str3}", list.get(2));
        range.replaceText("${date}", list.get(3));
        range.replaceText("${str5}", list.get(4));
        OutputStream os = new FileOutputStream("D:\\"+System.currentTimeMillis()+".doc");
        //把doc输出到输出流中
        doc.write(os);
        this.closeStream(os);
        this.closeStream(is);
    }

    /**
     * 关闭输入流
     * @param is
     */
    private void closeStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     * @param os
     */
    private void closeStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *
     * 根据提取出来的要素list，调整其中的字体样式、大小，输出到一个word文档中
     *
     * @author: ZhangHao
     * @date: 2017/11/1 10:58
     *
     * @param list: 提取出的要素list
     *
     */
    public void generateWord(List<String> list){
        XWPFDocument document= new XWPFDocument();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(System.currentTimeMillis() + ".docx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XWPFParagraph titleParagraph = document.createParagraph();
        setParagraphFontInfo(titleParagraph,list.get(0),"仿宋","16");

        XWPFParagraph firstParagraph = document.createParagraph();
        setParagraphFontInfo(firstParagraph,list.get(1),"仿宋","16");

        XWPFParagraph firstParagraph1 = document.createParagraph();
        setParagraphFontInfo(firstParagraph1,list.get(2),"仿宋","16");

        try {
            document.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("create_table document written success.");
    }


    /**
     *
     * 设置段落的内容、字体样式（仿宋）、字体大小
     *
     * @author: ZhangHao
     * @date: 2017/11/1 11:01
     *
     * @param p: 段落对象
     * @param content: 段落内容
     * @param fontFamily: 字体样式
     * @param fontSize: 字体大小
     */
    public void setParagraphFontInfo(XWPFParagraph p, String content, String fontFamily, String fontSize) {
        XWPFRun pRun = null;
        /*对于同一个XWPFParagraph，
          如果已经有了pRun，在后面追加内容；
          如果没有，则新创建一个pRun。
          传入不同的XWPFParagraph,
          对应不同的段落*/
        if (p.getRuns() != null && p.getRuns().size() > 0) {
            pRun = p.getRuns().get(0);
        } else {
            pRun = p.createRun();
        }
        //设置段落内容
        pRun.setText(content);

        CTRPr pRpr = null;
        if (pRun.getCTR() != null) {
            pRpr = pRun.getCTR().getRPr();
            if (pRpr == null) {
                pRpr = pRun.getCTR().addNewRPr();
            }
        }

        // 设置字体
        CTFonts fonts = pRpr.isSetRFonts() ? pRpr.getRFonts() : pRpr.addNewRFonts();
        fonts.setAscii(fontFamily);
        fonts.setEastAsia(fontFamily);
        fonts.setHAnsi(fontFamily);

        // 设置字体大小
        BigInteger bint = new BigInteger("" + fontSize);
        CTHpsMeasure ctSize = pRpr.isSetSz()?pRpr.getSz():pRpr.addNewSz();
        ctSize.setVal(bint.multiply(new BigInteger("2"))); //输入的字体数字 乘以 2 .
    }


    /**
     *
     * 网上down的代码，供参考
     *
     * @author: ZhangHao
     * @date: 2017/11/1 11:05
     */
    public void generateWordExample(){
        //Blank Document
        XWPFDocument document= new XWPFDocument();
        //Write the Document in file system
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(System.currentTimeMillis() + ".docx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        //设置段落居中
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText("Java PoI");
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(20);

        //段落
        XWPFParagraph firstParagraph = document.createParagraph();
        XWPFRun run = firstParagraph.createRun();
        run.setText("Java POI 生成word文件。");
        run.setColor("696969");
        run.setFontSize(16);

        //设置段落背景颜色
        CTShd cTShd = run.getCTR().addNewRPr().addNewShd();
        cTShd.setVal(STShd.CLEAR);
        cTShd.setFill("97FFFF");

        //换行
        XWPFParagraph paragraph1 = document.createParagraph();
        XWPFRun paragraphRun1 = paragraph1.createRun();
        paragraphRun1.setText("\r");

        //基本信息表格
        XWPFTable infoTable = document.createTable();
        //去表格边框
        infoTable.getCTTbl().getTblPr().unsetTblBorders();

        //列宽自动分割
        CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
        infoTableWidth.setType(STTblWidth.DXA);
        infoTableWidth.setW(BigInteger.valueOf(9072));

        //表格第一行
        XWPFTableRow infoTableRowOne = infoTable.getRow(0);
        infoTableRowOne.getCell(0).setText("职位");
        infoTableRowOne.addNewTableCell().setText(": Java 开发工程师");

        //表格第二行
        XWPFTableRow infoTableRowTwo = infoTable.createRow();
        infoTableRowTwo.getCell(0).setText("姓名");
        infoTableRowTwo.getCell(1).setText(": seawater");

        //表格第三行
        XWPFTableRow infoTableRowThree = infoTable.createRow();
        infoTableRowThree.getCell(0).setText("生日");
        infoTableRowThree.getCell(1).setText(": xxx-xx-xx");

        //表格第四行
        XWPFTableRow infoTableRowFour = infoTable.createRow();
        infoTableRowFour.getCell(0).setText("性别");
        infoTableRowFour.getCell(1).setText(": 男");

        //表格第五行
        XWPFTableRow infoTableRowFive = infoTable.createRow();
        infoTableRowFive.getCell(0).setText("现居地");
        infoTableRowFive.getCell(1).setText(": xx");


        //两个表格之间加个换行
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText("\r");

        //工作经历表格
        XWPFTable ComTable = document.createTable();

        //列宽自动分割
        CTTblWidth comTableWidth = ComTable.getCTTbl().addNewTblPr().addNewTblW();
        comTableWidth.setType(STTblWidth.DXA);
        comTableWidth.setW(BigInteger.valueOf(9072));

        //表格第一行
        XWPFTableRow comTableRowOne = ComTable.getRow(0);
        comTableRowOne.getCell(0).setText("开始时间");
        comTableRowOne.addNewTableCell().setText("结束时间");
        comTableRowOne.addNewTableCell().setText("公司名称");
        comTableRowOne.addNewTableCell().setText("title");

        //表格第二行
        XWPFTableRow comTableRowTwo = ComTable.createRow();
        comTableRowTwo.getCell(0).setText("2016-09-06");
        comTableRowTwo.getCell(1).setText("至今");
        comTableRowTwo.getCell(2).setText("seawater");
        comTableRowTwo.getCell(3).setText("Java开发工程师");

        //表格第三行
        XWPFTableRow comTableRowThree = ComTable.createRow();
        comTableRowThree.getCell(0).setText("2016-09-06");
        comTableRowThree.getCell(1).setText("至今");
        comTableRowThree.getCell(2).setText("seawater");
        comTableRowThree.getCell(3).setText("Java开发工程师");


        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy policy = null;
        try {
            policy = new XWPFHeaderFooterPolicy(document, sectPr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlException e) {
            e.printStackTrace();
        }

        //添加页眉
        CTP ctpHeader = CTP.Factory.newInstance();
        CTR ctrHeader = ctpHeader.addNewR();
        CTText ctHeader = ctrHeader.addNewT();
        String headerText = "Java POI create MS word file.";
        ctHeader.setStringValue(headerText);
        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
        //设置为右对齐
        headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFParagraph[] parsHeader = new XWPFParagraph[1];
        parsHeader[0] = headerParagraph;
        try {
            policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //添加页脚
        CTP ctpFooter = CTP.Factory.newInstance();
        CTR ctrFooter = ctpFooter.addNewR();
        CTText ctFooter = ctrFooter.addNewT();
        String footerText = "http://blog.csdn.net/zhouseawater";
        ctFooter.setStringValue(footerText);
        XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFParagraph[] parsFooter = new XWPFParagraph[1];
        parsFooter[0] = footerParagraph;
        try {
            policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            document.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("create_table document written success.");
    }
    
}
