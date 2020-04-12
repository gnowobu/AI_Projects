import java.util.HashMap;

public class Play {
    public static void play(ChessBoard cb,int depth, String gameId) {

        double max = -Double.MAX_VALUE;
        int movei = 0,movej = 0;
        boolean checked = false;

        System.out.print("== Start Search ==");

        if(cb.nomove){
            RpcMove.Move(gameId, cb.N/2,cb.N/2);
            return;
        }

        for(int i=0;i<cb.N;i++) {
            for(int j=0;j<cb.N;j++) {
                if(cb.board[i][j] == '-' ) {
                    double[] ab = {-Double.MAX_VALUE,Double.MAX_VALUE};
                    cb.move(i, j);
                    double v = Minimax.value(cb, -Double.MAX_VALUE,Double.MAX_VALUE, depth);
                    if(max < v) {
                        max = v;
                        movei = i;
                        movej = j;
                        checked = true;
                    }
                    cb.regret(i, j);
                }
            }
        }


        System.out.print("== Search End ==");

        if(checked) {
            System.out.print("\n");
            System.out.print(movei);
            System.out.print(",");
            System.out.println(movej);
            System.out.println(max);
            RpcMove.Move(gameId, movei, movej);
        }else {
            System.out.println("NO MOVES");
        }
    }

    public static void main(String[] args) {

        RpcMove rpc = new RpcMove();

        String rival = "1205";

        HashMap<String,Character> mymove_map = new HashMap<String,Character>();//

        //System.out.print(rpc.GetGame());

        String gameId = rpc.CreateGame(rival);
        System.out.println(gameId);
        //String gameId = "1385";
        while(true) {


            ChessBoard cb = RpcMove.GetBoardString(gameId);
            if(cb == null) {
                System.out.println("Game End");
                break;
            }

            if(mymove_map.containsKey(gameId)) {
                cb.mymove = mymove_map.get(gameId);
            }else {
                mymove_map.put(gameId, cb.mymove);
            }

            play(cb, 4, gameId);//

            if(cb.mymove != cb.nextmove) {
                continue;
            }
            //cb = RpcMove.GetBoardString(gameId);
            Minimax.print(cb.board);


            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
