## 线索二叉树(Threaded-Binary-Tree)
二叉树添加了直接指向节点的前驱和后继的指针的二叉树称为线索二叉树。
> 线索二叉树的构建是通过将二叉树中原本为空的右孩子指针改为指向该节点在中序序列中的后继，所有原本为空的左孩子指针改为指向该节点的中序序列的前驱。

线索二叉树能线性地遍历二叉树，因此比递归的中序遍历更快；使用线索二叉树能够更方便的找到一个节点的父节点，这在占空间有限或者无法使用存储父节点的栈时很有作用。

### 莫里斯遍历
莫里斯遍历使用了线索二叉树来对二叉树进行中序遍历，可以在 O(n) 的时间，O(1) 的空间范围内完成遍历，对其中序遍历修改后可以推广到前序遍历和后序遍历。

莫里斯遍历过程包含三个部分：
- 创建指向中序序列的后继节点的线索(指针)
- 遍历输出节点
- 删除线索，恢复树的结构

#### 二叉树中序遍历([LeetCode](https://leetcode-cn.com/problems/binary-tree-inorder-traversal/))
采用莫里斯遍历的思路：
> curr 表示当前节点，如果 curr 的左子节点为 nil 则表示中序遍历没有左子节点，于是输出根节点并将 curr 移动到右子节点<br>
> 如果 curr 的左子节点不为 nil，则在左子树中寻找 curr 的中序序列前驱结点 pre，如果 pre 的右孩子结点为 Nil 则构造 pre 到 curr 的线索，如果 pre 的右孩子结点为 curr，则表示已经构造过线索，此时打印 curr 并将 curr 移到右子结点
```go
func inorderTraversal(root *TreeNode) []int {
    var res []int
    
    var curr = root
    var pre *TreeNode
    for curr != nil{
        if curr.Left == nil{
            res = append(res, curr.Val)
            curr = curr.Right
        }else{
            pre = curr.Left
            for pre.Right != nil && pre.Right != curr{
                pre = pre.Right
            }
			if pre.Right == nil{
			    pre.Right = curr
				curr = curr.Left
			}else{
			    res = append(res, curr.Val)
			    curr = curr.Right
			}
        }
    }
    
    return res
}
```

#### 二叉树前序遍历
二叉树前序遍历采用采用莫里斯遍历的思路：
> curr 表示当前节点，如果 curr 的左子节点为 nil 则表示中序遍历没有左子节点，于是输出根节点并将 curr 移动到右子节点<br>
> 如果 curr 的左子节点不为 nil，则在左子树中寻找 curr 中序遍历下的前驱结点(左子树的最右结点)：如果前驱右子结点为 Nil，则构建前驱到 curr 的线索(前驱的右子结点指针指向 curr)，如果前驱右子结点不为 Nil(已经构建了线索)则将前驱结点的右子结点设置为 nil 并将 curr 更新为前驱结点的右子结点
```go
func preorderTraversal(root *TreeNode) []int {
    var res []int
    cur := root
    var pre *TreeNode
    for cur != nil{
        if cur.Left == nil{
            res = append(res, cur.Val)
            cur = cur.Right
        }else{
            pre = cur.Left
            // 找到 curr 的前驱节点
            for pre.Right != nil && pre.Right != cur{
                pre = pre.Right
            }
            if pre.Right == nil{
                pre.Right = cur
                res = append(res, cur.Val)
                cur = cur.Left
            }else{
                // 恢复二叉树，将构造的线索删除
                pre.Right = nil
                cur = cur.Right
            }
        }
    }
    return res
}
```
#### 二叉树后序遍历
```java
public void morrisPos(Node head){
    if(head == null){
        return;
    }
    Node cur = head;
    Node mostRight = null;
    while(cur != null){
        mostRight = cur.left;
        if(mostRight != null){
            while(mostRight.right != null && mostRight.right != cur){
                mostRight = mostRight.right;
            }
            if(mostRight.right == null){
                mostRith.right = cur;
                cur = cur.left;
                continue;
            }else{
                mostRight.right = null;
                // 打印右边界
                printEdge(cur.left);
            }
        }
        cur = cur.right;
    }
    printEdge(head);
}

public void printEdge(Node head){
    Node tail = reverseEdge(head);
    Noe cur = tail;
    while(cur != null){
        System.out.print(cur.value + " ");
        cur = cur.right;
    }
    reverseEdge(tail);
}
```