package com.wintone.JFormEx;

public class JFormEx 
{
	public static native int jInitRecogForm(String templatePathIn,String WorkPath);
	public static native boolean jStartRecogForm(String templatePathIn,String WorkPath);//2012.5.11添加
	public static native int jRecognizeSingleForm(String imagePathIn,char modeNameOut[],char resultOut[],int cellOut[]);
	public static native int jRecognizeForm(String imagePathIn,char modeNameOut[],char resultOut[],int cellOut[]);//2012.5.11添加
	public static native int jUninitRecogForm();
	public static native void jEndRecogForm();
	public static native int jRecognizeSpecifiedForm(String specifiedModIn,String imageNameIn,char modeNameOut[],char resultOut[],int cellOut[]);
  public static native int  jExtractPDF2TIFF(String PDFFilePathIn, String imageDirectoryIn, int nBitCountIn, int nResolutionIn, int nTiffCountOut[], long pStart, long pFinish, long pSetStage, long pSetPerc);
  public static native int jLoadTemplates(String templatePathIn);
  public static native int jGetRecognizeResult(char resultOut[],int countOut[]);
}
