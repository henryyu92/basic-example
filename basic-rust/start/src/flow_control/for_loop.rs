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

    // into_iter 迭代会获取集合的所有权，循环后不可重用
    let names_into = vec!["Bob", "Frank", "Ferris"];
    for name in names_into.into_iter(){
        match name {
            "Ferris" => println!("There is a rustacean among us!"),
            _ => println!("Hello {}", name),
        }
    }

    // iter_mut 可变借用可以改变集合中元素的值
    let mut names_mut = vec!["Bob", "Frank", "Ferris"];
    for name in names_mut.iter_mut() {
        *name = match name {
            &mut "Ferris" => "There is a rustacean among us!",
            _ => "Hello",
        }
    }
    println!("names: {:?}", names_mut)
}

#[test]
fn test_for_range(){
    for_range();
}

#[test]
fn test_for_iterator(){
    for_iterator();
}