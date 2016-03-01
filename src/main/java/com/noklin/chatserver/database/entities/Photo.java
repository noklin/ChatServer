package com.noklin.chatserver.database.entities;

import java.io.Serializable; 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author noklin
 */

@Entity
@Table(name = "photo")
public class Photo implements Serializable{
    
    private static final long serialVersionUID = 1L; 
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    
    @OneToOne(mappedBy = "photo")
    private User owner;
    
    @Column(name = "photo")
    private byte[] photo;
    
    public Photo(){}
    
    public Photo(byte[] photo){
        this.photo = photo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }  
}
