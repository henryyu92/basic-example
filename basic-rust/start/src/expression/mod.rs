// Rust 程序基本上是由一系列的语句构成，语句最常见的就是 变量绑定 和 表达式+;

fn expression(){

    let x = 5;     // 变量绑定

    x + 1;      // 表达式+;

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