package com.noklin.chatserver.network;  

import com.noklin.network.packets.clientpackets.*;
import com.noklin.chatserver.database.DatabaseDao; 
import com.noklin.chatserver.database.entities.Chat;
import com.noklin.chatserver.database.entities.Letter;
import com.noklin.chatserver.database.entities.LetterState;
import com.noklin.chatserver.database.entities.Photo;
import com.noklin.chatserver.database.entities.User;
import com.noklin.network.packets.*;    
import com.noklin.network.packets.serverpackets.ChatInfoPacket;
import com.noklin.network.packets.serverpackets.UserInfoPacket;
import com.noklin.network.simplechat.ChatConnection;
import java.io.IOException;  
import java.net.Socket; 
import java.util.List; 
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author noklin
 */

public class ConnectionHandler implements Runnable{ 
    private final ChatConnection mainConnection;
    private final DatabaseDao dao = new DatabaseDao();
    private final ConcurrentMap<String, ChatConnection> connectionHub;
    private String currentUserId;
    private User  userSender;
    
    
    
    public ConnectionHandler(Socket socket
            , ConcurrentMap<String, ChatConnection> connectionHub){
        this.mainConnection = new ChatConnection(socket); 
        this.connectionHub = connectionHub;
        System.out.println("Connected: " + socket.getInetAddress());
    }
        
    private AuthorizePacket authorizePacket;
    private Packet input; 
    private DataPacket dataPacket;
    private LetterStatePacket letterStatePacket;
    private RegistratePacket registratePacket;    
    
    @Override
    public void run() { 
        try(ChatConnection senderConnection = mainConnection){
            
            input = senderConnection.receivePacket();
            System.out.println("input: " + input);
            if(input == null)return;
            if(input.getType() == Packet.Type.REGISTRATE){
                registratePacket = new RegistratePacket(input);
                dao.openSession();
                userSender = dao.getUser(registratePacket.getLogin());
                if(userSender == null){
                    userSender = new User(registratePacket.getLogin() 
                        , registratePacket.getPassword());
                    if(dao.registrate(userSender)){
                        senderConnection.sendPacket(registratePacket); 
                        dao.closeSession();
                        return;
                    } 
                }
                dao.closeSession();
                senderConnection.sendPacket(new FailPacket("This account alredy exist")); 
                
            }
                    
            if(input.getType() != Packet.Type.AUTHORIZE) return;
            System.out.println("next input: " + input);
            authorizePacket = new AuthorizePacket(input);
            currentUserId = authorizePacket.getLogin();
            String password = authorizePacket.getPassword();
            System.out.println("input login " + currentUserId);
            System.out.println("input password " + password);
            dao.openSession();
            userSender = dao.getUser(currentUserId);  
            if(userSender == null || !userSender.getPassword().equals(password)) return;
               
            senderConnection.sendPacket(authorizePacket);  
            connectionHub.put(userSender.getLogin(), senderConnection);  
            for(Letter letter : userSender.getLostLetters()){ 
                System.out.println("Found lost letter for: " + currentUserId);
                dataPacket = new DataPacket(
                          letter.getContentType()
                        , letter.getAuthor().getLogin()
                        , userSender.getLogin()
                        , letter.getDate()
                        , letter.getData()
                        , letter.isChatField()
                );  
                senderConnection.sendPacket(dataPacket); 
            }  
            for(LetterState state : userSender.getLostLetterStates()){
                System.out.println("Found lost states for: " + currentUserId);
                letterStatePacket = new LetterStatePacket(state.getTarget()
                        , state.getState() , state.getLetterId());
                senderConnection.sendPacket(letterStatePacket); 
            }
            dao.removeAllLetterStates(userSender); 
            updateUserInfo(userSender);
            dao.closeSession(); 
            while((input = senderConnection.receivePacket()) != null){
                dao.openSession();
                System.out.println("received: " + input.getType());
                switch(input.getType()){
                    case DATA_PACKET:
                        dataPacket = new DataPacket(input);
                        System.out.println("CHATABLE? : " + dataPacket.isChatable());
                        if(dataPacket.isChatable()){
                            Chat targetChat = dao.getChat(dataPacket.getReceiver());
                            if(targetChat != null){
                                sendDataPacketToChat(targetChat, dataPacket);
                            }else return;
                        }else{
                            User tagretUser = dao.getUser(dataPacket.getReceiver());
                            if(tagretUser != null){
                                sendDataPacketToUser(tagretUser, dataPacket);
                            }else return;
                        }
                        break;
                        
                    case LETTER_STATE_PACKET: 
                        handleLetterStatePacket(new LetterStatePacket(input));
                        break;
                    case JOIN_CHAT:
                        joinChat(new JoinChatPacket(input)); 
                        break;
                    case LEAVE_CHAT:
                        leaveChat(new LeaveChatPacket(input));
                        break;
                    case FIND_CHAT: 
                        findChat(new FindChatPacket(input)); 
                        break;
                    case FIND_USER: 
                        findUser(new FindUserPacket(input)); 
                        break;
                    case USER_INFO_PACKET:  
                        userInfo(new UserInfoPacket(input));
                        break;  
                    case ADD_FRIEND:
                        addFriend(new AddFriendPacket(input));
                        break;
                    case REMOVE_FRIEND:
                        removeFriend(new RemoveFriendPacket(input));
                        break;
                    default: {
                        System.out.println("default");
                        return;
                    } 
                }  
                dao.closeSession();
            }
        }catch(IOException ex){
            System.out.println("Some problem: " + ex.getMessage());
        }finally{
            dao.closeSession();
            if(userSender != null){
                System.out.println("Disconnected urser: " + userSender.getLogin()); 
            }
            if(currentUserId != null){
                connectionHub.remove(userSender.getLogin()); 
            }
        } 
    }
    
    
    private void userInfo(UserInfoPacket infoPacket) throws IOException{ 
        User currentUser = dao.getUser(currentUserId);
        currentUser.setPhoto(new Photo(infoPacket.getPhoto()));
        currentUser.setPublicName(infoPacket.getPublicName());
        dao.updateUser(currentUser);
        Set<User> friends = currentUser.getFriends();
        ChatConnection friendConnetion;
        for(User friend: friends){
            friendConnetion = connectionHub.get(friend.getLogin());
            if(friendConnetion == null){
                dao.addUpdateUser(friend, currentUser); 
            }else{
                try{
                    friendConnetion.sendPacket(infoPacket); 
                }catch(IOException ex){
                    dao.addUpdateUser(friend, currentUser); 
                }
            }
        }
    }
         
    
    private void updateUserInfo(User user) throws IOException{
    UserInfoPacket infPacket;
        for(User updated: userSender.getUpdatedUsers()){
            System.out.println("Found update users: " + updated.getLogin());
            infPacket = new UserInfoPacket(
                      ""
                    , updated.getPublicName() == null ? "none" : updated.getPublicName()
                    , updated.getPhoto() == null ? new byte[0] : updated.getPhoto().getPhoto()
                    , "UPDATE");
            mainConnection.sendPacket(infPacket);
        } 
        dao.removeUpdated(user);
    }

         
    private void addFriend(AddFriendPacket friendPacket) throws IOException{ 
        User currentUser = dao.getUser(currentUserId); 
        User friend = dao.getUser(friendPacket.getTarget()); 
        if(friend == null) return;     
        if(currentUser.getFriends().contains(friend))return;
        dao.addFriend(currentUser, friend);
        mainConnection.sendPacket(friendPacket);
    }
    private void removeFriend(RemoveFriendPacket friendPacket) throws IOException{
        User currentUser = dao.getUser(currentUserId);
        User friend = dao.getUser(friendPacket.getTarget());
        if(friend == null) return;    
        if(!currentUser.getFriends().contains(friend))return;
        dao.removeFriend(currentUser, friend);
        mainConnection.sendPacket(friendPacket);
    } 
    
    private void findChat(FindChatPacket chatPacket)throws IOException{
        List<Chat> chats = dao.findChat(chatPacket.getChat()); 
        System.out.println("FOUND CHATS: " + chats);
        for(Chat chat: chats){
            mainConnection.sendPacket(new ChatInfoPacket(chat.getTitle()));
        } 
    }
    
    private void findUser(FindUserPacket userPacket)throws IOException{
        List<User> users = dao.findUser(userPacket.getUser());
        UserInfoPacket infoPacket;    
        for(User u : users){ 
            Photo photo = dao.loadPhoto(u);
            infoPacket = new UserInfoPacket(
                      u.getLogin()
                    , u.getPublicName() == null ? "" : u.getPublicName()
                    , photo == null ? new byte[0] : photo.getPhoto()
                    , "SEARCH"); 
            mainConnection.sendPacket(infoPacket);
        } 
    }
    
    private void joinChat(JoinChatPacket chatPacket) throws IOException{
        Chat chat = dao.getChat(chatPacket.getChat());
        if(chat == null){
            chat = new Chat(chatPacket.getChat());
            dao.putChat(chat);
        }
        User currentUser = dao.getUser(currentUserId);
        dao.joinChat(currentUser, chat);  
        mainConnection.sendPacket(chatPacket);
    }
    
    private void leaveChat(LeaveChatPacket chatPacket) throws IOException{
        Chat chat = dao.getChat(chatPacket.getChat());
        if(chat == null)return;
        User currentUser = dao.getUser(currentUserId);
        chat.getMembers().remove(currentUser);
        dao.leaveChat(currentUser, chat);
        mainConnection.sendPacket(chatPacket);
        
    }
    
    private Letter dataPacketToLetter(DataPacket dp){
        return new Letter( 
              dao.getUser(currentUserId)
            , dp.getData()
            , dp.getDate()
            , dp.getContentType()
            , dp.isChatable()
        ); 
    }
    
    
    private void handleLetterStatePacket(LetterStatePacket letterStatePacket){ 
        Letter letter = dao.getLetter(letterStatePacket.getLetterId());
        if(letter == null) return;
        User terget = letter.getAuthor();
        ChatConnection targetConnection = connectionHub.get(terget.getLogin());
        if(targetConnection != null){
            try{
                targetConnection.sendPacket(letterStatePacket);
            }catch(IOException ex){
                if(letter.isChatField()){
                    LetterState state = new LetterState( 
                              terget
                            , currentUserId
                            , letter.getId()
                            , letterStatePacket.getStatus());  
                    dao.addLostLetterStatetToUser(terget, state);
                }
            }
            
        }else{
            if(letter.isChatField()){
                LetterState state = new LetterState(
                          terget
                        , currentUserId
                        , letter.getId()
                        , letterStatePacket.getStatus());  
                dao.addLostLetterStatetToUser(terget, state);
            }
        }
        User currentUser = dao.getUser(currentUserId);  
        dao.removeLostLetter(currentUser, letter);
        if(letter.getReceivers().isEmpty()){
            dao.removeLetter(letter);
        } 
        
    }
    
    private void sendDataPacketToChat(Chat chat , DataPacket dp) throws IOException{
        Letter letter = dataPacketToLetter(dp); 
        for(User chatMember : chat.getMembers()){
            if(chatMember.getLogin().equals(currentUserId))continue;
            dao.addLostLetterToUser(chatMember, letter);
            ChatConnection out = connectionHub.get(chatMember.getLogin());
            if(out == null) continue;
            try{
                System.out.println("DATA_PACKET RECEIVER: " + dp.getReceiver());
                out.sendPacket(dataPacket);
            }catch(IOException ex){
                System.out.println("Broken connection: " + ex.getMessage());
            }
        }
        LetterStatePacket  statePacket = new LetterStatePacket(chat.getTitle(), "POS", letter.getId());
        try{
            mainConnection.sendPacket(statePacket);
        }catch(IOException ex){
            User currentUser = dao.getUser(currentUserId);
            LetterState state = new LetterState(currentUser, chat.getTitle(), letter.getId(), "POS");
            dao.addLostLetterStatetToUser(currentUser, state);
            throw ex;
        }
    }
    
    private void sendDataPacketToUser(User user , DataPacket dp)throws IOException{
        Letter  letter = dataPacketToLetter(dp);
        LetterStatePacket  statePacket = new LetterStatePacket(user.getLogin(), "POS", letter.getId());
        dao.addLostLetterToUser(user, letter);  
        try{
            mainConnection.sendPacket(statePacket);
        }catch(IOException ex){
            User currentUser = dao.getUser(currentUserId);
            LetterState state = new LetterState(currentUser, user.getLogin(), letter.getId(), "POS");
            dao.addLostLetterStatetToUser(currentUser, state);
            throw ex;
        }
        ChatConnection out = connectionHub.get(user.getLogin());
        if(out == null){
            dao.addLostLetterToUser(user, letter);
        }else{ 
            out.sendPacket(dp); 
        } 
    } 
}