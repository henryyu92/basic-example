// 基础数据类型可以隐式或者通过 as 关键字显式的转换
// 其他数据类型如 struct 和 enum 则需要通过 From 和 Into trait 来进行类型转换
// Rust 也提供了将特定类型转换为 String 或者从 String 转换到特定类型的方法

#[derive(Debug)]
struct Number {
    value: i32
}

impl From<i32> for Number {
    fn from(item: i32) -> Self {
        Number { value: item }
    }
}

// From trait 允许一个类型定义如何从另一个类型创建自己
fn conversion_from() {
    let num = Number::from(30);
    println!("number is {:?}", num);
}

// Into trait 是 From trait 的逆，Into 会调用 From，使用 Into trait 需要指定转换为的类型
// fn into(self) -> U {
//    U::from(self)
// }
fn conversion_into() {
    let int = 5;
    let num = int.into();
    println!("number is {:?}", num);
}


#[test]
fn test_conversion_from() {
    conversion_from();
}