package jvm;

/**
 * @author Administrator
 * @date 2019/9/27
 */
public class StaticInit {

    public static void main(String[] args) {
        new Sub();
    }

  static class Parent{
      static int i = 0;
      static {
          System.out.println("static in parent");
      }
      public Parent(){
          System.out.println(i);
      }
  }

  static class Sub extends Parent{
      static int i = 1;
      static {
          System.out.println("static in sub");
      }
      public Sub(){
          System.out.println(i);
      }
  }
}
