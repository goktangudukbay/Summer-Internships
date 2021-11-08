public class User {

    //properties
    private String name;
    private String lastName;
    private String birthDate;
    //gender: 0 for invalid, M for male, F for female
    private char gender;
    private String email;
    private String username;
    private String password;
    private boolean isAdmin;



    //constructors
    public User(){
        name = "";
        lastName = "";
        birthDate = "";
        gender = '0';
        email = "";
        username = "";
        password = "";
        isAdmin = false;
    }

    //input is in the form |ADDUSER|firstname|lastName|birthdate|gender|email|username|passwordcolumn|isAdmin|
    public User(String input){
        String[] words = input.split("\\|");
        name = words[1];
        lastName = words[2];
        birthDate = words[3];
        gender = words[4].charAt(0);
        email = words[5];
        username = words[6];
        password = words[7];
        if(words[8].charAt(0) == 'T' || words[8].charAt(1) == 't')
            isAdmin = true;
        else
            isAdmin = false;
    }

    public User(String name, String lastName, String birthDate, char gender, String email, String username, String password, boolean isAdmin) {
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }


    //methods

    //get methods
    public String getName() {
        return name;
    }

    public String getLastName(){
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public char getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin(){
        return isAdmin;
    }


    //set methods


    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    public void welcomeUser(){
        System.out.println("\nWelcome " + name + " " + lastName + ".\n");
    }
}
