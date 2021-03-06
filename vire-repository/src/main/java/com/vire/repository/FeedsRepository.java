package com.vire.repository;

import com.vire.dao.FeedsDao;
import com.vire.dto.FeedsDto;
import com.vire.repository.search.CustomSpecificationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedsRepository {

    @Autowired
    FeedsRepositoryJpa feedsRepositoryJpa;
    @Autowired
    FeedsSendToRepository feedsSendToRepository;
    @PersistenceContext
    private EntityManager entityManager;
    public FeedsDto createFeeds(final FeedsDto feedsDto) {
        var feedsDao = FeedsDao.fromDto(feedsDto);

        if (!CollectionUtils.isEmpty(feedsDao.getFeedsSendTo())) {
            for (var sendToDto : feedsDao.getFeedsSendTo()) {
                sendToDto.setFeed(feedsDao);
                sendToDto.onPrePersist();
            }
        }
        feedsDao.onPrePersist();
        //feedsDao.getSendTo().get(0).getFeeds().setFeedsId(feedsDao.getFeedsId());
        System.out.println("sa:::" + feedsDao);
        return feedsRepositoryJpa.save(feedsDao).toDto();
    }

    public FeedsDto updateFeeds(final FeedsDto feedsDto) {
        var existingObject = feedsRepositoryJpa.findById(feedsDto.getFeedId());

        if (existingObject.isEmpty()) {
            throw new RuntimeException("Object not exists in db to update");
        }
        var feedsDao = FeedsDao.fromDto(feedsDto);
        if (!CollectionUtils.isEmpty(feedsDao.getFeedsSendTo())) {
            for (var sendToDto : feedsDao.getFeedsSendTo()) {
                sendToDto.setFeed(feedsDao);
                sendToDto.onPreUpdate();
                sendToDto.setCreatedTime(existingObject.get().getCreatedTime());
            }
        }
        feedsDao.setCreatedTime(existingObject.get().getCreatedTime());
        feedsDao.onPreUpdate();
        return feedsRepositoryJpa.save(feedsDao).toDto();
    }

    public Optional<FeedsDto> deleteFeedsPost(final Long feedsId) {

        var optionalFeeds = retrieveById(feedsId);

        if (optionalFeeds.isPresent()) {
            feedsRepositoryJpa.deleteById(feedsId);
        } else {
            throw new RuntimeException("Feeds Post Object not exists in DB");
        }

        return optionalFeeds;
    }

    public List<FeedsDto> getAllFeeds() {

        return feedsRepositoryJpa.findAll()
                .stream()
                .map(dao -> dao.toDto())
                .collect(Collectors.toList());
    }

    public Optional<FeedsDto> retrieveById(Long feedId) {

        return feedsRepositoryJpa.findById(feedId).map(dao -> dao.toDto());
    }


    public List<FeedsDto> search(final String searchString) {

        var spec = new CustomSpecificationResolver<FeedsDao>(searchString).resolve();

        return feedsRepositoryJpa.findAll(spec).stream()
                .map(dao -> dao.toDto())
                .collect(Collectors.toList());
    }

    public List<FeedsDto> getAllBySendToInterestType(List<String> interests) {
        TypedQuery<FeedsDto> query
                = entityManager.createQuery(
                "SELECT s FROM FeedsDto s JOIN s.FeedsSendToDto d WHERE d.type=d.value IN", FeedsDto.class);
        List<FeedsDto> resultList = query.getResultList();
        return resultList;
    }
}
