// Java implementation of  Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 

import java.io.*;
import java.util.*;
import java.net.*;

// Server class 
public class Server
{

    public static void main(String[] args) throws IOException
    {
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5056);

        Database db = new Database("jdbc:postgresql://localhost:5432/UserMessagingAppDatabase", "postgres", "Gudukbay1905");
        // running infinite loop for getting 
        // client request 
        while (true)
        {
            Socket s = null;

            try
            {
                // socket object to receive incoming client requests 
                s = ss.accept();

                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object 
                Thread t = new ClientHandler(s, dis, dos, db);

                // Invoking the start() method 
                t.start();

            }
            catch (Exception e){
                if(s != null)
                    s.close();
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class
class ClientHandler extends Thread {
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Socket s;
    private Database db;


    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Database db) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.db = db;
    }

    @Override
    public void run() {
        String received;
        String username;
        String password;
        try {
            db.connection();
            while (true) {
                // Check for the user message
                received = dis.readUTF();
                String[] words = received.substring(1).split("\\|");
				System.out.println(words[0]);

                switch (words[0]) {
                    case "LOGIN":
                        username = words[1];
                        password = words[2];
                        if (db.loginSuccesfulCheck(username, password)) {
                            User user = db.queryAllUserInformation(username);
                            String isAdmin = "F";
                            if (user.isAdmin())
                                isAdmin = "T";
                            //login succesful message
                            dos.writeUTF("|LOGINSUCCESSFUL|" + user.getName() + "|" + user.getLastName() + "|" + user.getBirthDate() + "|" + user.getGender()
                                    + "|" + user.getEmail() + "|" + user.getUsername() + "|" + user.getPassword() + "|" + isAdmin + "|");
                        }
                        else
                            dos.writeUTF("|LOGINUNSUCCESSFUL|");

                        break;


                    case "INBOX":
                        //received will be in the format |INBOX|username|
                        username = words[1];
                        ArrayList<Message> inboxMessages = db.getInbox(username);
                        //|INBOX|SIZE|SENDER|TIME|TITLE|CONTENT|SENDER|TIME|TITLE|CONTENT|...
                        String inbox = "|INBOX|";
                        inbox += "" + inboxMessages.size() + "|";
                        for (Message m : inboxMessages)
                            inbox += m.getSender() + "|" + m.getTime() + "|" + m.getTitle() + "|" + m.getContent() + "|";

                        dos.writeUTF(inbox);
                        break;

                    case "OUTBOX":
                        //received will be in the format |OUTBOX|username|
                        username = words[1];
                        ArrayList<Message> outboxMessages = db.getOutbox(username);
                        //|OUTBOX|RECEIVER|TIME|TITLE|CONTENT|RECEIVER|TIME|TITLE|CONTENT|...
                        String outbox = "|OUTBOX|";
                        outbox += "" + outboxMessages.size() + "|";
                        for (Message m : outboxMessages)
                            outbox += m.getReceiver() + "|" + m.getTime() + "|" + m.getTitle() + "|" + m.getContent() + "|";

                        dos.writeUTF(outbox);
                        break;


                    case "SENDMSG":
                        //the received string will be in the form "|SENDMSG|SENDER|RECEIVER|TITLE|MESSAGE|"
                        Message m = new Message(words[1], words[2], java.time.LocalTime.now().toString(), words[3], words[4]);
                        if (db.sendMessage(m))
                            dos.writeUTF("|SUCCESSFUL|");
                        else
                            dos.writeUTF("|UNSUCCESSFUL|");

                        break;

                    case "ADDUSER":
                        //received will be in the format |ADDUSER|firstname|surname|birthdate|gender|email|username|passwordcolumn|isAdmin|
                        boolean isAdmin = false;
                        if (words[8].equals("T"))
                            isAdmin = true;
                        User temp = new User(words[1], words[2], words[3], words[4].charAt(0), words[5], words[6], words[7], isAdmin);
                        if (db.addUser(temp))
                            dos.writeUTF("|SUCCESSFUL|");
                        else
                            dos.writeUTF("|UNSUCCESSFUL|");

                        break;

                    case "UPDATEUSER":
                        //received will be in the format |UPDATEUSER|username|dataToChange|newData|
                        if (db.updateUser(words[1], words[2], words[3]))
                            dos.writeUTF("|SUCCESSFUL|");
                        else
                            dos.writeUTF("|UNSUCCESSFUL|");

                        break;


                    case "REMOVE":
                        //received will be in the format |REMOVE|username|
                        if (db.removeUser(words[1]))
                            dos.writeUTF("|SUCCESSFUL|");
                        else
                            dos.writeUTF("|UNSUCCESSFUL|");

                        break;

                    case "LIST":
                        ArrayList<User> users = db.listUsers();
                        String list = "|LIST|";
                        list += users.size() + "|";
                        for (User u : users)
                            list += u.getName() + "|" + u.getLastName() + "|" + u.getBirthDate() + "|"  + u.getGender() + "|" + u.getEmail() + "|"+ u.getUsername() + "|" + u.getPassword() + "|";
                        dos.writeUTF(list);

                        break;

                    default:

                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            db.closeConnection();
            this.s.close();
            // closing resources
            this.dis.close();
			this.dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
