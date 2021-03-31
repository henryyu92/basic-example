### 前缀树
trie 树称为前缀树或字典树，是一种多路树形结构，常用于词频统计

前缀树的基本性质：
- 根结点不包含字符，除根结点外每个节点只包含一个字符
- 从根结点到某一个节点，路径上经过的字符连接起来为该结点对应的字符串
- 每个节点的所有子节点包含的字符不相同

```java
public class TrieTree{
    public static class TrieNode{
        // 经过该结点的路径
        public int path;
        // 以该结点为结尾的路径数
        public int end;
        public TrieNode[] nexts;
        public TrieNode(){
            path = 0;
            end = 0;
            nexts = new TrieNode[26];
        }
    }

    private TrieNode root;

    public TrieTree(){
        root = new TrieNode();
    }

    public void insert(String word){
        if (word == null){
            return;
        }
        char[] chs = word.toCharArray();
        TrieNode node = root;
        int index = 0;
        for(int i = 0; i < chs.length; i++){
            index = chs[i] - 'a';
            if (node.nexts[index] == null){
                node.nexts[index] = new TrieNode();
            }
            node = node.nexts[index];
            node.path++;
        }
        node.end++;
    }

    public void delete(String word){
        if (search(word) != 0){
            char[] chs = word.toCharArray();
            TrieNode node = root;
            int index = 0;
            for(int i = 0; i < chas.length; i++){
                index = char[i] - 'a';
                // path = 0 表示没有路径经过该结点
                if(--node.nexts[index].path == 0){
                    node.nexts[index] = null;
                    return;
                }
                // path != 0 表示删除该路径之后还有相同的路径
                node = node.nexts[index];
            }
            node.end--;
        }
    }

    public int search(String word){
        if (word == null){
            return 0;
        }
        char[] chs = word.toCharArray();
        TrieNode node = root;
        int index = 0;
        for(int i = 0; i < chs.length; i++){
            index = chs[i] - 'a';
            if(node.nexts[index] == null){
                return 0;
            }
            node = node.nexts[index];
        }
        return node.end;
    }

    public int prefixNumber(String pre){
        if(pre == null){
            return 0;
        }
        char[] chs = pre.toCharArray();
        TrieNode node = root;
        int index = 0;
        for(int i = 0; i < chs.length; i++){
            index = chs[i] - 'a';
            if (node.nexts[index] == null){
                return 0;
            }
            node = node.nexts[index];
        }
        return node.path;
    }
}
```

> 给定一个数组，求子数组的最大异或和，即数组中所有的数异或起来的结果

暴力解
```java
public int getMaxE1(int[] arr){
    int max = Integer.MIN_VALUE;
    for(int i = 0; i < arr.length; i++){
        for(int start = 0; start <= i; start++){
            int res = 0;
            for(int k = start; k <= i; k++){
                res ^= arr[k];
            }
            max = Math.max(max, res);
        }
    }
    return res;
}
```
内部循环有重复求解，使用额外存储可以减少
```java
public int getMaxE2(int[] arr){
    int max = Integer.MIN_VALUE;
    // 记录从 0 到数组每个位置的异或结果
    int[] dp = new int[arr.length];
    int eor = 0;
    for(int i = 0; i < arr.length; i++){
        eor ^= arr[i];
        max = Math.max(max, eor);
        for(int start = 1; start <= i; start++){
            // 异或运算满足交换律和结合律
            int curEor = eor ^ dp[start - 1];
            max = Math.max(max, curEor);
        }
        dp[i] = eor;
    }
    return max;
}
```
使用前缀树方案
```java
class Node{
    public Node[] nexts = new Node[2];
}

class NumTrie{
    public Node head = new Node();

    // 创建前缀树
    public void add(int num){
        Node cur = head;
        for(int move = 31; move >= 0; move--){
            // 提取二进制的每一位
            int path = (num >> move) & 1;
            cur.nexts[path] = cur.nexts[path] == null ? new Node() : cur.nexts[path];
            cur = cur.nexts[path];
        }
    }

    // 返回从 0 到 i 的最大的异或和
    public int maxXor(int num){
        Node cur = head;
        int res = 0;
        for(int move = 31; move >= 0; move--){
            // 提取二进制的每一位
            int path = (num >> move) & 1;
            int best = move == 31 ? path : (path ^ 1);
            best = cur.nexts[best] != null ? best : (best ^ 1);
            res |= (path ^ best) << move;
            cur = cur.nexts[best];
        }
        return res;
    }
}
public int maxXorSubarray(int[] arr){
    if(arr == null || arr.length == 0){
        return 0;
    }
    int max = Integer.MIN_VALUE;
    int eor = 0;
    NumTrie numTrie = new NumTrie();
    numTrie.add(0);
    for(int i = 0; i < arr.length; i++){
        eor ^= arr[i];
        max = Math.max(max, numTrie.maxXor(eor));
        numTrie.add(eor);
    }
    return max;
}
```