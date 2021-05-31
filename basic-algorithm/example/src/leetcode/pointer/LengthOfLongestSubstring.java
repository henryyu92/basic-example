package leetcode.pointer;

import java.util.HashMap;
import java.util.Map;

/**
 * 无重复字符的最长子串
 *
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度
 */
public class LengthOfLongestSubstring {

    /**
     * 双指针法：
     *
     *      使用左右指针表示不重复字符子串的起始和终止位置，向右移动右指针，如果和子串字符不重复则继续，否则移动左指针到重复字符的下一个位置
     *
     *      左指针每次发生变动时需要计算子串的长度
     *
     *      abcabcbb
     *
     */
    public int lengthOfLongestSubstring(String s){
        if (s == null || s.length() == 0){
            return 0;
        }
        int n = s.length();
        Map<Character, Integer> charMap = new HashMap<>(n);
        charMap.put(s.charAt(0), 0);

        int begin = 0, end = 0, max = 1;
        for (int i = 1; i < n; i++){
            Integer index = charMap.get(s.charAt(i));
            // 存在
            if (index != null){
                max = Math.max(max, end - begin + 1);
                // 移除左指针之前的
                for (int j = begin; j <= index; j++){
                    charMap.remove(s.charAt(j));
                }
                begin = index + 1;
            }
            end++;
            charMap.put(s.charAt(end), end);
            if (end == n - 1){
                max = Math.max(max, end - begin + 1);
            }
        }
        return max;
    }

    public static void main(String[] args) {


        LengthOfLongestSubstring substring = new LengthOfLongestSubstring();
        System.out.println(substring.lengthOfLongestSubstring("abcabcbb"));


    }
}
