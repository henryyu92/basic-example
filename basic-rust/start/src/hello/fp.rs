
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

mod test{
    use crate::hello::fp::format_print;

    #[test]
    fn test_format_print(){
        format_print()
    }
}