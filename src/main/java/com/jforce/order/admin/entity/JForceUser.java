package com.jforce.order.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(schema = "sec", name = "jforce_users")
@NoArgsConstructor
public class JForceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column(nullable = false)
    private String role = "USER";

}