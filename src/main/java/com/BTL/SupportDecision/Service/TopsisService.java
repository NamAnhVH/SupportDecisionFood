package com.BTL.SupportDecision.Service;

import com.BTL.SupportDecision.Model.Dish;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TopsisService {

    private List<Double> bestSolution;
    private List<Double> worstSolution;
    private Map<Dish, Double> distanceToBestSolution;
    private Map<Dish, Double> distanceToWorstSolution;
    private Map<Dish, Double> similarity;

    public List<Dish> findMostSuitableDish(List<Dish> listDish, TableDecisionService tableDecisionService) {
        List<Dish> mostSuitableDishes = new ArrayList<>();

        bestSolution = getBestSolution(tableDecisionService);
        worstSolution = getWorstSolution(tableDecisionService);
        distanceToBestSolution = calculateDistanceToBestSolution(listDish,tableDecisionService);
        distanceToWorstSolution = calculateDistanceToWorstSolution(listDish,tableDecisionService);
        similarity = calculateSimilarity(listDish);
        List<Map.Entry<Dish, Double>> entryList = new ArrayList<>(similarity.entrySet());

        // Sắp xếp entryList theo giá trị giảm dần
        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Lấy n giá trị đầu tiên
        List<Map.Entry<Dish, Double>> topEntries = entryList.subList(0, Math.min(3, entryList.size()));
        for (Map.Entry<Dish, Double> entry : topEntries) {
            mostSuitableDishes.add(entry.getKey());
        }
        return mostSuitableDishes;
    }

    private List<Double> getBestSolution(TableDecisionService tableDecisionService) {
        List<Double> bestValue = new ArrayList<>();
        bestValue.add(getBestSolution(tableDecisionService.getSastisfactionType()));
        bestValue.add(getBestSolution(tableDecisionService.getSatisfactionBudget()));
        bestValue.add(getBestSolution(tableDecisionService.getSatisfactionPreparationTime()));
        bestValue.add(getBestSolution(tableDecisionService.getSatisfactionNutrition()));
        bestValue.add(getBestSolution(tableDecisionService.getSatisfactionFat()));
        bestValue.add(getBestSolution(tableDecisionService.getSatisfactionMealTime()));
        return bestValue;
    }

    private List<Double> getWorstSolution(TableDecisionService tableDecisionService) {
        List<Double> worstValue = new ArrayList<>();
        worstValue.add(getWorstSolution(tableDecisionService.getSastisfactionType()));
        worstValue.add(getWorstSolution(tableDecisionService.getSatisfactionBudget()));
        worstValue.add(getWorstSolution(tableDecisionService.getSatisfactionPreparationTime()));
        worstValue.add(getWorstSolution(tableDecisionService.getSatisfactionNutrition()));
        worstValue.add(getWorstSolution(tableDecisionService.getSatisfactionFat()));
        worstValue.add(getWorstSolution(tableDecisionService.getSatisfactionMealTime()));
        return worstValue;
    }

    private Double getBestSolution(Map<Dish, Double> attribute) {
        double maxValue = 0;
        for(Map.Entry<Dish, Double> attr: attribute.entrySet()){
            maxValue = attr.getValue() > maxValue ? attr.getValue() : maxValue;
        }
        return maxValue;
    }

    private Double getWorstSolution(Map<Dish, Double> attribute) {
        double minValue = Double.MAX_VALUE;
        for(Map.Entry<Dish, Double> attr: attribute.entrySet()){
            minValue = attr.getValue() < minValue ? attr.getValue() : minValue;
        }
        return minValue;
    }

    private Map<Dish, Double> calculateDistanceToBestSolution(List<Dish> listDish, TableDecisionService tableDecisionService) {
        Map<Dish, Double> distanceToBest = new HashMap<>();
        for(Dish dish: listDish){
            List<Double> attributeValues = new ArrayList<>();
            attributeValues.add(tableDecisionService.getSastisfactionType().get(dish) - bestSolution.get(0));
            attributeValues.add(tableDecisionService.getSatisfactionBudget().get(dish) - bestSolution.get(1));
            attributeValues.add(tableDecisionService.getSatisfactionPreparationTime().get(dish) - bestSolution.get(2));
            attributeValues.add(tableDecisionService.getSatisfactionNutrition().get(dish) - bestSolution.get(3));
            attributeValues.add(tableDecisionService.getSatisfactionFat().get(dish) - bestSolution.get(4));
            attributeValues.add(tableDecisionService.getSatisfactionMealTime().get(dish) - bestSolution.get(5));
            Double distance = 0.0;
            for (Double value: attributeValues){
                distance += value * value;
            }
            distanceToBest.put(dish, Math.sqrt(distance));
        }
        return distanceToBest;
    }

    private Map<Dish, Double> calculateDistanceToWorstSolution(List<Dish> listDish, TableDecisionService tableDecisionService) {
        Map<Dish, Double> distanceToWorst = new HashMap<>();
        for(Dish dish: listDish){
            List<Double> attributeValues = new ArrayList<>();
            attributeValues.add(tableDecisionService.getSastisfactionType().get(dish) - worstSolution.get(0));
            attributeValues.add(tableDecisionService.getSatisfactionBudget().get(dish) - worstSolution.get(1));
            attributeValues.add(tableDecisionService.getSatisfactionPreparationTime().get(dish) - worstSolution.get(2));
            attributeValues.add(tableDecisionService.getSatisfactionNutrition().get(dish) - worstSolution.get(3));
            attributeValues.add(tableDecisionService.getSatisfactionFat().get(dish) - worstSolution.get(4));
            attributeValues.add(tableDecisionService.getSatisfactionMealTime().get(dish) - worstSolution.get(5));
            Double distance = 0.0;
            for (Double value: attributeValues){
                distance += value * value;
            }
            distanceToWorst.put(dish, Math.sqrt(distance));
        }
        return distanceToWorst;
    }

    private Map<Dish, Double> calculateSimilarity(List<Dish> listDish) {
        Map<Dish, Double> similarity = new HashMap<>();
        for(Dish dish: listDish){
            Double value = distanceToWorstSolution.get(dish) / (distanceToBestSolution.get(dish) + distanceToWorstSolution.get(dish));
            similarity.put(dish, value);
        }
        return similarity;
    }

}
