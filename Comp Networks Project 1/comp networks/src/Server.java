import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

/*Name: Romelo Seals
 * Student id: 14341022
 * Date: 10/16/2022
 * This is the server side of the chatroom. Coded in Java SE 17
 */

public class Server {
    protected static String fp = "users1.txt";
    public static void main(String args[]) throws IOException
    {
        int port = 11022;
        ServerSocket server = new ServerSocket(port);
        System.out.println(server);
        boolean doit = true;
        boolean request = true;
        while(doit)
        {
            System.out.println("connecting to the server"); //seing if socket works
            Socket socket = server.accept();
            System.out.println("Server accepted"); //socket works
            String userString = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Writer writer = new OutputStreamWriter(socket.getOutputStream()); //create reading and wring logs
            while(request)
            {
                String action = reader.readLine();
                if(action.equals("logout"))
                {
                    writer.write(userString + " left\n"); //userstring leaves
                    writer.flush();
                    System.out.println(userString + " logout");
                    break;
                }else{
                    String[] userinfo = action.split(" ");
                    if(userinfo[0].equals("login")) //login in function
                    {
                        String username = userinfo[1];
                        String password = userinfo[2];
                        if(doesUserExist(username, password))
                        {
                            userString = username;
                            writer.write("Server: " + userString + " joins.\n");
                            System.out.println(userString + " login"); 
                        }
                        else
                        {
                            writer.write("Denied. Username or password is incorrect\n");
                            System.out.print("Denied. Username or password is incorrect\n");
                        }
                        
                    }else if(userinfo[0].equals("send")) //send function
                    {
                        if(userString != null && !userString.equals("") )
                        {
                            String message = "";
                            for(int i = 1; i < userinfo.length; i++)
                            {
                                message += userinfo[i] + " ";
                            }
                            writer.write(userString + ":" + message + "\n");
                            System.out.println(userString + ":" + message);
                        }else
                        {
                            writer.write("Denied. Please login first\n");
                            writer.flush();
                            break;
                        }
                    }else if(userinfo[0].equals("newuser")) //new user function
                    {
                        String username = userinfo[1];
                        String password = userinfo[2];
                        if(username.length() > 32)
                        {
                            writer.write("Username should be less than 32\n");
                            writer.flush();
                            continue;
                        }else if(password.length()<4 || password.length()>8)
                        {
                            writer.write("Password should have a length between 4 and 8(inclusive)\n ");
                            writer.flush();
                            continue;
                        }
                        if(isNewUser(username))
                        {
                            createNewUser(username, password);
                            writer.write("New user account created. Please login\n");
                            System.out.println("New user account created.");
                        }else
                        {
                            writer.write("The username already exists!\n");
                        }
                    }else
                    {
                        writer.write("That command does not exist!\n");
                    }
                }
                writer.flush();
            }
        }
        server.close();
    }
    public static boolean isNewUser(String newUser) { //check if new user, true false
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fp));
            String current = reader.readLine();
            while(current != null)
            {
                current = current.replaceAll("[()]", "");
                String[] userlistStrings = current.split(", ");
                if(userlistStrings[0].equals(newUser))
                {
                    reader.close();
                    return false;
                }
                current = reader.readLine();
            }
            reader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
        
    }
    public static boolean doesUserExist(String user, String password) //does this user already exist in .txt file?
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fp));
            String current = reader.readLine();
            while(current != null)
            {
                current = current.replaceAll("[()]", "");
                String[] userlistStrings = current.split(", ");
                if(userlistStrings[0].equals(user) && userlistStrings[1].equals(password))
                {
                    reader.close();
                    return true;
                }
                current = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void createNewUser(String username,String password) //creating a new user
    {
        try {
            FileWriter writer = new FileWriter(fp,true);
            writer.write("\n("+ username + ", " + password + ")");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }
}
