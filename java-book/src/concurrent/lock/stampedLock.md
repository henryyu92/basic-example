## StampedLock

`StampedLock` 是 `ReentrantReadWriteLock` 的增强，采用乐观锁的机制，使得读的过程中也可以写入数据。`StampedLock` 提供了三种锁的模式：独占的写锁、独占的读锁 和 共享的读锁。

```java
public class Point {
    
    private final StampedLock lock = new StampedLock();
    
    private double x;
    private double y;
    
    public void move(double deltaX, double deltaY){
        // 获取写锁
        long stamp = lock.writeLock();
        try{
            x += deltaX;
            y += deltaY;
        }finally {
            // 释放写锁
            lock.unlockWrite(stamp);
        }
    }
    
    public double distanceFormOrigin(){
        // 乐观读锁，获取版本号
        long stamp = lock.tryOptimisticRead();
        double currentX = x;
        double currentY = y;
        // 验证版本号(有其他写锁改变过数据则版本号发生变化)
        if (!lock.validate(stamp)){
            // 悲观读锁
            stamp = lock.readLock();
            try{
                currentX = x;
                currentY = y;
            }finally {
                // 释放悲观写锁
                lock.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * crrentX + currentY * currentY);
    }
    
}
```

### 实现原理

`StampedLock` 将同步状态变量分成两部分，其中低 7 位表示读锁，第 8 位表示写锁，因此 **`StampedLock` 的写锁是不可重入的**。