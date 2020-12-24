// Rust 是类型安全的，变量绑定可以在声明的时候指定，大多数情况下编译器会推断出变量类型

// 字面量可以使用 let 直接绑定到变量
fn literals_bind(){

    let an_integer = 1u32;
    let a_boolean = true;
    let unit = ();

    let copied_integer = an_integer;

    println!("An integer: {:?}", copied_integer);
    println!("A boolean: {:?}", a_boolean);
    println!("Meet the unit value: {:?}", unit);

    // 编译器对没有使用到的变量提示警告，使用 _ 可以屏蔽警告
    let _unused_variable = 3u32;
}

// 默认情况下变量是不可变的，使用 mut 修饰符可以声明变量可变
fn mutability_bind(){

    let _immutable_binding = 1;
    let mut mutable_binding = 1;

    println!("Before mutation: {}", mutable_binding);

    // Ok
    mutable_binding += 1;

    println!("After mutation: {}", mutable_binding);

    // _immutable_binding += 1;     // 不可变变量不能修改值
}

// 变量绑定的作用域在 {} 中，变量绑定可以覆盖
fn scope_bind(){

    let long_lived_binding = 1;

    {
        let short_lived_binding = 2;
        println!("inner short: {}", short_lived_binding);

        // 覆盖前面的变量绑定
        let long_lived_binding = 5_f32;
        println!("inner long: {}", long_lived_binding);
    }

    // println!("outer short: {}", short_lived_binding);    // 超出作用域范围导致编译错误
    println!("outer long: {}", long_lived_binding);

    // 覆盖变量绑定
    let long_lived_binding = 'a';
    println!("outer long: {}", long_lived_binding);
}


// 变量绑定可以先声明后初始化，但是这样并不安全，会有可能访问未初始化的变量
fn declare_bind(){

    // 声明变量但是没有初始化
    let a_binding;

    {
        let x = 2;
        a_binding = x * x;
    }

    println!("a binding: {}", a_binding);

    let another_binding;
    // println!("another binding: {}", another_binding);    // 访问未初始化变量到编译错误
    another_binding = 1;
    println!("another binding: {}", another_binding);
}

// 当变量被同名的不可变量绑定时就会冻结，直到在作用域范围外才能修改值
fn freezing_bind(){

    let mut _mutable_integer = 7i32;
    {
        // _mutable_integer 在此作用域内不可变
        let _mutable_integer = _mutable_integer;

        // _mutable_integer = 50;   // 不可变量赋值导致编译错误
    }

    // 次作用域内 _mutable_integer 可变
    _mutable_integer = 50;
}


#[test]
fn test_literals_bind(){
    literals_bind();
}

#[test]
fn test_mutability_bind(){
    mutability_bind();
}

#[test]
fn test_scope_bind(){
    scope_bind();
}

#[test]
fn test_declare_bind(){
    declare_bind();
}