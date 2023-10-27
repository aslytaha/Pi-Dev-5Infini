package com.example.pidev.Entities;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table( name = "Users",uniqueConstraints = { @UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email") })
public class User implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iduser", length = 255)
    private Long idUser;



    @NotBlank(message = "Username is required")
    @Size(max = 20)
    @NotNull
    private String  Username;

    @NotNull
    private String Password;
    @Email(message = "Email is not valid")
    private String Email;
    private String FirstName;
    private String LastName;
    private String Address;
    private Date BirthDate;
    private Long Phone;
    private Long CIN;
    private Integer IsVerified;
    private String verificationToken;
    private String verificationCode;
    private Integer Age;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Users_Role",
            joinColumns = @JoinColumn(name = "iduser"),
            inverseJoinColumns = @JoinColumn(name = "idRole"))

    private Set<Role> Roles;

    @OneToOne
    private Wallet userWallet;







}


