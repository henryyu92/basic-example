## 二进制操作算法

二进制操作的算法主要涉及位运算，常见的位操作有：
- ```x & (x-1)```：将 x 的二进制表示中的最低位的 1 清零，如 x=0b1010110 则 x&(x-1)=0b1010100
- ```x & !(x-1)```：获取 x 的二进制表示中的最低位的 1，如 x=0b1010110 则 x&!(x-1)=0b0000010

### 二进制位交换

对于给定的二进制数 x，交换 i 位和 j 位：
```java
int swap_bit(int x, int i, int j){
    if(((x >> i) & 1) != ((x >> j) & 1)) {
        x ^= (1 << i) | (1 << j)
    }
    return x;
}
```

倒转整数的二进制位：
```java
int reverse_bit(int x){
    int j = Integer.SIZE - 1, i = 0;
    while(i < j){
        x = swap_bit(x, i, j)
        j--;
        i++;
    }
    return x;
}
```

描述：对于无符号整数，其二进制中 1 的个数称为权重，记为 S(k)，给定整数 x 找到整数 y 使得其权重和 x 相等且 |x-y| 最小。

分析：要使得 |x-y| 最小则需要保证 x 和 y 的二进制表示只有在 i,j 两处不同，并且 i,j 需要尽可能地近且小。
```java
public int fin_min_abs(int x){
    int i = 0;
    // 遍历 x 的二进制位，找到最小的不同的两个位
    while(i < Integer.SIZE) {
        int y = swap_bit(x, i, i+1);
        if(y != x){
            return y;
        }
        i++;
    }
}
```

### 整数交换

问题描述：给定两个整形变量 a, b 在不使用其他变量的基础上实现变量的交换。

```java
public void swap(int a, int b){
    a = a^b;
    b = a^b;
    a = a^b;
}

public void swap2(int a, int b){
    a = b-a;
    b = b-a;
    a = b+a;
}
```

### 基础类型的集合运算

问题描述：给定一个集合 S，打印集合的所有子集

分析：根据数学原理可知集合 S 的子集个数为 2^N，其中 N 表示集合的长度。如果要打印子集的话需要知道这 2^N 个子集分别是哪些，可以将整个数组理解为一个二进制位，每个集合元素是否在子集中对应该位的二进制位是否为 1。

```java
public void handleAllSubSet(String[] set){
    int len = set.length;
    int val = 0;
    for(int i = 0; i < len; i++){
        val |= (1<<i)
    }
    while(val >=0){
        printSetByBinary(val, set);
        val--;
    }
}

public void printSetByBinary(int val, String[] set){
    String binary = Integer.toBinaryString(val);
    while(binary.length() < set.length){
        binary = '0' + binary;
    }

    int idx = 0;
    boolean isNull = true;
    while(idx < set.length){
        if(binary.charAt(idx) == '1'){
            if(!isNull){
                System.out.print(",")
            }
            Sytem.out.print(set[idx]);
            isNull = false;
        }
        idx++;
    }
    if(isNull){
        System.out.print("null");
    }
    System.out.print("\n");
}
```

### 基础数据类型数值运算

问题描述：给定两个整数 a 和 b，求 a 和 b 的最大公约数

分析：根据欧几里得算法(辗转相除法)

```java
public int gcd(int a, int b){
    if(b == 0 || a == 0){
        return 0;
    }
    if(a%b == 0){
        return b;
    }
    d = a%b;
    return gcd(b, d);
}
```