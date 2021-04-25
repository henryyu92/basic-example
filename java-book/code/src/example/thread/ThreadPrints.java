package example.thread;

/**
 * 多线程交替打印
 */
public class ThreadPrints {


    class waitNotify {

        private int flag;
        private int loopNum;

        public void print(String content, int flag, int nextFlag){

            for (int i = 0; i < loopNum; i++){
                synchronized (this){
                    // 条件不满足则 wait
                    while (this.flag != flag){
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 满足条件则执行
                    System.out.print(content);
                    // 改变条件
                    this.flag = nextFlag;
                    // 通知其他线程
                    this.notifyAll();
                }
            }
        }

    }
}
