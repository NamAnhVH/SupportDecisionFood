package com.BTL.SupportDecision.Controller;

import com.BTL.SupportDecision.FormData.Option;
import com.BTL.SupportDecision.FormData.UserSelectionDto;
import com.BTL.SupportDecision.Service.SupportDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private SupportDecisionService supportDecisionService;

    @GetMapping()
    public String mainPage(Model model){
        List<Option> options = Arrays.asList(
                new Option("Loại món ăn", false),
                new Option("Ngân sách", false),
                new Option("Thời gian chuẩn bị", false),
                new Option("Chế độ ăn uống", false),
                new Option("Lượng dinh dưỡng", false),
                new Option("Bữa ăn", false)
        );
        model.addAttribute("test", new UserSelectionDto(options));
        return "mainPage";
    }

    @PostMapping("/check")
    public String supportDecision(Model model, @ModelAttribute UserSelectionDto userSelection){
        List<Option> selectedOptions = userSelection.getOptions().stream().filter(Option::isChecked).collect(Collectors.toList());
        for(Option selectedOption: selectedOptions){
            System.out.println(selectedOption.getName());

        }
        model.addAttribute("test", supportDecisionService.getSuggestedDish(userSelection));
        return "checkPage";
    }
}
