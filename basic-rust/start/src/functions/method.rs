// Method 是绑定到对象的 Function，通过 self 关键字，Method 可以访问对象的其他数据和方法

// Method 需要在 impl 代码块中定义

struct Point {
    x: f64,
    y: f64,
}

impl Point {
    // static method，不需要通过实例调用，作为构造器使用
    fn origin() -> Point {
        Point { x: 0.0, y: 0.0 }
    }

    fn new(x: f64, y: f64) -> Point {
        Point { x, y }
    }
}

struct Rectangle {
    p1: Point,
    p2: Point,
}

impl Rectangle {
    // instance method，需要通过实例调用
    // &self 是 self:&Self 的语法糖，Self 表示实例的类型，此处为 Rectangle
    fn area(&self) -> f64 {
        // 通过 self 关键字可以获取实例的数据
        let Point { x: x1, y: y1 } = self.p1;
        let Point { x: x2, y: y2 } = self.p2;
        ((x1 - x2) * (y1 - y2)).abs()
    }

    fn perimeter(&self) -> f64 {
        let Point { x: x1, y: y1 } = self.p1;
        let Point { x: x2, y: y2 } = self.p2;
        2.0 * ((x1 - x2).abs() + (y1 - y2).abs())
    }
}

struct Pair(Box<i32>, Box<i32>);

fn test_method() {
    let rectangle = Rectangle {
        // Static method 通过 :: 的方式调用
        p1: Point::origin(),
        p2: Point::new(3.0, 4.0),
    };
    // Instance method 通过 . 的方式调用
    println!("Rectangle perimeter: {}", rectangle.perimeter());
    println!("Rectangle area: {}", rectangle.area());

    let mut square = Rectangle{
        p1: Point::origin(),
        p2: Point::new(1.0, 1.0),
    };
}