package leetcode.pointer;

/**
 * 盛水最多的容器
 *
 *      给你 n 个非负整数 a1，a2，...，an，每个数代表坐标中的一个点 (i, ai) 。在坐标内画 n 条垂直线，垂直线 i 的两个端点分别为 (i, ai) 和 (i, 0) 。找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水
 *
 */
public class MaxArea {

    /**
     *
     *  双指针法：
     *      以左右指针 start, end 为容器的两边，则该容器盛的水为 (end-start) * min(arr[start], arr[end])
     *
     *  双指针流程：
     *      - 如果 arr[start] <= arr[end] 则 start++
     *      - 如果 arr[start] > arr[end] 则 end--
     *  证明：
     *      - arr[start] 不可能比 arr[end...] 大，因此 (start, end) 为边界的容器盛水最多
     *      - 如果 (start, end) 之间存在值比 arr[start] 大则容器最多盛水为 arr[start] * (j - start) <= arr[start] * (end -start)
     *      - 如果 (start, end) 之间存在值比 arr[start] 小则容器最多盛水为 arr[j] * (j - start) < arr[start] * (end -start)
     *
     */
    public int maxArea(int[] height){


        return 0;

    }
}
