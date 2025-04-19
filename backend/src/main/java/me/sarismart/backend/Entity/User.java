package me.sarismart.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    private String supabaseUid;

    private String email;

    private String fullName;

    private String phone;
}