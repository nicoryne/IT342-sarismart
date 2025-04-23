package me.sarismart.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(mappedBy = "workers")
    private List<Store> stores = new ArrayList<>();
}