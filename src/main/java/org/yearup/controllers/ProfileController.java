package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

import java.util.List;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {



    private ProfileDao profileDao;

    @Autowired
    public ProfileController(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    @GetMapping("{userId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Profile> getProfile(@PathVariable int userId) {
        try {
        Profile profile = profileDao.getById(userId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    } catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch profile.");
        }
    }


    @PutMapping("{userId}")
    @PreAuthorize("permitAll()")
    //Response Entities give more precise control for HTTPS response
    public ResponseEntity<Profile> updateCategory(@PathVariable int userId, @RequestBody Profile profile) {
        {
            try {
                Profile updated = profileDao.update(userId, profile);
                if (updated == null){
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(updated);
            }
            catch(Exception ex)
            {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
            }
        }
    }

}
