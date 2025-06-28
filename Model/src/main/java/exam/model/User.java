package exam.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "Users")
public class User implements Serializable, exam.model.Entity<String> {
    @Id
    @Column(name = "username")
    String username;

    public User(String username){
        this.username = username;
    }

    public User() {
    }

    @Override
    public String getId() {
        return username;
    }

    @Override
    public void setId(String id) {
        username = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
