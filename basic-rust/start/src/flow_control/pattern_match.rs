// Rust 通过关键子 match 提供了模式匹配功能

fn pattern_match() {
    let number = 13;
    println!("Tell me about {}", number);
    match number {
        1 => println!("One!"),
        2 | 3 | 4 | 5 | 7 | 11 => println!("This is a prime"),
        13..=19 => println!("A teen"),
        _ => println!("Ain't special"),
    }

    let boolean = true;
    let binary = match boolean {
        false => 0,
        true => 1,
    };
    println!("{} -> {}", boolean, binary);
}

// match 可以分解多种数据类型
fn match_destructuring() {

    // destruct tuple
    let triple = (0, -2, 3);
    match triple {
        (0, y, z) => println!("First is `0`, `y` is {:?}, and `z` is {:?}", y, z),
        (1, ..) => println!("Fist is `1` and the rest doesn't matter"),
        _ => println!("It doesn't matter what they are"),
    }

    // destruct enum
    #[allow(dead_code)]
    enum Color {
        Red,
        Blue,
        Green,
        RGB(u32, u32, u32),
        HSV(u32, u32, u32),
        HSL(u32, u32, u32),
        CMY(u32, u32, u32),
        CMYK(u32, u32, u32, u32),
    }
    let color = Color::RGB(122, 17, 40);
    println!("What color is it?");
    match color {
        Color::Red => println!("The color is Red!"),
        Color::Blue => println!("The color is Blue!"),
        Color::Green => println!("The color is Green!"),
        Color::RGB(r, g, b) =>
            println!("Red: {}, green: {}, and blue: {}!", r, g, b),
        Color::HSV(h, s, v) =>
            println!("Hue: {}, saturation: {}, value: {}!", h, s, v),
        Color::HSL(h, s, l) =>
            println!("Hue: {}, saturation: {}, lightness: {}!", h, s, l),
        Color::CMY(c, m, y) =>
            println!("Cyan: {}, magenta: {}, yellow: {}!", c, m, y),
        Color::CMYK(c, m, y, k) =>
            println!("Cyan: {}, magenta: {}, yellow: {}, key (black): {}!",
                     c, m, y, k),
    }

    // destruct pointer
    let reference = &4;
    match reference {
        &val => println!("Got a value via destructuring: {:?}", val),
    }
    match *reference {
        val => println!("Got a value via dereferencing: {:?}", val),
    }
    // Rust 使用 ref 关键字创建一个引用
    let ref _is_a_reference = 3;

    let value = 5;
    let mut mut_value = 6;
    match value {
        // r 是一个引用
        ref r => println!("Got a reference to a value: {:?}", r),
    }
    match mut_value {
        // m 是一个可变引用，可以改变引用的值
        ref mut m => {
            *m += 10;
            println!("We added 10. `mut_value`: {:?}", m);
        }
    }

    // destruct struct
    struct Foo {
        x: (u32, u32),
        y: u32,
    }
    let foo = Foo { x: (1, 2), y: 3 };
    match foo {
        Foo { x: (1, b), y } => println!("First of x is 1, b = {},  y = {} ", b, y),
        // 重命名变量
        Foo { y: 2, x: i } => println!("y is 2, i = {:?}", i),
        // 忽略变量
        Foo { y, .. } => println!("y = {}, we don't care about x", y),
    }
}

fn match_guard() {
    let pair = (2, -2);
    match pair {
        // 匹配且满足条件才会执行
        (x, y) if x == y => println!("These are twins"),
        (x, y) if x + y == 0 => println!("Antimatter, kaboom!"),
        (x, _) if x % 2 == 1 => println!("The first one is odd"),
        _ => println!("No correlation..."),
    }
}

// Rust 提供 @ 符号绑定变量
fn match_binding() {
    fn age() -> u32 {
        15
    }
    match age() {
        0 => println!("I haven't celebrated my first birthday yet"),
        // 满足匹配后绑定变量
        n @ 1..=12 => println!("I'm a child of age {:?}", n),
        n @ 13..=19 => println!("I'm a teen of age {:?}", n),
        n => println!("I'm an old person of age {:?}", n),
    }
}

#[test]
fn test_pattern_match() {
    pattern_match();
}

#[test]
fn test_match_destructuring() {
    match_destructuring();
}

#[test]
fn test_match_guard() {
    match_guard();
}

#[test]
fn test_match_binding(){
    match_binding();
}