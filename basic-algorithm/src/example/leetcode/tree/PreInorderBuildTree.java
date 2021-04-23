package example.leetcode.tree;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据树的前序遍历和中序遍历构造二叉树
 */
public class PreInorderBuildTree {

    /**
     *      递归：
     *          前序遍历： [根结点, [左子树前序遍历], [右子树前序遍历]]
     *          中序遍历： [[左子树中序遍历], 根结点, [右子树中序遍历]]
     *
     *          在中序遍历中找到 根结点 之后就可得左子树和右子树的结点树，从而可以将问题划分为相同的子问题
     *
     *          使用 hash 记录每个结点在中序遍历的位置，这样可以快速找到根结点在中序遍历中的位置(二叉树中没有重复的元素)
     */
    public TreeNode buildTree_recursive(int[] preorder, int[] inorder){
        Map<Integer, Integer> positionMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++){
            positionMap.put(inorder[i], i);
        }
        return buildTree_recursive_helper(preorder, 0, preorder.length, inorder, 0, inorder.length, positionMap);
    }

    private TreeNode buildTree_recursive_helper(int[] preorder, int preorderLeft, int preorderRight, int[] inorder, int inorderLeft, int inorderRight, Map<Integer, Integer> positionMap){

        TreeNode root = new TreeNode(preorder[preorderLeft]);

        int i = inorder[positionMap.get(preorder[preorderLeft])];

        int leftLength = i - inorderLeft;

        root.left = buildTree_recursive_helper(preorder, preorderLeft + 1, preorderLeft + leftLength, inorder, inorderLeft, i - 1, positionMap);
        root.right = buildTree_recursive_helper(preorder, preorderLeft + i + 1, preorderRight, inorder, i+1, inorderRight, positionMap);

        return root;
    }

    public TreeNode buildTree_recursive_1(int[] preorder, int[] inorder){
        return buildTreeHelper(preorder,  inorder, (long)Integer.MAX_VALUE + 1);
    }
    int pre = 0;
    int in = 0;
    private TreeNode buildTreeHelper(int[] preorder, int[] inorder, long stop) {
        //到达末尾返回 null
        if(pre == preorder.length){
            return null;
        }
        //到达停止点返回 null
        //当前停止点已经用了，in 后移
        if (inorder[in] == stop) {
            in++;
            return null;
        }
        int root_val = preorder[pre++];
        TreeNode root = new TreeNode(root_val);
        //左子树的停止点是当前的根节点
        root.left = buildTreeHelper(preorder,  inorder, root_val);
        //右子树的停止点是当前树的停止点
        root.right = buildTreeHelper(preorder, inorder, stop);
        return root;
    }



    /**
     *  Stack
     *
     *  todo
     *
     */
    public TreeNode buildTree_stack(int[] preorder, int inorder){

        return null;
    }


    public static void main(String[] args) {



    }

}
