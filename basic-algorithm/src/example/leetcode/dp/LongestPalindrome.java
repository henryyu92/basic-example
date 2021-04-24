package example.leetcode.dp;

/**
 * 最长回文子串
 */
public class LongestPalindrome {

    /**
     *
     * 动态规划：
     *      设 f(i) 表示以  i 结尾的最长回文子串，于是
     *
     *          s[i - f(i-1).length - 1] = s[i]  则 f(i) = s[i-f(i-1).length-1, i]
     *          如果不相等则 f(i) = s[i]
     *      如果 f(i).length > largestStr.length 则说明在 s[0, i] 上的最大回文子串为 f(i)，否则为 largestStr
     *
     */
    public String longestPalindrome_dp(String s){

        int n = s.length();
        if (n < 2){
            return s;
        }

        int[] max = new int[]{0, 0};

        int[][] dp = new int[n][2];
        dp[0] = new int[]{0, 0};

        if (s.charAt(0) == s.charAt(1)){
            dp[1] = new int[]{0, 1};
            max = dp[1];
        }else{
            dp[1] = new int[]{1, 1};
        }

        for (int i = 2; i < dp.length; i++){
            int[] pos = dp[i - 1];
            int left = pos[0];
            if (left < 1 || s.charAt(left - 1) != s.charAt(i)){
                dp[i] = new int[]{i, i};
            }else{
                dp[i] = new int[]{left-1, i};
                if (i - left + 1 > (max[1] - max[0])){
                    max = dp[i];
                }
            }
        }

    }
}
