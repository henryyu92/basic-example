package example.classic.ds.bit;

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

        int number = (int) (Math.random() * 256);
        int bit_x = (int) (Math.random() * 8);
        int bit_y = (int) (Math.random() * 8);

        for (int i = 0; i < N; i++){
            int swap = operator.swapBit(number, bit_x, bit_y);
            assert number == operator.swapBit(swap, bit_x, bit_y);
        }
        System.out.println("test success.");
    }

    public static void main(String[] args) {

        BinaryOperatorTest test = init(100);

        test.testSwapBit();

    }




}
