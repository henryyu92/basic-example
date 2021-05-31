## 贪心

### 哈夫曼编码问题
```java
public int lessMoney(int[] arr){
    PriorityQueue<Integer> pQ = new PriorityQueue<>();
    for(int i = 0; i < arr.length; i++){
        pQ.add(arr[i]);
    }
    int sum =0;
    int cur = 0;
    while(pQ.size() > 1){
        cur = pQ.poll() + pQ.poll();
        sum += curr;
        pQ.add(cur);
    }
    return sum;
}
```
### 工程问题
```java
public class IPO{
    class Node{
        private int p;
        private int c;

        public Node(int p, int c){
            this.p = p;
            this.c = c;
        }
    }

    class MinCostComparator implements Comparator<Node>{
        public int compare(Node o1, Node o2){
            return o1.c - o2.c;
        }
    }

    class MaxProfitComparator implements Comparator<Node>{
        public int compare(Node o1, Node o2){
            return o2.p - o1.p;
        }
    }

    public int findMaxCapital(int k, int w, int[] profits, int[] capital){
        Node[] nodes = new Node[profites.length];
        for(int i = 0; i < profits.length; i++){
            nodes[i] = new Node(profits[i], capital[i]);
        }

        PriorityQueue<Node> minCostQ = new PriorityQueue<>(new MinCostComparator());
        PriorityQueue<Node> maxProfitQ = new PriorityQueue<>(new MaxCostComparator());
        for(int i = 0; i < nodes.length; i++){
            minCostQ.add(nodes[i]);
        }
        for(int i = 0; i < k; i++){
            while(minCostQ.isEmpty() && minCostQ.peek().c <= w){
                maxProfitQ.add(minCostQ.poll());
            }
            if(maxProfitQ.isEmpty()){
                return w
            }
            w += maxProfitQ.poll().p;
        }
    }
}
```
### 宣讲会问题
```java
public class ProgramComparator implements Comparator<Program>{
    public int compare(Program o1, Program o2){
        return o1.end - o2.end;
    }
}

public bestArrange(Program[] programs, int cur){
    Arrays.sort(programs, new ProgramComparator());
    int result = 0;
    for(int i = 0; i < programs.length; i++){
        if(cur <= programs[i].start){
            result++;
            cur = programs[i].end;
        }
    }
    return result;
}
```