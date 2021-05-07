## 递归
- 把问题转化为规模缩小的同类问题的子问题
- 有明确的不需要继续进行递归的条件(base case)
- 有当得到了子问题的结果之后的决策过程，不记录每一个子问题的解

> 给定一棵二叉树的头结点 head，返回最大搜索二叉子树的大小

```java
class ReturnType{
    private int size;
    private Node head;
    private int min;
    private int max;

    public ReturnType(int a, Node b, int c, int d){
        this.size = a;
        this.head = b;
        this.min = c;
        this.max = d;
    }
}

public ReturnType process(Node head){
    if(head == null){
        return new ReturnType(0, null, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }
    Node left = head.left;
    ReturnType leftSubTreeInfo = process(left);
    Node right = head.right;
    ReturnType rightSubTreeInfo = process(right);

    int includeItSelf = 0;
    if(leftSubTreeInfo.head == left && rightSubTreeInfo.head == right && head.value > leftSubTreeInfo.max && head.value < rightSubTreeInfo.min){
        includeItSelf = leftSubTreeInfo.size + 1 + rightSubTreeInfo.size;
    }
    int p1 = leftSubTreeInfo.size;
    int p2 = rightSubTreeInfo.size;
    int maxSize = Math.max(Math.max(p1, p2), includeItSelf);

    Node maxHead = p1 > p2 ? leftSubTreeInfo.head : rightSubTreeInfo.head;
    if(maxSize == includeItSelf){
        maxHead = head;
    }
    return new ReturnType(maxSize, maxHead, Math.min(Max.min(leftSubTreeInfo.min, rightSubTreeInfo.min), head.value), Math.min(Max.max(leftSubTreeInfo.max, rightSubTreeInfo.max), head.value))
}
```
> 二叉树中从节点 A 到节点 B 的距离为：A 走到 B 最短路径上的节点个数。返回一个树的最大距离

```java
class ReturnType{
    private int maxDistance;
    private int h;

    public ReturnType(int m, int h){
        this.maxDistance = m;
        this.h = h;
    }
}

public ReturnType process(Node head){
    if(head == null){
        return new ReturnType(0, 0);
    }
    ReturnType leftReturnType = process(head.left);
    ReturnType rightReturnType = process(head.right);
    int includeHeadDistance = leftReturnType.h + 1 + rightReturnType.h;
    int p1 = leftReturnType.maxDistance;
    int p2 = rightReturnType.maxDistance;
    int resultDistance = Math.max(Math.max(p1, p2), includeHeadDistance);
    int hitself = Math.max(leftReturnType.h, rightReturnType.h) + 1;
    return new ReturnType(resultDistance, hitself);
}
```
## 动态规划

> 问题：从矩阵左上角出发，每次只能向右或者向下移动一位，求到左下角路径的最小和
```java
public int minPath(int[][] matrix){
    return walk(matrix, 0, 0);
}

public int walk(int[][] matrix, int i, int j){
    // 最后一个位置
    if(i == matrix.length - 1 && j == matrix[0].length - 1){
        return matrix[j][j];
    }
    // 最后一行
    if(i == matrix.length - 1){
        return matrix[i][j] + walk(matrix, i, j+1);
    }
    // 最后一列
    if(j == matrix[0].length - 1){
        return matrix[i][j] + walk(matrix, i+1, j);
    }
    // 存在重复调用 walk(matrix, j+1, j+1) 这个过程
    int right = walk(matrix, i, j+1);
    int down = walk(matrix, i+1, j);
    return matrix[i][j] + Math.min(right, down);
}
```
决策过程存在重复调用同一个过程，使用动态规划将重复调用过程的值缓存可以减少过程调用：
```java
public int minPaht(int[][] m){
    if(m == null || m.length == 0 || m[0] == null || m[0].lenght == 0){
        return 0;
    }
    int row = m.length;
    int col = m[0].length;
    int[][] dp = new int[row][col];
    // 使用 dp 记录到达某点的路程的最小长度
    dp[0][0] = m[0][0];
    for(int i = 1; i < row; i++){
        dp[i][0] = dp[i-1][0] + m[i][0];
    }
    for(int j = 1; j < col; j++){
        dp[0][j] = dp[0][j-1] + m[0][j];
    }
    for(int i = 1; i < row; i++){
        for(int j = 1; j < col; j++){
            dp[i][j] = Math.min(dp[i-1][j], dp[i][j-1]) + m[i,j];
        }
    }
    return dp[row-1][col-1];
}
```


> 给定数组 arr 中所有的值都为正数且不重复

递归解法
```java
public int coins1(int[] arr, int aim){
    if(arr == null || arr.length == 0 || aim < 0){
        return 0;
    }
    return process1(arr, 0, aim);
}

public int process1(int[] arr, int index, int aim){
    int res = 0;
    if(index == arr.length){
        res = aim == 0 ? 1 : 0;
    }else{
        for(int i = 0; arr[index] * i <= aim; i++){
            res += process1(arr, index +1, aim - arr[index] * i);
        }
    }
    return res;
}
```
当给定 index 和 aim 后 process 的结果是一样的，因此无论 index 之前的如何变化都不会影响该 process 结果，这种情况称为无后向性问题，通过存储该 process 的结果用于后续调用可以减少无效的递归。
```java
public int coins2(int[] arr, int aim){
    if(arr == null || arr.length == 0 || aim < 0) {
        return ;
    }
    int[][] map = new int[arr.length + 1][aim + 1];
    return process2(arr, 0, aim, map);
}

public int process2(int[] arr, int index, int aim, int[][] map){
    int res = 0;
    if(index == arr.length){
        res = aim == ) ? 1 : 0;
    }else{
        int mapValue = 0;
        for(int i = 0; arr[index] * i <= aim; i++){
            mapValue = map[index + 1][aim - arr[index] * i];
            if(mapValue != 0){
                res += mapValue == -1 ? 0 : mapValue;
            }else{
                res += process2(arr, index+1, aim-arr[index]*i, map);
            }
        }
    }
    map[index][aim] = res == 0 ? -1 : res;
    return res;
}
```

```java
public int coins3(int[] arr, int aim){
    if(arr == null || arr.length == 0 || aim < 0) {
        return ;
    }
    int[][] dp = new int[arr.length][aim + 1];
    for(int i = 0; i < arr.length; i++){
        dp[i][0] = 1;
    }
    for(int j = 1; arr[0] * j <= aim; j++){
        dp[0][arr[0]*j] = 1;
    }
    int num = 0;
    for(int i = 1; i < arr.length; i++){
        for(int j = 1; j <= aim; j++){
            num = 0;
            for(int k = 0; j - arr[i] * k >= 0; k++){
                num += dp[i - 1][j - arr[i] * k];
            }
            dp[i][j] = num;
        }
    }
    return dp[arr.length - 1][aim];
}
```
由于变化的参数只有两个，因此可以在一张二维表中将所有变化参数产生的结果记录下来。在二维表中首先可以由 base case 确定初始值，然后根据递归和初始值可以推出任意位置的值。
```java
public int conis4(int[] arr, int aim){
    if(arr == null || arr.length == 0 || aim < 0){
        return 0;
    }
    int[][] dp = new int[arr.length][aim + 1];
    for(int i = 0; i < arr.length; i++){
        dp[i][0] = 1;
    }
    for(int j = 1; arr[0] * j <= aim; j++){
        dp[0][arr[0]*j] = 1;
    }
    for(int i = 1; i < arr.length; i++){
        for(int j = 1; j <= aim; j++){
            dp[i][j] = dp[i - 1][j];
            dp[i][j] +=  j - arr[i] >= 0 ? dp[i][j - arr[i]] : 0;
        }
    }
    return dp[arr.length - 1][aim];
}
```

```java
public int coins5(int[] arr, int aim){
    if(arr == null || arr.length == 0 || aim < 0){
        return 0;
    }
    int[] dp = new int[aim + 1];
    for(int j = 0; arr[0] * j <= aim; j++){
        dp[arr[0] * j] = 1;
    }
    for(int i = 1; i < arr.length; i++){
        for(int j = 1; j <= aim; j++){
            dp[j] += j - arr[i] >= 0 ? dp[j - arr[i]] : 0;
        }
    }
    return dp[aim];
}
```

> 给定一个整型数组，A, B 每次从最左或最右取数直到取完，获取最后最大的分数和

```java
public static int win1(int[] arr){
    if(arr == null || arr.length == 0){
        return 0;
    }
    return Max.max(f(arr, 0, arr.length - 1), s(arr, 0, arr.length - 1));
}

public int f(int[] arr, int i, int j){
    if(i == j){
        return arr[i];
    }
    return Math.max(arr[i] + s(arr, i + 1, j), arr[j] + s(arr, i, j - 1));
}

public int s(int[] arr, int i, int j){
    if(i == j){
        return 0;
    }
    return Math.min(f(arr, i + 1, j), f(arr, i, j - 1));
}
```


> 给定一个数组 arr 全是正数，一个整数 aim，求累加和等于 aim 的最长子数组
```java
public int getMaxLength(int[] arr, int aim){
    if(arr == null || arr.length == 0 || aim <= 0){
        return 0;
    }
    int L = 0;
    int R = 0;
    int sum = arr[0];
    while(R < arr.length){
        if(sum == aim){
            len = Max.max(len, R-L+1);
            sum -= arr[L++];
        }else if(sum < aim){
            R++;
            if(R == arr.length){
                break;
            }
            sum += arr[R];
        }else{
            sum -= arr[L++];
        }
    }
}
```
> 给定一个数组 arr，值可正可负可0，一个整数 aim，求累加和小于等于 aim 的最长子数组

```java
public int maxLengthAwesome(int[] arr, int aim){
    if(arr == null || arr.length == 0){
        return 0;
    }
    // 表示从当前下标开始的子数组的最小累加和
    int[] sums = new int[arr.length];
    // 表示从当前下标开始的最小累加和子数组
    int[] ends = new int[arr.lenth];
    sums[arr.length - 1] = arr[arr.length - 1];
    ends[arr.length - 1] = arr.length - 1;
    for(int i = arr.length - 2; i >= 0; i--){
        if(sums[i + 1] < 0){
            sums[i] = arr[i] + sums[i + 1];
            ends[i] = ends[i+1];
        }else{
            sums[i] = arr[i];
            ends[i] = i;
        }
    }
    int end = 0;
    int sum = 0;
    int res = 0;
    // 数组的每一个位置作为开始查找满足条件的子数组
    for(int i = 0; i < arr.length; i++){
        while(end < arr.length && sum + sum[end] <= aim){
            sum += sums[end];
            end = ends[end] + 1;
        }
        sum -= end > i ? arr[i] : 0;
        res = Math.max(res, end - i);
        end = Math.max(end, i + 1);
    }
    return res;
}
```

### 递归回溯
> N 皇后问题：<br>
>给定 N * N 的

```go
// M means number of queen
func queen(arr []int, M int) {
    if M == len(arr) {
		fmt.Println(arr)
		return
	}

	// 遍历列
	for i := 0; i < len(arr); i++ {
		arr[M] = i
		flag := true
		// 计算前 M-1 个 queen 是否和当前位置冲突
		for j := 0; j < M; j++ {
			ab := i - arr[j]
			tmp := 0
			if ab > 0 {
				tmp = ab
			} else {
				tmp = -ab
			}
			if arr[j] == i || tmp == M-j {
				flag = false
				break
			}
		}
		if flag {
			queen(arr, M+1)
		}
	}
}
```