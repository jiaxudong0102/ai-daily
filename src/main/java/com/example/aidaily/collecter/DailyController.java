package com.example.aidaily.collecter;


import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.service.DailyService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class DailyController {
    private final DailyService  dailyService;

    public DailyController(DailyService dailyService) {
        this.dailyService = dailyService;
    }
    @GetMapping("/daily")
    public String getDaily(@RequestParam(name="date", required = false) String date,
                           Model model) {
        LocalDate date1 ;
        if (date == null || date.isBlank()) {
            date1 = LocalDate.now(ZoneId.of("Asia/Shanghai"));

        }else{
        try {
            date1 = LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "日期格式应为 yyyy-MM-dd",
                    exception
            );
        }}
        List<ArticleEntity> articleEntities= dailyService.getArticles(date1);
        model.addAttribute("date", date1);
        model.addAttribute("articles", articleEntities);

        return "daily";
    }

}
