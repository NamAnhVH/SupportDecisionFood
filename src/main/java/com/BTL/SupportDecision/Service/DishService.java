package com.BTL.SupportDecision.Service;

import com.BTL.SupportDecision.Model.Dish;
import com.BTL.SupportDecision.Repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {
    @Autowired
    private DishRepository dishRepository;

    public List<Dish> getAllDish(){
        return dishRepository.findAll();
    }
}
