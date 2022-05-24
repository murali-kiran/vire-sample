package com.vire.repository;

import com.vire.dao.FeedReportDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedReportRepositoryJpa
    extends JpaRepository<FeedReportDao, Long>, JpaSpecificationExecutor<FeedReportDao> {}
