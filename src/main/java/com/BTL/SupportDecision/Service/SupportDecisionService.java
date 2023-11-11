package com.BTL.SupportDecision.Service;

import com.BTL.SupportDecision.FormData.UserSelectionDto;
import com.BTL.SupportDecision.Model.Dish;
import com.BTL.SupportDecision.Model.Meal;
import com.BTL.SupportDecision.Repository.DishRepository;
import com.BTL.SupportDecision.Repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Service
public class SupportDecisionService {


    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private TableDecisionService tableDecisionService;
    @Autowired
    private AttributeWeightService attributeWeightService;
    @Autowired
    private TopsisService topsisService;

    public List<Dish> getSuggestedDish(UserSelectionDto userSelection){
        List<Dish> listDish = dishRepository.findAll();
        tableDecisionService.createTableDecision(listDish, userSelection);
        attributeWeightService.calculateWeight(userSelection.getOptions());
        tableDecisionService.calculateAttributeValueByWeight(attributeWeightService);
        List<Dish> mostSuitableDishes = topsisService.findMostSuitableDish(listDish, tableDecisionService);
        return mostSuitableDishes;
    }




}
