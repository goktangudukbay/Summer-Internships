
import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client class
public class Client
{
    public static void main(String[] args) throws IOException
    {
        //variables
        boolean loggedIn;
        User user;
        int index1;
        String temp;
        int index2;
        String received;
		String[] words;
		String un;
		String pw;

        //code
        loggedIn = false;
        user = new User();

        try
        {
            Scanner scn = new Scanner(System.in);

            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 5056
            Socket s = new Socket(ip, 5056);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true)
            {
                if(!loggedIn){
                    System.out.println("\nYou are not logged in. \n" +
                            "If you want to log in enter \"login\n"
                            + "Enter 'E' or 'e' for exiting the system.");
                }
                else{
                    System.out.println("\nYou are logged in. Enter:\n\t\t'L' or 'l' for logging out, \n\t\t'I' or 'i' for Inbox, \n\t\t'O' or 'o' for Outbox," +
                            " \n\t\t'S' or 's' for Sending Messages. \n\t\t'E' or 'e' for exiting the system.\n");
                    if(user.isAdmin()){
                        System.out.println("You are an admin. Enter: \n\t\t'A' or 'a' for adding a new user, \n\t\t'U' or 'u' for updating the information," +
                                "\n\t\t'R' or 'r' for removing a user, \n\t\t'B' or 'b' for browsing all users.");

                    }
                }
                String tosend = scn.nextLine();
                //check for exit
                if(tosend.equals("E") || tosend.equals("e")){
					dos.writeUTF("|EXIT|");
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
                if(!loggedIn && tosend.equals("login")){
                    un = "";
                    pw = "";
                    System.out.println();
                    System.out.print("Enter your username: ");
                    un = scn.nextLine();
                    System.out.print("Enter your password: ");
                    pw = scn.nextLine();
                    tosend = "|LOGIN|" + un + "|" + pw + "|";
                    dos.writeUTF(tosend);
                    received = dis.readUTF();
                    words = received.substring(1).split("\\|");
                    //check for the message |loginSuccesful|
                    if(words[0].equals("LOGINSUCCESSFUL")) {
                        loggedIn = true;
                        //received will be in the form |LOGINSUCCESSFUL|name|lastname|birthdate|gender|email|username|password|isAdmin|
                        user.setName(words[1]);
                        user.setLastName(words[2]);
                        user.setBirthDate(words[3]);
                        user.setGender(words[4].charAt(0));
                        user.setEmail(words[5]);
                        user.setUsername(words[6]);
                        user.setPassword(words[7]);
                        if(words[8].equals("T"))
                            user.setAdmin(true);
                        else
                            user.setAdmin(false);
                    }
                }
                else {
                    switch (tosend) {
                        case ("L"):
                        case ("l"):
                            user = new User();
                            loggedIn = false;
                            System.out.println("\nYou are logged out from the system.");
                            break;

                        case ("I"):
                        case ("i"):
                            tosend = "|INBOX|" + user.getUsername() + "|";
                            dos.writeUTF(tosend);
                            received = dis.readUTF();
                            //inbox will be received|INBOX|SIZE|SENDER|TIME|TITLE|CONTENT|SENDER|TIME|TITLE|CONTENT|...
                            words = received.substring(1).split("\\|");
                            String inbox = "";
                            if (words[1].equals("0"))
                                inbox = "INBOX is empty.\n";
                            else {
                                int index = 2;
                                for (int i = 0; i < Integer.parseInt(words[1]); i++) {
                                    inbox += "\nMESSAGE " + (i + 1) + ":\n";
                                    inbox += "SENDER: " + words[index++] + "\n";
                                    inbox += "TIME: " + words[index++] + "\n";
                                    inbox += "TITLE: " + words[index++] + "\n";
                                    inbox += "MESSAGE: " + words[index++] + "\n";
                                }
                            }
                            System.out.println(inbox);

                            break;

                        case ("O"):
                        case ("o"):
                            tosend = "|OUTBOX|" + user.getUsername() + "|";
                            dos.writeUTF(tosend);
                            received = dis.readUTF();
                            //inbox will be received|OUTBOX|SIZE|RECEIVER|TIME|TITLE|CONTENT|RECEIVER|TIME|TITLE|CONTENT|...
                            words = received.substring(1).split("\\|");
                            String outbox = "";
                            if (words[1].equals("0"))
                                outbox = "OUTBOX is empty.\n";
                            else {
                                int index = 2;
                                for (int i = 0; i < Integer.parseInt(words[1]); i++) {
                                    outbox += "\nMESSAGE " + (i + 1) + ":\n";
                                    outbox += "RECEIVER: " + words[index++] + "\n";
                                    outbox += "TIME: " + words[index++] + "\n";
                                    outbox += "TITLE: " + words[index++] + "\n";
                                    outbox += "MESSAGE: " + words[index++] + "\n";
                                }
                            }
                            System.out.println(outbox);

                            break;

                        case ("S"):
                        case ("s"):
                            String rc = "";
                            String t = "";
                            String msg = "";
                            System.out.println();
                            System.out.print("Please enter the receiver: ");
                            rc = scn.nextLine();
                            System.out.print("Please enter the title: ");
                            t = scn.nextLine();
                            System.out.print("Please enter the message: ");
                            msg = scn.nextLine();
                            tosend = "|SENDMSG|" + user.getUsername() + "|" + rc + "|" + t + "|" + msg + "|";
                            dos.writeUTF(tosend);
                            received = dis.readUTF();
                            words = received.substring(1).split("\\|");
                            if (words[0].equals("SUCCESSFUL"))
                                System.out.println("Message was sent.");
                            else
                                System.out.println("Message could not been sent.");

                            break;

                            default:
                        }

                        if(user.isAdmin()) {
                            switch (tosend) {
                                case ("A"):
                                case ("a"):
                                    String fn = "";
                                    String ln = "";
                                    String bd = "";
                                    String g = "";
                                    String e = "";
                                    un = "";
                                    pw = "";
                                    String iA = "";
                                    String ib = "";
                                    String ob = "";
                                    System.out.println();
                                    System.out.print("Please enter the first name: ");
                                    fn = scn.nextLine();
                                    System.out.print("Please enter the last name: ");
                                    ln = scn.nextLine();
                                    System.out.print("Please enter the birthdate(yyyy/mm/dd): ");
                                    bd = scn.nextLine();
                                    System.out.print("Please enter the gender (M: Male, F: Female): ");
                                    g = scn.nextLine();
                                    System.out.print("Please enter the email: ");
                                    e = scn.nextLine();
                                    System.out.print("Please enter the username: ");
                                    un = scn.nextLine();
                                    System.out.print("Please enter the password: ");
                                    pw = scn.nextLine();
                                    System.out.print("Please enter the isAdmin (T: true, F: false): ");
                                    iA = scn.nextLine();

                                    tosend = "|ADDUSER|" + fn + "|" + ln + "|" + bd + "|" + g + "|" + e + "|" + un + "|" + pw + "|" + iA + "|";
                                    dos.writeUTF(tosend);
                                    String[] words5 = dis.readUTF().substring(1).split("\\|");
                                    if (words5[0].equals("SUCCESSFUL"))
                                        System.out.println("User was added succesfully. ");
                                    else
                                        System.out.println("The user was not added.");
                                    break;

                                case ("U"):
                                case ("u"):
                                    un = "";
                                    String dtc = "";
                                    String nd = "";
                                    System.out.println();
                                    System.out.print("Enter the user's username: ");
                                    un = scn.nextLine();
                                    System.out.print("Enter the info you want to " +
                                            "change(firstname, lastname, birthdate, gender (M: Male, F: Female), email, admin (T: true, F: false), username, password: ");
                                    dtc = scn.nextLine();
                                    System.out.print("Enter the new data: ");
                                    nd = scn.nextLine();

                                    tosend = "|UPDATEUSER|" + un + "|" + dtc + "|" + nd + "|";
                                    dos.writeUTF(tosend);
                                    words = dis.readUTF().substring(1).split("\\|");
                                    if (words[0].equals("SUCCESSFUL"))
                                        System.out.println("User was updated succesfully. The updated user should login to see updated info.");
                                    else
                                        System.out.println("The user was not updated.");

                                    break;

                                case ("R"):
                                case ("r"):
                                    System.out.println();
                                    un = "";
                                    System.out.print("Enter the user's username you want to remove from the system: ");
                                    un = scn.nextLine();
                                    tosend = "|REMOVE|" + un + "|";
                                    dos.writeUTF(tosend);
                                    words = dis.readUTF().substring(1).split("\\|");
                                    if (words[0].equals("SUCCESSFUL"))
                                        System.out.println("User was removed succesfully.");
                                    else
                                        System.out.println("The user was not removed.");

                                    break;

                                case ("B"):
                                case ("b"):
                                    System.out.println("\nUSERS LIST\n");
                                    tosend = "|LIST|";
                                    dos.writeUTF(tosend);
                                    words = dis.readUTF().substring(1).split("\\|");
                                    int noOfUsers = Integer.parseInt(words[1]);
                                    String list = "USERS:\n\n";
                                    int index = 2;
                                    for (int i = 0; i < noOfUsers; i++) {
                                        list += "\nUSER " + (i + 1) + ":\n";
                                        list += "FIRST NAME: " + words[index++] + "\n";
                                        list += "LAST NAME: " + words[index++] + "\n";
                                        list += "BIRTH DATE: " + words[index++] + "\n";
                                        list += "GENDER: " + words[index++] + "\n";
                                        list += "EMAIL: " + words[index++] + "\n";
                                        list += "USERNAME: " + words[index++] + "\n";
                                        list += "PASSWORD: " + words[index++] + "\n";
                                    }
                                    System.out.println(list);

                                    break;

                                default:
                            }
                        }
                    }
                }

                // closing resources
                scn.close();
                dis.close();
                dos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
//update message server eklendi
