// array 是 T 类型的对象集合，存储在一块连续的内存， array 的容量在创建时确定
// slice 和 array 类似，区别是 slice 的容器是变化的，也就是说 slice 包含两个值：指向数据的指针和 slice 的长度

use std::mem;

fn type_array(){

    // 创建固定大小容量的 array 并初始化
    let xs: [i32;5] = [1,2,3,4,5];

    // 初始化 array 的所有元素为相同的值 0
    let ys: [i32;500] = [0; 500];

    // 使用下标可以访问 array 的元素
    println!("first element of the array: {}", xs[0]);
    println!("second element of the array: {}", ys[1]);

    // print!("out of bound indexing array: {}", xs[5]);    // 下标越界导致编译错误

    // len 方法获取 array 的长度
    println!("array size: {}", xs.len());

    // array 是在栈上分配的
    println!("array occupies {} bytes", mem::size_of_val(&xs));

    // 对 array 取地址就会转换成 slice
    analyze_slice(&xs);

    // 可以通过下标截取部分 array 转换成 slice
    analyze_slice(&xs[1 .. 4]);
}

// slice 可以从 array 获取截取得到，此时的类型签名为 &[T]
fn analyze_slice(slice: &[i32]){
    println!("first element of the slice: {}", slice[0]);
    println!("the slice has {} elements", slice.len());
}

#[test]
fn test_type_array(){
    type_array()
}