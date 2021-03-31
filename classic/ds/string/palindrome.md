## 回文算法

字符串回文指的是字符串从前往后看和从后往前看是相同的字符串，最大回文子串指的是给定字符串中长度最大的回文子串。

### 字符串回文
回文字符串具有对称特性，一般有两种方法判断字符串是否是回文：
- 分别从字符串的首尾开始往中间遍历，依次比较对应的字符是否相等
- 从字符串中间往首尾两侧遍历，依次判断对应的字符是否相等


### Brute-Force 算法

暴力算法原理是以字符串的每个字符为中心向两边扩展并记录以该字符为中心的回文子串的长度，但是对于字符串为偶数时直接使用该算法会导致错误的结果，因此一般在使用这种算法前需要对字符串进行预处理，即在字符之间加入特殊的字符，此时无论原始字符串的长度是奇数还是偶数，处理之后的新字符串长度都为奇数。

暴力算法的时间复杂度为： O(N^2)

```go
func preHandle(str string){
    s := make([]rune, 0)
	s = append(s, '#')
	for _, r := range str {
		s = append(s, r)
		s = append(s, '#')
	}
	return string(s)
}

func maxSubPalindrome(str string) int{
    l := len(strings.TrimSpace(str))
	if l == 0 {
		return 0
	}
	s := preHandle(str)
	max := 0
	for i, l := 0, len(s); i < l; i++ {
		p := 0
		for j := 1; i-j >= 0 && i+j < l; j++ {
			if s[i-j] == s[i+j] {
				p++
				continue
			}
			break
		}
		if p > max {
			max = p
		}
	}
	return max
}
```

### Manacher 算法

Manacher 算法是在暴力算法上的改进，该算法也需要对字符串进行预处理。

Manacher 算法的核心在于使用了辅助数组 Len[i] 表示以 str_s[i] 为中心的最长回文子串的半径，于是有 Len(i) -1 就是回文串的长度
```
src        ababc
src_s      #a#b#a#b#c#
len        12141412121
```
Len 数组的求解假设 `0<=j<=i` 且 mx 为最长回文串的右端点，p 为取得这个端点的回文串的中点，也就是 Len(p)=mx-p+1。
- 如果 `i<mx`，此时可以找到 i 关于 p 的对称位置 i1
  - 如果 Len[i1] < mx-i 则说明 i 位置的最大子串包含在 p 位置的最大子串中
  - 如果 Len[i1] >= mx-i 则说明 i 位置的最大子串右端超出了 mx，超出部分需要逐个字符匹配，匹配完之后需要更新 Len[i] 以及 p 和 mx
- 如果 i > mx，此时需要逐个字符匹配，匹配完之后需要更新 Len[i] 以及 p 和 mx

```go
```