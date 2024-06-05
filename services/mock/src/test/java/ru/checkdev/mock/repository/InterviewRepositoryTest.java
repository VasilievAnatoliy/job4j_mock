package ru.checkdev.mock.repository;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.domain.Interview;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest()
@RunWith(SpringRunner.class)
class InterviewRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private InterviewRepository interviewRepository;

    @Test
    public void injectedComponentAreNotNull() {
        Assertions.assertNotNull(entityManager);
        Assertions.assertNotNull(interviewRepository);
    }

    @Test
    public void whenFindInterviewByIdThenReturnEmpty() {
        Optional<Interview> interview = interviewRepository.findById(-1);
        MatcherAssert.assertThat(interview, is(Optional.empty()));
    }

    @Test
    public void whenInterviewFindByType() {
        var interview = new Interview();
        interview.setMode(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        entityManager.createQuery("delete from interview").executeUpdate();
        entityManager.persist(interview);
        var interviews = interviewRepository.findByMode(1);
        assertTrue(interviews.size() > 0);
        Assertions.assertEquals(interviews.get(0), interview);
    }

    @Test
    public void whenInterviewNotFoundByType() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var interviews = interviewRepository.findByMode(1);
        Assertions.assertEquals(0, interviews.size());
    }

    @Test
    public void whenFindAllInterview() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var listInterview = interviewRepository.findAll();
        MatcherAssert.assertThat(listInterview, is(Collections.emptyList()));
    }

    @Test
    public void whenInterviewFindByTopicId() {
        var interview = new Interview();
        interview.setMode(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        entityManager.createQuery("delete from interview").executeUpdate();
        entityManager.persist(interview);
        var interviews =
                interviewRepository.findByTopicId(1, PageRequest.of(0, 5));
        Assertions.assertTrue(interviews.toList().size() > 0);
        assertTrue(interviews.toList().size() > 0);
        Assertions.assertEquals(interviews.toList().get(0), interview);
    }

    @Test
    public void whenGetInterviewsBy3onPageAndFindByTopicId() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var interviewsList = IntStream
                .range(0, 8).mapToObj(i -> {
                    var interview = new Interview();
                    interview.setMode(1);
                    interview.setSubmitterId(1);
                    interview.setTitle(String.format("Interview_%d", i));
                    interview.setAdditional(String.format("Some text_%d", i));
                    interview.setContactBy("Some contact");
                    interview.setApproximateDate("30.02.2024");
                    interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
                    interview.setTopicId(1);
                    entityManager.persist(interview);
                    return interview;
                }).toList();
        var firstPage =
                interviewRepository.findByTopicId(1, PageRequest.of(0, 3));
        var secondPage =
                interviewRepository.findByTopicId(1, PageRequest.of(1, 3));
        var thirdPage =
                interviewRepository.findByTopicId(1, PageRequest.of(2, 3));
        var firstPageList = interviewsList.subList(0, 3);
        var secondPageList = interviewsList.subList(3, 6);
        var thirdPageList = interviewsList.subList(6, 8);
        Assertions.assertEquals(firstPage.toList().size(), 3);
        Assertions.assertEquals(firstPage.toList(), firstPageList);
        Assertions.assertEquals(secondPage.toList().size(), 3);
        Assertions.assertEquals(secondPage.toList(), secondPageList);
        Assertions.assertEquals(thirdPage.toList().size(), 2);
        Assertions.assertEquals(thirdPage.toList(), thirdPageList);
    }

    @Test
    public void whenInterviewNotFoundByTopicId() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var interviews =
                interviewRepository.findByTopicId(1, PageRequest.of(1, 5));
        Assertions.assertEquals(0, interviews.toList().size());
    }

    @Test
    public void whenUpdateStatusInterviewThenUpdateStatus() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var newStatus = 5;
        var interview = new Interview();
        interview.setMode(1);
        interview.setStatus(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        entityManager.persist(interview);
        entityManager.clear();
        interviewRepository.updateStatus(interview.getId(), newStatus);
        var interviewInDb = interviewRepository.findById(interview.getId());
        assertThat(interviewInDb.isPresent()).isTrue();
        assertThat(interviewInDb.get().getStatus()).isEqualTo(newStatus);
    }

    @Test
    public void whenUpdateStatusInterviewThenNotUpdateStatus() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var newStatus = 5;
        var interview = new Interview();
        interview.setMode(1);
        interview.setStatus(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        entityManager.persist(interview);
        entityManager.clear();
        interviewRepository.updateStatus(interview.getId() + 99, newStatus);
        var interviewInDb = interviewRepository.findById(interview.getId());
        assertThat(interviewInDb.isPresent()).isTrue();
        assertThat(interviewInDb.get().getStatus()).isEqualTo(interview.getStatus());
    }

    public void whenInterviewsFoundByTopicIdsList() {
        entityManager.createQuery("delete from interview").executeUpdate();
        List<Interview> oddTopicIdsInterviewList = new ArrayList<>();
        List<Interview> evenTopicIdsInterviewList = new ArrayList<>();
        IntStream.range(0, 8).forEach(i -> {
            var interview = new Interview();
            interview.setMode(1);
            interview.setSubmitterId(1);
            interview.setTitle(String.format("Interview_%d", i));
            interview.setAdditional(String.format("Some text_%d", i));
            interview.setContactBy("Some contact");
            interview.setApproximateDate("30.02.2024");
            interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
            interview.setTopicId(i + 1);
            entityManager.persist(interview);
            if (interview.getTopicId() % 2 == 0) {
                evenTopicIdsInterviewList.add(interview);
            } else {
                oddTopicIdsInterviewList.add(interview);
            }
        });
        var firstOddPage =
                interviewRepository.findByTopicIdIn(List.of(1, 3, 5, 7), PageRequest.of(0, 3));
        var secondEvenPage =
                interviewRepository.findByTopicIdIn(List.of(1, 3, 5, 7), PageRequest.of(1, 3));
        var evenPage =
                interviewRepository.findByTopicIdIn(List.of(2, 4, 6), PageRequest.of(0, 3));
        var firstPageList = oddTopicIdsInterviewList.subList(0, 3);
        var secondPageList = oddTopicIdsInterviewList.subList(3, 4);
        var thirdPageList = evenTopicIdsInterviewList.subList(0, 3);
        Assertions.assertEquals(firstOddPage.toList().size(), 3);
        Assertions.assertEquals(firstOddPage.toList(), firstPageList);
        Assertions.assertEquals(secondEvenPage.toList().size(), 1);
        Assertions.assertEquals(secondEvenPage.toList(), secondPageList);
        Assertions.assertEquals(evenPage.toList().size(), 3);
        Assertions.assertEquals(evenPage.toList(), thirdPageList);
    }

    @Test
    void whenFindByDifferentStatuses() {
        entityManager.createQuery("delete from interview").executeUpdate();
        List<Interview> oddStatusIdsInterviewList = new ArrayList<>();
        List<Interview> evenStatusIdsInterviewList = new ArrayList<>();
        IntStream.rangeClosed(1, 9).forEach(i -> {
            var interview = new Interview();
            interview.setMode(1);
            interview.setSubmitterId(1);
            interview.setTitle(String.format("Interview_%d", i));
            interview.setAdditional(String.format("Some text_%d", i));
            interview.setContactBy("Some contact");
            interview.setApproximateDate("30.02.2024");
            interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
            interview.setTopicId(1);
            entityManager.persist(interview);
            if (i % 2 == 0) {
                interview.setStatus(2);
                evenStatusIdsInterviewList.add(interview);
            } else {
                interview.setStatus(1);
                oddStatusIdsInterviewList.add(interview);
            }
        });
        assertThat(oddStatusIdsInterviewList).hasSize(5);
        assertThat(evenStatusIdsInterviewList).hasSize(4);

        List<Interview> status1Interviews = interviewRepository
                .findByStatus(1, PageRequest.of(0, 5)).toList();
        List<Interview> status2Interviews = interviewRepository
                .findByStatus(2, PageRequest.of(0, 5)).toList();

        assertThat(status1Interviews).allMatch(interview -> interview.getStatus() == 1);
        assertThat(status2Interviews).allMatch(interview -> interview.getStatus() == 2);
        assertThat(oddStatusIdsInterviewList).isEqualTo(status1Interviews);
        assertThat(evenStatusIdsInterviewList).isEqualTo(status2Interviews);
    }


    @Test
    public void whenFindByStatusWithPagination() {
        entityManager.createQuery("delete from interview").executeUpdate();
        IntStream.range(0, 10).forEach(i -> {
            var interview = new Interview();
            interview.setTitle("Interview");
            interview.setContactBy("Some contact");
            interview.setApproximateDate("30.02.2024");
            interview.setTopicId(1);
            interview.setStatus(1);
            interview.setCreateDate(new Timestamp(System.currentTimeMillis() - i * 100_000L));
            entityManager.persist(interview);
        });
        Sort sort = Sort.by("createDate").descending();
        List<Interview> firstPage =
                interviewRepository.findByStatus(1, PageRequest.of(0, 5, sort)).toList();
        List<Interview> secondPage =
                interviewRepository.findByStatus(1, PageRequest.of(1, 5, sort)).toList();

        assertThat(firstPage).hasSize(5);
        assertThat(secondPage).hasSize(5);
        assertThat(firstPage).isSortedAccordingTo((i1, i2) -> i2.getCreateDate()
                .compareTo(i1.getCreateDate()));
        assertThat(secondPage).isSortedAccordingTo((i1, i2) -> i2.getCreateDate()
                .compareTo(i1.getCreateDate()));

    }
}