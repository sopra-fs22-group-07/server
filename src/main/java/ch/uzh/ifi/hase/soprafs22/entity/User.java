package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 * - OneToMany: 1:n Relation with other entities
 * - OneToOne: 1:1 Relation with other entities
 */

@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;


    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String token;

    @Column
    private Date creationDate = new Date();

    // @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private String password;

    @Column
    private Date birthday;

    @Column
    private Gender gender;

    // to see how elementCollection works:
    /*
    @ElementCollection
    @CollectionTable(name = "whiteCard", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "whiteCards")
    private List<WhiteCard> whiteCards;
     */

    @OneToOne
    private BlackCard blackCard;

    @OneToMany
    private List<WhiteCard> whiteCards = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Date getCreationDate(){return this.creationDate; }

    public void setCreationDate(Date creationDate){this.creationDate = creationDate; }

    public Date getBirthday(){return this.birthday; }

    public void setBirthday(Date birthday){this.birthday = birthday; }

    public String getPassword(){return this.password; }

    public void setPassword(String password){this.password = password; }

    public Gender getGender(){return this.gender; }

    /**
     * Method for Testing the gender of the user in JSON, as the gender should be
     * as string and not ENUM there
      * @return User.gender as string
     */
    public String getGenderString(){
        return this.gender.toString();
    }

    public void setGender(Gender gender){this.gender = gender; }

    public BlackCard getBlackCard(){return this.blackCard; }

    public void setBlackCard(BlackCard blackCard){this.blackCard = blackCard; }

}
