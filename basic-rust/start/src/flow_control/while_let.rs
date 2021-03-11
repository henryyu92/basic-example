// while let 语法可以简化循环中使用 match


fn while_let(){

    let mut optional = Some(0);

    // 当能够解构时执行代码块，否则就 break 循环
    while let Some(i) = optional {
        if i > 9{
            println!("Greater then 9, quit!");
            optional = None
        } else {
            println!("`i` is `{:?}`. Try again.", i);
            optional = Some(i+1);
        }
    }
}

#[test]
fn test_while_let(){
    while_let()
}