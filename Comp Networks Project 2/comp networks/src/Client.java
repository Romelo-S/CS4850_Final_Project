import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Scanner;

/*Name: Romelo Seals
 * Student id: 14341022
 * Date: 10/16/2022
 * This is the client side of the chatroom. Coded in Java SE 17
 */
public class Client {
    public static void main(String[] args) throws Exception {
        //String hosted = "127.0.0.1";
        int port = 11022;
        
        boolean doit = true;
        //create socket time
        Socket socket = new Socket("127.0.0.1", port);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        //make thread to start multiple threading
        Thread thread = new Thread(new ClientTask(socket));
        thread.start();
        while(doit)
        {    
                String message = reader.readLine();
                System.out.println(message);
                if(message.equals("logout")) //thread doing the logging out
                {
                    thread.interrupt(); //close thread and close this socket
                    socket.close();
                    System.exit(0);
                    break;
                }
            }   
        }
        
    
    static class ClientTask implements Runnable
    {
        private Socket socket;
        //private String user = "";
        public ClientTask(Socket socket)
        {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
				clientRun();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        private void clientRun() throws Exception 
        {
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            Scanner input = new Scanner(System.in); //input of user
            while(true)
            {
                System.out.println("Please enter a command from the list of commands given\nsend\nlogin\nlogout\nnewuser\nwho"); //list of commands. this is mostly for myself because I could not remember them for the life of me
                String action = input.next(); //take the command
                if(action.equals("send"))
                {
                    String sent = input.nextLine();
                    if(!sent.equals(""))
                    {
                        writer.write(action + " " + sent);
                    }else if(sent.equals("all")){
                        writer.write(action +"all " + input.nextLine()); //writing it into the log
                        writer.flush();
                    }else
                    {
                        writer.write(action + " " + sent + " " + input.nextLine());
                    }
                
                }else if(action.equals("logout") || action.equals("who")) //these both don't require anymore than just the command so I put these with each other
                {
                    writer.write(action);
                }
                else if(action.equals("login")) //logging in
                {
                    String username = input.next();
                    String password = input.next();
                    writer.write(action + " " + username + " " + password);
                }
                else if(action.equals("newuser"))
                {
                    String newuser = input.next();
                    String newpass = input.next();
                    writer.write(action + " " + newuser + " " + newpass);
                }
                else
                {
                    System.out.println("Please enter something from the list");
                }
                writer.write("\n");
                writer.flush();
            }        
        } 
    } 
}
