package com.BTL.SupportDecision.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "meal")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private int id;

    @Column(nullable = false)
    private String meal;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dish", nullable = false)
    private Dish dish;
}
