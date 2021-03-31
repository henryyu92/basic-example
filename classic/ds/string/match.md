## 字符串匹配

字符串匹配是指在目标字符串中寻找给定的模式串,并返回目标串中和模式串匹配的第一个子串的首字符位置。

### Brute-Force 算法

暴力算法是最直接朴素的算法，算法思想是：从主串的首字符开始作为起始字符和模式串匹配，如果匹配不成功则以主串的下一个字符为起始字符和模式串匹配，直到匹配成功或者匹配到主串结尾。

暴力算法的时间复杂度为 O(N*M), 空间复杂度为 O(1)

```go
func bruteForceMatch(str1, str2 string) int {
    l1, l2 := len(str1), len(str2)
    if l1 < l2 {
		return -1
	}
    for i := 0; i <= l1 - l2; i++ {
        for j := 0; j < l2; j++ {
            if str1[i+j] == str2[j] && j == l2-1{
                return j
            }
            if str1[i+j] == str2[j]{
                continue
            }
            break
        }
    }
    return -1
}
```

### KMP 算法

KMP(Knuth–Morris–Pratt) 算法由 Donald Knuth(K), James H. Morris(M), Vaughan Pratt(P) 三位在 1977 年提出，是对暴力算法的一种改进。暴力算法中如果在匹配过程中有不一致的字符则需要回退主串然后重新比较，KMP 算法就是针对主串的回退进行优化，使得在匹配过程中有不一致字符出现时不回退主串，从而大大的提高了效率。

KMP 算法利用了已经匹配的子串信息来减少了主串的回退，考虑到模式串 P 的前 m 个字符与主串匹配，如果在模式串中 `P[0:k]` 与 `P[m-k:m]`相同，则可以直接跳过一定不会匹配的位置继续匹配。

```
    ------+---+---+---+---+---+---------------
    ..    | a | b | c | a | b |     ...
    ------+---+---+---+---+---+---+---+---+---
            ^                   ^
            i                  i+m
                      +---+---+---+---+---+---+
                      | a | b | c | a | b | c |
                      +---+---+---+---+---+---+
                            ^           ^
                            k           m
```
KMP 算法对于模式串 P 定义了 next 数组：**`next[i]` 表示 `P[0:i]` 这个子串中前缀和后缀相同的最大长度 k，其中 `k < i+1`**。于是在匹配的过程中如果在某个字符上不匹配，则只需要从 next 数组中找出前面匹配的子串的 k，然后从模式串的第 k 个位置继续与主串进行比较即可。

KMP 算法的事件复杂度为 O(n+m)，空间复杂度为 O(m)

```go
func kpmMatch(str1, str2 string) int{
    l1, l2 := len(str1), len(str2)
    if l1 < l2 {
		return -1
    }
    next := getNext(l2)
    // p pattern string index
    // t target string index
    p, t := 0, 0
    
    for t < l1{
        if str1[t] == str2[p]{
            p++
            t++
        }else if p != 0{
            p = next[p-1]
        }else{
            t++
        }
        if p == len(next){
            return t - p
        }
    }
    
}
```
KMP 中最核心的就是 next 数组的构建，如果 next[i] 已知，即 P[0:i] 前缀和后缀相等的最大长度为 k，则对于 next[i+1] 有两种情况：
- P[i+1] 和 P[k] 相等则可得 next[i+1] 为 k+1
- P[i+1] 和 P[k] 不相等，此时需要在 0～k-1 之间找到一个最大的 m 使得 P[0:m] 和 P[i-m:i]，由于 P[0:k-1] 和 P[i-k:i] 是相等的，所以 m 就是 P[0:k-1] 的前缀和后缀相等的最大值，然后比较 P[i+1] 和 P[m+1] 是否相等，如果相等则为 next[next[i]] + 1 否则为 0
```go
// 构建 next 数组
func getNext(str string) []int{
    l = len(str)
    if l == 1{
        return []int{0}
    }
    
    
}
```

### 扩展 KMP 算法


参考：
- [https://www.zhihu.com/question/21923021](https://www.zhihu.com/question/21923021)

- [https://blog.csdn.net/dyx404514/article/details/41831947](https://blog.csdn.net/dyx404514/article/details/41831947)