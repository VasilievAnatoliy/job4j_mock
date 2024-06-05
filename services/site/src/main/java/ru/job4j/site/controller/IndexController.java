package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.domain.StatusInterview;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.service.*;
import ru.job4j.site.util.InterviewPage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;
    private final InterviewPage interviewPage;
    private final ProfilesService profilesService;
    private final AuthService authService;
    private final NotificationService notifications;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model, HttpServletRequest req) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        try {
            model.addAttribute("categories", categoriesService.getMostPopular());
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO", notifications.findCategoriesByUserId(userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        List<InterviewDTO> interviews = interviewsService.getByStatus(StatusInterview.IS_NEW.getId(),
                        interviewPage.getPage(), interviewPage.getSize()).toList();
        Set<ProfileDTO> userList = profilesService.getAllProfileById(interviews);
        model.addAttribute("new_interviews", interviews);
        model.addAttribute("users", userList);
        return "index";
    }
}