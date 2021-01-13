// Rust 提供 loop 关键字表示无限循环
// break 关键字可以跳出循环，continue 关键字可以跳过当前循环执行下一次的循环
#![allow(unreachable_code)]

fn loop_control() {
    let mut count = 0u32;

    println!("Let's count until infinity!");

    loop {
        count += 1;

        if count == 3 {
            println!("three");
            continue;
        }

        println!("{}", count);

        if count == 5 {
            println!("OK, that's enough");
            break;
        }
    }
}

// 在 loop 嵌套的情况下，使用 'label 可以 break 或者 continue 外部的 loop 循环
fn label_loop() {
    'outer: loop {
        println!("Enter the outer loop");

        'inner: loop {
            println!("Enter the inner loop");

            // break inner loop
            // break;

            // break outer loop
            break 'outer
        }

        println!("This point will never be reached");
    }

    println!("Exited the outer loop");
}

// loop 也是表达式，因此可以有返回值
fn return_from_loop(){

    let mut counter = 0;

    let result = loop {
        counter += 1;
        if counter == 10 {
            break counter * 2
        }
    };

    assert_eq!(result, 20)
}

#[test]
fn test_loop_control() {
    loop_control();
}

#[test]
fn test_label_loop(){
    label_loop()
}

#[test]
fn test_return_from_loop(){
    return_from_loop()
}