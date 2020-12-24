mod primitives;
mod tuple;
mod slice;
mod structure;
mod enums;
mod constants;
mod conversion;


// Rust 提供了基础类型的隐式转换，也可以使用 as 关键字进行显式的转换
fn type_casting(){

    // 屏蔽类型转换时发生溢出的警告
    #![allow(overflowing_literals)]

    let decimal = 65.4321_f32;
    // let integer: u8 = decimal        // f32 类型不能隐式转换成 u8 类型
    let integer = decimal as u8;    // f32 类型显式转换成 u8 类型，存在精度丢失

    let character = integer as char;    // u8 类型显式转换成 char 类型，超过 char 范围的转换会导致错误
    // let character = decimal as char;       // f32 不能直接转换成 char

    println!("Casting: {} -> {} -> {}", decimal, integer, character);


    // 转换成 无符号 类型 T 时，如果不在类型值范围内，则会加上 T::MAX+1 或者减去 T::Max+1 直到值在 T 的范围内
    println!("1000 as a u16 is: {}", 1000 as u16);      // 1000 在 u16 范围内，不用处理
    println!("1000 as a u8 is: {}", 1000 as u8);        // 低 8 位保留，其他位截断， 1000-256-256-256 = 232
    println!("-1 as u8 is: {}",(-1i8) as u8);               // 低 8 位保留，-1 + 256 = 255

    println!("1000 mod 256 is: {}", 1000 % 256);

    // 转换成 有符号 类型，如果最高位为 1 则表示负数
    println!("128 as a i16 is: {}", 138 as i16);    // 在值范围内，不需要处理
    println!("128 as a i8 is: {}", 128 as i8);      // 二进制位为 10_000_000，表示 -128
    println!("232 as a i8 is: {}", 232 as i8);    // 取低 8 位 11_101_000，表示 -24

}

// 数字类型字面量可以添加类型后缀指定数据类型， 如果没有指定则编译器默认使用 i32 作为整数的类型， f64 作为浮点数的类型
fn type_literals(){

    let x = 1u8;
    let y = 2u32;
    let z = 3f32;

    let i = 1;
    let f = 1.0;

    // std::mem::size_of_val 是一种全路径函数调用
    // size_of_val 函数定义在 mem module 中，而 mem module 定义在 std crate 中
    println!("size of `x` in bytes: {}", std::mem::size_of_val(&x));
    println!("size of `y` in bytes: {}", std::mem::size_of_val(&y));
    println!("size of `z` in bytes: {}", std::mem::size_of_val(&z));
    println!("size of `i` in bytes: {}", std::mem::size_of_val(&i));
    println!("size of `f` in bytes: {}", std::mem::size_of_val(&f));

}

// Rust 类型推断系统在初始化时可以推断值表达式类型，在能通过变量的使用来推断类型
fn type_inference(){

    let elem = 5u8;

    // 此时编译器并不知道 vec 的具体类型
    let mut vec = Vec::new();

    // push 数据之后编译器知道 vec 的类型为 Vec<u8>
    vec.push(elem);         // 注释之后会导致编译错误，因为编译器不知道 vec 的具体数据类型

    println!("{:?}", vec);
}

// Rust 使用 type 声明语句给存在的类型重新命名，类型名必须是驼峰式的，否则编译器抛出告警信息(原生类型除外)
// 别名的作用最主要是减少冗余，例如 IoResult<T> 是 Result<T, IoError> 的别名
fn type_aliasing(){

    type NanoSecond = u64;
    type Inch = u64;

    #[allow(non_camel_case_types)]
    type u64_t = u64;

    let nanoseconds: NanoSecond = 5 as u64_t;
    let inches: Inch = 2 as u64_t;

    println!("{} nanoseconds + {} inches = {} unit?", nanoseconds, inches, nanoseconds + inches);

}


#[test]
fn test_type_casting(){
    type_casting();
}

#[test]
fn test_type_literals(){
    type_literals();
}

#[test]
fn test_type_inference(){
    type_inference();
}

#[test]
fn test_type_aliasing(){
    type_aliasing();
}