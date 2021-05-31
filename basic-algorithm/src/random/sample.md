## 抽样

### 蓄水池算法
蓄水池算法适用于对一个不清楚规模的数据集进行采样，保证每个元素被采样到的概率是相同的。
#### 蓄水池算法过程
假设数据序列的规模为 n，需要采样的数量为 k。首先构建一个可容纳 k 个元素的数组将序列的前 k 个元素放入数组中，当第 i(i >= k+1) 个元素时在 [1,i]范围内取随机数 d，若 d 在 [1, k] 范围内则使用第 i 个元素替换蓄水池中的第 d 个数据。遍历完成后，所有的数据都是以 k/n 的概率保留在蓄水池中。
#### 蓄水池算法证明
对于第 i 个数据，如果需要保留在蓄水池中则必须保证第 i 个数据到达时需要被替换到蓄水池并且之后不被替换出蓄水池。

> 当 i <= k 时，数据直接进入蓄水池，所以数据进入蓄水池的概率为 1<br>
> 当 i > k 时，数据进入蓄水池的概率为 k/i<br>
> 当 i < k 时，从 k+1 开始执行替换动作，第 i 个数据不被替换的概率为 ```1 - (k/(K+1))*(1/k) = k/(k+1)```，因此第 i 个数据在遍历完保留在蓄水池中的概率为 ```k/(k+1)*(k+1)/(k+2)...*(n-1)/n = k/n```<br>
> 当 i > k 时，数据需要从 i+1 个数据开始不被替换，不被替换的概率为 ```1 - (k/(i+1)*(1/k)) = i/(i+1)```，因此第 i 个数据在遍历完成后保留在蓄水池中的概率为 ```i/(i+1)*(i+1)/(i+2)...*(n-1)/n = i/n```<br>
> 结合第 i 个元素进入蓄水池的概率和不被替换出蓄水池的概率可得每个元素在遍历完之后最终留在蓄水池中的概率为 k/n

#### 算法实现
```java
public class ReservoirSamplingTest{
    // 数据流
    private int[] pool;
    // 数据规模
    private final int N = 100000;
    private Random random = new Random();

    @Before
    public void setUp() throws Exception{
        pool = new int[N];
        for (int i = 0; i < N; i++){
            pool[i] = i;
        }
    }

    private int[] sampling(int K){
        int[] result = new int[K];
        for (int i = 0; i < N; i++){
            if (i < K){
                result[i] = pool[i];
            }
            int r = random.nextInt(i + 1)
            if (r < K){
                result[r] = pool[i];
            }
        }
        return result;
    }

    @Test
    public void test() throws Exception{
        for(int i : sampling(100)){
            System.out.println(i);
        }
    }
}
```

#### 分布式蓄水池抽样
- 假设有 K 台机器，将数据集分成 K 个数据流，每台机器使用蓄水池抽样处理一个数据流，抽样 m 个数据并记录处理的数据量为 N1,N2,...Nk，N1+N2+...+Nk = N
- 在 [1,N] 中取随机数 d，如果 d < N1 则在第一台机器的蓄水池中等概率不放回(1/m)的选取一个数据；若 N1 <= d < (N1+N2) 则在第二台机器的蓄水池中等概率不放回地选取一个数据；依次类推重复 m 次最终从大数据机 N 抽取出 m 个数据。

算法分析：
- 第 K 台机器中每个数据被选入蓄水池的概率为 m/Nk
- 从第 K 台机器中选取数据进入最终蓄水池的概率为 Nk/N
- 从第 K 台机器中选取一个数据的概率为 1/m
- 重复 m 次则每个数据被选中的概率为 ```m * (m/Nk * Nk/N * 1/m) = m/N```