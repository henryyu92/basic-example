
### 局部最小值
> 定义局部最小概念：arr 长度为 1 时，arr[0] 是局部最小。arr 的长度为 N(N>1) 时，如果 arr[0] < arr[1] 那么 arr[0] 是局部最小；如果 arr[N-1] < arr[N-2] 那么 arr[N-1] 是局部最小；如果 0 < i < N-1 且 arr[i] < arr[i-1] && arr[i] < arr[i+1] 那么 arr[i] 是局部最小。给定无序数组 arr，其中任意两个相邻的数都不相等，找出 arr 中任意一个局部最小值的位置

算法思想：

## 矩阵打印问题
> 问题：给定一个矩阵，在额外空间复杂度为 O(1) 下转圈打印

> 算法思路：考虑节点在遍历过程中的行和列的变化会使得整个问题非常复杂，可以将这个问题简化为如何打印一圈，然后如何定位下一圈这两个子问题：矩阵的一圈可以由左上和右下两个顶点确定，此时从左上顶点开始向右遍历直到节点的列等于右下顶点的列，然后开始转向下遍历，依次类推完成一圈的打印；下一圈的定位只需要将左上顶点向右下移动一个且右下顶点向左上移动一个即可，依次移动遍历直到左上顶点的列大于右下顶点的列

算法实现：
```java
public void spiralOrderPrint(int[][] matrix){
    int row1 = 0;
    int col1 = 0;
    int row2 = matrix.length - 1;
    int col2 = matrix[0].length - 1;

    while(row1 <= row2 && col1 <= col2){
        printEdge(matrix, row1++, col1++, row2--, col2--);
    }
}

public void printEdge(int[][] m, int row1, int col1, int row2, int col2){
    if(row1 == row2){
        for(int i = col1; i <= col2; i++){
            System.out.println(m[row1][i] + " ");
        }
    }else if(col1 == col2){
        for(int i = row1; i <= row2; i++){
            System.out.println(m[i][col1] + " ");
        }
    }else{
        int curC = col1;
        int curR = row1;
        while(curC != col2){
            System.out.println(m[row1][curC] + " ");
            curC++;
        }
        while(curR != row2){
            System.out.println(m[curR][col2] + " ");
            curR++;
        }
        while(curC != col1){
            System.out.println(m[row2][curC] + " ");
            curC--;
        }
        while(curR != row1){
            System.out.println(m[curR][col1] + " ");
            curR--;
        }
    }
}
```

> 问题：给定一个矩阵 matrix，在时间复杂度 O(1) 下按照 “之” 子形的方式打印这个矩阵

> 解题思路：考虑如何打印对角线的节点以及如何切换到下一个对角线

算法实现：
```java
public void printMatrixZigZag(int[][] matrix){
    int row1 = 0;
    int col1 = 0;
    int row2 = 0;
    int col2 = 0;
    int endR = matrix.length - 1;
    int endC = matrix[0].length - 1;
    boolean fromUp = false;
    while(row1 <= endRow){
        printLevel(matrix, row1, col1, row2, col2, fromUp);
        row1 = col1 < endC ? row1 : row1 + 1;
        col1 = col1 < endC ? col1 + 1 : col1;
        row2 = row2 < endR ? row2 + 1 : row2;
        col2 = row2 < endR ? col2 : col2 + 1;
        fromUp = !fromUp;
    }
}

public void printLevel(int[][] m, int row1, int col1, int row2, int col2, boolean fromUp){
    // 从上往下打印对角线
    if(fromUp){
        while(row1 <= row2){
            System.out.println(m[row1++][col1--] + " ");
        }
    // 从下往上打印对角线
    }else{
        while(col2 <= col1){
            System.out.println(m[row2--][col2++] + " ");
        }
    }
}
```

> 问题：给定一个 N*M 的整型矩阵 matrix 和一个整数 k，matrix 的每一行和每一列都是有序的，判断 k 是否在 matrix 中。<br>
> 时间复杂度：O(M+N)，空间复杂度：O(1)

> 解题思路：由于矩阵的每行每列都是有序的，考虑右上角这个顶点：当 k 小于该顶点值时，则 k 一定不在该顶点所在的列上，于是查找的范围缩减一列；如果 k 大于该顶点值时，则 k 一定不再该顶点所在的行上，于是查找的范围缩减一行。如此每次判断移动之后直到右上角顶点移动越界。

算法实现：
```java
public int[] findKey(int[][] m, int key, int row, int col){
    int[] res = int[2]{};
    while(row < m.length && col >= 0){
        if(m[row][col] > key){
            row++;
        }else if(m[row][col] < key){
            col--;
        }else{
            res[0] = row;
            res[1] = col;
            break;
        }
    }
    return res;
}
```

> 给定字符串为一个公式，返回公式的计算结果

```java
public int getValue(String str){
    return value(str.toCharArray(), 0)[0];
}

public int[] value(char[] str, int i){
    LinkedList<String> que = new LinkedList<String>();
    int pre = 0;
    int[] bra = null;
    while(i < str.length && str[i] != ')'){
        if(str[i] >= '0' && str[i] <= '9'){
            // 数字处理
            pre = pre * 10 + str[i++] - '0';
        }else if (str[i] != '('){
            // 运算符号处理
            addNum(que, pre);
            que.addLast(String.valueOf(str[i++]));
            pre = 0;
        }else{
            // '(' 处理
            bra = value(str, i + 1);
            pre = bra[0];
            i = bra[1] + 1
        }
    }
    addNum(que, pre);
    return new int[]{getNum(que), i};
}

public void addNum(LinkedList<String> que, int num){
    if(!que.isEmpty()){
        int cur = 0;
        String top = que.pollLast();
        if(top.equals("+") || top.equals("-")){
            que.addLast(top);
        }else{
            cur = Integer.valueOf(que.pollLast());
            num = top.equals("*") ? (cur * num) : (cur / num);
        }
    }
}

public int getNum(LinkedList<String> que){
    int res = 0;
    boolean add true;
    String cur = null;
    int num = 0;
    while(!que.isEmpty()){
        cur = que.pollFirst();
        if(cur.equals("+")){
            add = true;
        }else if(cur.equals("-")){
            num = Integer.valueOf(cur);
            res += add ? num : (-num);
        }
    }
    return res;
}
```

## 完美洗牌算法
问题描述：
> 一副扑克牌分成大致相等的两份，通过洗牌后使得洗后的牌刚好是左右两份交叉的。即给定一个数组 a1, a2, a3, ..., an, b1, b2, b3, ..., bn，洗牌完成之后数组为 a1, b1, a2, b2, ..., an, bn