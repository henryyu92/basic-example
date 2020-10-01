//! Rust 定义了两种可以在任意作用域声明的常量类型：
//! - const     不可改变的值
//! - static    在 'static 生命周期内是可变的，静态生命周期是推断出来的，访问或者修改可变静态变量是不安全的

// 全局声明
static LANGUAGE: &str = "Rust";
const THRESHOLD: i32 = 10;

fn is_big(n: i32) -> bool{

    return n > THRESHOLD;
}

fn type_constant(){
    let n = 16;

    println!("This is {}", LANGUAGE);
    println!("The threshold is {}", THRESHOLD);
    println!("{} is {}", n, if is_big(n) { "big" } else { "small" });

    // THRESHOLD = 5;   // 修改常亮导致编译错误
}

#[test]
fn test_type_constant(){
    type_constant();
}