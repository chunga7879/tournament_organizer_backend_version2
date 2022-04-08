package com.hairlesscat.app.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends  JpaRepository<Result, Long>{

	@Query(value = "SELECT * FROM result WHERE result_match_id = ?1", nativeQuery = true)
	List<Result> findByMatch(Long mid);
}
