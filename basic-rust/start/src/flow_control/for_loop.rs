// Rust `for in` 结构可以用于迭代 `Iterator`
// 使用 range 可以快速创建一个 `Iterator`


// .. 表示左包含，..= 表示左右包含
fn for_range() {
    // 每次循环取出 range 中的一个值
    // for n in 1..=100 {
    for n in 1..101 {
        if n % 15 == 0 {
            println!("fizzbuzz");
        } else if n % 3 == 0 {
            println!("fizz");
        } else if n % 5 == 0 {
            println!("buzz");
        } else {
            println!("{}", n);
        }
    }
}

fn for_iterator() {

    // iter 迭代的时候会借用集合的元素，从而时集合保持不变，并可在循环后重用
    let names = vec!["Bob", "Frank", "Ferris"];
    for name in names.iter(){
        match name {
            &"Ferris" => println!("There is a rustacean among us!"),
            _ => println!("Hello {}", name),
        }
    }

    // into_iter
    let names_into = vec!["Bob", "Frank", "Ferris"];

}
