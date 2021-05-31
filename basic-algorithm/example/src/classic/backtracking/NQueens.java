package classic.backtracking;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * N 皇后问题
 *
 * https://zhuanlan.zhihu.com/p/199293851
 */
public class NQueens {


    public List<String[]> queens(int n){

        // init record
        int[] record = new int[n];
        for (int i = 0; i < n; i++){
            record[i] = -1;
        }
        List<String[]> result = new ArrayList<>();
        place(0, n, record, result);

        return result;
    }

    /**
     *
     * @param row           当前放置的行
     * @param n             每行可以选择放置数
     * @param record        每行放置的记录
     * @param result        放置结果
     */
    private void place(int row, int n, int[] record, List<String[]> result){
        // 最后一行已经放置了
        if (row == n){
            result.add(printBoard(record));
            return;
        }
        // 每行有 N 个可以放置的位置
        for (int i = 0; i < n; i++){
            //  当前位置不能放置
            if (!canPlace(row, i, record)){
                continue;
            }
            record[row] = i;
            // 当前行放置完毕后，放置下一行
            place(row + 1, n, record, result);
            // 恢复记录，避免影响下一次的放置
            record[row] = -1;
        }
    }

    private boolean canPlace(int row, int col, int[] record){
        // 之前的行才会已放置
        for (int i = 0; i < row; i++){
            // 当前列已经放置
            if (record[i] == col){
                return false;
            }
            // 斜对角，如果相等则说明已经放置了
            if(Math.abs(row - i) == Math.abs(col - record[i])){
                return false;
            }
        }
        return true;
    }

    private String[] printBoard(int[] record){
        String[] strings = new String[Array.getLength(record)];
        for (int i = 0; i < Array.getLength(record); i++){
            // 打印一行
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < Array.getLength(record); j++){
                if (j != record[i]){
                    builder.append("- ");
                }else {
                    builder.append("Q ");
                }
            }
            builder.append("\n");
            strings[i] = builder.toString();
        }
        return strings;
    }

    public static void main(String[] args) {

        NQueens queens = new NQueens();

        List<String[]> list = queens.queens(4);

        list.forEach(strings -> {
            System.out.println("[");
            for (int i = 0; i < Array.getLength(strings); i++){
                System.out.print(strings[i]);
            }
            System.out.println("]");
        });
    }
}
