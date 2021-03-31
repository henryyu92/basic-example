## 图


```java
public class Graph{
    
    public HashMap<Integer, Node> nodes;
    public HashMapM<Edge> edges;

    public Graph(){
        nodes = new HashMap<>();
        edges = new HashMap<>();
    }

    class Edge{
        private int weight;
        private Node from;
        private Node to;

        public Edge(int weight, Node from, Node to){
            this.weight = weight ;
            this.from = from;
            this.to = to;
        }
    }
    class Node{
        private int value;
        private int in;
        private int out;
        private ArrayList<Node> nexts;
        private ArrayList<Edge> edges;

        public Node(int value){
            this.value = value;
            in = 0;
            out = 0;
            nexts = new ArrayList<>();
            edges = new ArrayList<>();
        }
    }

    public Graph createGraph(Integer[][] matrix){
        Graph graph = new Graph();
        for(int i = 0; i < matrix.length; i++){
            Integer weight = matrix[i][0];
            Integer from = matrix[i][1];
            Integer to = matrix[i][2];
            if(!graph.nodes.containsKey(from)){
                graph.nodes.put(from, new Node(from));
            }
            if(!graph.nodes.containsKey(to)){
                graph.nodes.put(to, new Node(to));
            }
            Node fromNode = graph.nodes.get(from);
            Node toNode = graph.nodes.get(to);
            Edge newEdge = new Edge(weight, fromNode, toNode);
            fromNode.nexts.add(toNode);
            fromNode.out++;
            toNode.in++;
            fromNode.edges.add(newEdge);
            graph.edges.add(newEdge);
        }
    }
}
```

### 广度优先遍历
```java
public void bfs(Node node){
    if(node == null){
        return;
    }
    Queue<Node> queue = new LinkedList<>();
    HashSet<Node> set = new HashSet<>();
    queue.add(node);
    set.add(node);
    while(!queue.isEmpty()){
        Node cur = queue.poll();
        System.out.println(cur.value);
        for(Node next : cur.nexts){
            if(!set.contains(next)){
                set.add(next);
                queue.add(next);
            }
        }
    }
}
```
### 深度优先遍历
```java
public void dfs(Node node){
    if(node == null){
        return;
    }
    Stack<Node> stack = new Stack<>();
    HasHset<Node> set = new HashSet<>();
    stack.add(node);
    set.add(node);
    System.out.println(node.value);
    while(!stack.isEmpty()){
        Node cur = stack.pop();
        for(Node next : cur.nexts){
            if(!set.contains(next)){
                stack.push(cur);
                stack.push(next);
                set.add(next);
                System.out.println(next.value);
                break;
            }
        }
    }
}
```
### 拓扑排序算法
适用范围：要求有向图且有入度为 0 的节点，没有环
```java
public static List<Node> sortedTopology(Graph graph){
    HashMap<Node, Integer> inMap = new HashMap<>;
    Queue<Node> zeroInQueue = new LinkedList<>();
    for(Node node : graph.nodes.values()){
        inMap.put(node, node.in);
        if(node.in == 0){
            zeroInQueue.add(node);
        }
    }
    List<Node> result = new ArrayList<>();
    while(!zeroInQueue.isEmpty()){
        Node cur = zeroInQueue.poll();
        result.add(cur);
        for(Node next : cur.nexts){
            // 将入度为 0 的节点的所有子节点入度减 1 消除该节点的影响
            inMap.put(next, inMap.get(next) - 1);
            if(inMap.get(next) == 0){
                zeroInQueue.add(next);
            }
        }
    }
    return result;
}
```
### kruskal 算法
适用范围：要求无向图
```java
public Set<Edge> kruskalMST(Graph graph){
    UnionFind unionFind = new UionFind();
    unionFind.makeSets(graph.nodes.values());
    PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(new EdgeComparator());
    for(Edge edge : graph.edges){
        priorityQueue.add(edge);
    }
    Set<Edge> result = new HashSet<>();
    while(!priorityQueue.isEmpty()){
        Edge edge = priorityQueue.poll();
        if(!unionFind.isSameSet(edge.from, edge.to)){
            result.add(edge);
            unionFind.union(edge.from, edge.to);
        }
    }
    return result;
}
```
### prim 算法
适用范围：要求无向图
### Dijkstra 算法
适用范围：没有权值为负数的边