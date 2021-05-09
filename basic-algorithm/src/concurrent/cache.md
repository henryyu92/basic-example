## 缓存算法
缓存算法是指令的一个明细表，用于提示计算设备的缓存信息中哪些条目应该被删去。常用的缓存算法有：
- **FIFO (First In First Out)**：FIFO 是一种先进先出的数据缓存器，是最简单、最公平的一种思想，即如果一个数据是最先进入的，那么可以认为在将来它被访问的可能性很小。空间满的时候，最先进入的数据会被最早置换（淘汰）掉
- **LRU (Least Recently Used)**：最近最少使用算法将最近使用的数据存放在靠近缓存顶部的位置，当数据被访问时将会被放置到缓存的顶部，当缓存达到极限时最早被访问的数据会从缓存的底部开始被删除
- **LFU (Least Frequently Used)**：最不经常使用算法使用一个计数器来记录数据被访问的频率，当缓存空间满的时候最低访问数的数据首先被删除
- **MRU (Most Recently Used)**：最近最常使用算法最先移除最近最常使用的数据，适用于处理一个数据存放时间越久越容易被访问的情况
- **ARC (Adaptive Replacement Cache)**：自适应缓存算法同时跟踪记录 LFU 和 LRU 以及驱逐缓存条目，来获得可用缓存的最佳使用
### FIFO
FIFO 队列使用链表作将数据链接起来，并将置换指针指向队列的队首。在进行置换时，只需把置换指针所指的数据（页面）顺次换出，并把新加入的数据插到队尾即可。

FIFO 算法实现简单，但是有一个显著的缺点，即 FIFO 假定先进入的数据被访问的可能性很小而没有考虑实际上程序执行的动态特征会导致在某些特定的时刻缺页率反而会随着分配页面的增加而增加，即 Belady 现象。产生 Belady 现象的原因是 FIFO 置换算法与进程访问内存的动态特征是不相容的，被置换的内存页往往还会经常被访问，因此 FIFO 算法会使一些页会频繁地被替换从而导致缺页率增加。

```go

```
### LRU
LRU 算法是最常用的算法，其基于的思想是“如果一个数据在最近一段时间没有访问，那么其将来一段时间内被访问的可能性也很小”。

LRU 通常采用双向链表和哈希结构作为缓存，通过双向链表可以快速的实现数据位置的调整，而哈希结构能够保证查询的效率。

LRU 算法在每一次访问缓存时都会引起缓存结构的变化，但是通过双向链表和哈希的存储结构使得 LRU 算法操作的复杂度为 O(1).

```go

```

```java
class Node<K, V>{
    public K key;
    public V value;
    public Node<V> last;
    public Node<V> next;

    public Node(K key, V value){
        this.key = key;
        this.value = value;
    }
}

class NodeDoubleLinkedList<K, V>{
    private Node<K, V> head;
    private Node<K, V> tail;

    public NodeDoubleLinkedList(){
        this.head = null;
        this.tail = null;
    }

    public void addNode(Node<K, V> newNode){
        if(newNode == null){
            return;
        }
        if(this.head == null){
            this.head = newNode;
            this.tail = newNode;
        }else{
            this.tail.next = newNode;
            newNode.last = this.tail;
            this.tail = newNode;
        }

    }

    public Node moveNodeToTail(Node<K, V> node){
        if(this.tail == node){
            return;
        }
        if(this.head == node){
            this.head = node.next;
            this.head.last = null;
        }else{
            node.last.next = node.next;
            node.next.last = node.last;
        }
        // 加入链表尾部
        node.last = this.tail;
        node.next = null;
        this.tail.next = node;
        this.tail = node;
    }

    public Node<K, V> removeHead(){
        if(this.head == null){
            return null;
        }
        Node<K, V> res = this.head;
        if(this.head == this.tail){
            this.head = null;
            this.tail = null;
        }else{
            this.head = res.next;
            res.next = null;
            this.head.last = null;
        }
        return res;
    }
}

class Cache<K, V>{
    private HashMap<K, Node<K, V>> keyNodeMap;
    private NodeDoubleLinkedList<K, V> nodeList;
    private int capacity;

    public Cache(int capacity){
        if(capacity < 1){
            throw new IllegalArgumentsException("");
        }
        this.keyNodeMap = new HashMap<>();
        this.nodeList = new NodeDoubleLinkedList<>();
    }

    public V get(K key){
        if(this.keyNodeMap.containsKey(key)){
            Node<K, V> res = this.keyNodeMap.get(key);
            this.nodeList.moveNodeToTail(res);
            return res.value;
        }
        return null;
    }

    public void set(K key, V value){
        if(this.keyNodeMap.containsKey(key)){
            Node<K, V> node = this.keyNodeMap.get(key);
            node.value = value;
            this.nodeList.moveNodeToTail(node);
        }else{
            Node<K, V> newNode = new Node<>(key, value);
            keyNodeMap.put(key, newNode);
            this.nodeList.addNode(newNode);
            if(this.keyNodeMap.size() == this.capacity + 1){
                this.removeMostUnusedCache();
            }
        }
    }

    public void removeMostUnusedCache(){
        Node<K, V> removeNode = this.nodeList.removeHead();
        this.keyNodeMap.remove(removeNode.key);
    }
}
```
### LFU
LFU 算法需要使用额外的空间保存数据访问的频率，其设计思想为“如果一个数据在最近一段时间内很少次数使用，那么在将来一段时间内被使用的可能性也很小”。

LFU 算法有一个问题是它无法对一个拥有最初高访问率之后长时间没有被访问的数据负责，因此 LFU 算法并不经常使用。

LFU 算法实现有两种方案实现：双向链表+哈希 和 最小堆+哈希。

双向链表+哈希实现：
```go

```
最小堆+哈希实现：
```go

```

```java
class Node{
    public Integer key;
    public Integer value;
    public Integer times;
    public Node up;
    public Node down;

    public Node(int key, int value, int times){
        this.key = key;
        this.value = value;
        this.times = times;
    }
}

class LFUCache{
    class NodeList{
        public Node head;
        public Node tail;
        public NodeList last;
        public NodeList next;

        public NodeList(Node node){
            head = node;
            tail = node;
        }

        public void addNodeFromHead(Node newHead){
            newHead.down = head;
            head.up = newHead;
            head = newHead;
        }

        public boolean isEmpty(){
            return head == null;
        }

        public void deleteNode(Node node){
            if(head == tail){
                head = null;
                tail = null;
            }else{
                if(node == head){
                    head = node.down;
                    head.up = null;
                }else if(node == tail){
                    tail = node.up;
                    tail.down = null;
                }else{
                    node.up.down = node.down;
                    node.down.up = node.up;
                }
            }
            node.up = null;
            node.down = null;
        }
    }

    private int capacity;
    private int size;
    private HashMap<Integer, Node> records;
    private HasMap<Node, NodeList> heads;
    private NodeList headList;

    public LFUCache(int capacity){
        this.capacity = 0;
        this.size = 0;
        this.records = new HashMap<>();
        this.heads = new HashMap<>();
        headList = null;
    }

    public void set(int key, int value){
        if(records.containsKey(key)){
            Node node = records.get(key);
            node.value = value;
            node.times++;
            NodeList curNodeList = heads.get(node);
            move(node, curNodeList);
        }else{
            if(size == capacity){
                Node node = headList.tail;
                headList.deleteNode(node);
                modifyHeadList(headList);
                records.remove(node.key);
                heads.remove(node);
                size--;
            }
            Node node = new Node(key, value, 1);
            if(headList == null){
                headList = new NodeList(node);
            }else{
                if(headList.head.times.equals(node.times)){
                    headList.addNodeFromHead(node);
                }else{
                    NodeList newList = new NodeList(node);
                    newList.next = headList;
                    headList.last = newList;
                    headList = newList;
                }
            }
            records.put(key, node);
            heads.put(node, headList);
            size++;
        }
    }

    public void move(Node node, NodeList oldNodeList){
        oldNodeList.deleteNode(node);
        NodeList preList = modifyHeadList(oldNodeList) ? oldNodeList.last : oldNodeList;
        NodeList nextList = oldNodeList.next;
        if(nextList == null){
            NodeList nextList = new NodeList(node);
            if(preList != null){
                preList.next = newList;
            }
            newList.last = preList;
            if(headList == null){
                headList = newList;
            }
            heads.put(node, newList);
        }else{
            if(nextList.head.times.equals(node.times)){
                nextList.addNodeFromHead(node);
                heads.put(node, nextList);
            }else{
                NodeList newLIst = new NodeList(node);
                if(preList != null){
                    preList.next = newList;
                }
                newList.last = preList;
                newList.next = nextList;
                nextList.last = newList;
                if(headList == nextList){
                    headList = newList;
                }
                heads.put(node, newList);
            }
        }
    }

    public boolean modifyHeadList(NodeList nodeList){
        if(nodeList.isEmpty()){
            if(headList == nodeList){
                headList = nodeList.next;
                if(headList != null){
                    headList.last = null;
                }
            }else{
                nodeList.last.next = nodeList.next;
                if(nodeList.next != null){
                    nodeList.next.last = nodeList.last;
                }
            }
            return true;
        }
        return false;
    }

    public int get(int key){
        if(!records.containsKey(key)){
            return -1;
        }
        Node node = records.get(key);
        node.times++;
        NodeList curNodeList = heads.get(node);
        move(node, curNodeList);
        return node.value;
    }
}
```
### MRU

### ARC

## 多级缓存
