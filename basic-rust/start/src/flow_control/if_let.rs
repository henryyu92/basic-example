// Rust 提供 if let 语法简化 match

fn if_let() {
    let number = Some(7);
    let letter: Option<i32> = None;
    let emoticon: Option<i32> = None;

    // 如果 number 能被 Some(i) 解构则执行
    if let Some(i) = number {
        println!("Matched {:?}!", i)
    }

    // 如果不能解构，则执行 else
    if let Some(i) = letter {
        println!("Matched {:?}!", i)
    } else {
        println!("Didn't match a number. Let's go with a letter !");
    }

    let i_like_letters = false;
    if let Some(i) = emoticon {
        println!("Matched {:?}!", i)
    } else if i_like_letters {
        println!("Didn't match a number. Let's go with a letter !");
    } else {
        println!("I don't like letters. Let's go with an emoticon :)!");
    }
}

fn match_any() {
    enum Foo {
        Bar,
        Baz,
        Qux(u32),
    }

    let a = Foo::Bar;
    let b = Foo::Bar;
    let c = Foo::Qux(100);

    if let Foo::Bar = a {
        println!("a is a foobar!");
    }
    // 不能解构则不会执行
    if let Foo::Bar = b {
        println!("b is a foobar!");
    }

    if let Foo::Qux(value) = c {
        println!("c is {}", value)
    }
    // 变量绑定
    if let Foo::Qux(value @ 100) = c {
        println!("c is one hundred");
    }
}