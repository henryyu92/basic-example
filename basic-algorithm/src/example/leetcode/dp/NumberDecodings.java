package example.leetcode.dp;

/**
 *  定义字母 A-Z 的消息影射编码： ‘A’ -> 1,..., 'Z'->26
 *
 *  给定非空字符串 num, 返回解码的方法的总数
 */
public class NumberDecodings {

    /**
     *  以 i 位置结尾的字符串解码方法数为  dp(i)
     *
     *  dp(i) = dp(i-1) + dp(i-2)
     *
     *  注意 '0' 不能单独解码，且 '0x' 也不能解码
     *
     *
     * 边界条件 a[i-1] <=2 && a[i]
     */
    public int numDecodings_dp(String s) {
        int length = s.length();
        if (length == 0) return 0;
        if (s.charAt(0) == '0') return 0;

    }
}
