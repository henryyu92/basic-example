// rust 原生数据类型
// 有符号整数： i8, i16, i32, i64, i128 和 isize(指针的大小)
// 无符号整数： u8, u16, u32, u64, u128 和 usize
// 浮点数： f32, f64
// 字符： char，每个字符占用 4 个字节
// 布尔值： true 和 false

// 复合类型
// array, tuple

fn primitive_type(){

    // 指定变量类型
    let logical: bool = true;

    let an_integer   = 5i32; // 数字类型可以通过后缀指定

    let default_float   = 3.0; // 浮点数默认是 `f64`
    let default_integer = 7;   // 整数默认是 `i32`

    // mut 表示可变变量
    let mut inferred_type = 12;
    inferred_type = 4294967296i64;  // 数据类型可以放大

    // inferred_type = true;    // 数据类型不能改变

    let inferred_type = false;  // 变量可以被覆盖
}

// 类型字面量
fn type_literals(){

    println!("1 + 2 = {}", 1u32 + 2);

    println!("1 - 2 = {}", 1i64 - 2);
    // println!("1 - 2 = {}", 1u32 - 2);   // 不同类型的变量不能进行运算

    // 布尔运算，具有短路效应
    println!("true AND false is {}", true && false);
    println!("true OR false is {}", true || false);
    println!("NOT true is {}", !true);

    // 整数字面量可以使用十六进制、八进制和二进制
    println!("{}, {}, {}", 0x123, 0o443, 0b100100011);

    // 位运算
    println!("0011 AND 0101 is {:04b}", 0b0011u32 & 0b0101);
    println!("0011 OR 0101 is {:04b}", 0b0011u32 | 0b0101);
    println!("0011 XOR 0101 is {:04b}", 0b0011u32 ^ 0b0101);
    println!("1 << 5 is {}", 1u32 << 5);
    println!("0x80 >> 2 is 0x{:x}", 0x80u32 >> 2);

    // 使用下划线 _ 分隔整数增强可读性
    println!("One million is written as {}", 1_000_000u32);
}

#[test]
fn test_primitive_type(){
    primitive_type()
}

#[test]
fn test_type_literals(){
    type_literals()
}
