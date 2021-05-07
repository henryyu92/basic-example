## 并查集

Disjoint Set Union

```java
public class UnionFindSet{

    class Node{}

    // 父结点映射
    private HashMap<Node, Node> fatherMap;
    // 节点所在集合节点数映射
    private HashMap<Node, Integer> sizeMap;

    public void UnionFindSet(){
        fatherMap = new HashMap<>();
        sizeMap = new HashMap<>();
    }

    public void makeSets(List<Node> nodes){
        fatherMap.clear();
        sizeMap.clear();
        for(Node node : nodes){
            fatherMap.put(node, node);
            sizeMap.put(node, 1);
        }
    }

    public Node findHead(Node node){
        Node father = fatherMap.get(node);
        if(father != node){
            father = findHead(father);
        }
        fatherMap.put(node, father);
        return father;
    }

    public boolean isSameSet(Node a, Node b){
        return findHead(a) == findHead(b);
    }

    public void union(Node a, Node b){
        if(a == null || b == null){
            return;
        }
        Node aHead = findHead(a);
        Node bHead = findHead(b);
        if(aHead != bHead){
            int aSetSize = sizeMap.get(aHead);
            int bSetSize = sizeMap.get(bHead);
            if(aSetSize <= bSetSize){
                fatherMap.put(aHead, bHead);
                sizeMap.put(bHead, aSetSize + bSetSize);
            }else{
                fatherMap.put(bHead, aHead);
                sizeMap.put(aHead, aSetSize + bSetSize);
            }
        }
    }
}
```
### 岛问题
矩阵中只有 0 和 1 两种值，每个位置都可以和自己的上、下、左、右四个位置相连，如果有一片 1 连在一起则这部分称为一个岛，获取一个矩阵中岛的个数。

```java
public static int countIslands(int[][] m){
    if(m == null || m[0] == null){
        return 0;
    }
    int N = m.lenght;
    int M = m[0].length;
    int res = 0;
    for(int i = 0; i < N; i++){
        for(int j = 0; j < M; j++){
            if(m[i][j] == 1){
                res++;
                infect(m, i, j, N, M);
            }
        }
    }
    return res;
}

public void infect(int[][] m, int i, int j, int N, int M){
    if(i < 0 || i > N || j < 0 || j >= M || m[i][j] != 1){
        return;
    }
    m[i][j] = 2;
    infect(m, i+1, j, N, M);
    infect(m, i - 1, j, N, M);
    infect(m, i, j+1, N, M);
    infect(m, i, j-1, N, M);
}
```
### 子数组问题
```java
public int maxLength(int[] arr, int aim){
    if(arr == null || arr.length == 0){
        return 0;
    }
    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    map.put(0, -1);
    int len = 0;
    int sum = 0;
    for(int i = 0; i < arr.length; i++){
        sum += arr[i];
        if(map.containsKey(sum - aim)){
            len = Math.max(i - map.get(sum - aim), len);
        }
        if(!map.containsKey(sum)){
            map.put(sum, i)
        }
    }
    return len;
}
```

```java
public int mostEOR(int[] arr){
    int ans = 0;
    int xor = 0;
    int[] dp = new int[arr.length];
    HashMap<Integer, Integer> map = new HashMap<>();
    map.put(0, -1);
    for(int i = 0; i < arr.length; i++){
        xor ^= arr[i];
        if(map.containsKey(xor)){
            int pre = map.get(xor);
            dp[i] = pre == -1 ? 1 : (dp[pre] + 1);
        }
        if(i > 0){
            dp[i] = Math.max(mosts[i - 1], dp[i]);
        }
        map.put(xor, i);
        ans = Math.max(ans, dp[i]);
    }
    return ans;
}
```