package util;

import java.io.File;
import java.util.List;

/**
 * Created by ZhangHao on 2017/9/30.
 */
public class SimilarityRatioUtil {

    //编辑距离算法
    private static int cost = 0;
    public static int minEdit_distance(String source, String target)
    {
        final int n = target.length();
        final int m = source.length();
        if(m == 0 )return m;
        if(n == 0)return n;
        int[][] distance_matrix = new int[m+1][n+1];
        distance_matrix[0][0] = 0;
        for(int i=0;i <= n;i++){
            distance_matrix[0][i] = i;
        }
        for(int j=0;j <= m;j++){
            distance_matrix[j][0] = j;
        }
        for(int i=1;i <= m;i++){
            char ci = source.charAt(i - 1);
            for(int j=1;j <= n;j++){
                char cj = target.charAt(j - 1);
                if(ci == cj){
                    cost = 0;
                }else{
                    cost = 2;
                }
                distance_matrix[i][j] = Math.min(distance_matrix[i-1][j-1]+cost, Math.min(distance_matrix[i-1][j]+1, distance_matrix[i][j-1]+1));
            }
        }

        return distance_matrix[m][n];
    }

    //取最长
    int max(String source,String target){
        int sourceL=source.length();
        int targetL=target.length();
        if(sourceL>targetL){
            return sourceL;
        }
        return targetL;
    }

    //获取两字符串相似度
    public float getSimilarityRatio(String source, String target){
        return 1-(float)minEdit_distance(source,target)/this.max(source,target);
    }
}
