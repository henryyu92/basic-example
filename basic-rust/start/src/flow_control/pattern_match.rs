// Rust 通过关键子 match 提供了模式匹配功能

fn pattern_match(){
    let number = 13;
    println!("Tell me about {}", number);
    match number{
        1 => println!("One!"),
        2 | 3 | 4 | 5 | 7 | 11 => println!("This is a prime"),
        13..=19 => println!("A teen"),
        _ => println!("Ain't special"),
    }

    let boolean = true;
    let binary = match boolean{
        false => 0,
        true => 1,
    };
    println!("{} -> {}", boolean, binary);
}

fn match_destructuring(){

    // destruct tuple
    let triple = (0, -2, 3);
    match triple{
        (0, y, z) => println!("First is `0`, `y` is {:?}, and `z` is {:?}", y, z),
        (1, ..) => println!("Fist is `1` and the rest doesn't matter"),
        _ => println!("It doesn't matter what they are"),
    }

    // destruct enum



    // destruct pointer



    // destruct struct
}


#[test]
fn test_pattern_match(){
    pattern_match();
}