package com.BTL.SupportDecision.Model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "dish")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double calories;
    @Column(nullable = false)
    private Double fat;
    @Column(nullable = false)
    private int prepareTime;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private int price;
}
