## 红黑树
红黑树是一棵二叉搜索树，它在每个结点上增加了一个存储位来表示结点的颜色，颜色可以为红色或黑色。通过对任意一条从根结点到叶子结点的简单路径上各个结点的颜色进行约束，红黑树确保没有一条路径会比其他路径长出 2 倍，因而是近似于平衡的。

树中的每个结点包含 5 个属性：color、key、left、right 和 p，分别表示结点的颜色、键值、左孩子指针、右孩子指针和父结点指针。如果一个结点没有孩子结点或者没有父结点则对应指针的值为 NIL，把这些 NIL 视为指向二叉搜索树的叶结点(外部结点)的指针，而把带有关键字的结点视为树的内部结点。

红黑树是一棵二叉搜索树，满足红黑性质为：
- 每个结点是红色的或者是黑色的
- 根结点是黑色的
- 每个叶结点(外部结点)是黑色的
- 如果一个结点是红色的，则它的两个子结点都是黑色的
- 对于每个节点，从该结点到其所有后代叶结点的简单路径上都包含数目相同的黑色结点

从某个结点 x 出发(不包含该结点)到达一个叶节点的任意一条简单路径上的黑色结点个数称为该结点的黑高(black-height)，记为 bh(x)。

> 一棵有 n 个内部节点的红黑树的高度至多为 2lg(n+1)

动态集合操作 search, minimum, maximum, successor 和 predecessor 可在红黑树上在 O(lgn) 时间内执行，因为这些操作在一棵高度为 h 的二叉搜索树上的运行时间为 O(h)。

### 旋转
红黑树的插入和删除操作对树做了修改，为了维护红黑树的性质需要改变树中某些节点的颜色以及指针结构。指针结构的修改是通过旋转(ratation)来完成的，这是一种能保持二叉搜索树性质的搜索树局部操作。
- 当在某个结点 x 上做左旋时，假设它的右孩子为 y 则左旋之后 x 成为 y 的左孩子，y 的左孩子成为 x 的右孩子
- 当在某个结点 x 上做右旋时，假设它的左孩子为 y 则右旋之后 x 成为 y 的右孩子，y 的右孩子成为 x 的左孩子

左旋(逆时针旋转)：
```go
func left_rotate(T *treeNode, x *treeNode){
    y := x.right
    // y 的左孩子成为 x 的右孩子
    x.right = y.left
    if y.left != nil{
        y.left.p = x
    }
    // y 成为 x 的父结点
    y.p = x.p
    // x 的父结点成为 y 的父结点
    if x.p == nil{
        T.root = y
    }else if x == x.p.left{
        x.p.left = y
    }else{
        x.p.right = y
    }
    // x 成为 y 的左孩子
    y.left = x
    x.p = y
}
```

右旋(顺时针旋转)：
```go
func right_ratate(T *treeNode, x *treeNode){
    y := x.left
    // y 的右孩子成为 x 的左孩子
    x.left = y.right
    if y.right != nil{
        y.right.p = x
    }
    // x 的父结点成为 y 的父结点
    if x.p == nil{
        T.root = y
    }else if x = x.p.left{
        x.p.left = y
    }else{
        x.p.right = y
    }
    // y 成为 x 的父结点
    x.p = y
    // x 成为 y 的右孩子
    x.right = y

}
```
### 插入
红黑树的插入先将节点 z 插入树 T 内，然后将 z 着为红色，之后对树进行重新着色使之满足红黑树的性质：
```go
func rb_insert(T *Tree, z *TreeNode){
    y = T.nil
    x = T.root
    // 找到 z 插入的地方
    for x != T.nil{
        y = x
        if z.key < x.key{
            x = x.left
        }else{
            x = x.right
        }
    }
    // 插入 z
    z.p = y
    if y == T.nil{
        T.root = z
    }else if z.key < y.key{
        y.left = z
    }else{
        y.right = z
    }
    z.left = T.nil
    z.right = T.nil
    z.color = RED
    rb_insert_fixup(T, z)
}

func rb_insert_fixup(T *Tree, z *TreeNode){
    for z.p.color == RED{
        if z.p == z.p.p.left{
            y = z.p.p.right
            if y.color == RED{
                z.p.color = BLACK
                y.color = BLACK
                z.p.p.color = RED
                z = z.p.p
            }else if z == z.p.right{
                z = z.p
                left_ratate(T, z)
            }
            z.p.color = BLACK
            z.p.p.color = RED
            right_ratate(T, z.p.p)
        }else{
            
        }
    }
}
```
### 删除


#### 大楼轮廓
```java
static class Node{
    private boolean isUp;
    private int posi;
    private int h;

    public Node(boolean bORe, int position, int height){
        isUp = bOre;
        posi = position;
        h = height;
    }
}

static class NodeComparator implements Comparator<Node>{
    public int compare(Node o1, Node o2){
        if(o1.posi != o2.posi){
            return o1.posi - o2.posi;
        }
        if(o1.isUp != o2.isUp){
            return o1.isUp ? -1 : 1;
        }
        return 0;
    }
}

public List<List<Integer>> buildingOutline(int[][] buildings){
    Node[] nodes = new Node[buildings.length * 2];
    for(int i = 0; i < buildings.length; i++){
        node[i * 2] = new Node(true, buildings[i][0], buildings[i][2]);
        node[i*2 + 1] = new Node(false, buildings[i][1], buildings[i][2])
    }
    Arrays.sort(nodes, new NodeComparator());
    TreeMap<Integer, Integer> htMap = new TreeMap<>();
    TreeMap<Integer, Integer> pmMap = new TreeMap<>();
    for(int i = 0; i < nodes.length; i++){
        if(nodes[i].isUp){
            if(!htMap.containsKey(nodes[i].h)){
                htMap.put(nodes[i].h, 1);
            }else{
                htMap.put(nodes[i].h, htMap.get(nodes[i].h) + 1);
            }
        }else{
            if(htMap.containsKey(nodes[i].h)){
                if(htMap.get(nodes[i].h) == 1){
                    htMap.remove(nodes[i].h);
                }else{
                    htMap.put(nodes[i].h, htMap.get(nodes[i].h) - 1);
                }
            }
        }
        if(htMap.isEmpty()){
            pmMap.put(nodes[i].posi, 0);
        }else{
            pmMap.put(nodes[i].posi, htMap.lastKey());
        }
    }
    List<List<Integer>> res = new ArrayList<>();
    int start = 0;
    int height = 0;
    for(Entry<Integer, Integer> entry : pmMap.entrySet()){
        int curPosition = entry.getKey();
        int curMaxHeight = entry.getValue();
        if(height != curMaxHeight){
            if(height != 0){
                List<Integer> newRecord = new ArrayList<Integer>();
                newRecord.add(start);
                newRecord.add(curPosition);
                newRecord.add(height);
                res.add(newRecord);
            }
            start = curPosition;
            height = curMaxHeight;
        }
    }
    return res;
}
```