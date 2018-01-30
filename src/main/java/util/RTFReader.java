package util;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghao on 2017/10/2.23:19
 */
public class RTFReader {
    private static String filePath = "F:\\OCR_output\\2016苏8602民初00052号\\Z\\0087.rtf";
    private static List<Character> buffer;
    public static void main(String[] args) throws Exception {
//        readFile();
//        parseRTF();
        //printIns();

        String bodyText = null;
        DefaultStyledDocument styledDoc = new DefaultStyledDocument();    //javax.swing.text.Document的一个实例
        try {
            InputStream is = new FileInputStream(new File("F:\\OCR_output\\2016苏8602民初00052号\\Z\\0087.rtf"));
            new RTFEditorKit().read(is, styledDoc, 0);
            bodyText = new String(styledDoc.getText(0, styledDoc.getLength()).getBytes("gbk"));    //提取文本
        } catch (IOException e) {
//            throw new DocumentHandlerException("不能从RTF中摘录文本!", e);
        } catch (BadLocationException e) {
//            throw new DocumentHandlerException("不能从RTF中摘录文本!", e);
        }
        System.out.println(bodyText);
    }



    /**
     * 将内码转成16进制编码
     * @throws IOException
     */
    public static void readFile() throws IOException {
        InputStream in = new FileInputStream(new File(filePath));
        int ch;
        buffer = new ArrayList<Character>();
        while((ch=in.read()) > 0) {
            if (ch == '\\') {
                ch = in.read();
            }//这种形式的都是双字节字符
            if (ch == '\''){
                char ch1 = (char) in.read();
                char ch2 = (char) in.read();
                // ']'或'^'
                if (ch1 == '9' && (ch2 == '3' || ch2 == '4')) {

                }
                //将两个十进制的数，合并转变成16进制
                /**98--11
                 * 99--12
                 * 合成11*16+12=188
                 */
                ch = Character.digit(ch1,16)*16 + Character.digit(ch2,16);
                buffer.add((char) ch);
            }
        }
    }


    /**
     * 将16进制编码字符转成unicode编码的字符，并显示成中文字符串
     * @throws UnsupportedEncodingException
     */
    public static void parseRTF() throws UnsupportedEncodingException {
        byte[]byteArr = new byte[buffer.size()];
        for(int i = 0;i < buffer.size();i++) {
            char ch = buffer.get(i);
            if (ch < 0x80) {
                byteArr[i] = (byte)ch;
            }  else if (ch < '\u00FF') {
                char a = toHex(ch / 16);
                char b = toHex(ch % 16);
                String t = "" + a + b;
                int hb = Integer.parseInt(t, 16);
                byteArr[i] = (byte)hb;
            }
        }
        System.out.println(new String(byteArr));
    }

    public static char toHex(int ch) {
        if (ch < 10) {
            return (char) ('0' + ch);
        } else {
            return (char) ('a' + ch - 10);
        }
    }

    // 16进制字符转成中文字符
    public static String hexToStringGBK(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        try {
            s = new String(baKeyword, "GBK");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
        return s;
    }//查看RTF文件原始编码字符
    public static void printIns() throws IOException {
        InputStream in = new FileInputStream(new File(filePath));
        int ch;
        while((ch=in.read()) > 0) {
            System.out.print((char)ch);
        }
    }

}
