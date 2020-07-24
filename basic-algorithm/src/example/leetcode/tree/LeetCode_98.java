package example.leetcode.tree;

/**
 * @see <a href="https://leetcode-cn.com/problems/validate-binary-search-tree/"/>
 */
public class LeetCode_98 {

    public boolean isValidBST(TreeNode root){

        return true;
    }

    /**
     * 递归实现
     * @param root
     * @return
     */
    public boolean recursive(TreeNode root){
        return bstHelper(root, null, null);
    }

    public boolean bstHelper(TreeNode node, Integer lower, Integer upper){
        if (node == null){
            return true;
        }
        if (lower != null && node.val <= lower) return false;
        if (upper != null && node.val >= upper) return false;

        if (!bstHelper(node.left, lower, node.val)) return false;
        if (!bstHelper(node.right, node.val, upper)) return false;

        return true;
    }

    /**
     * 非递归实现：二叉搜索树的中序遍历是单调递增的，在中序遍历时判断当前结点是否大于前驱结点
     * @param root
     * @return
     */
    public boolean nonRecursive(TreeNode root){

        return true;
    }
}
