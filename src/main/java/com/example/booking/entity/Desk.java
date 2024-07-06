package com.example.booking.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "desks")
public class Desk {

    @Id
    private Integer id;
    private int capacity;
    private boolean available;
}
