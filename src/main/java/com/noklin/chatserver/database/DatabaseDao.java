
package com.noklin.chatserver.database;

import com.noklin.chatserver.database.entities.Chat;
import com.noklin.chatserver.database.entities.Letter; 
import com.noklin.chatserver.database.entities.LetterState;
import com.noklin.chatserver.database.entities.Photo;
import com.noklin.chatserver.database.entities.User; 
import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author noklin
 */

public class DatabaseDao {
    
    private final SessionFactory sf;
    private Session currentSession;
    
    public DatabaseDao(){
        sf = HibernateUtil.getSessionFactory();
    } 
    
    public SessionFactory getSessionfactory() {
        return sf;
    } 
      
    public Session getSession(){
        openSession();
        return currentSession;
    }
    
    public Session openSession(){
        if(currentSession == null){
            currentSession = sf.openSession();  
        }
        return currentSession;
    }
    
    public void closeSession(){
        if(currentSession!= null) 
            currentSession.close();
        currentSession = null;
    }
    
 
    
    public void putUser(User user){  
        try{ 
            currentSession.beginTransaction(); 
            currentSession.save(user);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    } 
    
    public boolean registrate(User user){
        boolean registrated = false;
        try{ 
            currentSession.beginTransaction(); 
            currentSession.save(user);
            currentSession.getTransaction().commit(); 
            registrated = true;
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
        return registrated;
    }
    
    public boolean authorize(){
        boolean authorized = false;
            
        return authorized;
    }
    
    public User getUser(String id){   
        return (User)currentSession.get(User.class , id);  
    } 
    
    public void updateUser(User user){
        try{ 
            currentSession.beginTransaction(); 
            currentSession.update(user); 
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        }
    }
    
    public void putLetter(Letter letter){ 
        try{
            currentSession.beginTransaction(); 
            currentSession.save(letter);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        }  
    }   
    
    public void putLetterState(LetterState report){
        try{
            currentSession.beginTransaction(); 
            currentSession.save(report);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
    
    public void removeLetterState(LetterState letterState){
        try{
            currentSession.beginTransaction(); 
            currentSession.delete(letterState); 
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            System.out.println("here" + ex.getMessage());
            currentSession.getTransaction().rollback();
        } 
    }
    
    public void removeLostLetterState(User user , LetterState state){
        try{
            currentSession.beginTransaction(); 
            user.getLostLetterStates().remove(state);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
        
    }
    
    public void removeLostLetter(User user , Letter letter){
        try{
            currentSession.beginTransaction(); 
            user.getLostLetters().remove(letter);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
        
    }
    
    public void removeAllLetterStates(User user){
        try{
            currentSession.beginTransaction(); 
            user.getLostLetterStates().clear();
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
    
    public void addFriend(User user, User friend){
        try{
            currentSession.beginTransaction(); 
            user.getFriends().add(friend);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
    
    public void removeFriend(User user, User friend){
        try{
            currentSession.beginTransaction(); 
            user.getFriends().remove(friend);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
    
    public void addLostLetterToUser(User user , Letter letter){
        try{
            currentSession.beginTransaction(); 
            user.getLostLetters().add(letter);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
    
    public void addUpdateUser(User user, User updatedUser){
        try{
            currentSession.beginTransaction(); 
            user.getUpdatedUsers().add(updatedUser);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        }
    }
    
    public void addLostLetterStatetToUser(User user , LetterState state){
        try{
            currentSession.beginTransaction(); 
            user.getLostLetterStates().add(state);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
    
    public void removeLetter(Letter letter){ 
        try{   
            currentSession.beginTransaction(); 
            currentSession.delete(letter); 
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            System.out.println("REM EX : " + ex.getMessage());
            currentSession.getTransaction().rollback();
        } 
    }     
    
    public void removeUpdated(User user){
        try{   
            currentSession.beginTransaction(); 
            user.getUpdatedUsers().clear();
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            System.out.println("REM EX : " + ex.getMessage());
            currentSession.getTransaction().rollback();
        }
    }
    
    public Letter getLetter(long id){ 
        return (Letter)currentSession.get(Letter.class , id);   
    }
    
    public Set<Letter> loadLostLetters(User user){   
        Hibernate.initialize(user.getLostLetters()); 
        return user.getLostLetters();  
    }  
    
        
    public Photo loadPhoto(User user){
        Hibernate.initialize(user.getPhoto()); 
        return user.getPhoto();  
    }
    
    public Set<LetterState> loadLostLetterStates(User user){
        Hibernate.initialize(user.getLostLetterStates()); 
        return user.getLostLetterStates();  
    }
    
    public Set<User> loadChatMembers(Chat chat){
        Hibernate.initialize(chat.getMembers()); 
        return chat.getMembers();   
    }    
    
    
    public void putChat(Chat chat){
        try{ 
            currentSession.beginTransaction(); 
            currentSession.save(chat);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
    
    public Chat getChat(String id){  
        return (Chat)currentSession.get(Chat.class , id);   
    } 
    
    public void joinChat(User user , Chat chat){
        try{ 
            currentSession.beginTransaction(); 
            chat.getMembers().add(user);
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
    public void leaveChat(User user , Chat chat){
        try{ 
            currentSession.beginTransaction(); 
            chat.getMembers().remove(user); 
            currentSession.getTransaction().commit(); 
        }catch(HibernateException ex){ 
            currentSession.getTransaction().rollback();
        } 
    }
       
    public List<User> findUser(String userlogin){
        Criteria criteria = currentSession.createCriteria(User.class);
        criteria.add(Restrictions.like("login", userlogin, MatchMode.ANYWHERE)); 
        return criteria.list();
    } 
    
    public List<Chat> findChat(String title){
        Criteria criteria = currentSession.createCriteria(Chat.class);
        criteria.add(Restrictions.like("title", title, MatchMode.ANYWHERE)); 
        return criteria.list();
    }   
}