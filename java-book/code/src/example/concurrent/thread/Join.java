package example.concurrent.thread;

public class Join {

  public static void main(String[] args) {

    Thread previous = Thread.currentThread();

    for (int i = 0; i < 10; i++){
      Thread thread = new Thread(new R(previous), "thread-" + i);
      thread.start();
      previous = thread;
    }
  }

  static class R implements Runnable{

    private Thread previous;

    public R(Thread previous){
      this.previous = previous;
    }

    @Override
    public void run() {
      try {
        previous.join();
        System.out.println(Thread.currentThread().getName());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
