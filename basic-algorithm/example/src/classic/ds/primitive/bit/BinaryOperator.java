package classic.ds.primitive.bit;

import java.util.ArrayList;

/**
 * 二进制操作
 */
public class BinaryOperator {

    /**
     * 交换 x 的第 i 位和第 j 位
     */
    public int swapBit(int x, int i, int j) {

        // bit 位上不同才需要交换
        if (((x >> i) & 1) != ((x >> j) & 1)) {
            // 与仅 i 和 j 位为 1 的数做异或运算
            x ^= (1 << i) | (1 << j);
        }

        return x;
    }

    /**
     * 反转 x 的二进制
     */
    public int reverseBit(int x) {
        // 二进制反转只需要遍历二进制位然后交换即可
        int i = Integer.SIZE, j = 0;
        while (i > j) {
            swapBit(x, i, j);
            i--;
            j++;
        }
        return x;
    }

    /**
     * 交换整数
     */
    public void swapInt(int a, int b) {

        // a 和 b 不同的位
        a ^= b;
        // b 和 a、b 不同的位异或得到的就是 a
        b ^= a;
        // a 和 a、b 不同的位异或得到的就是 b
        a ^= b;

    }

    /**
     * 使用位运算求余
     */
    public int module(int a, int b) {
        ArrayList<Integer> T = new ArrayList<>();
        int k = 0;
        while ((b << k) <= a) {
            k++;
        }
        T.add(k--);

        int a_prime = a - (b << T.get(T.size() - 1));

        do {
            while ((b << k) > a_prime) {
                k--;
            }
            T.add(k);
            a_prime = a_prime - (b << T.get(T.size() - 1));
        } while (a_prime >= b);

        int d = 0;
        for (int i = 0; i < T.size(); i++) {
            a -= (b << T.get(i));
        }
        return a;
    }

    /**
     * 给定无符号整数 x，其二进制表示中 1 的个数为 k，求无符号整数 y 使得其二进制表示中 1 的个数为 k 且 |x-y| 的值最小
     * <p>
     * 证明：x 和 y 具有相同的 1 的个数，如果要使的 |x-y| 最小则需要满足两个条件：
     * - x 和 y 在二进制位上的不同越少越好，因为假设多个不同位 i>j>k>l 且 x 的 i,l 为 1，y 的 j,k 为 1，则差的绝对值为 |2^i + 2^l -2^j - 2^k|， 而 2^i >  2^j+2^k，所以 |2^i-2^j| < |2^i+2^l-2^k-2^j|
     * - x 和 y 在二进制上的不同位越低越好
     *
     */
    public int minAbs(int x) {
        return 0;
    }
}
