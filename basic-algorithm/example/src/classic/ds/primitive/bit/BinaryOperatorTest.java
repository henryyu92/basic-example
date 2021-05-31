package classic.ds.primitive.bit;

/**
 * 二进制操作
 */
public class BinaryOperatorTest {

    private final int N;
    private final BinaryOperator operator;

    public static BinaryOperatorTest init(int N){
        return new BinaryOperatorTest(N);
    }

    private BinaryOperatorTest(int N){
        this.N = N;
        operator = new BinaryOperator();
    }

    public void testSwapBit(){

        for (int i = 0; i < N; i++){

            int number = (int) (Math.random() * 256);
            int bit_x = (int) (Math.random() * 8);
            int bit_y = (int) (Math.random() * 8);
            int swap = operator.swapBit(number, bit_x, bit_y);
            assert number == operator.swapBit(swap, bit_x, bit_y);

            System.out.println("before " + number + ", swap " + bit_x + " and " + bit_y + ": " + swap);
        }
        System.out.println("test success.");
    }

    public void testSwapInt(){

        for (int i = 0; i < N; i++){

            int a = (int) (Math.random() * Integer.MAX_VALUE + Math.random() * Integer.MIN_VALUE);
            int b = (int) (Math.random() * Integer.MIN_VALUE + Math.random() * Integer.MIN_VALUE);

//            int swap = operator.swapInt(a, b);

        }
    }

    public static void main(String[] args) {

        BinaryOperatorTest test = init(100);

        test.testSwapBit();

    }




}
