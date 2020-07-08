package example.bit;

import java.util.ArrayList;

/**
 * 二进制操作
 */
public class BinaryOperator {

    /**
     * 交换 x 的第 i 位和第 j 位
     *
     * @param x
     * @param i
     * @param j
     * @return
     */
    public int swap_bit(int x, int i, int j) {

        // bit 位上不同才需要交换
        if (((x >> i) & 1) != ((x >> j) & 1)) {
            x ^= (1 << i) | (1 << j);
        }

        return x;
    }

    /**
     * 反转 x 的二进制
     *
     * @param x
     * @return
     */
    public int reverse_bit(int x) {
        // 二进制反转只需要遍历交换即可
        int i = Integer.SIZE, j = 0;
        while (i > j) {
            swap_bit(x, i, j);
            i--;
            j++;
        }
        return x;
    }

    /**
     * 交换整数
     *
     * @param a
     * @param b
     */
    public void swap_int(int a, int b) {

        // a 和 b 不同的位
        a ^= b;
        // b 和 a、b 不同的位异或得到的就是 a
        b ^= a;
        // a 和 a、b 不同的位异或得到的就是 b
        a ^= b;

    }

    /**
     * 使用位运算求余
     * @param a
     * @param b
     * @return
     */
    public int module(int a, int b){
        ArrayList<Integer> T = new ArrayList<>();
        int k = 0;
        while ((b << k) <= a){
            k++;
        }
        T.add(k--);

        int a_prime = a - (b << T.get(T.size() - 1));

        do{
            while ((b<<k) > a_prime){
                k--;
            }
            T.add(k);
            a_prime = a_prime - (b<<T.get(T.size() - 1));
        }while (a_prime >= b);

        int d = 0;
        for (int i = 0; i < T.size(); i++){
            a -= (b << T.get(i));
        }
        return a;
    }


}
