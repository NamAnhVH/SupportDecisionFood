package com.BTL.SupportDecision.Service;

import com.BTL.SupportDecision.Enum.MealTime;
import com.BTL.SupportDecision.Enum.Nutrition;
import com.BTL.SupportDecision.Enum.Diet;
import com.BTL.SupportDecision.FormData.UserSelectionDto;
import com.BTL.SupportDecision.Model.Dish;
import com.BTL.SupportDecision.Model.Meal;
import com.BTL.SupportDecision.Repository.MealRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;


@Service
public class TableDecisionService {

    @Autowired
    private MealRepository mealRepository;

    public static final int NUMBER_OF_ATTRIBUTE = 6;

    @Getter
    private Map<Dish, Double> satisfactionType;
    @Getter
    private Map<Dish, Double> satisfactionBudget;
    @Getter
    private Map<Dish, Double> satisfactionPreparationTime;
    @Getter
    private Map<Dish, Double> satisfactionNutrition;
    @Getter
    private Map<Dish, Double> satisfactionFat;
    @Getter
    private Map<Dish, Double> satisfactionMealTime;


    private final double TIME_PRIORITY_INDEX = 0.99;
    private final double COST_PRIORITY_INDEX = 0.99;

    public void createTableDecision(List<Dish> listDish, UserSelectionDto userSelectionDto){
        calculateAttribute(userSelectionDto, listDish);
        normalizedAttribute();
    }

    public void calculateAttributeValueByWeight(AttributeWeightService attributeWeightService) {
        calculateValueByWeight(satisfactionType,attributeWeightService.getSatisfactionTypeWeight());
        calculateValueByWeight(satisfactionBudget ,attributeWeightService.getSatisfactionBudgetWeight());
        calculateValueByWeight(satisfactionPreparationTime ,attributeWeightService.getSatisfactionPreparationTimeWeight());
        calculateValueByWeight(satisfactionNutrition ,attributeWeightService.getSatisfactionNutritionWeight());
        calculateValueByWeight(satisfactionFat ,attributeWeightService.getSatisfactionFatWeight());
        calculateValueByWeight(satisfactionMealTime ,attributeWeightService.getSatisfactionMealTimeWeight());
    }

    private void calculateAttribute(UserSelectionDto userSelectionDto, List<Dish> listDish) {
        satisfactionType = calculateSatisfactionType(userSelectionDto.getDesiredType(), listDish);
        satisfactionBudget = calculateSatisfactionBudget(userSelectionDto.getBudget(), listDish);
        satisfactionPreparationTime = calculateSatisfactionPreparationTime(userSelectionDto.getDesiredTimeMinute(), listDish);
        satisfactionNutrition = calculateSatisfactionNutrition(userSelectionDto.getDesiredNutrition(), listDish);
        satisfactionFat = calculateSatisfactionFat(userSelectionDto.getDesiredFat(), listDish);
        satisfactionMealTime = calculateSatisfactionMealTime(listDish);
    }

    private void normalizedAttribute() {
        normalizedAttribute(satisfactionBudget);
        normalizedAttribute(satisfactionPreparationTime);
        normalizedAttribute(satisfactionNutrition);
        normalizedAttribute(satisfactionFat);
    }

    private Map<Dish, Double> calculateSatisfactionType(String type, List<Dish> listDish){
        Map <Dish,Double> satisfactionAttribute = new Hashtable<>();
        for(Dish dish: listDish){
            satisfactionAttribute.put(dish, dish.getType().equals(type) ? (double)1 : 0);
        }
        return satisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionBudget(int budget, List<Dish> listDish) {
        Map<Dish, Double> satisfactionAttribute = new Hashtable<>();
        for (Dish dish : listDish) {
            double value = Math.abs(COST_PRIORITY_INDEX * budget - dish.getPrice());
            satisfactionAttribute.put(dish, value);
        }
        return satisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionPreparationTime(int minute, List<Dish> listDish){
        Map <Dish,Double> satisfactionAttribute = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(TIME_PRIORITY_INDEX * minute - dish.getPrepareTime());
            satisfactionAttribute.put(dish, value);
        }
        return satisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionNutrition(String desiredNutrition, List<Dish> listDish){
        Map <Dish,Double> satisfactionAttribute = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(calculateDesiredNutrition(desiredNutrition) - dish.getCalories());
            satisfactionAttribute.put(dish, value);
        }
        return satisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionFat(String desiredFat, List<Dish> listDish){
        Map <Dish,Double> satisfactionAttribute = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(calculateDesiredFat(desiredFat) - dish.getFat());
            satisfactionAttribute.put(dish, value);
        }
        return satisfactionAttribute;
    }

    private Map<Dish, Double> calculateSatisfactionMealTime(List<Dish> listDish){
        Map <Dish,Double> satisfactionAttribute = new Hashtable<>();
        LocalTime time = LocalTime.now();
        String mealTime = String.valueOf(calculateTimeMeal(time));
        for(Dish dish: listDish){
            List<Meal> listMeal = mealRepository.findByDishId(dish.getId());
            for(Meal meal: listMeal){
                if(meal.getMeal().equals(mealTime)){
                    satisfactionAttribute.put(dish, (double)1);
                    break;
                }
            }
            if(!satisfactionAttribute.containsKey(dish)){
                satisfactionAttribute.put(dish, (double)0);
            }
        }
        return satisfactionAttribute;
    }

    private void normalizedAttribute(Map<Dish, Double> attribute) {
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

    private MealTime calculateTimeMeal(LocalTime time){
        if(time.getHour() >= 5 && time.getHour() < 11){
            return MealTime.BREAKFAST;
        }
        if(time.getHour() >= 11 && time.getHour() < 15){
            return MealTime.LUNCH;
        }
        return MealTime.DINNER;
    }

    private double calculateDesiredNutrition(String desiredNutrition){
        return switch (desiredNutrition) {
            case Nutrition.HIGH_CALORIE -> 500;
            case Nutrition.RICH_CALORIE -> 380;
            case Nutrition.STANDARD_CALORIE -> 300;
            case Nutrition.LOW_CALORIE -> 180;
            case Nutrition.LITTLE_CALORIE -> 80;
            default -> 290;
        };
    }

    private double calculateDesiredFat(String desiredFat){
        return switch (desiredFat) {
            case Diet.LOSE_WEIGHT -> 12.5;
            case Diet.STANDARD -> 22.5;
            case Diet.GAIN_WEIGHT -> 27.5;
            default -> 9;
        };
    }


    private void calculateValueByWeight(Map<Dish, Double> attribute ,Double weight) {
        for(Map.Entry<Dish, Double> attr: attribute.entrySet()){
             attr.setValue(attr.getValue() * weight);
        }
    }

}
