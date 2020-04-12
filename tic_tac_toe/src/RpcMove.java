import org.json.JSONObject;

import java.util.Iterator;

public class RpcMove {

    public String GetGame() {
        return RequestHelper.sendGet("type=myGames");
    }

    public String GetMoves(String gameId,int cnt) {
        return RequestHelper.sendGet("type=moves&gameId"+gameId+"count="+String.valueOf(cnt));
    }

    public static ChessBoard GetBoardString(String gameId) {
        String result = RequestHelper.sendGet("type=boardString&gameId="+gameId);
        JSONObject json = new JSONObject(result);

        if(json.get("code").equals("OK")) {
            String str = (String)json.get("output");

            String[] cb = str.split("\n");
            char[][] board = new char[cb.length][cb.length];
            int cnto = 0;
            int cntx = 0;
            for(int i = 0; i < cb.length; i++) {
                for(int j = 0; j < cb.length; j++) {
                    board[i][j] = cb[i].charAt(j);
                    if(board[i][j] == 'O') {
                        cnto++;
                    }
                    if(board[i][j] == 'X') {
                        cntx++;
                    }
                }
            }
            return new ChessBoard(board,board.length,(int)json.get("target"),cnto<=cntx? 'O':'X', cnto == 0);
        }else {
            System.out.print("Get Board Map Failed");
            return null;
        }

    }

    public static String Move(String gameId,int move1,int move2) {
        String result = RequestHelper.sendPost("type=move&teamId=1228&gameId="+gameId+"&move="+String.valueOf(move1)+","+String.valueOf(move2));
        JSONObject json = new JSONObject(result);
        if(json.getString("code").equals("OK")) {
            return String.valueOf(json.get("moveId"));
        }else {
            return "Move Failed";
        }
    }

    public String CreateGame(String rival) {
        String result = RequestHelper.sendPost("type=game&gameType=TTT&teamId1=1228&teamId2="+rival);
        JSONObject json = new JSONObject(result);
        if(json.getString("code").equals("OK")) {
            return String.valueOf(json.get("gameId"));
        }else {
            return "Create Game Failed";
        }
    }

}
