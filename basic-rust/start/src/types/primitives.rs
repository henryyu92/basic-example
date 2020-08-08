// rust 原生数据类型
// 有符号整数： i8, i16, i32, i64, i128 和 isize(指针的大小)
// 无符号整数： u8, u16, u32, u64, u128 和 usize
// 浮点数： f32, f64
// 字符： char，每个字符占用 4 个字节
// 布尔值： true 和 false

// 复合类型
// array, tuple


use std::fmt::Formatter;

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

// 元组类型
fn type_tuple(){

    use std::fmt;

    // 元组类型可以作为函数参数的类型和返回值的类型
    fn reverse(pair: (i32, bool)) -> (bool, i32){
        let (integer, boolean) = pair;

        (boolean, integer)
    }

    #[derive(Debug)]
    struct Matrix(f32, f32, f32, f32);

    impl fmt::Display for Matrix{
        fn fmt(&self, f: &mut Formatter) -> fmt::Result {

            write!(f, "({} {})\n", self.0, self.1)?;
            write!(f, "({} {})",self.2, self.3)
        }
    }

    // 元组中元素的数据类型可以不同
    let long_tuple = (1u8, 2u16, 3u32, 4u64,
                      -1i8, -2i16, -3i32, -4i64,
                      0.1f32, 0.2f64, 'a', true);
    // 可以通过下标索引元组中的元素
    println!("long tuple first value: {}", long_tuple.0);
    println!("long tuple second value: {}", long_tuple.1);

    // 元组也可以作为元组的元素
    let tuple_of_tuples = ((1u8, 2u16, 2u32), (4u64, -1i8), -2i16);


    let pair = (1, true);
    println!("pair is {:?}", pair);
    println!("the reversed pair is {:?}", reverse(pair));

    let matrix = Matrix(1.1, 1.2, 2.1, 2.2);
    println!("{:?}", matrix);
    println!("{}", matrix);

    fn transpose(matrix: Matrix) -> Matrix{
        let mut m = matrix;

        // todo 使用更加优雅的方式
        let a = m.2;
        m.2 = m.1;
        m.1 = a;

        m
    }
    println!("Matrix:\n{}", matrix);
    println!("Transpose:\n{}", transpose(matrix));

}


#[test]
fn test_primitive_type(){
    primitive_type()
}

#[test]
fn test_type_literals(){
    type_literals()
}

#[test]
fn test_type_tuple(){
    type_tuple()
}