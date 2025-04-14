package me.sarismart.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String supabaseUid;

    private String fullName;

    @OneToMany(mappedBy = "user")
    private List<StoreRole> storeRoles;
}