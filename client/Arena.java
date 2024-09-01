import java.util.ArrayList;
import java.util.List;


public class Arena {
    public Player me;
    public List<Player> Players = new ArrayList<>();

    public synchronized Player getMe() {
        return this.me;
    }

    public synchronized void setArena(Player p, List<Player> lp) {
        this.me = p;
        this.Players = lp;
    }

    public synchronized void setArena(String username, String response){
        this.Players.clear();
        String[] positionsString = response.split("\\|");
        for(String PlayerString: positionsString) {
            String[] PlayerInfo = PlayerString.split(" ");
            Player Player = new Player(PlayerInfo[0], PlayerInfo[1], Float.parseFloat(PlayerInfo[2]), Float.parseFloat(PlayerInfo[3]), Float.parseFloat(PlayerInfo[4]), Integer.parseInt(PlayerInfo[5]));
            
            if(Player.username.equals(username))
                this.me = Player;
            else
                this.Players.add(Player);
        }
        for(Player p : this.Players){
            p.x -= me.x;
            p.y -= me.y;
        }
        me.x = 0;
        me.y = 0;
    }

    public synchronized Tuple<Player,List<Player>> getArena() {
        return new Tuple<>(this.me, this.Players);
    }
}
