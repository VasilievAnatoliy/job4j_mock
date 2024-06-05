package ru.job4j.site.util;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Класс отвечает за количество интервью,
 * отображающихся на странице "index"
 */
@Component
@RequiredArgsConstructor
@Getter
public class InterviewPage {
    @Value("${interviews.size}")
    private final Integer size;
    @Value("${interviews.page}")
    private final Integer page;
}