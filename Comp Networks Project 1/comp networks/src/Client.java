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
        Scanner input = new Scanner(System.in);
        boolean doit = true;
        boolean done = false;
        //create socket time
        Socket socket = new Socket("127.0.0.1", port); //new socket at the port
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Writer writer = new OutputStreamWriter(socket.getOutputStream());
        while(doit)
        {
            System.out.println("Please enter a command from the list of commands given\nsend\nlogin\nlogout\nnewuser"); //list of commands. this is mostly for myself because I forgot what commands I have. My apologizes for not having the exact output
            String action = input.next();
            if(action.equals("send")) //send function. I forgot to check the size of message
            {
                String sent = input.nextLine();
                if(sent != "")
                {
                   writer.write(action + " " + sent);
                }else{
                    writer.write(action);
                    writer.flush();
                }
                 
                //System.out.println("Denied. Please login first.");
            }else if(action.equals("logout"))
            {
                writer.write(action);
                //done = true;
                //doit = false;
            }
            else if(action.equals("login")) //login function
            {
                String username = input.next();
                String password = input.next();
                writer.write(action + " " + username + " " + password);

            }else if(action.equals("newuser")) //new user allows
            {
                String newuser = input.next();
                String newpass = input.next();
                writer.write(action + " " + newuser + " " + newpass);
            }
            else
            {
                System.out.println("Please enter something from the list");
            }
            if(!done) //done is never changed so this is irrelevent but it was for testing
            {
                writer.write("\n");
                writer.flush();
                //System.out.println(reader.readLine());
                String message = reader.readLine();
                System.out.println(message);
                if(message.equals("Server: logout"))
                {
                    break;
                }
            }   
        }
        input.close();
        socket.close();
    }
}
