package example.leetcode.array;

public class ArrayHelper {

    public static int[] init(int len, int min, int max){

        return new int[]{};
    }

    public static int[] randomArray(int len, int min, int max){
        return new int[]{};
    }

    public static void print(int[] arr){
        if (arr == null){
            System.out.println("[]");
        }else{
            System.out.print("[");
            for (int i = 0; i < arr.length; i++){
                System.out.print(arr[i] + " ");
            }
            System.out.println("]");
        }
    }
}
