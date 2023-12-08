package com.BTL.SupportDecision.Service;

import com.BTL.SupportDecision.Enum.Attribute;
import com.BTL.SupportDecision.FormData.Option;
import com.BTL.SupportDecision.FormData.UserSelectionDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;

@Getter
@Setter
@Service
public class AttributeWeightService {

    private List<Option> selectedOptions;
    private List<Option> unselectedOptions;
    private Double satisfactionTypeWeight;
    private Double satisfactionBudgetWeight;
    private Double satisfactionPreparationTimeWeight;
    private Double satisfactionNutritionWeight;
    private Double satisfactionFatWeight;
    private Double satisfactionMealTimeWeight;

    public void calculateWeight(List<Option> options) {
        resetWeight();
        setRankOption(options);
        if (unselectedOptions.size() != 0){
            double firstFactor = calculateFirstFactor();
            calculateSelectedOptionWeight(firstFactor);
            calculateUnselectedOptionWeight(firstFactor);
        } else {
            calculateSelectedOptionWeight();
        }
    }

    private void resetWeight() {
        setSatisfactionTypeWeight(0.0);
        setSatisfactionBudgetWeight(0.0);
        setSatisfactionPreparationTimeWeight(0.0);
        setSatisfactionNutritionWeight(0.0);
        setSatisfactionFatWeight(0.0);
        setSatisfactionMealTimeWeight(0.0);
    }

    private void setRankOption(List<Option> options){
        selectedOptions = new ArrayList<>();
        unselectedOptions = new ArrayList<>();
        for(Option option: options){
            if(option.isChecked()){
                selectedOptions.add(option);
            }
            else{
                unselectedOptions.add(option);
            }
        }
        Collections.sort(selectedOptions, Comparator.comparing(Option::getPosition));
        int i = 1;
        for(Option option: selectedOptions){
            option.setRank(i);
            i++;
        }
    }

    private double calculateFirstFactor() {
        int n = selectedOptions.size();
        return 2.0/ (n * n + 3 * n + 2);
    }

    private void calculateSelectedOptionWeight() {
        int n = selectedOptions.size();
        double firstFactor = 2.0 / (n * n + n);
        for(Option option: selectedOptions){
            double weight = firstFactor * (selectedOptions.size() - option.getRank() + 1);
            setWeightAttribute(option, weight);
        }
    }

    private void calculateSelectedOptionWeight(double firstFactor) {
        for(Option option: selectedOptions){
            double weight = firstFactor * (selectedOptions.size() - option.getRank() + 2);
            setWeightAttribute(option, weight);
        }
    }

    private void calculateUnselectedOptionWeight(double firstFactor) {
        int n = unselectedOptions.size();
        for(Option option: unselectedOptions){
            double weight = firstFactor / n;
            setWeightAttribute(option, weight);
        }
    }

    private void setWeightAttribute(Option option, double weight) {
        switch (option.getName()){
            case Attribute.TYPE:
                setSatisfactionTypeWeight(weight);
                break;
            case Attribute.BUDGET:
                setSatisfactionBudgetWeight(weight);
                break;
            case Attribute.TIME_PREPARE:
                setSatisfactionPreparationTimeWeight(weight);
                break;
            case Attribute.NUTRITION:
                setSatisfactionNutritionWeight(weight);
                break;
            case Attribute.DIET:
                setSatisfactionFatWeight(weight);
                break;
            case Attribute.MEAL:
                setSatisfactionMealTimeWeight(weight);
                break;
        }
    }
}
