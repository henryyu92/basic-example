mod fp;

pub fn print_hello(){
    println!("Hello, world!");
}

pub fn comment(){
    let x  = 5 + /* + 90 + */ 5;
    println!("Is `x` 10 or 100? x = {}", x)
}

mod test{
    use crate::hello::{comment, print_hello};

    #[test]
    fn test_print_hello(){
        print_hello()
    }

    #[test]
    fn test_comment(){
        comment()
    }
}