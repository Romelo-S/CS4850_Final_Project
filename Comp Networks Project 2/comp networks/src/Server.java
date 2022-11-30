import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.lang.model.util.ElementScanner14;

/*Name: Romelo Seals
 * Student id: 14341022
 * Date: 10/16/2022
 * This is the client side of the chatroom. Coded in Java SE 17
 */

public class Server {
    protected static String fp = "users1.txt";
    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<Socket> sockets = new ArrayList<>();
    public static void main(String args[]) throws IOException, InterruptedException
    {
        int port = 11022;
        ServerSocket server = new ServerSocket(port);
        System.out.println(server);
        boolean doit = true;
        boolean request = true;
        while(doit)
        {
            if(users.size()<3){ //only 3 users at any given time
                Socket socket = server.accept();
                sockets.add(socket);
                new Thread(new ClientTask(socket)).start(); //start thread on server
            }else
            {
                Thread.sleep(1000);
            }
        }
    }
    
    static class ClientTask implements Runnable
    {
        private Socket socket; //create socket variable to hold whatever socket is giving the command
        String currentUser = ""; //user
        
        public ClientTask(Socket socket){
            this.socket = socket;
        }
        public void run() {
            try {
                ServerRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }

        private void ServerRun() throws Exception {
            
            while(true)
            {
                String userString = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Writer writer = new OutputStreamWriter(socket.getOutputStream());
                String action = reader.readLine();
                if(action.equals("logout"))
                {
                    Iterator<Socket> iterator = sockets.iterator(); //create an iterator to go through the array of sockets
                    while(iterator.hasNext())
                    {
                        Socket socket = iterator.next();
                        if(socket != this.socket) //check if right socket to logout
                        {
                            PrintWriter print = new PrintWriter(socket.getOutputStream());
                            print.println(currentUser + " left.");
                        }else
                        {
                            iterator.remove();
                        }
                        
                    }
                    for (String user : users) { //remove from list
						if (user.equals(currentUser)) {
							users.remove(user);
							break;
						}
					}
                    writer.write(currentUser + " left\n");
                    writer.flush();
                    System.out.println(currentUser + " logout");
                    currentUser = "";
                    break;
                }
                else if (action.equals("who")) //this establishes the who command
                {
					String userlist = "";
					// Append all usernames to userlist
					for (String user : users) {
						userlist += user + ", ";
					}
					writer.write(userlist + "\n");
                }else//these require user outputted
                {
                    String[] userinfo = action.split(" ");
                    if(userinfo[0].equals("login"))
                    {
                        String username = userinfo[1];
                        String password = userinfo[2];
                        if(doesUserExist(username, password))
                        {
                            users.add(username);
                            userString = username;
                            for (Socket socket : sockets) {
								if (socket != this.socket) {
									PrintWriter print = new PrintWriter(socket.getOutputStream());//print the join
									print.println(username + " joins.");
								}
							}
							currentUser = username;
							writer.write("Login confirmed\n");
							System.out.println(username + " login");
						}  
                        else
                        {
                            writer.write("Denied. Username or password is incorrect\n");
                            System.out.print("Denied. Username or password is incorrect\n");
                        }
                        
                    }else if(userinfo[0].equals("send")) //just sending message to server does not complete the function that the project once
                    {
                        if(currentUser != null && !currentUser.equals("") )
                        {
                            String reciever = userinfo[1];
                            String message = "";
                            //message creation
                            for(int i = 2; i < userinfo.length; i++)
                            {
                                message += userinfo[i] + " ";
                            }
                            for (int i = 0; i < users.size(); i++) {
								if (users.get(i).equals(reciever)) { //where message goes
									PrintWriter out = new PrintWriter(sockets.get(i).getOutputStream());
									out.println(currentUser + ":" + message);
									break;
								}
							}
							System.out.println(currentUser + " (to " + reciever + ")"
									+ ":" + message); //sends to the server from the current user
                            writer.write(currentUser + ":" + message + "\n");
                            System.out.println(currentUser + ":" + message);
                        }else
                        {
                            writer.write("Denied. Please login first\n");
                            writer.flush();
                            break;
                        }
                    }else if(userinfo[0].equals("newuser"))  //new user joins
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
        
    }
    public static boolean isNewUser(String newUser) {
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
    public static boolean doesUserExist(String user, String password)
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
    public static void createNewUser(String username,String password) 
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

