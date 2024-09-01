public class Client {
    public static void main(String[] args) {
        //wsl2 172.20.128.1
        try {
            if ( args.length < 2) {
                print("Incorrect syntax:");
                print("java client [host] [port] ");
                System.exit(1);
            }

            ConnectionManager CC = new ConnectionManager(args[0], Integer.parseInt(args[1]));
            Keyboard keyboard= new Keyboard();
            Mouse mouse = new Mouse();
            Arena arena = new Arena();
            Data data = new Data();

            new Thread(new Screen(mouse,keyboard, arena, data)).start();

            new Thread(new Handler(CC,keyboard,arena, data)).start();


            
        } catch (Exception e) {
            print(e.getMessage());
        }
    }

    public static void print(String message) {
        System.out.println(message);
    }
}