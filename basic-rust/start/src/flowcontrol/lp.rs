// Rust 提供 loop 关键字表示无限循环
// break 关键字可以跳出循环，continue 关键字可以跳过当前循环执行下一次的循环

fn loop_control(){
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

#[test]
fn test_loop_control(){
    loop_control();
}