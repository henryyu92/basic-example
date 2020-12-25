// Rust 中 if-else 的布尔表达式不需要使用 ()，此外 if-else 是表达式，因此 if 和 else 部分返回的值必须是相同的类型

fn if_else_control() {
    let n = 5;

    if n < 0 {
        print!("{} is negative", n);
    } else if n > 0 {
        print!("{} is positive", n);
    } else {
        print!("{} is zero", n);
    }

    let big_n = if n < 10 && n > -10 {
        println!(", and is a small number, increase ten-fold");
        10 * n
    } else {
        println!(", and is a big number, halve the number");
        n / 2
    };

    println!("{} -> {}", n, big_n);
}

#[test]
fn test_if_else_control(){
    if_else_control();
}