package com.vire.repository;

import com.vire.dao.FeedsSendToDao;
import com.vire.dao.SocialSendToDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedsSendToRepositoryJpa extends JpaRepository<FeedsSendToDao, Long> ,
        JpaSpecificationExecutor<FeedsSendToDao> {

}
