package example.classic.dp;

/**
 * 最长子序列问题：给定序列 X 和 Y 求解最长的子序列 Z
 */
public class Lcs {

    public static void lcs(String[] x, String[] y) {
        if (x == null || y == null) {
            return;
        }
        int[][] c = new int[x.length][y.length];
        for (int i = 0; i < x.length; i++){
            for (int j = 0; j < y.length; j++){
                c[i][j] = 0;
            }
        }


        int dp = dp(x, y, x.length - 1, y.length - 1, c);
        System.out.println(dp);

    }

    public static int dp(String[] x, String[] y, int i, int j, int[][] c) {

        if (i < 0 || j < 0){
            return 0;
        }

        if (c[i][j] != 0){
            return c[i][j];
        }

        if (x[i].equals(y[j])){
            int max = dp(x, y, i-1, j-1, c) + 1;
            c[i][j] = max;
            return max;
        }

        int xdp = dp(x, y, i - 1, j, c);
        int ydp = dp(x, y, i, j - 1, c);
        int max = Math.max(xdp, ydp);
        c[i][j] = max;
        return max;
    }

    public static int bottom(String[] x, String[] y){
        int[][] c = new int[x.length+1][y.length+1];

        for (int i = 0; i <= x.length; i++){
            c[i][0] = 0;
        }
        for (int j = 0; j <= y.length; j++){
            c[0][j] = 0;
        }

        for (int i = 1; i <= x.length; i++){
            for (int j = 1; j <= y.length; j++){
                // 坐标转换
                if (x[i-1].equals(y[j-1])){
                    c[i][j] = c[i-1][j-1] + 1;
                }else{
                    c[i][j] = Math.max(c[i-1][j], c[i][j-1]);
                }
            }
        }
        return c[x.length][y.length];

    }


    public static void print(String x) {
        System.out.println(x);
    }

    public static void main(String[] args) {
        String[] x = new String[]{"A", "B", "C", "B", "D", "A", "B"};
        String[] y = new String[]{"B", "D", "C", "A", "B", "A"};

        lcs(x, y);

        System.out.println(bottom(x, y));
    }
}
