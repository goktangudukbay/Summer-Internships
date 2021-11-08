package com.example.consumingChat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.consumingChat.wsdl.*;


import java.util.List;

@SpringBootApplication
public class ConsumingWebServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumingWebServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner lookup(ChatClient quoteClient) {
        return args->{

                GetUserResponse response = quoteClient.getUser("serverAdmin", "admin123");
                User u = response.getUser();
                System.out.println("User info:\nFirst name: " + u.getFirstName() + "\nLast name: " +
                u.getLastName() + "\nBirthdate: " + u.getBirthDate() + "\nUsername: " + u.getUsername() +
                "\nPassword: " + u.getPassword());

                InboxResponse ir = quoteClient.getInbox("serverAdmin");
                System.out.println("Inbox");
                List<Message> l = ir.getMessage();
                for(Message m:l)
                    System.out.println("\n\nMessage\n" + "Sender: " + m.getSender() + "\nReceiver: " +
                            "\nTime" + m.getTime() + "\nTitle: " + m.getTitle() + "\nContent:" + m.getContent());


                OutboxResponse or = quoteClient.getOutbox("serverAdmin");
                System.out.println("Inbox");
                List<Message> l1 = or.getOutboxMessage();
                for(Message m:l1)
                     System.out.println("\n\nMessage\n" + "Sender: " + m.getSender() + "\nReceiver: " +
                        "\nTime" + m.getTime() + "\nTitle: " + m.getTitle() + "\nContent:" + m.getContent());


                SendMessageResponse sr = quoteClient.getSendMessage("serverAdmin" , "cristiano",
                        "12:00", "Merhaba", "Nasilsin");
                System.out.print("Message info " + sr.getMessageInfo());


        };

    }

}