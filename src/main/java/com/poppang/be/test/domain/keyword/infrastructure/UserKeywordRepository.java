package com.poppang.be.test.domain.keyword.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKeywordRepository extends JpaRepository<com.poppang.be.test.domain.keyword.entity.UserKeyword, Long> {
}
