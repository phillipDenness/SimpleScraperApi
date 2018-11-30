package com.phillip.denness.scraper.repository;

import com.phillip.denness.scraper.domain.Searchterms;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchtermsRepository extends CrudRepository<Searchterms, Integer> {

    @Override
    List<Searchterms> findAll();

}
