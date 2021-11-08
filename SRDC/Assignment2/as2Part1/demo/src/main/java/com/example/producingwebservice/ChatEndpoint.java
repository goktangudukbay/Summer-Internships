package com.example.producingwebservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import asm.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Endpoint
public class ChatEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    private Database db;

    @Autowired
    public ChatEndpoint() {
        db = new Database("jdbc:postgresql://localhost:5432/UserMessagingAppDatabase",
                "postgres", "Gudukbay1905");
        try{
            db.connection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        GetUserResponse response = new GetUserResponse();
        boolean succesfulLogin;
        String username = request.getUsername();
        String password = request.getPassword();
        try {
            succesfulLogin = db.loginSuccesfulCheck(username, password);
            if(succesfulLogin)
                response.setUser(db.queryAllUserInformation(username));
        }catch (SQLException e){
            e.printStackTrace();
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inboxRequest")
    @ResponsePayload
    public InboxResponse getInbox(@RequestPayload InboxRequest request) {
        InboxResponse response = new InboxResponse();
        String username = request.getUsername();
        try {
            ArrayList<Message> inbox = db.getInbox(username);
            response.getMessage().addAll(inbox);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return response;
    }



    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "outboxRequest")
    @ResponsePayload
    public OutboxResponse getOutbox(@RequestPayload OutboxRequest request) {
        OutboxResponse response = new OutboxResponse();
        String username = request.getUsername();
        try {
            ArrayList<Message> outbox = db.getOutbox(username);
            response.getOutboxMessage().addAll(outbox);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "sendMessageRequest")
    @ResponsePayload
    public SendMessageResponse sendMessageResponse(@RequestPayload SendMessageRequest request) {
        SendMessageResponse response = new SendMessageResponse();
        Message m = request.getMessageToSend();
        System.out.println(m.getSender());
        System.out.println(m.getReceiver());
        boolean success;
        try {
            success = db.sendMessage(m);
            if(success)
                response.setMessageInfo("SUCCESSFUL");
            else
                response.setMessageInfo("UNSUCCESSFUL");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addUserRequest")
    @ResponsePayload
    public AddUserResponse addUserResponse(@RequestPayload AddUserRequest request) {
        AddUserResponse response = new AddUserResponse();
        User userToBeAdded = request.getUserToAdd();
        boolean success;
        try {
            success = db.addUser(userToBeAdded);
            if(success)
                response.setAddInfo("SUCCESSFUL");
            else
                response.setAddInfo("UNSUCCESSFUL");
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateUserRequest")
    @ResponsePayload
    public UpdateUserResponse updateUserResponse(@RequestPayload UpdateUserRequest request) {
        UpdateUserResponse response = new UpdateUserResponse();
        boolean success;
        try {
            success = db.updateUser(request.getUsername(), request.getDataToChange(), request.getNewData());
            if(success)
                response.setUpdateInfo("SUCCESSFUL");
            else
                response.setUpdateInfo("UNSUCCESSFUL");
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeUserRequest")
    @ResponsePayload
    public RemoveUserResponse removeUserResponse(@RequestPayload RemoveUserRequest request) {
        RemoveUserResponse response = new RemoveUserResponse();
        boolean success;
        try {
            success = db.removeUser(request.getUsername());
            if(success)
                response.setRemoveInfo("SUCCESSFUL");
            else
                response.setRemoveInfo("UNSUCCESSFUL");
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listUsersRequest")
    @ResponsePayload
    public ListUsersResponse listUsersResponse(@RequestPayload ListUsersRequest request) {
        ListUsersResponse response = new ListUsersResponse();
        try {
            response.getUser().addAll(db.listUsers());
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

}