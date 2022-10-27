package com.vire.repository;

import com.vire.dao.SocialNotificationDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialNotificationRepositoryJpa
    extends JpaRepository<SocialNotificationDao, Long>, JpaSpecificationExecutor<SocialNotificationDao> {}
