package example.leetcode.dp;

/**
 * 回文子串问题
 */
public class Palindrome {

    /**
     *  最长回文子串，在给定的字符串中找出最长的回文子串
     * 
     * 动态规划：
     *
     *      如果 (i, j) 为回文子串，则 (i+1, j-1) 必然为回文子串，并且 s(i) = s(j)
     * 
     *      dp[i][j] = dp[i+1][j-1] && s(i) == s(j)
     *
     */
    public String longestPalindrome_dp(String s){

        int n = s.length();
        if (n < 2){
            return s;
        }

        boolean[][] dp = new boolean[n][n];
        // 所有长度为 1 的子串都是回文的
        for (int i = 0; i < n; i++){
            dp[i][i] = true;
        }
        char[] chars = s.toCharArray();
        int[] maxLen = new int[2];
        // 计算 (i, j) 是否是回文子串，如果是则判断长度
        for (int R = 1; R < n; R++){
            for (int L = R - 1; L >= 0; L--){
                 if (R - L == 1){
                     dp[L][R] = chars[L] == chars[R];
                 }else{
                     dp[L][R] = dp[L+1][R-1] && chars[L] == chars[R];
                 }
                if (dp[L][R] && (R-L) > (maxLen[1] - maxLen[0])){
                    maxLen[0] = L;
                    maxLen[1] = R;
                }
            }
        }
        return s.substring(maxLen[0], maxLen[1] + 1);
    }
}
