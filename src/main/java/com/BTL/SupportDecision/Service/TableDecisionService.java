package com.BTL.SupportDecision.Service;

import com.BTL.SupportDecision.Enum.MealTime;
import com.BTL.SupportDecision.Enum.Nutrition;
import com.BTL.SupportDecision.Enum.Diet;
import com.BTL.SupportDecision.FormData.UserSelectionDto;
import com.BTL.SupportDecision.Model.Dish;
import com.BTL.SupportDecision.Model.Meal;
import com.BTL.SupportDecision.Repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;


@Service
public class TableDecisionService {

    @Autowired
    private MealRepository mealRepository;

    public static final int NUMBER_OF_ATTRIBUTE = 6;

    private Map<Dish, Double> sastisfactionType;
    private Map<Dish, Double> satisfactionBudget;
    private Map<Dish, Double> satisfactionPreparationTime;
    private Map<Dish, Double> satisfactionNutrition;
    private Map<Dish, Double> satisfactionFat;
    private Map<Dish, Double> satisfactionMealTime;


    private final double TIME_PRIORITY_INDEX = 0.95;
    private final double COST_PRIORITY_INDEX = 0.95;

    public void createTableDecision(List<Dish> listDish, UserSelectionDto userSelectionDto){
        calculateAttribute(userSelectionDto, listDish);
        normalizedAttribute();
    }

    public void calculateAttributeValueByWeight(AttributeWeightService attributeWeightService) {
        calculateValueByWeight(sastisfactionType ,attributeWeightService.getSatisfactionTypeWeight());
        calculateValueByWeight(satisfactionBudget ,attributeWeightService.getSatisfactionBudgetWeight());
        calculateValueByWeight(satisfactionPreparationTime ,attributeWeightService.getSatisfactionPreparationTimeWeight());
        calculateValueByWeight(satisfactionNutrition ,attributeWeightService.getSatisfactionNutritionWeight());
        calculateValueByWeight(satisfactionFat ,attributeWeightService.getSatisfactionFatWeight());
        calculateValueByWeight(satisfactionMealTime ,attributeWeightService.getSatisfactionMealTimeWeight());
    }

    private void calculateAttribute(UserSelectionDto userSelectionDto, List<Dish> listDish) {
        sastisfactionType = calculateSatisfactionType(userSelectionDto.getDesiredType(), listDish);
        satisfactionBudget = calculateSatisfactionBudget(userSelectionDto.getBudget(), listDish);
        satisfactionPreparationTime = calculateSatisfactionPreparationTime(userSelectionDto.getDesiredTimeMinute(), userSelectionDto.getDesiredTimeSecond(), listDish);
        satisfactionNutrition = calculateSatisfactionNutrition(userSelectionDto.getDesiredNutrition(), listDish);
        satisfactionFat = calculateSatisfactionFat(userSelectionDto.getDesiredFat(), listDish);
        satisfactionMealTime = calculateSatisfactionMealTime(listDish);
    }

    private void normalizedAttribute() {
        normalizedMinBetterAttribute(satisfactionBudget);
        normalizedMinBetterAttribute(satisfactionPreparationTime);
        normalizedMaxBetterAttribute(satisfactionNutrition);
        normalizedMaxBetterAttribute(satisfactionFat);
    }

    private Map<Dish, Double> calculateSatisfactionType(String type, List<Dish> listDish){
        Map <Dish,Double> sastisfactionAttribute = new Hashtable<>();
        for(Dish dish: listDish){
            sastisfactionAttribute.put(dish, dish.getType().equals(type) ? (double)1 : 0);
        }
        return sastisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionBudget(int budget, List<Dish> listDish) {
        Map<Dish, Double> sastisfactionAttribute = new Hashtable<>();
        for (Dish dish : listDish) {
            double value = Math.abs(COST_PRIORITY_INDEX * budget - dish.getPrice());
            sastisfactionAttribute.put(dish, value);
        }
        return sastisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionPreparationTime(int minute, int second, List<Dish> listDish){
        Map <Dish,Double> sastisfactionAttribute = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(TIME_PRIORITY_INDEX * calculateTimeSecond(minute, second) - dish.getPrepareTime());
            sastisfactionAttribute.put(dish, value);
        }
        return sastisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionNutrition(String desiredNutrition, List<Dish> listDish){
        Map <Dish,Double> sastisfactionAttribute = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(calculateDesiredNutrition(desiredNutrition) - dish.getCalories());
            sastisfactionAttribute.put(dish, value);
        }
        return sastisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionFat(String desiredFat, List<Dish> listDish){
        Map <Dish,Double> sastisfactionAttribute = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(calculateDesiredFat(desiredFat) - dish.getFat());
            sastisfactionAttribute.put(dish, value);
        }
        return sastisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionMealTime(List<Dish> listDish){
        Map <Dish,Double> sastisfactionAttribute = new Hashtable<>();
        LocalTime time = LocalTime.now();
        for(Dish dish: listDish){
            List<Meal> listMeal = mealRepository.findByDishId(dish.getId());
            for(Meal meal: listMeal){
                if(meal.getMeal().equals(calculateTimeMeal(time).getValue())){
                    sastisfactionAttribute.put(dish, (double)1);
                    break;
                }
            }
            sastisfactionAttribute.put(dish, (double)0);
        }
        return sastisfactionAttribute;
    }

    private void normalizedMinBetterAttribute(Map<Dish, Double> attribute) {
        double maxValue = 0;
        double minValue = Double.MAX_VALUE;
        for(Map.Entry<Dish, Double> attr: attribute.entrySet()){
            maxValue = attr.getValue() > maxValue ? attr.getValue() : maxValue;
            minValue = attr.getValue() < minValue ? attr.getValue() : minValue;
        }
        for(Map.Entry<Dish, Double> attr: attribute.entrySet()){
            double value = (maxValue - attr.getValue()) / (maxValue - minValue);
            attr.setValue(value);
        }
    }

    private void normalizedMaxBetterAttribute(Map<Dish, Double> attribute) {
        double maxValue = 0;
        double minValue = Double.MAX_VALUE;
        for(Map.Entry<Dish, Double> attr: attribute.entrySet()){
            maxValue = attr.getValue() > maxValue ? attr.getValue() : maxValue;
            minValue = attr.getValue() < minValue ? attr.getValue() : minValue;
        }
        for(Map.Entry<Dish, Double> attr: attribute.entrySet()){
            double value = (attr.getValue() - minValue) / (maxValue - minValue);
            attr.setValue(value);
        }
    }

    private int calculateTimeSecond(int minute, int second){
        return minute * 60 + second;
    }

    private MealTime calculateTimeMeal(LocalTime time){
        if(time.getHour() >= 5 && time.getHour() < 11){
            return MealTime.BREAKFAST;
        }
        if(time.getHour() >= 11 && time.getHour() < 13){
            return MealTime.LUNCH;
        }
        if(time.getHour() >= 13 && time.getHour() < 18){
            return MealTime.AFTERNOON;
        }
        return MealTime.DINNER;
    }

    private double calculateDesiredNutrition(String desiredNutrition){
        double amountNutrition = 0;
        switch (desiredNutrition){
            case Nutrition.HIGH_CALORIE:
                amountNutrition = 500;
                break;
            case Nutrition.RICH_CALORIE:
                amountNutrition = 350;
                break;
            case Nutrition.STANDARD_CALORIE:
                amountNutrition = 300;
                break;
            case Nutrition.LOW_CALORIE:
                amountNutrition = 180;
                break;
            case Nutrition.LITTLE_CALORIE:
                amountNutrition = 80;
                break;
            default:
                amountNutrition = 290;
                break;
        }
        return amountNutrition;
    }

    private double calculateDesiredFat(String desiredFat){
        double amountFat = 0;
        switch (desiredFat){
            case Diet.LOSE_WEIGHT:
                amountFat = 4;
                break;
            case Diet.STANDARD:
                amountFat = 7.5;
                break;
            case Diet.GAIN_WEIGHT:
                amountFat = 12;
                break;
            default:
                amountFat = 9;
                break;
        }
        return amountFat;
    }


    private void calculateValueByWeight(Map<Dish, Double> attribute ,Double weight) {
        for(Map.Entry<Dish, Double> attr: attribute.entrySet()){
             attr.setValue(attr.getValue() * weight);
        }
    }

    public Map<Dish, Double> getSastisfactionType() {
        return sastisfactionType;
    }

    public Map<Dish, Double> getSatisfactionBudget() {
        return satisfactionBudget;
    }

    public Map<Dish, Double> getSatisfactionPreparationTime() {
        return satisfactionPreparationTime;
    }

    public Map<Dish, Double> getSatisfactionNutrition() {
        return satisfactionNutrition;
    }

    public Map<Dish, Double> getSatisfactionFat() {
        return satisfactionFat;
    }

    public Map<Dish, Double> getSatisfactionMealTime() {
        return satisfactionMealTime;
    }

}
