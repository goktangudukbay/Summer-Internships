package com.example.consumingChat;
import com.example.consumingChat.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.example.consumingChat.wsdl.*;
//import com.example.consumingwebservice.wsdl.GetCountryResponse;

public class ChatClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(ChatClient.class);

    public GetUserResponse getUser(String username, String password) {

        GetUserRequest request = new GetUserRequest();
        request.setUsername(username);
        request.setPassword(password);

        log.info("Requesting info for " + username + "with the password " + password);

        GetUserResponse response = (GetUserResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/chat", request,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/GetUserResponse"));

        return response;
    }

    public InboxResponse getInbox(String username) {

        InboxRequest request = new InboxRequest();
        request.setUsername(username);

        log.info("Requesting inbox for " + username);

        InboxResponse response = (InboxResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/chat", request,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/InboxResponse"));

        return response;
    }

    public OutboxResponse getOutbox(String username) {

        OutboxRequest request = new OutboxRequest();
        request.setUsername(username);

        log.info("Requesting outbox for " + username);

        OutboxResponse response = (OutboxResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/chat", request,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/OutboxResponse"));

        return response;
    }

    public SendMessageResponse getSendMessage(String sender, String receiver, String time, String title, String content) {

        SendMessageRequest request = new SendMessageRequest();
        Message m = new Message();
        m.setSender(sender);
        m.setReceiver(receiver);
        m.setTime(time);
        m.setTitle(title);
        m.setContent(content);
        request.setMessageToSend(m);

        log.info("Sending a message in the form " + sender + receiver + time + title + content);

        SendMessageResponse response = (SendMessageResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/chat", request,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/SendMessageResponse"));

        return response;
    }

    public AddUserResponse getAddUser(String firstname, String lastname, String birthdate, String gender, String email,
                                      String username, String password, String isAdmin) {

        AddUserRequest request = new AddUserRequest();
        User u = new User();
        u.setFirstName(firstname);
        u.setLastName(lastname);
        u.setBirthDate(birthdate);
        u.setGender(gender);
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(password);
        if(isAdmin.equals("T"))
            u.setIsAdmin(true);
        else
            u.setIsAdmin(true);
        request.setUserToAdd(u);

        log.info("Adding a user with the username " + username);

        AddUserResponse response = (AddUserResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/chat", request,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/AddUserResponse"));

        return response;
    }

    public RemoveUserResponse getRemoveUser(String username) {

        RemoveUserRequest request = new RemoveUserRequest();
        request.setUsername(username);

        log.info("Removing user with the username " + username);

        RemoveUserResponse response = (RemoveUserResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/chat", request,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/RemoveUserResponse"));

        return response;
    }

    public UpdateUserResponse getUpdateUser(String username, String dataToChange, String newData) {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername(username);
        request.setDataToChange(dataToChange);
        request.setNewData(newData);

        log.info("Updating user with the username " + username);

        UpdateUserResponse response = (UpdateUserResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/chat", request,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/UpdateUserResponse"));

        return response;
    }

    public ListUsersResponse getListUser() {

        ListUsersRequest request = new ListUsersRequest();

        log.info("Listing Users");

        ListUsersResponse response = (ListUsersResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8080/ws/chat", request,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/ListUsersResponse"));

        return response;
    }



}