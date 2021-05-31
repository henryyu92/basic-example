package example.classic.ds.primitive.bit;

/**
 * 位掩码
 *
 * 老鼠试药： 10 瓶药中有 1 瓶毒药，老鼠吃药后 1 小时会死亡，最少需要多少老鼠才能在 1 小时内找到毒药
 *
 *  10 可以使用 4 位二进制表示，于是有：
 *   药
 *   1  0  0  0  1
 *   2  0  0  1  0
 *   3  0  0  1  1
 *   4  0  1  0  0
 *   5  0  1  0  1
 *   6  0  1  1  0
 *   7  0  1  1  1
 *   8  1  0  0  0
 *   9  1  0  0  1
 *  10  1  0  1  0
 *
 *      b1 b2 b3 b4
 *
 *      于是 4 只老鼠总共可以表示 10 状态：
 *      - b1, b2, b3 正常，b4 死亡 表示药 1 是毒药
 *      - b1, b2, b4 正常，b3 死亡 表示药 2 是毒药
 *      - ...
 *
 */
public class BitMask {

    public int findPoison(int poisons){

        int bits = 1;
        int s = 1;
        while ((s << bits) < poisons){
            bits++;
        }

        return 0;
    }

}
