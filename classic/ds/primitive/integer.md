## 素数判定

给定一个正整数 n，返回 1 到 n 间的素数

**暴力枚举法**

从 1 到 n 进行遍历，对处于 1 到 n 中的每一个数 k 判断 k 是否是素数，如果是则加入集合，对于素数只能被 1 和本身整除。

```java
boolean isPrime(int k){
    for(int i = 2; i < k; i++){
        if(k % i == 0) return false;
    }
    return true;
}

Array<Integer> getPrimes(int n){
    Array<Integer> primes = new Array<>();
    for(int i = 1; i <=n; i++){
        if(isPrime(i)){
            primes.add(i);
        }
    }
    return primes;
}
```
对于素数的判断，如果一个数不是素数的，则其可以被一系列小于它的素数整除，因此判断一个数是否是素数只需要判断能否被小于它的素数整除即可：
```java
Array<Integer> prime_arr = new Array<Integer>(){1,2,3};
boolean isPrime(int k){
    if(k <= 3){
        return true;
    }
    for(int i = 1; i< prime_arr.length();i++){
        if(k % prime_arr.get(i) == 0) return false
    }
    prime_arr.add(k);
    return true;
}
```
利用消去法，如果将将素数的所有倍数的数都删除，则剩余的数就全部都是素数
```java
boolean[] primes = new boolean[];
for(int i = 0; i <= n;i++){
    primes[i] = true;
}
```
for(int i = 2; i <=n; i++){
    if(primes[i] == true){
        int p = i;
        for(int j = 2; j * p <= n;j++){
            primes[j*p] = false;
        }
    }
}

## 矩形相交

在二维平面中，如果矩形的长与 x 轴平行，高与 y 轴平行，则称这种矩形是坐标轴对齐的，这种矩形在数据结构上可以用做下角坐标(x,y)以及宽 w 和高 h 来表示。给定两个坐标轴对齐的矩形，判断两个矩形是否相交。

```java
class Rectangle{
    int x;
    int y;
    int w;
    int h;
}
```
is_interset(Rectangel R, Rectangle S){
    return R.x <= S.x + S.w && S.x <= R.x + R.w && R.y <= S.y + S.h && S.y <= R.y + R.h
}
对于相交的矩形，左下角的 x 坐标为 max(R.x, S.x)，左下角的 y 坐标为 max(R.y, S.y)，矩形的宽度为 min(R.x+R.w, S.x+S.w) - max(R.x, S.x)，矩形的高度为 min(R.y+R.h, S.y+S.h)-max(R.y, S.y)

## 数字与字符串转换

实现字符串数字到不同进制间的转换

对于不同进制的数字转换，最好的处理办法是将其他进制的数字转换成 10 进制，然后再转换成其他进制的数字。

```java
int strToInt(String s, int b){
    int val = 0; base = 1;
    for(int i = s.length() -1 ; i >= 0; i--){
        char c = s.charAt(i);
        int v = 0;
        if('0' <= c && c <= '9'){
            v = c - '0';
        }else if(c >= 'A' && c <= 'F'){
            x = 10 + c - 'A';
        }

        if(i < s.length() - 1){
            base *= b;
        }

        val += v * base;
    }
}
```