//! structure 使用 struct 关键字创建，structure 包含三种类型：
//! - Tuple structure
//! - C structure
//! - Unit structure


// C structure
#[derive(Debug)]
struct Person<'a>{

    // ‘a 定义了生命周期
    name: &'a str,

    age: u8,
}

// Unit structure，不包含任何字段
struct Unit;

// Tuple structure
struct Pair(i32, f32);

struct Point{
    x: f32,
    y: f32,
}
// 结构体可以作为字段
struct Rectangle{
    top_left: Point,
    bottom_right: Point,
}

fn type_structure(){

    // 使用变量作为字段创建结构体
    let name = "Peter";
    let age = 27;
    let peter = Person{name, age};

    println!("{:?}", peter);

    // 初始化结构体
    let point: Point = Point{x:10.3, y:0.4};
    // 访问结构体成员
    println!("point coordinates: ({}, {})", point.x, point.y);

    // 使用更新结构体的语法创建一个新的结构体
    let bottom_right = Point{x:5.2, ..point};
    println!("second point: ({}, {})", bottom_right.x, bottom_right.y);   // bottom_right.y 和 point.y 相同，因为 bottom_right.y 由 point 而来

    // 分解结构体
    let Point{x: top_edge, y: left_edge} = point;
    println!("point coordinates: ({}, {})", top_edge, left_edge);

    let _rectangle = Rectangle{
        top_left: Point{x:top_edge, y:left_edge},
        bottom_right,
    };

    // 初始化 Unit Structure
    let _unit = Unit;

    // 初始化 Tuple Structure
    let pair = Pair(1, 0.1);
    println!("pair contains {:?} and {:?}", pair.0, pair.1);
    // Tuple structure 分解
    let Pair(integer, decimal) = pair;
    println!("Pair contains {:?} and {:?}", integer, decimal);
}

// using nested destructuring
fn rect_area(rect: Rectangle){
    let Rectangle{
        top_left: Point{x:tf_x, y:tf_y},
        bottom_right: Point{x:br_x, y:br_y},
    } = rect;
    let width = br_x - tf_x;
    let height = tf_y - br_y;
    println!("area of rectangle: {}", width * height);
}

// return value
fn square(p: Point, len: f32) -> Rectangle {
    let bottom_right = Point{
        x: p.x + len,
        y: p.y - len,
    };
    return Rectangle{top_left:p, bottom_right};
}


#[test]
fn test_type_structure(){
    type_structure();
}

#[test]
fn test_rect_area(){
    let rect = Rectangle{
        top_left: Point{x:3.3, y:10.4},
        bottom_right: Point{x:13.3, y:0.4},
    };
    rect_area(rect);
}

#[test]
fn test_square(){
    let p = Point{
        x: 3.0,
        y:4.0,
    };
    let rect = square(p, 2.0);
    println!("Rectangle.top_left: ({}, {}), Rectangle.bottom_right: ({}, {})",
             rect.top_left.x, rect.top_left.y,
             rect.bottom_right.x, rect.bottom_right.y);
}