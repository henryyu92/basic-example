package example.leetcode.tree;

/**
 * 根据一棵树的中序遍历与后序遍历构造二叉树
 */
public class PostInorderBuildTree {

    /**
     *      中序遍历： [[左子树中序遍历], 根结点, [右子树中序遍历]]
     *      后序遍历： [[左子树后序遍历], [右子树后序遍历], 根结点]
     *
     */
    public TreeNode buildTree_recursive(int[] inorder, int[] postorder){

    }

    public TreeNode buildTreeRecursiveHelper(int[] inorder, int inorderLeft, int inorderRight, int[] postorder, int postorderLeft, int postorderRight){
        TreeNode root = new TreeNode(postorder[postorderRight]);


    }
}
