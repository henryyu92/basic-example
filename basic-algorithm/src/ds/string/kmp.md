## KMP 算法

判断字符串中是否包含另外一个字符串

```java
public int getIndexOf(String s, String m){
    if(s == null || m == null || m.length() < 1 || s.length() < m.length()){
        return -1;
    }
    char[] str1 = s.toCharArray();
    char[] str2 = m.toCharArray();
    int i1 = 0;
    int i2 = 0;
    int[] next = getNextArray();
    while(i1 < str1.length && i2 < str2.length){
        if(str1[i1] == str2[i2]){
            i1++;
            i2++;
        }else if(next[i2] == -1){
            i1++;
        }else{
            i2 = next[i2];
        }
    }
    return i2 == str2.length ? i1 - i2 : -1;
}

public int[] getNextArray(char[] str){
    if(str.length == 1){
        return new int[]{-1};
    }
    int[] next = new int[str.length];
    next[0] = -1;
    next[1] = 0;
    int i = 2;
    int cn = 0;
    while(i < next.length){
        if(str[i - 1] == str[cn]){
            next[i++] = ++cn;
        }else if(cn > 0){
            cn = next[cn];
        }else{
            next[i++] = 0;
        }
    }
    return next;
}
```
## Manacher 算法
```java
public char[] manacherString(String str){
    char[] charArr = str.toCharArray();
    char[] res = new char[str.length() * 2 + 1];
    int index = 0;
    for(int i = 0; i != res.length; i++){
        res[i] = (i & 1) == 0 ? '0' : charArr[index];
    }
    return res;
}

public int maxLcpsLength(String str){
    if(str == null || str.length() == 0){
        return 0;
    }
    char[] charArr = manacherString(str);
    int[] pArr = new int[charArr.length];
    int C = 0;
    int R = -1;
    int max = Integer.MIN_VALUE;
    for(int i = 0; i != charArr.length; i++){
        pArr[i] = R > i ? Math.max(pArr[2*C-i], R-i) : 1;
        while(i + pArr[i] < charArr.length && i - pArr[i] > -1){
            if(charArr[i + pArr[i]] == charArr[i - pArr[i]]){
                pArr[i]++;
            }else{
                break;
            }
        }
        if(i + pArr[i] > R){
            R = i + pArr[i];
            C = i;
        }
        max = Math.max(max, pArr[i]);
    }
    return max - 1;
}
```
## BFPRT 算法
```java
public int getMinKthByBFPRT(int[] arr, int k){
    int[] copyArr = copyArray(arr);
    return bfprt(copyArr, 0, copyArr.length - 1, k - 1);
}

public int bfprt(int arr, int begin, int end, int i){
    if(begin == end){
        return arr[begin];
    }
    int pivot = medianOfMedians(arr, begin, end);
    int[] pivotRange = partition(arr, begin, end, pivot);
    if(i >= pivotRange[0] && i <= pivotRnage[1]){
        return arr[i];
    }else if(i < pivotRange[0]){
        return bfprt(arr, begin, pivotRange[0] - 1, i);
    }else{
        return bfprt(arr, pivotRnage[1] + 1, end, i);
    }
}

public int medianOfMedians(int[] arr, int begin, int end){
    int num = end - begin + 1;
    int offset = num % 5 == 0 ? 0 : 1;
    int[] mArr = new int[num / 5 + offset];
    for(int i = 0; i < mArr.length; i++){
        int beginI = begin + i * 5;
        int endI = beginI + 4;
        mArr[i] = getMedian(arr, beginI, Math.min(end, endI));
    }
    return bfprt(mArr, 0, mArr.length - 1, mArr.length / 2);
}

public int[] partition(int[] arr, int begin, int end, int pivotValue){
    int small = begin - 1;
    int cur = begin;
    int big = end + 1;
    while(cur != big){
        if(arr[cur] < pivotValue){
            swap(arr, ++small, cur++);
        }else if(arr[cur] > pivotValue){
            swap(arr, --big, cur);
        }else{
            cur++;
        }
    }
    int[] range = new int[2];
    range[0] = small + 1;
    range[1] = big - 1;

    return range;
}
```


### 窗口问题
> 一个整型数组 arr 和一个大小为 w 的窗口从数组的最左边滑到最右边，窗口每次向右滑一个位置，返回滑动窗口中最大值组成的数组

```java
public int[] getMaxWindow(int[] arr, int w){
    if(arr == null || w < 1 || arr.length < w){
        return null;
    }
    // ListList 是一个双向链表
    LinkedLlist<Integer> qmax = new LinkedList<Integer>();
    int[] res = new int[arr.length - w + 1];
    int index = 0;
    for(int i = 0; i < arr.length; i++){
        while(!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[i]){
            qmax.pollLast();
        }
        qmax.addLast(i);
        // 窗口形成才会队头出队  
        if(qmax.peekFirst() == i - w){
            qmax.pollFirst();
        }
        // 收集窗口内的最大值
        if(i >= w- 1){
            res[index++] = arr[qmax.peekFirst()];
        }
    }
    return res;
}
```

> 给定数组 arr 和 整数 num，返回有多少个子数组满足：```max(arr[i..j]) - min(arr[i...j]) <= num```。时间复杂度 O(N)

> 如果 start~end 子数组满足要求则其中的所有子数组都满足要求；如果 start~end 子数组不满足要求则 start ~ end 中以 start 作为开始的后续子数组不可能满足要求

```java
public int getNum(int[] arr, int num){
    if (arr == null || arr.length == 0){
        return 0;
    }
    LinkedList<Integer> qmin = new LinkedList<Integer>();
    LinkedList<Integer> qmax = new LinkedList<Integer>();
    int start = 0;
    int end = 0;
    int res = 0;
    while(start < arr.length){
        while(end < arr.length){
            while(!qmin.isEmpty() && arr[qmin.peekLast()] >= arr[end]){
                qmin.pollLast();
            }
            qmin.addLast(end);
            while(!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[end]){
                qmax.pollLast();
            }
            qmax.addLast(end);
            if (arr[qmax.getFirst()] - arr[qmax.getFirst()] > num){
                break;
            }
            end++;
        }
        if(qmin.peekFirst() == start){
            qmin.pollFirst();
        }
        if(qmax.peekFirst == start){
            qmax.pollFirst();
        }
        rest += end - start;
        start++;
    }
    return res;
}
```