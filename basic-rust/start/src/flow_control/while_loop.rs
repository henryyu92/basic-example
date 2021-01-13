// while 关键字在条件为 true 时执行循环语句

fn while_loop() {
    let mut n = 1;

    while n <= 100 {
        if n % 15 == 0 {
            println!("fizzbuzz");
        }else if n % 3 == 0 {
            println!("fizz");
        }else if n % 5 == 0{
            println!("buzz");
        }else{
            println!("{}", n);
        }
        n += 1;
    }
}

#[test]
fn test_while_loop(){
    while_loop()
}