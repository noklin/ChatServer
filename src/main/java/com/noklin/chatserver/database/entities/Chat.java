package com.noklin.chatserver.database.entities;

import java.io.Serializable;
import java.util.HashSet; 
import java.util.Set; 
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;  
import javax.persistence.Id;  
import javax.persistence.ManyToMany; 
import javax.persistence.Table;

/**
 *
 * @author noklin
 */

@Entity
@Table(name = "chat")
public class Chat implements Serializable{
    
    private static final long serialVersionUID = 1L;  
       
    @Id
    @Column(name = "title" , nullable = false)
    private String title;
     
    
    @ManyToMany(cascade = {CascadeType.ALL}) 
    private Set<User> members = new HashSet<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

      
    public Chat(){}
    public Chat(String title){
        this.title = title;
    } 

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "chat title: " + title;
    } 
}
