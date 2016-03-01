package com.noklin.chatserver.database.entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; 
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id; 
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "letter_state")
public class LetterState implements Serializable{
    
    private static final long serialVersionUID = 1L; 
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false)
    private User receiver;
    
    @Column
    private String target;
    
    @Column(name = "letter_id", nullable = false)
    private long letterId; 
    
    @Column(name = "_state", nullable = false)
    private String state;

    public LetterState(){}

    public LetterState(User receiver, String target, long letterId , String state) {
        this.letterId = letterId; 
        this.state = state;
        this.receiver = receiver;
        this.target = target;
    } 

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    } 
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }  
   
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    } 

    public long getLetterId() {
        return letterId;
    }

    public void setLetterId(long letterId) {
        this.letterId = letterId;
    } 

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
  
    
    
}