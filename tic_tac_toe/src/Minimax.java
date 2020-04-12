import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Minimax {

    //public static double[] punish = {0.01,0.1,1,100,1000,10000,100000,1000000,10000000,100000000};


    public static boolean[][][] dir_checked = new boolean[ChessBoard.N][ChessBoard.N][4];//4 directions(two diagonal,vertical and horizon) whether checked.

    public static int[][] count = new int[2][8];//记录棋型数量，一共八种. count[0]是己方，count[1]是对方

    public static void reset(){
        for(int i = 0; i < ChessBoard.N; i++)
            for(int j = 0; j < ChessBoard.N; j++)
                for(int k = 0; k < 4; k++){
                    dir_checked[i][j][k] = false;}

        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 8; j++){
                count[i][j] = 0;
            }


    }


    public static double eval(ChessBoard cb, char m) { //m = mymove
        char r = m == 'O' ? 'X' : 'O';
        int side;
        reset();
        /*double res = roweval(cb.board,cb.mymove,cb.M)
                +roweval(cb.d1,cb.mymove,cb.M)
                +roweval(cb.d2,cb.mymove,cb.M)
                +roweval(cb.v,cb.mymove,cb.M);*/


        for (int i = 0; i < cb.board.length; i++) {
            for (int j = 0; j < cb.board[0].length; j++) {
                if (cb.board[i][j] == m) {
                    side = 0;
                    pointeval(cb.board, i, j, side, m, r);//the value of side decides which array of count will be used
                    //System.out.println(count[side][0]);
                } else if (cb.board[i][j] == r) {
                    side = 1;
                    pointeval(cb.board, i, j, side, r, m);
                }
            }
        }

        int[] m_count = count[0];
        int[] r_count = count[1];

        return getScore(m_count, r_count);
    }

    public static double getScore(int[] m, int[] r){
        int mscore = 0;
        int rscore = 0;

        if(m[0] > 0)
            return 10000;
        if(r[0] > 0)
            return -10000;

        if(m[2] >= 2)
            m[1] += 1;

        if(r[1] > 0)
            return -9050;
        if(r[2] > 0)
            return -9040;

        if(m[1] > 0)
            return 9030;
        if(m[2] > 0 && m[3] == 0)
            return 9020;

        if(r[3] > 0 && m[2] == 0)
            return -9010;

        if(m[3] > 1 && r[3] == 0 && r[4] == 0)
            return 9000;

        if(m[2] > 0)
            mscore += 2000;

        if(m[3] > 1)
            mscore += 500;
        else if(m[3] > 0)
            mscore += 100;

        if(r[3] > 1)
            rscore += 2000;
        else if(r[3] > 0)
            rscore += 400;

        if(m[4] > 0)
            mscore += m[4] * 10;
        if(r[4] > 0)
            rscore += r[4] * 10;

        if(m[5] > 0)
            mscore += m[5] * 4;
        if(r[5] > 0)
            rscore += r[5] * 4;

        if(m[6] > 0)
            mscore += m[6] * 4;
        if(r[6] > 0)
            rscore += r[6] * 4;


        return (mscore - rscore);

    }




    public static void pointeval(char[][] board, int x, int y, int side, char m, char r) {//m represents the side being considered
        int[][] dir_offset = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        double sum = 0;
        for (int dir = 0; dir < 4; dir++) {
            if (!dir_checked[x][y][dir])
                cal_Line(board, x, y, dir, dir_offset[dir], count[side], m, r);
        }

    }


    public static void cal_Line(char[][] board, int x, int y, int dir_index, int[] dir_offset, int[] count, char m, char r) {

        int left_idx, right_idx;
        left_idx = right_idx = 4;

        char[] line = get_Line(board, x, y, dir_offset, m, r);

        while (right_idx < 8) {
            if (line[right_idx + 1] != m) break;
            right_idx += 1;
        }
        while (left_idx > 0) {
            if (line[left_idx - 1] != m) break;
            left_idx -= 1;
        }

        int left_range = left_idx;
        int right_range = right_idx;

        while (right_range < 8){
            if (line[right_range + 1] == r) break ;

            right_range += 1;
        }
        while (left_range > 0) {
            if (line[left_range - 1] == r) break;

            left_range -= 1;
        }

        int chess_range = right_range - left_range + 1;
        if (chess_range < 5) {
            setRecord(x, y, left_range, right_range, dir_index, dir_offset);

            return;
        }
        setRecord(x, y, left_idx, right_idx, dir_index, dir_offset);

        int m_range = right_idx - left_idx + 1;

        if (m_range == 5)
            count[0] += 1; //count[0] is set to store the number of m_range==5(Five)

        // Live Four : XMMMMX
        // Chong Four : XMMMMP, PMMMMX
        if (m_range == 4) {
            boolean left_empty = false;
            boolean right_empty = false;
            if (line[left_idx - 1] == '-')  left_empty = true;
            if (line[right_idx + 1] == '-') right_empty = true;
            if (left_empty && right_empty) count[1] += 1;//count[1] stores the number of LiveFour(-mmmm-)
            else if (left_empty || right_empty) count[2] += 1;//count[2] = SFour(rmmmm- || -mmmmr)
        }


        // Chong Four : MXMMM, MMMXM, the two types can both exist
        // Live Three : XMMMXX, XXMMMX
        // Sleep Three : PMMMX, XMMMP, PXMMMXP
        if(m_range == 3) {
            boolean left_empty = false;
            boolean right_empty = false;
            boolean left_four = false;
            boolean right_four = false;
            if (line[left_idx - 1] == '-') {
                if (line[left_idx - 2] == m) { //MXMMM
                    setRecord(x, y, left_idx - 2, left_idx - 1, dir_index, dir_offset);
                    count[2] += 1;//SFour
                    left_four = true;
                }
                left_empty = true;
            }


            if (line[right_idx + 1] == '-') {
                if (line[right_idx + 2] == m) {  // MMMXM
                    setRecord(x, y, right_idx + 1, right_idx + 2, dir_index, dir_offset);
                    count[2] += 1;
                    right_four = true;
                }
                right_empty = true;
            }

            if (left_four || right_four) {;}
            else if (left_empty && right_empty) {
                if (chess_range > 5)
                    count[3] += 1;//XMMMXX, XXMMMX
                else  //PXMMMXP
                    count[4] += 1;
            }
            else if (left_empty || right_empty)  //PMMMX, XMMMP
                count[4] += 1; // count[4] = SThree
        }

        if(m_range == 2){
            boolean left_empty = false;
            boolean right_empty = false;
            boolean left_three = false;
            boolean right_three = false;

            if (line[left_idx - 1] == '-') {
                if (line[left_idx - 2] == m) {
                    setRecord(x, y, left_idx - 2, left_idx - 1, dir_index, dir_offset);
                    if (line[left_idx - 3] == '-') {
                        if (line[right_idx + 1] == '-') count[3] += 1;
                        else
                            count[4] += 1;

                        left_three = true;
                    }

                    else if (line[left_idx - 3] == r) { //rm-mm-
                        if (line[right_idx + 1] == '-') {
                            count[4] += 1;
                            left_three = true;
                        }
                    }
                }
                left_empty = true;
            }

            if(line[right_idx + 1] == '-'){
                if(line[right_idx + 2] == m){
                    if(line[right_idx + 3] == m){
                        setRecord(x, y, right_idx + 1, right_idx + 2, dir_index, dir_offset);
                        count[2] += 1;
                        right_three = true;
                    }
                    else if(line[right_idx + 3] == '-'){
                        if(left_empty)
                            count[3] += 1;
                        else
                            count[4] += 1;

                            right_three = true;
                        }
                    else if(left_empty){
                        count[4] += 1;
                        right_three = true;
                        }
                    }

                    right_empty = true;
                }

            if(left_three || right_three){;}

            else if(left_empty && right_empty)
                count[5] += 1;
            else if(left_empty || right_empty)
                count[6] += 1;

            }

        if(m_range == 1){
            boolean left_empty = false;
            boolean right_empty = false;

            if(line[left_idx - 1] == '-'){
                if(line[left_idx - 2] == m){
                    if(line[left_idx - 3] == '-'){
                        if(line[right_idx + 1] == r){
                            count[6] += 1;
                        }
                    }
                }
                left_empty = true;
            }

            if(line[right_idx + 1] == '-'){
                if(line[right_idx + 2] == m){
                    if(line[right_idx + 3] == '-'){
                        if(left_empty)
                            count[5] += 1;
                        else
                            count[6] += 1;
                    }
                }
                else if(line[right_idx + 2] == '-'){
                    if(line[right_idx + 3] == m && line[right_idx + 4] == '-')
                        count[5] += 1;
                }
            }
        }

        return;


    }


    public static char[] get_Line(char[][] board, int x, int y, int[] dir_offset, char m, char r) {
        char[] line = new char[9];

        int tmp_x = x + (-5 * dir_offset[0]);
        int tmp_y = y + (-5 * dir_offset[1]);

        for (int i = 0; i < 9; i++) {
            tmp_x += dir_offset[0];
            tmp_y += dir_offset[1];
            if (tmp_x < 0 || tmp_x >= board.length || tmp_y < 0 || tmp_y >= board[0].length)
                line[i] = r;  //set out of range as opponent chess
            else {
                line[i] = board[tmp_y][tmp_x];
            }
        }
        return line;
    }


    public static void setRecord(int x, int y, int left, int right, int dir_index, int[] dir_offset) {

        int tmp_x = x + (-5 + left) * dir_offset[0];
        int tmp_y = y + (-5 + left) * dir_offset[1];
        for (int i = left; i < right; i++) {
            tmp_x += dir_offset[0];
            tmp_y += dir_offset[1];
            dir_checked[tmp_y][tmp_x][dir_index] = true;
        }
    }



    public static double value(ChessBoard cb,double a,double b,int depth) {
        char winner = cb.check_win();

        if(winner != '-') {
            return winner == cb.mymove ? Double.MAX_VALUE : -Double.MAX_VALUE;
        }
        else if(depth == 0) {
            return eval(cb,cb.mymove);
        }
        if(cb.mymove == cb.nextmove) {
            return max_value(cb,a,b,depth-1);
        }else {
            return min_value(cb,a,b,depth-1);
        }
    }

    public static double max_value(ChessBoard cb,double a,double b,int depth){
        double v =  -Double.MAX_VALUE;

        for(int i = 0; i < cb.N; i++) {
            for(int j = 0;j< cb.N; j++) {
                if(cb.board[i][j] == '-' && check_neighbor(cb,i,j,1)) {
                    cb.move(i, j);
                    v = Math.max(v, value(cb,a,b,depth - 1));
                    cb.regret(i, j);

                    if(v >= b){
                        return v;
                    }
                    //ab[0] = Math.max(ab[0], v);
                    a = Math.max(a, v);
                }
            }
        }
        return v;
    }

    public static double min_value(ChessBoard cb,double a,double b,int depth){
        double v =  Double.MAX_VALUE;

        for(int i = 0; i < cb.N; i++) {
            for(int j = 0; j < cb.N; j++) {
                if(cb.board[i][j] == '-' && check_neighbor(cb,i,j,1)) {
                    cb.move(i, j);
                    v = Math.min(v, value(cb,a,b,depth-1));
                    cb.regret(i, j);

                    if(v <= a){
                        return v;
                    }
                    b = Math.min(b, v);
                }
            }
        }
        return v;
    }

    public static boolean check_neighbor(ChessBoard cb,int x,int y,int r) {
        int xstart = x - r <0 ? 0:x-r;
        int xend = x + r >= cb.N? cb.N - 1:x + r;
        int ystart = y - r < 0 ? 0:y-r;
        int yend = y + r >= cb.N ? cb.N-1:y + r;

        for(int i = xstart; i <= xend; i++) {
            for(int j = ystart; j <= yend; j++) {
                if(cb.board[i][j] != '-') {
                    return true;
                }
            }
        }
        return false;
    }

    public static void print(char[][] board) {
        for(int i=0;i<board.length;i++) {
            for(int j=0;j<board[0].length;j++) {
                System.out.print(board[i][j]);
            }
            System.out.println("");
        }
    }

    /*public static double roweval(char[][] board, char m, int target) {
        double sum = 0;
        //r = rival move
        //m = my move
        char r = m == 'O' ? 'X' : 'O';

        char[] rend = new char[target];
        char[] mend = new char[target];
        for(int i = 0; i < target; i++) {
            rend[i] = r;
            mend[i] = m;
        }

        int itercnt = target;


        Set<String> rcheckset = new HashSet<String>();
        Set<String> mcheckset = new HashSet<String>();

        String mend_str = new String(mend);
        String rend_str = new String(rend);

        mcheckset.add(mend_str);
        rcheckset.add(rend_str);
        int cur_punish = 10000000;

        // my part weight
        while(itercnt > 1) {

            //update mcheckset
            Set<String> mcheckset_tmp = new HashSet<String>();
            for(String check_str: mcheckset) {

                for(int i = 0; i < target; i++) {
                    StringBuilder strbuilder = new StringBuilder(check_str);
                    if(strbuilder.charAt(i) != '-') {
                        strbuilder.setCharAt(i, '-');
                        mcheckset_tmp.add(strbuilder.toString());
                    }
                }
            }
            mcheckset = mcheckset_tmp;

            cur_punish /= (4 * board[0].length * mcheckset.size() + 1);

            //calculate eval
            for(String str:mcheckset) {
                for(int i = 0; i < board.length; i++) {
                    String row = new String(board[i]);

                    int index = 0;
                    while(index != -1) {
                        index = str.indexOf(str,index);
                        if(index != -1) {
                            sum += cur_punish;
                            index += 1;
                        }
                    }
                }
            }
            itercnt--;
        }

        // rival part weight
        itercnt = target;

        while(itercnt > 1) {
            //update rcheckset
            Set<String> rcheckset_tmp = new HashSet<String>();
            for(String check_str: rcheckset) {
                for(int i = 0; i < target; i++) {
                    StringBuilder strbuilder = new StringBuilder(check_str);
                    if(strbuilder.charAt(i) != '-') {
                        strbuilder.setCharAt(i, '-');
                        rcheckset_tmp.add(strbuilder.toString());
                    }
                }
            }
            rcheckset = rcheckset_tmp;
            cur_punish /= (4 * board[0].length*mcheckset.size() + 1);

            //calculate eval
            for(String str:rcheckset) {
                for(int i = 0; i < board.length; i++) {
                    String row = new String(board[i]);

                    int index = 0;
                    while(index!=-1) {
                        index = str.indexOf(str,index);
                        if(index!=-1) {
                            sum -= cur_punish;
                            index += 1;
                        }
                    }

                }
            }

            itercnt--;
        }

        return sum;
    }*/

    public static void main(String[] args) throws Exception{

        char[][] b = {
                {'X','-','-','-','-','-','-','-','-','-','-','-'},
                {'-','O','-','-','-','-','-','-','-','-','-','-'},
                {'-','-','X','-','-','-','-','-','-','-','-','-'},
                {'-','-','-','O','-','-','-','-','-','-','-','-'},
                {'-','-','-','-','O','-','-','-','-','-','-','-'},
                {'-','-','-','-','-','-','-','-','-','-','-','-'},
                {'-','-','-','-','-','-','-','-','-','O','-','O'},
                {'-','-','-','-','X','-','-','-','-','O','-','X'},
                {'-','-','-','O','-','-','-','-','-','O','-','X'},
                {'-','-','O','-','-','-','-','-','-','O','X','X'},
                {'-','X','-','-','-','-','-','-','-','-','-','-'},
                {'X','-','-','-','-','-','-','-','-','-','-','-'}};

        ChessBoard cb = new ChessBoard(b,12,6,'X',false);


        /*for(int i=0;i<cb.N;i++) {
            for(int j=0;j<cb.N;j++) {
                System.out.print("  ");
                System.out.print(cb.d1_pos[i][j].x);
                System.out.print(",");
                System.out.print(cb.d1_pos[i][j].y);
            }
            System.out.println("");
        }


        for(int i=0;i<cb.N;i++) {
            for(int j=0;j<cb.N;j++) {
                System.out.print("  ");
                System.out.print(cb.d2_pos[i][j].x);
                System.out.print(",");
                System.out.print(cb.d2_pos[i][j].y);
            }
            System.out.println("");
            System.out.println(eval(cb, 'X'));
        }*/

        eval(cb, 'X');



    }
}