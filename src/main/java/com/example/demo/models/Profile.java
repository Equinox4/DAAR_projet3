package com.example.demo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.Generated;

@Data
@Document(indexName = "profiles")
public class Profile {

    @Id
    @Generated
    private String id;
    
    @Field(type = FieldType.Text)
    private String firstName;
    
    @Field(type = FieldType.Text)
    private String lastName;
}
