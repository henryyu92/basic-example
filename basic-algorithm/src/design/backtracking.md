## 回溯

回溯算法也称为试探法，是一种优选搜索法。回溯法的原理是按照优选条件向前搜索，如果在某一步发现发现不能达到目标就退回上一步重新选择，通过遍历优选条件集合找到问题的解。

回溯法类似枚举搜索，不同的是在枚举的过程中存在剪枝，因此不会遍历所有的组合。


#### 回溯法范式
```
for 选择 in 选择列表:
    # 做选择
    将该选择从选择列表移除
    路径.add(选择)
    backtrack(路径, 选择列表)
    # 撤销选择
    路径.remove(选择)
    将该选择再加入选择列表
```

### 全排列
全排列问题：给定数组 arr，输出数组的全排列
```java
public List<List<Integer>> permute(int[] arr){

    List<List<Integer>> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    backtrace(arr, path, result);
}

public void backtrace(int[] arr, List<Integer> path, List<List<Integer>> result){
    // 边界条件，完成了一个路径
    if(path.size() == arr.length){
        result.add(new ArrayList<>(path));
        return;
    }
    for(int i = 0; i < arr.length; i++){
        // 排除不合法选择
        if(path.contains(arr[i])){
            continue;
        }
        // 做选择
        path.add(arr[i]);
        // 下一层决策
        backtrace(arr, path, result);
        // 撤销选择
        path.remove(path.size() - 1);
    }
}
```


### N 皇后

```java
```