package com.example.demo.ressources;

import com.google.gson.Gson;

import org.apache.commons.codec.Charsets;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.xml.sax.ContentHandler;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.Profile;
import com.example.demo.services.ProfileService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.util.TextExtractor.textFromDoc;
import static com.example.demo.util.Utils.multipartToFile;
import static com.example.demo.util.Utils.readFile;

@RestController()
@RequestMapping("/")
public class ProfileRessource {

    Logger logger = LoggerFactory.getLogger(ProfileRessource.class);

    @Autowired
    private ProfileService profileService;

    private Gson gs;

    /*
    @GetMapping("/")
    public ResponseEntity<List<Profile>> getProfiles() throws Exception {
        return ResponseEntity.ok(profileService.getProfiles());
    }
    */
    /*
    @PostMapping("/")
    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile) {
        return ResponseEntity.created(URI.create("/profiles"))
                .body(profileService.createProfile(profile));
    }
    */

    @GetMapping("/search")
    public ResponseEntity<List<Profile>> findProfileKeyWords(@RequestParam(value = "text") String keyword) throws Exception {
        return ResponseEntity.ok(profileService.getProfilesByKeyWord(keyword));
    }

    /*
    @GetMapping("/{id}")
    public ResponseEntity<List<Profile>> findProfileById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(profileService.getProfiles());
    }
     */

    @GetMapping("/liste_cv")
    public String findByText(@RequestParam(value = "text") String text) throws Exception {
        String web_page = "";
        try {
            web_page = readFile("index.html", Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            web_page = "y a rien dsl";
        }
        ResponseEntity<List<Profile>> response = ResponseEntity.ok(profileService.getProfilesByKeyWord(text));
        ArrayList<Profile> profileDocuments = new ArrayList<>(response.getBody());

        String liste_cvs_html = "";

        for(Profile profileDocument : profileDocuments){
            liste_cvs_html += "<pre class=\"case_resultat\">" + profileDocument.getText() + "</pre>";
        }

        return web_page.replace("<!--REPONSE-->", liste_cvs_html.replace(text, "<mark>" + text + "</mark>"));
    }

    @GetMapping
    public String index() throws Exception {
        System.out.println(">>>>  vide");
        String web_page = "";
        try {
            web_page = readFile("index.html", Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            web_page = "y a rien dsl";
        }

        return web_page;
    }

    @PostMapping
    public String createProfile(@RequestParam("file") MultipartFile tmp_file,
                                RedirectAttributes redirectAttributes) throws Exception {

        String mime = tmp_file.getContentType();
        File file = multipartToFile(tmp_file);
        String parsedText = "y a rien DDDD:";

        if (mime.contains("image")) {
            /*
            Tesseract tesseract = Tesseract.getInstance();
            try {
                tesseract.setDatapath("Tess4J/tessdata");
                parsedText = tesseract.doOCR(new File("L3.jpeg"));
            }
            catch (TesseractException e) {
                e.printStackTrace();
            }
             */
            String commande_tesseract = "tesseract " + file.getAbsolutePath() + " -";
            Process p = Runtime.getRuntime().exec(commande_tesseract);
            
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            String s;
            while ((s = stdInput.readLine()) != null) {
                parsedText += s;
            }
        }
        else { // pdf & word
            parsedText = textFromDoc(file);

            /*
            PDFParser parser = null;
            PDDocument pdDoc = null;
            COSDocument cosDoc = null;
            PDFTextStripper pdfStripper;

            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(1);
            pdfTextStripper.setEndPage(5);
            parsedText  = pdfTextStripper.getText(document);
            */
        }

        Profile profile = new Profile("" + System.currentTimeMillis(), parsedText);
        ResponseEntity reponse_serv = ResponseEntity.created(URI.create("/profiles"))
                .body(profileService.createProfile(profile));

        String web_page = "";
        try {
            web_page = readFile("index.html", Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            web_page = "y a rien dsl";
        }
        boolean is_added = reponse_serv.toString().contains("201");

        return web_page.replace("<!--REPONSE-->", is_added ? "<strong>CV ajouté avec succes ! \\o/</strong>" : "ERREUR (╯°□°)╯︵ ┻━┻");
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
