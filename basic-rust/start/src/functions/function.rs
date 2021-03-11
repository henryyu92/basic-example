// Rust 使用 fn 关键字声明 Function，参数需要指定类型，如果有返回直则需要使用标记 -> 指定返回值的类型

// Function 的最后一个表达式结果作为 Function 的返回值，也可以使用 return 关键字显式指定返回值

fn functions(){
    fizzbuzz_to(100)
}

fn fizzbuzz(n: u32) -> () {
    if is_divisible_by(n, 15) {
        println!("fizzbuzz");
    } else if is_divisible_by(n, 3) {
        println!("fizz");
    } else if is_divisible_by(n, 5) {
        println!("buzz");
    } else {
        println!("{}", n);
    }
}

fn fizzbuzz_to(n: u32) {
    for n in 1..n + 1 {
        fizzbuzz(n);
    }
}

fn is_divisible_by(lhs: u32, rhs: u32) -> bool {
    // Corner case, early return
    if rhs == 0 {
        return false;
    }

    // This is an expression, the `return` keyword is not necessary here
    lhs % rhs == 0
}

#[test]
fn test_functions(){
    functions();
}