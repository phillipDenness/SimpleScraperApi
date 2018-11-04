package com.phillip.denness.gumtree.Service;

import com.phillip.denness.gumtree.domain.Searchterms;
import com.phillip.denness.gumtree.repository.SearchtermsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SearchtermsService {

    private SearchtermsRepository searchTermsRepository;

    @Autowired
    public SearchtermsService(SearchtermsRepository searchTermsRepository) {
        this.searchTermsRepository = searchTermsRepository;
    }

    public List<Searchterms> getAllSearchTerms() {
        return searchTermsRepository.findAll();
    }

    public Searchterms createSearchTerms(Searchterms searchTerms) {
        return searchTermsRepository.save(searchTerms);
    }

    public Optional<Searchterms> getSearchTerms(Integer id) {
        return searchTermsRepository.findById(id);
    }
}
