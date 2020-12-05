package com.example.demo.document;

import org.springframework.data.annotation.Id;

public class CV {
    @Id private String id;
    private String firstName;
    private String lastName;
}
