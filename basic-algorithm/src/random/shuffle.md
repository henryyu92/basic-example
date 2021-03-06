## Shuffle

Shuffle 算法用于将原数组打散，使得每个元素打散后在数组的每个位置上等概率出现。
#### Fisher-Yates 算法
Fisher-Yates 算法的基本思想是从原始数组中随机取一个之前没有取到过的数字到新的数组中。具体为：
- 初始化原始数组和新数组，原数组长度为 N
- 从还没处理的数组(假设为 k)中，随机产生一个 [0,k) 之间的数字 p
- 从剩下的 k 个数中把第 p 个数取出
- 重复步骤 2 和 3 直到数字全部取完

随机性证明：
> 一个元素 m 被放入第 i 个位置的概率 P = 前 i-1 个位置选择元素时没有选中 m 的概率 * 第 i 个位置选中 m 的概率，即： 
```shell
p = (n-1)/n * (n-2)/(n-1) * ... * 1/(n-i+1) = 1/n

p = C(n-1, i-1)/C(n,i-1) * 1/(n- i + 1) 
```
复杂度分析：
- 时间复杂度：O(N^2)，需要删除元素
- 空间复杂度：O(N)，需要新数组
#### Knuth-Durstenfeld 算法
K-D 算法在 Fisher 算法的基础上进行了改进，在原始数组上对数字进行交互，省去了额外的 O(N) 的空间。该算法的基本思想和 Fisher 类似，每次从未处理的数据中随机取出一个数字，然后把该数字放在数组的尾部，即数组尾部存放的是已经处理过的数字。

算法步骤：
- 建立一个数组大小为 N 的数组 arr，分别存放 1~N 的数值
- 生成一个从 0 到 N-1 的随机数 x
- 输出 arr 下标为 x 的数值，即为第一个随机数
- 将 arr 的尾元素和下标为 x 的元素互换
- 生成 0 到 N-2 的随机数 x ，输出下标为 x 的数值，即为第二个随机数，将 arr 的倒数第二个数与 x 位置的数互换
- ...

复杂度分析：
- 时间复杂度：O(N)
- 空间复杂度：O(1)

#### Inside-Out 算法
K-D 算法时内部打乱的算法，算法完成后原始数据被直接打算，如果要保留原始数据需要另外开辟一个数组来存储生成的新序列。

Inside-Out 算法的基本思想是从前向后扫描数据，把位置 i 的数据随机插入到新数组的 k(k <= i) 位置上，并将 k 位置上原始的数据插入到 i 位置上(相当与在新数组上将 k 位置和 i 位置数据交换)。

随机性证明：
> 原数组第 i 个元素在新数组第 i 个位置的概率为：原素组第 i 个元素插入新数组第 i 个位置 * 原数组后续所有元素都不插入到新数组第 i 个位置，即 ```p = 1/i * (i/(i+1) * (i+1)/(i+2)) * ... * (n-1)/n) = 1/i * i/n = 1/n```

复杂度分析：
- 时间复杂度：O(N)
- 空间复杂度：O(N)，需要额外的新数组