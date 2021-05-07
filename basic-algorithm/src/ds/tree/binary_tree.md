## 二叉树

二叉树是每个结点最多有两个子结点的树结构，二叉树以递归的形式定义：二叉树是 n(n >= 0) 个结点的有限集合：如果 n=0 则是一棵空二叉树，如果 n > 0 则一棵二叉树由一个根结点和两个互不相交的称为左子树和右子树的子树组成，其中左子树和右子树都是一棵二叉树。

二叉树的相关术语：
- 结点的度：结点拥有的子树数目称为结点的度
- 树的层：根节点的层定义为 1，根的孩子为第二层结点，依次类推
- 树的深度：树中最大的结点层
- 树的度：树中最大的结点度

从二叉树的定义可得二叉树有以下特点：
- 每个结点最多有两棵子树，所以二叉树中不存在度大于 2 的结点
- 左子树和右子树是由顺序的，次序不能任意颠倒
- 即使树中某结点只有一棵子树，也要区分是左子树还是右子树

二叉树的性质：
- 二叉树的第 i 层上最多有 ```2^(i-1)``` 个结点
- 深度为 k 的二叉树最多有 ```2^k - 1``` 个结点
- 对于任意一棵二叉树，如果其叶结点数为N0，而度数为2的结点总数为N2，则 ```N0 = N2 + 1```
- 具有 N 个结点的完全二叉树的深度为 ```[logN] + 1```，其中 [logN] 是向下取整的
- 若对含 n 个结点的完全二叉树从上到下且从左到右进行 1 ~ n 的编号，则有如下特性：
  - 若 i = 1，则该结点是二叉树的根，否则编号为 [i/2] 的结点为其双亲结点
  - 若 2i > n，则该结点没有左孩子，否则编号为 2i 的结点为其左孩子结点
  - 若 2i + 1 > n，则该结点没有右孩子，否则编号为 2i+1 的结点为其右孩子结点

常见的二叉树：
- **斜树**：所有的结点都只有左子树或者右子树称为斜树。其中所有结点只有左子树的二叉树称为左斜树，所有结点只有右子树的二叉树称为右斜树
- **满二叉树**：如果二叉树中所有分支结点都存在左子树和右子树，并且所有叶子结点都在同一层上，这样的二叉树称为满二叉树。满二叉树有以下特点：
  - 叶子结点只能出现在最后一层，出现在其他层就不可能达成平衡
  - 非叶子结点的度一定是 2
  - 每层的结点数为 ```2^(i-1)```
- **完全二叉树**：对一棵具有 n 个结点的二叉树按层编号，如果编号为 i(1 <= i <= n) 的结点与具有同样深度的满二叉树的编号为 i 的结点在二叉树中的位置相同，则这棵二叉树称为完全二叉树。完全二叉树是由满二叉树引申而来，**满二叉树一定是完全二叉树，但反过来不一定成立**。完全二叉树具有以下特点：
  - 如果编号 ```i <= [n/2]``` 则编号对应的结点为分支结点，否则为叶子结点
  - 叶子结点只能出现在最后一层和倒数第二层
  - 最后一层的叶子结点集中在树的左部，倒数第二层的叶子结点一定集中在右部连续位置
  - 如果结点的度为 1，则该结点只有左孩子
  - 同样结点数目的二叉树，完全二叉树深度最小

### 二叉树遍历

二叉树的遍历是指从二叉树的根结点出发，按照某种次序依次访问二叉树中的所有结点，使得每个结点只被访问到一次。二叉树的遍历可以分为四种：前序遍历、中序遍历、后序遍历 和 层次遍历。

#### 前序遍历

前序遍历先遍历根结点，然后再遍历左子树，最后遍历右子树。由于二叉树具有递归结构，因此使用递归的方式可以很容易的实现二叉树前序遍历：
```java
public void preOrderRecur(Node root){
    if(root == null){
        return;
    }
    // 根结点
    Sytem.out.print(root.value + " ");
    // 左子树
    preOrderRecur(root.left);
    // 右子树
    preOrderRecur(root.right);
}
```
使用非递归方式遍历则需要使用栈的数据结构，对于每个结点先把右孩子结点压入栈然后把左孩子压入栈，从而保证出栈时左孩子结点先出栈，即先遍历左子树，而右子结点后出栈，即右子树后遍历：

```java
public void preOrderRecur(Node root){
    if(root == null){
        return;
    }
    Stack<Node> stack = new Stack<Node>();
    // 根结点入栈
    stack.add(root);
    while(!stack.isEmpty()){
        Node head = stack.pop();
        System.out.print(head.value + " ");
        // 右子结点入栈
        if(head.right != null){
            stack.push(head.right);
        }
        // 左子结点入栈
        if(head.left != null){
            stack.push(head.left);
        }
    }
}
```
#### 中序遍历

中序遍历先遍历左子树，然后遍历根结点，然后再遍历右子树。利用二叉树的递归结构，可以使用递归的方式遍历二叉树：
```java
public void inOrderRecur(Node root){
    if(root == null){
        return;
    }
    // 遍历左子树
    inOrderRecur(root.left);
    // 遍历根结点
    System.out.print(root.value + " ");
    // 遍历右子树
    inOrderRecur(root.right);
}
```
非递归方式中序遍历二叉树需要借助于栈的结构，从根结点出发将结点的左子结点依次入栈，直到没有左子结点然后出栈将右子结点入栈，循环直到遍历完整棵树：
```java
public void inOrderRecur(Node root){
    if(root == null){
        return;
    }
    Stack<Node> stack = new Stack<>();

    while(!stack.isEmpty() || root != null){
        // 左子结点入栈
        if(root != null){
            stack.push(root);
            root = root.left;
        }else{
            Node head = stack.pop();
            System.out.println(head.value + " ");
            root = head.right;
        }
    }
}
```
#### 后序遍历

后序遍历先遍历左子树，然后遍历右子树，最后遍历根结点。利用二叉树的递归结构，可以使用递归的方式实现二叉树的后序遍历：
```java
public void postOrderRecur(Node root){
    if(root == null){
        return;
    }
    // 遍历左子树
    postOrderRecur(root.left);
    // 遍历右子树
    postOrderRecur(root.right);
    // 遍历根结点
    System.out.print(root.value + " ");
}
```
二叉树的非递归方式后序遍历需要使用到栈结构，利用前序遍历的结果将其按照根-右-左的形式放入辅助栈中，然后将辅助栈出栈即可：
```java
public void posOrderRecur(Node root){
    if(root == null){
        return;
    }
    Stack<Node> stack = new Stack<Node>();
    Stack<Node> help = new Stack<>();
    // 根结点入栈
    stack.push(head);
    
    while(!stack.isEmpty()){
        Node head = stack.pop();
        // 根结点入 help 栈
        help.push(head);
        if(head.left != null){
            stack.push(head.left);
        }
        if(head.right != null){
            stack.push(head.right);
        }
    }
    // 遍历辅助栈
    while(!help.isEmpty()){
        System.out.print(help.pop().value + " ");
    }
}
```
如果不使用辅助栈，则需要使用一个指针记录已经访问过的子树：
```java
public void posOrderRecur(Node root){
    if(root == null){
        return;
    }
    Stack<Node> stack = new Stack<>();
    stack.push(root);

    TreeNode h = null;

    while(!stack.isEmpty()){
        TreeNode c = stack.peek();
        // 左子树没有访问过
        if(c.left != null && h != c.left && h != c.right){
            stack.push(c.left);
        // 右子树没有访问过
        }else if(c.right != null && h != c.right){
            stack.push(c.right);
        // 左、右子树均被访问过 或者 没有左右子结点则需要出栈
        }else{
            System.out.print(stack.pop().avalue + " ");
            // 子树访问过的标记
            h = c;
        }
    }
}
```
#### 层次遍历

二叉树的层次遍历需要借助队列实现，将每层的结点按照顺序入栈，在结点出栈的时候按照顺序将子结点入栈：
```java
public void levelTraversal(Node root){
    if(root == null){
        return;
    }
    Queue<Node> queue = new LinkedList<>();
    queue.offer(root);
    
    while(!queue.isEmpty()){
        Node h = queue.poll();
        System.out.print(h.value + " ");

        if(h.left != null){
            queue.offer(h.left);
        }
        if(h.right != null){
            queue.offer(h.right);
        }
    }
}
```

### 查找后继

二叉树的后继结点指的时中序遍历中一个结点的后一个结点。如果一个结点有子树则其后继会在子树的最左边，如果结点没有子树则需要考虑两种情况：
- 结点是父节点的左子结点，则结点的后继为其父节点
- 结点时父节点的右子结点，则需要判断父节点是否时爷爷结点的左子节点，如果是则其后继为其爷爷结点，否则需要判断爷爷结点直到不为右子结点或者为根结点

```java
public Node successor(Node node){
    if(node == null){
        return node;
    }
    // 存在右子树，则为右子树的最左结点
    if(node.right != null){
        Node curr = node.right;
        while(curr.left != null){
            curr = curr.left;
        }
        return curr;
    // 判断是否为父节点的左子结点
    }else{
        Node parent = node.parent;
        while(paren != null && parent.left != node){
            node = parent;
            parent = node.parent;
        }
        return parent;
    }
}
```
### 构建二叉树

已知二叉树的前序遍历和中序遍历，构建二叉树。根据前序遍历可知子树的根结点最先出现，而在中序遍历中根结点的左边为左子树，右边为右子树，使用递归的思想可以实现二叉树的重建：
```java
public TreeNode buildTree(int[] preOrder, int pstart, int pend, int[] inOrder, int istart, int iend){
    if(pstart > pend){
        return null;
    }
    TreeNode root = new TreeNode(preOrder[pstart]);
    for(int j = istart; j <= iend; j++){
        if(preOrder[pstart] == inOrder[j]){
            root.left = buildTree(preOrder, pstart+1, pstart + j - istart, inOrder, istart, j-1);
            root.right = buildTree(preOrder, pstart+j-istart+1, pend, inOrder, j+1, iend);
        }
    }
    return root;
}

public TreeNode build(int[] preOrder, int[] inOrder){
    return buildTree(preOrder, inOrder, 0, inOrder.length, 0);
}
```

已知二叉树的中序遍历和后序遍历，构建二叉树。根据后续遍历可以得到子树的根结点在子树遍历顺序的最后，根据根结点的值可以在中序遍历中分为左子树和右子树两部分，使用递归的方式可以实现二叉树的重建：
```java
public TreeNode buildTree(int[] postOrder, int[] inOrder, int pstart, int pend, int istart, int iend){
    if(pstart > pend){
        return null;
    }
    TreeNode root = new TreeNode(postOrder[pend]);
    for(int i = istart; i <= iend; i++){
        if(postOrder[pend] == inOrder[i]){
            root.left = buildTree(postOrder, inOrder, pstart, pstart + i - istart - 1, istart, i - 1);
            root.right = buildTree(postOrder, inOrder, pstart+i-istart, pend-1, i + 1, iend);
        }
    }
    return root;
}
```

### 序列化和反序列化
先序遍历序列化：
```java
public String serialByPre(Node head){
    if(head == null){
        return "#!";
    }
    String res = head.value + "!";
    res += serialByPre(head.left);
    res += serialByPre(head.right);
    return res;
}
```
先序遍历反序列化：
```java
public Node recorverByPreString(String preStr){
    String[] values = preStr.split("!");
    Queue<String> queue = new LinkedList<String>();
    for(int i = 0; i != values.length; i++){
        queue.offer(values[i]);
    }
    return recoverPreOrder(queue);
}

public Node recoverPreOrder(Queue<String> queue){
    String value = queue.poll();
    if(value.equals("#")){
        return null;
    }
    Node head = new Node(Integer.valueOf(value));
    head.left = recoverPreOrder(queue);
    head.right = recoverPreOrder(queue);
    return head;
}
```
#### 按层序列化
```java
public String serialByLevel(Node node){
    if(head == null){
        return "#!";
    }
    String res = head.value + "!";
    Queue<Node> queue = new LinkedList<Node>();
    queue.offer(head);
    while(!queue.isEmpty()){
        head = queue.poll();
        if(head.left != null){
            res += head.left.value + "!";
            queue.offer(head.left);
        }else{
            res += "#!";
        }
        if(head.right != null){
            res += head.right.value + "!";
            queue.offer(head.right);
        }else{
            res += "#!";
        }
    }
    return res;
}
```
按层次反序列化：
```java
public Node recoverByLevelString(String levelStr){
    String[] values = levelStr.split("!");
    int index = 0;
    Node head = generateNode(values[index++]);
    Queue<Node> queue = new LinkedList<Node>();
    if(head != null){
        queue.offer(head);
    }
    Node node = nul;
    while(!queue.isEmpty()){
        node = queue.poll();
        node.left = generateNode(values[index++]);
        node.right = generateNode(values[index++]);
        if(node.left != null){
            queue.offer(node.left);
        }
        if(node.right != null){
            queue.offer(node.right);
        }
    }
    return head;
}
```
### 平衡性
对于树中的任意一个结点，其左子树与右子树的高度差不超过 1。

对于一棵树判断其是否是平衡可以判断树中任意节点是否满足三个条件：
- 左子树是是否平衡
- 右子树是否平衡
- 左、右子树的高度差是否不超过 1

```java
public class ReturnData{
    private boolean isB;
    private int h;

    public ReturnData(boolean isB, int h){
        this.isB = isB;
        this.h = h;
    }

    public ReturnData process(Node node){
        if(node == null){
            return new ReturnData(true, 0);
        }
        ReturnData leftData = process(node.left);
        if(!leftData.isB){
            return new ReturnData(false, 0);
        }
        ReturnData rightData = process(node.right);
        if(!rightData.isB){
            return new ReturnData(false, 0);
        }
        if(Math.abs(leftData.h - rightData.h) > 1){
            return new ReturnData(false, 0);
        }
        return new ReturnData(true, Math.max(leftData.h, rightData.h) + 1);
    }
}
```
### 折纸问题
> 将一个纸条从下往上对折 1 次，此时的折痕是凹下去的；再次从下往上对折 1 次，此时的折痕从上到下一次是 凹、凹、凸；当对折次数为 N 时，从上倒下打印折痕的方向。

解题思路：
> 通过归纳可得：整个对折过程类似于一个二叉树的构建过程，对折次数表示树的层数，每次对折产生的折痕为当前层的所有结点。构成的二叉树有一个特点：对于任意一个结点(折痕)，其左孩子折痕是凹的，其右孩子折痕是凸的。整个二叉树的中序遍历即为从上到下折痕的遍历。

算法实现：
```java
public class PaperFolding{
    public static void printAllFolds(int N){
        printProcess(1, N, true);
    }

    public static void printProcess(int i, int N, boolen down){
        if (i > N){
            return;
        }
        printProcess(i + 1, N, true);
        System.out.printLn(down ? "down" : "up");
        printProcess(i + 1, N, false);
    }

    public static void main(String[] args){
        int N = 4;
        printAllFolds(N);
    }
}
```
### 完全二叉树判断
- 某结点只有右孩子则该二叉树不可能是完全二叉树
- 某结点孩子不全，则只有在层次遍历过程中该结点的后续所有节点都是叶结点才是完全二叉树

```java
public boolean isCBT(Node head){
    if(head == null){
        return true;
    }
    Queue<Node> queue = new LinkedList<Node>();
    boolean leaf = false;
    Node l = null;
    Node r = null;
    queue.offer(head);
    while(!queue.isEmpty()){
        head = queue.poll();
        l = head.left;
        r = head.right;
        // 右孩子为 Null 左孩子不为 Null 则不为完全二叉树
        if((leaf && (l != null || r != null)) || (l == null && r != null)){
            return false;
        } 
        if(l != null){
            queue.offer(l);
        }
        if(r != null){
            queue.offer(r)
        }else{
            leaf = true;
        }
    }
    return true;
}
```
### 完全二叉树节点个数
在时间复杂度低于 O(N) 下获取完全二叉树的结点个数

算法思想：

算法实现：
```java
public iint nodeNum(Node head){
    if(head == null){
        return 0;
    }
    return bs(head, 1, mostLeftLevel(head, 1));
}

public int bs(Node node, int level, int h){
    if(level == h){
        return 1;
    }
    if(mostLeftLevel(node.right, level+1) == h){
        return (1<<(h-l)) + bs(node.right, level+1, h);
    }else{
        return (1<<(h-level-1)) + bs(node.left, level+1, h);
    }
}

public int mostLeftLevel(Node node, int level){
    while(node.left != null){
        level++;
        node = node.left;
    }
    return level - 1;
}
```
