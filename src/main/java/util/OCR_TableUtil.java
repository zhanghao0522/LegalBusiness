package util;
import com.wintone.JFormEx.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ZhangHao on 2017/10/13.
 */
public class OCR_TableUtil {
    //"F:\\OCR_table\\JFormEx.dll"

    static
    {
        System.load("F:\\OCR_table\\JFormEx.dll");
    }
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        OCR_TableUtil.testNew();
        //DemoDLL.testOld();
    }
    public static void testOld()
    {
        JFormEx.jStartRecogForm("<模板路径>","<库路径>");
        int ret;
        String imgPath="<图像路径>";
        char[] modeName=new char[256];
        char[] result=new char[2048];
        int[] cell={0};
        ret=JFormEx.jRecognizeForm(imgPath, modeName, result, cell);
        if(ret==0)
        {
            String strMod=new String(modeName);
            strMod=strMod.substring(0,strMod.indexOf('\0'));
            System.out.println("模板名："+strMod);

            String strRes=new String(result);
            strRes=strRes.substring(0,strRes.indexOf('\0'));
            System.out.println("识别结果："+strRes);

            System.out.print("单元个数：");
            System.out.println(cell[0]);
        }
        else
        {
            return;
        }
        JFormEx.jEndRecogForm();
    }
    public static void testNew()
    {
        int ret = JFormEx.jInitRecogForm("F:\\soft\\司法文档\\新智数通 (2)\\新智数通\\0000.mod","F:\\OCR_table");
        System.out.println("ret = "+ret);
        String strRes = "";
        if(ret==0)
        {
            String imgPath="F:\\OCR_司法文档\\司法文档\\2016苏8602民初00052号\\Z\\0000.jpg";
            char[] modeName=new char[256];
            char[] result= new char[0];
            int[] cell={0};
            ret=JFormEx.jRecognizeSingleForm(imgPath, modeName, result, cell);
            if(ret==0)
            {
                String strMod=new String(modeName);
                strMod=strMod.substring(0,strMod.indexOf('\0'));
                System.out.println("模板名："+strMod);

                int[] countOut={0};
                char[] resultOut = new char[0];
                ret = JFormEx.jGetRecognizeResult(resultOut, countOut);

                if(countOut[0]!=0)
                {
                    resultOut = new char[countOut[0]+1];
                    JFormEx.jGetRecognizeResult(resultOut, countOut);
                    resultOut[countOut[0]] = 0;
                    strRes=new String(resultOut);
                    strRes=strRes.substring(0,strRes.indexOf('\0'));
                    System.out.println("识别结果："+strRes);

                    System.out.print("单元个数：");
                    System.out.println(cell[0]);
                }
            }
            else
            {
                return;
            }

            ret=JFormEx.jUninitRecogForm();
            System.out.print(ret);
            writeResult(strRes,"0000","F:\\OCR_output2");
            //"F:\\OCR_output\\2016苏8602民初00052号\\Z"

        }
        else
            return;
    }

    public static void writeResult(String res, String name, String filePath) {
        name = filePath + File.separator + name + ".txt";
        File f = new File(name);
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(f));
            br.write(res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
