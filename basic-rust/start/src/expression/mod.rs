// Rust 程序大部分是由一系列的语句构成

fn expression(){

    // Rust 中最常见的语句就是变量绑定和以 ; 结尾的表达式
    let x = 5;     // 变量绑定

    x + 1;      // 以 ; 结尾的表达式

    // {} 也是表达式，因此有返回值可以赋值给变量
    let y = {
        let x_squared= x * x;
        let x_cube = x_squared * x;
        // 块的最后一个表达式作为返回值，如果最后一个表达式以 ; 结尾则返回 ()
        x_cube + x_squared + x
    };

    let z = {

        2 * x;
    };

    println!("x:{:?}, y:{:?}, z:{:?}", x, y, z)

}

#[test]
fn test_expression(){
    expression();
}