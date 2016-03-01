package com.noklin.chatserver.database.entities;

 
import java.io.Serializable;
import java.util.HashSet; 
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType; 
import javax.persistence.Id; 
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;   
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "login" , nullable = false)
    private String login;
    
    @Column(name = "password" , nullable = false)
    private String password;
    
    @Column(name = "public_name")
    private String publicName;
     
    
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Photo photo;
     
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<LetterState> lostLetterStates;
      
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Letter> lostLetters = new HashSet<>(); 
    
    @OneToMany(mappedBy = "author", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Letter> outputLetters = new HashSet<>();
    
    
    @ManyToMany(mappedBy = "members")
    private Set<Chat> joinChats = new HashSet<>();
    
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_user")
    private Set<User> friends;
    
    @ManyToMany(mappedBy = "friends")
    private Set<User> user;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_updated_user")
    private Set<User> updatedUsers;
    
    @ManyToMany(mappedBy = "updatedUsers")
    private Set<User> _user;
    
    
    public User(){}
    public User(String login, String password){ 
        this.login = login;
        this.password = password;
    }    

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public Set<User> getUpdatedUsers() {
        return updatedUsers;
    }

    public void setUpdatedUsers(Set<User> updatedUsers) {
        this.updatedUsers = updatedUsers;
    }
     
 
    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }
 
    public String getLogin() {
        return login;
    }

    public Set<LetterState> getLostLetterStates() {
        return lostLetterStates;
    }

    public void setLostLetterStates(Set<LetterState> lostLetterStates) {
        this.lostLetterStates = lostLetterStates;
    }

    public Set<Letter> getLostLetters() {
        return lostLetters;
    }

    public void setLostLetters(Set<Letter> lostLetters) {
        this.lostLetters = lostLetters;
    }

    public Set<Letter> getOutputLetters() {
        return outputLetters;
    }

    public void setOutputLetters(Set<Letter> outputLetters) {
        this.outputLetters = outputLetters;
    }
  
    
    
    public void setLogin(String login) {
        this.login = login;
    }
 
    
    @Override
    public String toString() {
        return "User: " + login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }  

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        return getLogin().equals(other.getLogin()) ;
    } 

    public Set<Chat> getJoinChats() {
        return joinChats;
    }

    public void setJoinChats(Set<Chat> joinChats) {
        this.joinChats = joinChats;
    }
 
}