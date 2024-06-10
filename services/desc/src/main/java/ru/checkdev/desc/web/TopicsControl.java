package ru.checkdev.desc.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.desc.domain.Topic;
import ru.checkdev.desc.dto.TopicDTO;
import ru.checkdev.desc.service.TopicService;

import java.util.List;
import java.util.Map;

@RequestMapping("/topics")
@RestController
@AllArgsConstructor
public class TopicsControl {
    private final TopicService topicService;

    @GetMapping("/")
    public List<Topic> getAll() {
        return topicService.getAll();
    }

    @GetMapping("/{id}")
    public List<Topic> getByCategory(@PathVariable int id) {
        return topicService.findByCategory(id);
    }

    @GetMapping("/getByCategoryId/{categoryId}")
    public ResponseEntity<List<TopicDTO>> getByCategoryId(@PathVariable int categoryId) {
        return new ResponseEntity<>(topicService
                .getTopicDTOsByCategoryId(categoryId), HttpStatus.OK);
    }

    @GetMapping("/count")
    public Map<Integer, Integer> getTopicsCount(@RequestParam List<Integer> ids) {
        return topicService.getTopicsCountForCategories(ids);
    }
}
