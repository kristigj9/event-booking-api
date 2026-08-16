package com.lhind.event_booking_api.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nameCategory;

    private String descriptionCategory;
    //Nje kategori ka disa Evente
    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Event> events = new ArrayList<>();
}