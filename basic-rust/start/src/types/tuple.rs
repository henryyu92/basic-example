// 元组类型

use std::fmt::Formatter;

fn type_tuple(){

    use std::fmt;

    // 元组类型可以作为函数参数的类型和返回值的类型
    fn reverse(pair: (i32, bool)) -> (bool, i32){
        let (integer, boolean) = pair;

        (boolean, integer)
    }

    #[derive(Debug)]
    struct Matrix(f32, f32, f32, f32);

    impl fmt::Display for Matrix{
        fn fmt(&self, f: &mut Formatter) -> fmt::Result {

            write!(f, "({} {})\n", self.0, self.1)?;
            write!(f, "({} {})",self.2, self.3)
        }
    }

    // 元组中元素的数据类型可以不同
    let long_tuple = (1u8, 2u16, 3u32, 4u64,
                      -1i8, -2i16, -3i32, -4i64,
                      0.1f32, 0.2f64, 'a', true);
    // 可以通过下标索引元组中的元素
    println!("long tuple first value: {}", long_tuple.0);
    println!("long tuple second value: {}", long_tuple.1);

    // 元组也可以作为元组的元素
    let tuple_of_tuples = ((1u8, 2u16, 2u32), (4u64, -1i8), -2i16);


    let pair = (1, true);
    println!("pair is {:?}", pair);
    println!("the reversed pair is {:?}", reverse(pair));

    let matrix = Matrix(1.1, 1.2, 2.1, 2.2);
    println!("{:?}", matrix);
    println!("{}", matrix);

    fn transpose(matrix: Matrix) -> Matrix{
        let mut m = matrix;

        // todo 使用更加优雅的方式
        let a = m.2;
        m.2 = m.1;
        m.1 = a;

        m
    }
    println!("Matrix:\n{}", matrix);
    println!("Transpose:\n{}", transpose(matrix));

}


#[test]
fn test_type_tuple(){
    type_tuple()
}