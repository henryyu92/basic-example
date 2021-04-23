package example.leetcode.tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 给你两棵二叉树的根节点 p 和 q ，编写一个函数来检验这两棵树是否相同
 */
public class SameTree {

    /**
     * 深度优先遍历：
     *  同时对两棵树进行深度优先遍历
     */
    public boolean isSameTree_dfs(TreeNode p, TreeNode q){
        if (p == null && q == null){
            return true;
        }else if (p == null || q == null){
            return false;
        }else{
            return isSameTree_dfs(p.left, q.left) && isSameTree_dfs(p.right, q.right);
        }
    }

    /**
     *  广度优先遍历
     */
    public boolean isSameTree_bfs(TreeNode p, TreeNode q){

        if (p == null && q == null){
            return true;
        }
        if (p == null || q == null){
            return false;
        }

        Queue<TreeNode> pQueue = new LinkedList<>();
        Queue<TreeNode> qQueue = new LinkedList<>();

        pQueue.offer(p);
        qQueue.offer(q);
        while (!pQueue.isEmpty() && !qQueue.isEmpty()){
            TreeNode pNode = pQueue.poll();
            TreeNode qNode = qQueue.poll();
            if (pNode.val != qNode.val){
                return false;
            }
            TreeNode pLeft = pNode.left, pRight = pNode.right, qLeft = qNode.left, qRight = qNode.right;
            if ((pLeft == null && qLeft != null) || (pLeft != null && qLeft == null)){
                return false;
            }
            if ((pRight == null && qRight != null) || (pRight != null && qRight == null)){
                return false;
            }
            if (pLeft != null){
                pQueue.offer(pLeft);
            }
            if (pRight != null){
                pQueue.offer(pRight);
            }
            if (qLeft != null){
                qQueue.offer(qLeft);
            }
            if (qRight != null){
                qQueue.offer(qRight);
            }
        }
        return true;
    }
}
