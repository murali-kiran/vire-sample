package com.vire.repository;

import com.vire.dao.FileDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepositoryJpa extends JpaRepository<FileDao, Long> , JpaSpecificationExecutor<FileDao> {
}
