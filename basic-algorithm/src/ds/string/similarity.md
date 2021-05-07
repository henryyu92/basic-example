## 相似度算法
### Levenshtein 距离
指的是两个字符串之间，由一个转换成另一个所需的最少编辑操作次数。允许的编辑操作包括字符替换、插入字符、删除字符。假设：
- 插入一个字符的代价为 a
- 删除一个字符的代价为 b
- 修改一个字符的代价为 c

考虑到对于字符串 t 与字符串 temp 的最短距离为 k，且字符串 temp 与字符串 s 只需要一次操作，则字符串 t 与字符串 s 的最短路径为 cost(t,s) = k + min(a, b, c)，于是可以得到状态转移方程：
```
cost(n, m) = min(cost(n-1, m) + min(a, b), cost(n, m-1) + min(a, b), cost(n-1, m-1) + c)
```
边界条件
```
cost(0, 0) = 0
cost(0, 1) = min(a, b)
cost(1, 0) = min(a, b)
```
使用递归实现：
```java
public static final int INSERT = ?;
public static final int DELETE = ?;
public static final int MODIFY = ?;

public static getEditDistance(String a, String b, int aIndex, int bIndex){
    if(Math.min(aIndex, bIndex) == 0){
        return Math.max(aIndex, bIndex);
    }
    if(a.charAt(aIndex) == b.charAt(bIndex)){
        return getEditDistance(a, b, aIndex - 1, bIndex - 1);
    }
    return Math.min(getEditDistance(a, b, aIndex - 1, bIndex) + Math.min(INSERT, DELETE),
                    getEditDistance(a, b, aIndex, bIndex - 1) + Math.min(INSERT, DELETE),
                    getEditDistance(a, b, aIndex - 1, bIndex - 1) + MODIFY);
}
```
递归实现会出现重复计算，使用代价表方法可以将递归转换为动态规划实现
```java
public static int getEditDistance(String a, String b){
    if(a == null || b == null){
        return Integer.MAX_VALUE;
    }
    if(a.length() == 0){
        return a * b.length();
    }
    if(b.length() == 0){
        return b * a.length();
    }

    int[][] dp = new int[a.length() + 1][b.length() + 1];
    // 初始化边界条件
    for(int i = 0; i <= a.length(); i++){
        dp[0][i] = i;
    }
    for(int i = 0; i <= b.length(); i++){
        dp[i][0] = i;
    }
    // 填充代价表
    for(int i = 1; i <= a.length(); i++){
        for(int j = 1; j <= b.length(); j++){
            if(a.charAt(i - 1) == b.charAt(j - 1)){
                dp[i][j] = dp[i-1][j-1]
            }else{
                dp[i][j] = dp[i-1][j-1] + 1;
            }
            dp[i][j] = Math.min(dp[i][j], Math.min(dp[i-1][j] + 1, dp[i][j-1]+1));
        }
    }
    return dp[a.length()][b.length()];
}
```
### SimHash 算法
SimHash 算法分为 5 个步骤：分词、hash、加权、合并、降维
- 分词：给定一段语句，进行分词，得到有效的特征向量，然后为每一个特征向量设置1-5等5个级别的权重（如果是给定一个文本，那么特征向量可以是文本中的词，其权重可以是这个词出现的次数）
- hash：通过hash函数计算各个特征向量的hash值，hash值为二进制数01组成的n-bit签名
- 加权：在hash值的基础上，给所有特征向量进行加权，即W = Hash * weight，且遇到1则hash值和权值正相乘，遇到0则hash值和权值负相乘
- 合并：将上述各个特征向量的加权结果累加，变成只有一个序列串
- 降维：对于n-bit签名的累加结果，如果大于0则置1，否则置0，从而得到该语句的simhash值，最后我们便可以根据不同语句simhash的海明距离来判断它们的相似度