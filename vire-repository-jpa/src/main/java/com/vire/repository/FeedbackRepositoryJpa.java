package com.vire.repository;

import com.vire.dao.FeedbackDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepositoryJpa
    extends JpaRepository<FeedbackDao, Long>, JpaSpecificationExecutor<FeedbackDao> {}