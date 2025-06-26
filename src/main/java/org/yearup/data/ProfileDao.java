package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile create(Profile profile);
    Profile update(int id, Profile profile); //need a profile object for body so it updates
    Profile getById(int id);
}
