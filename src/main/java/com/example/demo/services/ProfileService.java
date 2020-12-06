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


        if (keyword.indexOf(' ') > 0) {
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
    
/*

    @GetMapping
    public Profile findProfileById(Long id) throws IOException {
         GetRequest getRequest = new GetRequest("profiles", id.toString());
         GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
         
        return gson.toJson(getResponse.getSource().values(), Profile.class);
    }

    public String createProfileDocument(ProfileDocument document) throws Exception {

        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, document.getId())
                .source(convertProfileDocumentToMap(document), XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.getResult().name();
    }

    public ProfileDocument findById(String id) throws Exception {

            GetRequest getRequest = new GetRequest(INDEX, TYPE, id);

            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> resultMap = getResponse.getSource();

            return convertMapToProfileDocument(resultMap);

    }

    public String updateProfile(ProfileDocument document) throws Exception {

            ProfileDocument resultDocument = findById(document.getId());

            UpdateRequest updateRequest = new UpdateRequest(
                    INDEX,
                    TYPE,
                    resultDocument.getId());

            updateRequest.doc(convertProfileDocumentToMap(document));
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

            return updateResponse
                    .getResult()
                    .name();

    }

    public List<ProfileDocument> findAll() throws Exception {


        SearchRequest searchRequest = buildSearchRequest(INDEX,TYPE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public List<ProfileDocument> findProfileByName(String name) throws Exception{


        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX);
        searchRequest.types(TYPE);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                .matchQuery("name",name)
                .operator(Operator.AND);

        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);

    }

    public String deleteProfileDocument(String id) throws Exception {

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        DeleteResponse response = client.delete(deleteRequest,RequestOptions.DEFAULT);

        return response
                .getResult()
                .name();

    }

    private Map<String, Object> convertProfileDocumentToMap(ProfileDocument profileDocument) {
        return objectMapper.convertValue(profileDocument, Map.class);
    }

    private ProfileDocument convertMapToProfileDocument(Map<String, Object> map){
        return objectMapper.convertValue(map,ProfileDocument.class);
    }


    public List<ProfileDocument> searchByTechnology(String technology) throws Exception{

        SearchRequest searchRequest = buildSearchRequest(INDEX,TYPE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .matchQuery("technologies.name",technology));

        searchSourceBuilder.query(QueryBuilders.nestedQuery("technologies",queryBuilder,ScoreMode.Avg));

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);

        return getSearchResult(response);
    }

    private List<ProfileDocument> getSearchResult(SearchResponse response) {

        SearchHit[] searchHit = response.getHits().getHits();

        List<ProfileDocument> profileDocuments = new ArrayList<>();

        for (SearchHit hit : searchHit){
            profileDocuments
                    .add(objectMapper
                            .convertValue(hit
                                    .getSourceAsMap(), ProfileDocument.class));
        }

        return profileDocuments;
    }

    private SearchRequest buildSearchRequest(String index, String type) {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);

        return searchRequest;
    }
*/
}
