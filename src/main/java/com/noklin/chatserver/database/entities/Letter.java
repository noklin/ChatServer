package com.noklin.chatserver.database.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType; 
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne; 
import javax.persistence.Table;

/**
 *
 * @author noklin
 */

@Entity
@Table(name = "letter")
public class Letter implements Serializable{
    
    private static final long serialVersionUID = 1L; 
    
    @ManyToMany(mappedBy = "lostLetters", fetch = FetchType.LAZY)
    private Set<User> receivers = new HashSet<>();
    
    @Id
    private long id;
    
    @Column(name = "chat_field" , nullable = false)
    private boolean chatField = false;
  
    
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false)
    private User author;  
     
    @Column(name = "letter_data" , nullable = false , columnDefinition = "MEDIUMBLOB")
    private byte[] data;
    
    
    @Column(name = "letter_date" , nullable = false)
    private long date;
    
    @Column(name = "content_type" , nullable = false)
    private String contentType;
    
  
    
    public Letter(){}

    public Letter(User author,  byte[] data, long date, String contentType , boolean chatField) {
        this.id = author.getLogin().hashCode() + date; 
        this.data = data;
        this.date = date;
        this.contentType = contentType;
        this.chatField = chatField;
        this.author = author;
    }

    
    
 
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Set<User> getReceivers() {
        return receivers;
    }

    public void setReceivers(Set<User> receivers) {
        this.receivers = receivers;
    }
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isChatField() {
        return chatField;
    }

    public void setChatField(boolean chatField) {
        this.chatField = chatField;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    } 

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    } 
}