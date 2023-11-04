package com.BTL.SupportDecision.Service;

import com.BTL.SupportDecision.FormData.UserSelectionDto;
import com.BTL.SupportDecision.Model.Dish;
import com.BTL.SupportDecision.Model.Meal;
import com.BTL.SupportDecision.Repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;


@Service
public class TableDecisionService {

    @Autowired
    private MealRepository mealRepository;

    private Dictionary<Dish, Double> sastisfactionType;
    private Dictionary<Dish, Double> satisfactionPreparationTime;
    private Dictionary<Dish, Double> satisfactionBudget;
    private Dictionary<Dish, Double> satisfactionMealTime;
    private Dictionary<Dish, Double> satisfactionNutrition;
    private Dictionary<Dish, Double> satisfactionFat;

    private final double TIME_PRIORITY_INDEX = 0.95;
    private final double COST_PRIORITY_INDEX = 0.95;

    public void createTableDecision(List<Dish> listDish, UserSelectionDto userSelectionDto){
        calculateSatisfactionType(userSelectionDto.getDesiredType(), listDish);
        calculateSatisfactionPreparationTime(userSelectionDto.getDesiredTimeMinute(), userSelectionDto.getDesiredTimeSecond(), listDish);
        calculateSatisfactionBudget(userSelectionDto.getBudget(), listDish);
        calculateSatisfactionMealTime(listDish);
        calculateSatisfactionNutrition(userSelectionDto.getDesiredNutrition(), listDish);
        calculateSatisfactionFat(userSelectionDto.getDesiredFat(), listDish);
//        System.out.println(sastisfactionType);
//        System.out.println(satisfactionPreparationTime);
//        System.out.println(satisfactionBudget);
//        System.out.println(satisfactionMealTime);
//        System.out.println(satisfactionNutrition);
//        System.out.println(satisfactionFat);

    }

    private void calculateSatisfactionType(String type, List<Dish> listDish){
        sastisfactionType = new Hashtable<>();
        for(Dish dish: listDish){
            sastisfactionType.put(dish, dish.getType().equals(type) ? (double)1 : 0);
        }
    }

    private void calculateSatisfactionPreparationTime(int minute, int second, List<Dish> listDish){
        satisfactionPreparationTime = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(TIME_PRIORITY_INDEX * calculateTimeSecond(minute, second) - dish.getPrepareTime());
            satisfactionPreparationTime.put(dish, value);
        }
    }

    private void calculateSatisfactionBudget(int budget, List<Dish> listDish){
        satisfactionBudget = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(COST_PRIORITY_INDEX * budget - dish.getPrice());
            satisfactionBudget.put(dish, value);
        }
    }

    private void calculateSatisfactionMealTime(List<Dish> listDish){
        satisfactionMealTime = new Hashtable<>();
        LocalTime time = LocalTime.now();
        for(Dish dish: listDish){
            List<Meal> listMeal = mealRepository.findByDishId(dish.getId());
            for(Meal meal: listMeal){
                if(meal.getMeal().equals(calculateTimeMeal(time))){
                    satisfactionMealTime.put(dish, (double)1);
                    break;
                }
            }
            satisfactionMealTime.put(dish, (double)0);
        }
    }

    private void calculateSatisfactionNutrition(String desiredNutrition, List<Dish> listDish){
        satisfactionNutrition = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(calculateDesiredNutrition(desiredNutrition) - dish.getCalories());
            satisfactionNutrition.put(dish, value);
        }
    }

    private void calculateSatisfactionFat(String desiredFat, List<Dish> listDish){
        satisfactionFat = new Hashtable<>();
        for(Dish dish: listDish){
            double value = Math.abs(calculateDesiredFat(desiredFat) - dish.getFat());
            satisfactionFat.put(dish, value);
        }
    }

    private int calculateTimeSecond(int minute, int second){
        return minute * 60 + second;
    }

    private String calculateTimeMeal(LocalTime time){
        if(time.getHour() >= 5 && time.getHour() < 11){
            return "Bữa sáng";
        }
        if(time.getHour() >= 11 && time.getHour() < 13){
            return "Bữa trưa";
        }
        if(time.getHour() >= 13 && time.getHour() < 18){
            return "Bữa chiều";
        }
        return "Bữa tối";
    }

    private double calculateDesiredNutrition(String desiredNutrition){
        double amountNutrition = 0;
        switch (desiredNutrition){
            case "Ăn rất no":
                amountNutrition = 1250;
                break;
            case "Ăn no":
                amountNutrition = 875;
                break;
            case "Ăn vừa":
                amountNutrition = 750;
                break;
            case "Ăn ít":
                amountNutrition = 450;
                break;
            case "Ăn rất ít":
                amountNutrition = 200;
                break;
            default:
                amountNutrition = 725;
                break;
        }
        return amountNutrition;
    }

    private double calculateDesiredFat(String desiredFat){
        double amountFat = 0;
        switch (desiredFat){
            case "Ăn kiêng":
                amountFat = 12.5;
                break;
            case "Ăn bình thường":
                amountFat = 22.5;
                break;
            case "Ăn tăng cân":
                amountFat = 27.5;
                break;
            default:
                amountFat = 20;
                break;
        }
        return amountFat;
    }
}
