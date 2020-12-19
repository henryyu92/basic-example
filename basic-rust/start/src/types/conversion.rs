// 基础数据类型可以隐式或者通过 as 关键字显式的转换
// 其他数据类型如 struct 和 enum 则需要通过 From 和 Into trait 来进行类型转换
// Rust 也提供了将特定类型转换为 String 或者从 String 转换到特定类型的方法

use std::convert::{TryFrom, TryInto};
use std::fmt;

#[derive(Debug)]
struct Number {
    value: i32
}

impl From<i32> for Number {
    fn from(item: i32) -> Self {
        Number { value: item }
    }
}

impl Into<i32> for Number {
    fn into(self) -> i32 {
        self.value
    }
}

// From trait 允许一个类型定义如何从另一个类型创建自己
fn conversion_from() {
    let num = Number::from(30);
    println!("number is {:?}", num);
}

// Into trait 是 From trait 的逆，使用 Into trait 需要指定转换为的类型
fn conversion_into() {
    let num = Number { value: 5 };
    // 需要指定转换为的类型
    let i: i32 = num.into();
    println!("number is {:?}", i);
}

#[derive(Debug, PartialEq)]
struct EventNumber(i32);

impl TryFrom<i32> for EventNumber {
    type Error = ();

    fn try_from(value: i32) -> Result<Self, Self::Error> {
        if value % 2 == 0 {
            Ok(EventNumber(value))
        } else {
            Err(())
        }
    }
}

// tryFrom 和 tryInto 返回 Result
fn conversion_try_from_try_into() {

    // TryFrom
    assert_eq!(EventNumber::try_from(8), Ok(EventNumber(8)));
    assert_eq!(EventNumber::try_from(5), Err(()));

    // TryInto
    let result: Result<EventNumber, ()> = 8i32.try_into();
    assert_eq!(result, Ok(EventNumber(8)));
    let result: Result<EventNumber, ()> = 5i32.try_into();
    assert_eq!(result, Err(()));
}

// 实现 ToString trait 可以将任意类型转换为 String，也可以通过实现 fmt::Display trait 将任意类型转换为 String，其会自动提供 ToString 的实现
struct Circle {
    radius: i32
}

impl fmt::Display for Circle {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "Circle of radius {}", self.radius)
    }
}

fn conversion_to_string(){
    let circle = Circle{radius:6};
    println!("{}", circle.to_string());
}


#[test]
fn test_conversion_from() {
    conversion_from();
}

#[test]
fn test_conversion_into() {
    conversion_into();
}

#[test]
fn test_conversion_try_from_try_into() {
    conversion_try_from_try_into();
}

#[test]
fn test_conversion_to_string(){
    conversion_to_string();
}