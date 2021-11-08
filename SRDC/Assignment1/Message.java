public class Message {

    //properties
    private String sender;
    private String receiver;
    private String time;
    private String title;
    private String content;


    //constructor
    public Message(){
        sender = "";
        receiver = "";
        time = "";
        title = "";
        content = "";
    }

    public Message(String sender, String receiver, String time, String title, String message){
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.title = title;
        this.content = message;
    }

    //methods

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
