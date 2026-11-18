package com.example.aidaily.collecter;

import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.service.DailyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class DailyControllerTest {
    private final DailyService dailyService = mock(DailyService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new DailyController(dailyService))
            .setSingleView(new View() {
                @Override
                public String getContentType() {
                    return "text/html";
                }

                @Override
                public void render(Map<String, ?> model, HttpServletRequest request,
                                   HttpServletResponse response) {
                    // The Thymeleaf template is rendered separately in DailyTemplateTest.
                }
            })
            .build();

    @Test
    void showsRequestedDateAndItsArticles() throws Exception {
        LocalDate date = LocalDate.of(2026, 11, 18);
        ArticleEntity article = new ArticleEntity();
        article.setTitle("日报文章");
        List<ArticleEntity> articles = List.of(article);
        when(dailyService.getArticles(date)).thenReturn(articles);

        mvc.perform(get("/daily").param("date", "2026-11-18"))
                .andExpect(status().isOk())
                .andExpect(view().name("daily"))
                .andExpect(model().attribute("date", date))
                .andExpect(model().attribute("articles", articles));
    }

    @Test
    void defaultsToTodayInShanghaiWhenDateIsOmitted() throws Exception {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));

        mvc.perform(get("/daily"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("date", today));
    }

    @Test
    void rejectsInvalidDate() throws Exception {
        mvc.perform(get("/daily").param("date", "2026-13-40"))
                .andExpect(status().isBadRequest());
    }
}
