package example.leetcode.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TreeTestUtil {

    public static TreeNode getTree(int length, int rangeLeft, int rangeRight){
        List<Integer> nodes = getRandomNumbers(length, rangeLeft, rangeRight);
        System.out.println("pre inorder: " + nodes);
        return buildTree(nodes);
    }

    public static List<Integer> getIncreasingNumbers(int length, int rangeLeft, int rangRight){
        List<Integer> randomNumbers = getRandomNumbers(length, rangeLeft, rangRight);
        randomNumbers.sort(Comparator.comparingInt(Integer::intValue));
        return randomNumbers;
    }

    public static List<Integer> getRandomNumbers(int length, int rangeLeft, int rangeRight){
        List<Integer> numbers = new ArrayList<>(length);
        for (int i = 0; i < length; i++){
            numbers.add((int) (Math.random() * (rangeRight - rangeLeft)));
        }
        return numbers;
    }

    private static TreeNode buildTree(List<Integer> nodes){
        TreeNode root = new TreeNode(nodes.get(0));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        TreeNode next;
        int i = 1, l = nodes.size();
        while ((next = queue.poll()) != null){
            if (i < l){
                next.left = new TreeNode(nodes.get(i++));
                queue.add(next.left);
            }
            if (i < l){
                next.right = new TreeNode(nodes.get(i++));
                queue.add(next.right);
            }
        }
        return root;
    }
}
