package com.BTL.SupportDecision.Service;

import com.BTL.SupportDecision.FormData.Option;
import com.BTL.SupportDecision.FormData.UserSelectionDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttributeWeightService {

    public void calculateWeight(UserSelectionDto userSelection) {
        List<Option> selectedOptions = userSelection.getOptions().stream().filter(Option::isChecked).collect(Collectors.toList());
        for(Option selectedOption: selectedOptions){
            System.out.println(selectedOption.getName());

        }
    }
}
