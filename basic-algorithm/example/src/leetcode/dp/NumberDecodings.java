package leetcode.dp;

/**
 *  定义字母 A-Z 的消息影射编码： ‘A’ -> 1,..., 'Z'->26
 *
 *  给定非空字符串 num, 返回解码的方法的总数
 */
public class NumberDecodings {

    /**
     * https://leetcode-cn.com/problems/decode-ways/solution/dong-tai-gui-hua-java-python-by-liweiwei1419/
     *
     *  以 i 位置结尾的字符串解码方法数为  dp(i)
     *
     *  dp(i) = dp(i-1) + dp(i-2)
     *
     *  注意 '0' 不能单独解码，且 '0x' 也不能解码
     *
     *  arr[i]=0   && 0 < arr[i-1] <=2  => dp[i] = dp[i-2]   else dp[i] = 0
     *  arr[i] !=0 && 0 < arr[i-1] <=2 && arr[i] < 6  => dp[i] = dp[i-2] + dp[i-1] else dp[i] = dp[i-1]
     *
     *  ==> if arr[i] = 0 dp[i] = 0 else dp[i] = dp[i-1]
     *  ==> if 0 < arr[i-1] <=2   dp[i] = dp[i-2]
     *
     *
     */
    public int numDecodings_dp(String s) {
        int length = s.length();
        if (length == 0) return 0;
        if (s.charAt(0) == '0') return 0;

        int[] dp = new int[length + 1];
        dp[0] = 1;

        for (int i = 0; i < length; i++){
            dp[i+1] = s.charAt(i) == '0' ? 0 : dp[i];
            if(i > 0 && (s.charAt(i-1) == '1' || (s.charAt(i-1) == '2' && s.charAt(i) <= '6'))){
                dp[i+1] += dp[i-1];
            }
        }
        return dp[length];
    }


    public static void main(String[] args) {
        System.out.println(new NumberDecodings().numDecodings_dp("100"));
    }
}
