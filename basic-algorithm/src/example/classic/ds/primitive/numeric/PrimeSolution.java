package example.classic.ds.primitive.numeric;

/**
 * 获取 [1..x] 之间的素数集合
 */
public class PrimeSolution {

    /**
     * 遍历集合的每个元素，判断元素是否时素数
     *
     */
    public int[] plain(int x){
        for (int i = 1; i <= x; i++){
            if (isPrime(x)){

            }
        }

        return new int[]{};
    }

    /**
     * 根据素数定义，如果小于 x 的数能够整除 x 则 x 不是素数
     *
     */
    public boolean isPrime(int x){

        for(int i = 2; i < x; i++){
            if (x % i == 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * 对于素数 x 有小于 x 的素数不能整除 x，即不存在 x 不能被小于 x 的数据整除却能够被小于 x 的非素数整除
     *
     * 证明：设任意的素数 u = mk + n，如果 x 不能被素数 u 整除则 x = uh + w = (mk+n)
     *
     */

}
