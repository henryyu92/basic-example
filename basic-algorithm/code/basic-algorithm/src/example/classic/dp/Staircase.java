package example.classic.dp;

/**
 * 爬楼梯问题：
 *
 * 有 N 级楼梯，每次只能爬 1 ～ 2 阶，有多少种方式可以到达楼梯顶
 */
public class Staircase {

    /**
     * 采用分治的思想
     * @param n     楼梯阶数
     * @return
     */
    public int brutal(int n){
        return brutal(n-1) + brutal(n-2);
    }

    /**
     * 动态规划思想：记忆搜索法
     * @param n
     * @param m
     * @return
     */
    public int dynamic(int n, int m, int[][] c){
        if (n <= 0) return 0;

        if (c[n][m] != 0){
            return c[n][m];
        }
        
        return 0;
    }

}
