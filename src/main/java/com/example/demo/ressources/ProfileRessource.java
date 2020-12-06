package com.example.demo.ressources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.Profile;
import com.example.demo.services.ProfileService;

import java.net.URI;
import java.util.List;


@RestController()
@RequestMapping("/profiles")
public class ProfileRessource {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/")
    public ResponseEntity<List<Profile>> getProfiles() throws Exception {
        return ResponseEntity.ok(profileService.getProfiles());
    }
    
    @PostMapping("/")
    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile) {
        return ResponseEntity.created(URI.create("/profiles"))
                .body(profileService.createProfile(profile));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<List<Profile>> findProfileById(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(profileService.getProfiles());
    }
    
    @GetMapping("/test")
    public String test(){
        return "Success";
    }

    
    
/*
    @PutMapping
    public ResponseEntity updateProfile(@RequestBody ProfileDocument document) throws Exception {

        return new ResponseEntity(service.updateProfile(document), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ProfileDocument findById(@PathVariable String id) throws Exception {

        return service.findById(id);
    }

    @GetMapping
    public List<ProfileDocument> findAll() throws Exception {

        return service.findAll();
    }

    @GetMapping(value = "/search")
    public List<ProfileDocument> search(@RequestParam(value = "technology") String technology) throws Exception {
        return service.searchByTechnology(technology);
    }

    @GetMapping(value = "/api/v1/profiles/name-search")
    public List<ProfileDocument> searchByName(@RequestParam(value = "name") String name) throws Exception {
        return service.findProfileByName(name);
    }


    @DeleteMapping("/{id}")
    public String deleteProfileDocument(@PathVariable String id) throws Exception {

        return service.deleteProfileDocument(id);

    }
*/
}
