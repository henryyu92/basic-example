// enum 关键字用于创建枚举类型，任意类型都可以作为有效的枚举

enum WebEvent {
    PageLoad,
    PageUnload,
    KeyPress(char),
    Paste(String),
    Click { x: i64, y: i64 },
}

fn inspect(event: WebEvent) {
    match event {
        WebEvent::PageLoad => println!("page loaded."),
        WebEvent::PageUnload => println!("page unloaded."),
        WebEvent::KeyPress(c) => println!("pressed '{}'", c),
        WebEvent::Paste(s) => println!("pasted \"{}\"", s),
        WebEvent::Click { x, y } => {
            println!("clicked at x ={}, y={}", x, y);
        }
    }
}

// 类型别名
#[allow(dead_code, unused_variables)]
fn type_aliases() {
    enum VeryVerboseEnumOfThingsToDoWithNumbers {
        Add,
        Subtract,
    }

    // 创建类型别名
    type Operations = VeryVerboseEnumOfThingsToDoWithNumbers;
    // 使用类型别名可以引用枚举值
    let x = Operations::Add;

    // 实现自动创建别名 Self
    impl VeryVerboseEnumOfThingsToDoWithNumbers {
        fn run(&self, x: i32, y: i32) -> i32 {
            match self {
                Self::Add => x + y,
                Self::Subtract => x - y,
            }
        }
    }
}

#[derive(Debug)]
enum Status {
    Rich,
    Poor,
}

#[allow(dead_code)]
enum Work {
    Civilian,
    Soldier,
}

// use 声明
fn use_declare() {

    // 隐藏 unused code 的警告
    #![allow(dead_code)]

    let before = Status::Poor;
    // use 显式声明后就不需要手动指定
    use crate::types::enums::Status::{Poor, Rich};
    let after = Poor;
    println!("before use declare: {:?}, after: {:?}", before, after);
    use crate::types::enums::Work::*;

    let status = Poor;
    let work = Civilian;

    match status {
        Rich => println!("The rich have lots of money!"),
        Poor => println!("The poor have no money..."),
    }

    match work {
        Civilian => println!("Civilians work!"),
        Soldier => println!("Soldiers fight!"),
    }
}


fn c_like() {
    #![allow(dead_code)]

    enum Number {
        Zero,
        One,
        Two,
    }

    enum Color {
        Red = 0xff0000,
        Green = 0x00ff00,
        Blue = 0x0000ff,
    }

    println!("zero is {}", Number::Zero as i32);
    println!("one is {}", Number::One as i32);

    println!("roses are #{:06x}", Color::Red as i32);
    println!("violets are #{:06x}", Color::Blue as i32);
}

// A common use for enums is to create a linked-list
fn linked_list() {
    enum List {
        // 包含当前节点元素以及下个节点的指针的 tuple
        Cons(u32, Box<List>),
        // linked list 的结束标识
        Nil,
    }
    impl List {
        // 创建一个空 List
        fn new() -> List {
            List::Nil
        }
        // 在 linked list 的头插入元素
        fn prepend(self, elem: u32) -> List {
            List::Cons(elem, Box::new(self))
        }

        fn len(&self) -> u32 {
            // self 是 &List 类型， *self 是 List 类型
            // 匹配具体的类型优先与引用类型的匹配
            match *self {
                List::Cons(_, ref tail) => 1 + tail.len(),
                List::Nil => 0,
            }
        }

        fn stringify(&self) -> String {
            match *self {
                List::Cons(head, ref tail) => format!("{}, {}", head, tail.stringify()),
                List::Nil => {
                    format!("Nil")
                }
            }
        }
    }

    let mut list = List::new();
    list = list.prepend(1);
    list = list.prepend(2);
    list = list.prepend(3);

    println!("linked list has length: {}", list.len());
    println!("{}", list.stringify())
}

#[test]
fn test_inspect() {
    let pressed = WebEvent::KeyPress('x');
    // `to_owned()` creates an owned `String` from a string slice.
    let pasted = WebEvent::Paste("my text".to_owned());
    let click = WebEvent::Click { x: 20, y: 80 };
    let load = WebEvent::PageLoad;
    let unload = WebEvent::PageUnload;

    inspect(pressed);
    inspect(pasted);
    inspect(click);
    inspect(load);
    inspect(unload);
}

#[test]
fn test_use_declare() {
    use_declare();
}

#[test]
fn test_c_like() {
    c_like();
}

#[test]
fn test_linked_list() {
    linked_list();
}