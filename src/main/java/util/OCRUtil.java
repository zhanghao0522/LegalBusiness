package util;

import com.wintone.ocr.JavaSDK;
import prehandle.FileReaderUtil;

import java.io.File;

/**
 * Created by ZhangHao on 2017/9/29.
 */
public class OCRUtil {

    public static void main(String[] args){
        String filePathInpt = "F:\\soft\\司法文档\\2016苏8602民初00461号\\Z1\\0049.jpg";
        String filePathOutput = "F:\\OCR_output2";
        convertSingleFile( filePathInpt,filePathOutput);

//      String filePathInpt = "F:\\司法文档\\tsbl\\2016苏8602民初00974号";
//      String filePathOutput = "F:\\OCR_output\\2016苏8602民初00052号_1\\Z";
//      convertAmountFiles( filePathInpt, filePathOutput);

    }

    private static void convertAmountFiles(String filePathInpt , String filePathOutput){
        FileReaderUtil.filelist = FileReaderUtil.getAllPath(filePathInpt);   //"F:\\soft\\司法文档\\2016苏8602民初00052号\\Z"

        JavaSDK javaSDK = new JavaSDK();
        for (File file : FileReaderUtil.filelist){

            int iTH_StartExW = javaSDK.TH_StartExW("F:\\OCR\\TH_OCR32_DLL.dll");
            System.out.println("TH_StartExW:" + iTH_StartExW);
            if (iTH_StartExW != 0) return;

            javaSDK.useCoInitialize();
            int iTH_LoadImageW = javaSDK.TH_LoadImageW(file.toString(), 0);
            System.out.println("TH_LoadImageW:" + iTH_LoadImageW);
            if(iTH_LoadImageW != 0) return;

            int iTH_SetLanguage = javaSDK.TH_SetLanguage(0);
            System.out.println("TH_SetLanguage:" + iTH_SetLanguage );
            if(iTH_SetLanguage != 0) return;

            int iTH_Recognize = javaSDK.TH_Recognize();
            System.out.println("TH_Recognize:" + iTH_Recognize);
            if (iTH_Recognize != 0) return;

            System.out.println("TH_OutputBeginW:" + javaSDK.TH_OutputBeginW(filePathOutput+File.separator
                    +file.getName().substring(0,file.getName().lastIndexOf("."))+".txt", 0, 0));
//          System.out.println("TH_OutputBeginW:" + javaSDK.TH_OutputBeginW(filePathOutput+File.separator
//                  +file.getName().substring(0,file.getName().lastIndexOf("."))+".pdf", 1, 0));
//          System.out.println("TH_OutputBeginW:" + javaSDK.TH_OutputBeginW(filePathOutput+File.separator
//                  +file.getName().substring(0,file.getName().lastIndexOf("."))+".rtf", 2, 0|8|16));
//          System.out.println("TH_OutputBeginW:" + javaSDK.TH_OutputBeginW(filePathOutput+File.separator
//                  +file.getName().substring(0,file.getName().lastIndexOf("."))+".xls", 3, 0));
            System.out.println("TH_OutPutFile:" + javaSDK.TH_OutPutFile());
//          System.out.println("TH_SaveOCRFileW:" + javaSDK.TH_SaveOCRFileW("F:\\OCR_output\\TH_SaveOCRFileW7.ocr"));
            System.out.println("TH_OutputEnd:" + javaSDK.TH_OutputEnd());
            System.out.println("TH_End:" + javaSDK.TH_End());
            javaSDK.useCoUninitialize();
        }
    }

    private static void convertSingleFile(String filePathInpt , String filePathOutput){
        JavaSDK javaSDK = new JavaSDK();

        File file = new File(filePathInpt);   //"F:\\soft\\司法文档\\2016苏8602民初00052号\\Z\\0087.jpg"
        int iTH_StartExW = javaSDK.TH_StartExW("F:\\OCR\\TH_OCR32_DLL.dll");
        System.out.println("TH_StartExW:" + iTH_StartExW);
        if (iTH_StartExW != 0) return;

        javaSDK.useCoInitialize();
        int iTH_LoadImageW = javaSDK.TH_LoadImageW(file.toString(), 0);
        System.out.println("TH_LoadImageW:" + iTH_LoadImageW);
        if(iTH_LoadImageW != 0) return;

        int iTH_SetLanguage = javaSDK.TH_SetLanguage(0);
        System.out.println("TH_SetLanguage:" + iTH_SetLanguage );
        if(iTH_SetLanguage != 0) return;

        int iTH_Recognize = javaSDK.TH_Recognize();
        System.out.println("TH_Recognize:" + iTH_Recognize);
        if (iTH_Recognize != 0) return;

        System.out.println("TH_OutputBeginW:" + javaSDK.TH_OutputBeginW(filePathOutput + File.separator
                +file.getName().substring(0,file.getName().lastIndexOf("."))+".txt", 0, 0));
//      System.out.println("TH_OutputBeginW:" + javaSDK.TH_OutputBeginW(filePathOutput + File.separator
//                    +file.getName().substring(0,file.getName().lastIndexOf("."))+".pdf", 1, 0));
//      System.out.println("TH_OutputBeginW:" + javaSDK.TH_OutputBeginW(filePathOutput + File.separator
//                +file.getName().substring(0,file.getName().lastIndexOf("."))+".doc", 2, 0|8|16));
//      System.out.println("TH_OutputBeginW:" + javaSDK.TH_OutputBeginW(filePathOutput + File.separator
//                    +file.getName().substring(0,file.getName().lastIndexOf("."))+".xls", 3, 0));
        System.out.println("TH_OutPutFile:" + javaSDK.TH_OutPutFile());
//      System.out.println("TH_SaveOCRFileW:" + javaSDK.TH_SaveOCRFileW("F:\\OCR_output\\TH_SaveOCRFileW7.ocr"));
        System.out.println("TH_OutputEnd:" + javaSDK.TH_OutputEnd());
        System.out.println("TH_End:" + javaSDK.TH_End());
        javaSDK.useCoUninitialize();
    }
}
