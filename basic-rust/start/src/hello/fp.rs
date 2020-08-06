use std::fmt::Formatter;
use std::path::Display;

// format!  将格式化的文本转成字符串
// print!   在 format! 的基础上打印到控制台
// println! 在 print! 的基础上打印换行
pub fn format_print(){

    // {} 在格式化时会自动填充，复杂数据类型不生效
    println!("{} days", 31);

    // 指定填充的内容
    println!("{0}, this is {1}. {1}, this is {0}", "Alice", "Bob");

    println!("{subject} {verb} {object}",
             object="the lazy dog",
             subject="the quick brown fox",
             verb="jumps over");

    // 指定格式
    println!("{} of {:b} people know binary, the other half doesn't", 1, 2);

    println!("{number:>width$}", number="你", width=6);    // 向又靠齐，指定总字符数
    println!("{number:>0width$}", number=1, width=6);   // 向右靠齐，使用 0 填充空格
}

// 自定义类型不能直接使用 print 进行打印，需要通过 debug 或者 display 方式打印

// debug 使用 {:?} 的形式
pub fn fmt_debug(){

    #[derive(Debug)]
    struct Structure(i32);

    #[derive(Debug)]
    struct Deep(Structure);

    println!("{:?} moths is a year", 12);
    println!("{1:?} {0:?} is the {actor:?} name.",
             "Slater",
             "Christian",
             actor="actor's");

    println!("Now {:?} will print!", Structure(3));

    println!("Now {:?} will print!", Deep(Structure(7)));


    #[derive(Debug)]
    struct Person <'a>{
        name: &'a str,
        age: u8
    }
    let name = "Peter";
    let age = 27;
    let peter = Person { name, age };

    // Pretty print
    println!("{:#?}", peter);

}

// display 使用 {} 形式
fn fmt_display(){
    use std::fmt;

    struct Structure(i32);

    // display 方式需要实现 fmt::Display，自定义打印格式
    impl fmt::Display for Structure {
        fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
            write!(f, "{}", self.0)
        }
    }

    #[derive(Debug)]
    struct MinMax(i64, i64);

    impl fmt::Display for MinMax {
        fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
            // 使用下标方式指定
            write!(f, "{}, {}", self.0, self.1)
        }
    }

    #[derive(Debug)]
    struct Point2D{
        x: f64,
        y: f64
    }
    impl fmt::Display for Point2D{
        fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
            // 直接使用变量指定
            write!(f, "{}, {}", self.x, self.y)
        }
    }

    // print
    let minmax = MinMax(0, 14);
    println!("Compare structures:");
    println!("Display: {}", minmax);
    println!("Debug: {:?}", minmax);

    let big_range =   MinMax(-300, 300);
    let small_range = MinMax(-3, 3);
    println!("The big range is {big} and the small is {small}",
             small = small_range,
             big = big_range);

    let point = Point2D { x: 3.3, y: 7.2 };
    println!("Compare points:");
    println!("Display: {}", point);
    println!("Debug: {:?}", point);

    #[derive(Debug)]
    struct Complex{
        real: f64,
        image: f64
    }
    impl fmt::Display for Complex{
        fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
            write!(f, "{} + {}", self.real, self.image)
        }
    }

    let complex = Complex{real:3.3, image:7.2};
    println!("Display: {}", complex);
    println!("Debug: {:?}", complex);

}

mod test{
    use crate::hello::fp::{fmt_debug, fmt_display, format_print};

    #[test]
    fn test_format_print(){
        format_print()
    }

    #[test]
    fn test_fmt_debug(){
        fmt_debug()
    }

    #[test]
    fn test_fmt_display(){
        fmt_display()
    }
}