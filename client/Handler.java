import java.io.IOException;

public class Handler implements Runnable{

    private ConnectionManager ConnectionManager;
      
    private Keyboard keyboard; 
    
    private Data data;
    private Arena arena;

    public Handler(ConnectionManager ConnectionManager,Keyboard keyboard, Arena Arena, Data data){
    
        this.ConnectionManager = ConnectionManager;
        this.keyboard=keyboard;
        this.arena = Arena;
        this.data = data;
    }

    public void run(){
        while (true) {
            try {
                data.lock.lock();
                data.waitHandler.await();

                switch(data.option) {
                    case USERNAME:
                    case PASSWORD:
                        ConnectionManager.login(data.username, data.password);
                        data.response = Response.DONE;
                        break;
                    case CREATE_USERNAME:
                    case CREATE_PASSWORD:
                        ConnectionManager.create_account(data.username, data.password);
                        data.username = "";
                        data.password = "";
                        data.response = Response.DONE;
                        break;
                    case DELETE:
                        ConnectionManager.remove_account(data.username, data.password);
                        data.username = "";
                        data.password = "";
                        data.response = Response.DONE;
                        break;
                    case LOGOUT:
                        ConnectionManager.logout(data.username, data.password);
                        data.username = "";
                        data.password = "";
                        data.response = Response.DONE;
                        break;
                    case JOIN:
                        data.leaderboard = ConnectionManager.leaderboard();
                        data.response = Response.DONE;
                        break;
                    case PLAY:
                        ConnectionManager.join(data.username, data.password);
                        data.response = Response.DONE;
                        data.option = State.LEADERBOARD;

                        new Thread(()->{
                            try {
                                String response = ConnectionManager.receive();
                                if(response.equals("start"))
                                    data.option = State.GAME;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                        break;
                    case GAME:
                        String response = ConnectionManager.receive();
                        if(response == null || response.equals("defeat")) {
                            data.option = State.LOGGED_IN;
                            data.response = Response.SWITCH;
                        } else if (response.equals("winner")) {
                            data.option = State.LOGGED_IN;
                            data.response = Response.SWITCH;
                        } else{
                            arena.setArena(data.username, response);
                            
                            keyboard.setKey(data.keyJogo);
                            ConnectionManager.Keyboard(keyboard.toString());
                            
                            data.response = Response.DONE;
                        }
                        break;
                    case LEAVE:
                        ConnectionManager.send("leave:");
                        data.response = Response.DONE;
                        break;
                    case QUIT:
                        ConnectionManager.send("leave:");
                        ConnectionManager.receive();
                        data.response = Response.DONE;
                        break;

                }
                data.waitScreen.signal();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            } catch (InvalidPassword | InvalidAccount | UserExists | FullServer e) {
                data.response = Response.ERROR;
                data.username = "";
                data.password = "";
                data.waitScreen.signal();
            } finally {
                data.lock.unlock();
            }

        }

    }
}
