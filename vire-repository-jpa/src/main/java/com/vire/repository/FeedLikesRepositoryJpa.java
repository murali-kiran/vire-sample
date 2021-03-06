package com.vire.repository;

import com.vire.dao.FeedLikesDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedLikesRepositoryJpa
    extends JpaRepository<FeedLikesDao, Long>, JpaSpecificationExecutor<FeedLikesDao> {}
