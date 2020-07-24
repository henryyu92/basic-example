package example.leetcode.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href=https://leetcode-cn.com/problems/binary-tree-paths/</a>
 *
 *给定一个二叉树，返回所有从根节点到叶子节点的路径。
 */
public class LeetCode_257 {

    public List<String> binaryTreePath(TreeNode root){
        return null;
    }

    public List<String> recur(TreeNode root){
        List<String> res = new ArrayList<>();

        if (root == null){
            return res;
        }

        recurProcess(root, "", res);

        return res;
    }

    public void recurProcess(TreeNode node, String path, List<String> paths){
        if (node == null){
            return;
        }
        path += node.val;
        if (node.left == null && node.right == null){
            paths.add(path);
            return;
        }
        path += "->";
        recurProcess(node.left, path, paths);
        recurProcess(node.right, path, paths);
    }

}
