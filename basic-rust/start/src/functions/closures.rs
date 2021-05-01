// 闭包是可以获取环境的匿名函数
// 闭包的调用和函数一样，但是闭包输入和返回类型可以推断，并且在调用闭包时需要指定输入变量名
// 闭包还有三个特定：
//  - 使用 || 包裹输入变量
//  - 如果函数体只有一个表达式，则可以省略 {}，而其他则必须使用 {}
//  - 闭包可以捕获外部环境的变量

fn closures() {
    // 函数
    fn function(i: i32) -> i32 { i + 1 }

    // 闭包
    let closure_inferred = |i| i + 1;

    let i = 1;
    // Call the dfc and closures.
    println!("function: {}", function(i));
    println!("closure_inferred: {}", closure_inferred(i));

    // A closure taking no arguments which returns an `i32`.
    // The return type is inferred.
    let one = || 1;
    println!("closure returning one: {}", one());
}

fn capture_variable(){
    use std::mem;

    let color = String::from("green");
    let print = || println!("`color`: {}", color);
}


#[test]
fn test_closures(){
    closures();
}
