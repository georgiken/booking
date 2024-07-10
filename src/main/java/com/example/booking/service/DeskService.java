package com.example.booking.service;

import com.example.booking.entity.Desk;
import com.example.booking.repository.DeskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeskService {

    @Autowired
    private DeskRepository deskRepository;

    public List<Desk> getAll() {
        return deskRepository.findAll();
    }

    public Optional<Desk> getById(Integer id) {
        return deskRepository.findById(id);
    }

    public Desk update(Desk desk) {
        return deskRepository.save(desk);
    }

}
