use super::{Error, PathBuf, File, Read, Write};

/**
 *  Rust 将文件路径抽象为 Path 和 PathBuf 两种类型，Path 没有所有权，而 PathBuf 有独立的所有权
 *
 *  std::fs 模块定义了操作本地文件系统的方法，这些方法都可以跨平台
 *
 *  std::io 模块中定义了核心 I/O 功能，包括 Read, Write, Seek, BufRead 四个 trait
 *
*/

pub fn load_csv(csv_file: PathBuf) -> Result<String, Error> {
    let file = read(csv_file)?;
    Ok(file)
}

pub fn write_csv(csv_data: &str, filename: &str) -> Result<(), Error>{
    write(csv_data, filename)?;
    Ok(())
}

fn read(path: PathBuf) -> Result<String, Error>{
    let mut buffer = String::new();
    let mut file = open(path)?;
    file.read_to_string(&mut buffer)?;
    if buffer.is_empty() {
        return Err("input file missing")?
    }
    Ok(buffer)
}

fn open(path: PathBuf) -> Result<File, Error>{
    let file = File::open(path)?;
    Ok(file)
}

fn write(data: &str, filename: &str) -> Result<(), Error>{
    let mut buffer = File::create(filename)?;
    buffer.write_all(data.as_bytes())?;
    Ok(())
}


/**
*   单元测试
*/
#[cfg(test)]
mod test{
    use std::path::PathBuf;
    use super::load_csv;

    #[test]
    fn test_valid_load_csv(){
        let filename = PathBuf::from("./input/challenge.csv");
        let csv_data = load_csv(filename);
        assert!(csv_data.is_ok());
    }
}