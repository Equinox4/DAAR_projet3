package com.example.demo.services;

import com.example.demo.models.Profile;
import com.example.demo.repositories.ProfileRepository;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProfileService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private Gson gson;

    public List<Profile> getProfiles() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(50)
                .query(QueryBuilders.matchAllQuery());

        return executeQuery(searchSourceBuilder);
    }

    public List<Profile> getProfilesByKeyWord(String keyword) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(50);

        if (keyword.indexOf(' ') > 0) { // assume that keywords are separated by a space character
            String[] keywords = keyword.split(" ");
            for (String word : keywords) {
                searchSourceBuilder.query(QueryBuilders.regexpQuery("text", ".*" + word + ".*"));
            }
        }
        else {
            searchSourceBuilder.query(QueryBuilders.regexpQuery("text", ".*" + keyword.toLowerCase() + ".*"));
        }

        return executeQuery(searchSourceBuilder);
    }

    public Profile createProfile(Profile profile) {
        return this.profileRepository.save(profile);
    }

    private List<Profile> executeQuery(SearchSourceBuilder searchSourceBuilder) throws IOException {
        SearchRequest searchRequest = new SearchRequest("profiles");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return Stream.of(searchResponse.getHits().getHits())
                .map(hit -> hit.getSourceAsString())
                .map(site -> gson.fromJson(site, Profile.class))
                .collect(Collectors.toList());
    }
}
