package me.sarismart.backend.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequest {
    private String storeName;
    private String location;
    private double latitude;
    private double longitude;
    private String ownerId;
}