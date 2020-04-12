public class ChessBoard {

    public class Pos{
        int x;
        int y;
        Pos(){
            x = 0;
            y = 0;
        }

    }

    public static int N = 12;
    public static int M = 6;

    public static char[][] board;
    public static char[][] v;
    public static char[][] d1;
    public static Pos[][] d1_pos;

    public static char[][] d2;
    public static Pos[][] d2_pos;

    public char mymove;
    public char nextmove;
    public boolean nomove;

    public String mend_str;
    public String rend_str;


    public ChessBoard(char[][] b, int n, int m, char nextmove, boolean nomove) {
        this.board = b;

        d1_pos = new Pos[N][N];
        d2_pos = new Pos[N][N];
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                d1_pos[i][j] = new Pos();
                d2_pos[i][j] = new Pos();
            }
        }

        this.v = getVertical(b);
        this.d1 = getDiagonal1(board);
        this.d2 = getDiagonal2(board);

        this.mymove = nextmove;
        this.nextmove = nextmove;
        this.nomove = nomove;
        this.M = m;
        this.N = n;

        char r = mymove == 'O'?'X':'O';
        char[] rend = new char[M];
        char[] mend = new char[M];

        for(int i = 0; i < M; i++) {
            rend[i] = r;
            mend[i] = mymove;
        }

        this.rend_str = new String(rend);
        this.mend_str = new String(mend);

    }

    public char check_win() {
        char[] winchar = {'-','-','-','-'};

        winchar[0] = get_win_char(board);
        winchar[1] = get_win_char(this.v);
        winchar[2] = get_win_char(this.d1);
        winchar[3] = get_win_char(this.d1);

        for(int i=0;i<4;i++) {
            if(winchar[i] != '-') {
                return winchar[i];
            }
        }

        return '-';

    }

    public char get_win_char(char[][] board) {

        for(int i=0;i<board.length;i++) {
            String row = new String(board[i]);
            if(row.contains(mend_str)) {
                return mymove;
            }

            if(row.contains(rend_str)) {
                return rend_str.charAt(0);
            }
        }

        return '-';

    }

    public void move(int i, int j) {
        board[i][j] = nextmove;
        v[i][j] = nextmove;
        d1[d1_pos[i][j].x][d1_pos[i][j].y] = nextmove;
        d2[d2_pos[i][j].x][d2_pos[i][j].y] = nextmove;

        nextmove = (nextmove == 'O')?'X':'O';

    }

    public void regret(int i, int j) {
        this.nextmove = board[i][j];

        v[i][j] = '-';
        d1[d1_pos[i][j].x][d1_pos[i][j].y] = '-';
        d2[d2_pos[i][j].x][d2_pos[i][j].y] = '-';
        board[i][j] = '-';
    }

    public void print() {
        System.out.println("=================================");
        System.out.println("Nextmove:" + String.valueOf(nextmove) + " || Mymove:"+ String.valueOf(mymove));
        System.out.println("N:" + String.valueOf(N) +" || M:" + String.valueOf(M));

        System.out.println("ChessBoard:");
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j<board[0].length; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println(" ");
        }
    }

    public static char[][] getVertical(char[][] board){
        char[][] res = new char[N][N];
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                res[j][i] = board[i][j];
            }
        }
        return res;
    }

    public static char[][] getDiagonal1(char[][] board){
        char[][] d = new char[N+N][N];

        int i=0;
        int x=0,y=N-1;

        while(x<N) {
            int xi = x,yi = y;

            int j=0;
            while( xi < N && yi < N ) {

                d[i][j] = board[xi][yi];

                d1_pos[xi][yi].x = i;
                d1_pos[xi][yi].y = j;

                xi++;
                yi++;
                j++;

            }
            while(j<N) {
                d[i][j++] = '*';
            }

            if(y>0) {
                y--;
            }else {
                x++;
            }
            i++;
        }

        return d;
    }

    public static char[][] getDiagonal2(char[][] board){
        char[][] d = new char[N+N][N];

        int i=0;
        int x=0,y=0;

        while(x<N ) {
            int xi = x,yi = y;
            int j=0;
            while( xi < N && yi >= 0 ) {
                d[i][j] = board[xi][yi];

                d2_pos[xi][yi].x = i;
                d2_pos[xi][yi].y = j;

                xi++;
                yi--;
                j++;
            }
            while(j<N) {
                d[i][j++] = '*';
            }
            if(y==N-1) {
                x++;
            }else {
                y++;
            }
            i++;
        }
        return d;
    }


}
