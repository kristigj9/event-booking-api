package com.lhind.event_booking_api.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String venueName;

    @Column(nullable = false)
    private String venueAddress;

    @Column(nullable = false)
    private String venueCity;

    @Column(nullable = false)
    private Integer venueCapacity;
    //Lidhja nje venue ka disa evente
    @OneToMany(mappedBy = "venue")
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    //Lidhja e nje Veneu me disa Seat
    @OneToMany(mappedBy = "venue")
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

}