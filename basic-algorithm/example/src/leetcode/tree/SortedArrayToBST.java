package example.leetcode.tree;

import java.lang.reflect.Array;
import java.util.List;

/**
 * 将升序数组转换为高度平衡的二叉搜索树
 */
public class SortedArrayToBST {

    /**
     *  二叉搜索树的中序遍历是升序的，考虑到需要构造高度平衡的二叉树，则可以确定数组的中间位置为根结点
     *
     */
    public TreeNode sortedArrayToBST(int[] nums) {
        return sortedArrayToBST(nums, 0, Array.getLength(nums) - 1);
    }

    public TreeNode sortedArrayToBST(int[] nums, int left, int right){

        if (right < left){
            return null;
        }
        // >> 优先级较 + 低
        int mid = left + ((right - left) >> 1);
        TreeNode root = new TreeNode(nums[mid]);
        root.left = sortedArrayToBST(nums, left, mid - 1);
        root.right = sortedArrayToBST(nums, mid + 1, right);

        return root;
    }


    public static void main(String[] args) {

        List<Integer> orderedNumber = TreeTestUtil.getIncreasingNumbers(20, 1, 20);
        System.out.println(orderedNumber);

        SortedArrayToBST bst = new SortedArrayToBST();
        TreeNode root = bst.sortedArrayToBST(bst.list2array(orderedNumber));

        InorderTraversal inorder = new InorderTraversal();
        System.out.println(inorder.inorderTraversal_morris(root));

    }

    private int[] list2array(List<Integer> list){
        int[] nums = new int[list.size()];
        for(int i = 0; i < list.size(); i++){
            nums[i] = list.get(i);
        }
        return nums;
    }
}
