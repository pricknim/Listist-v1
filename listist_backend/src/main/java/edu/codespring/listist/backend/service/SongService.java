package edu.codespring.listist.backend.service;

import edu.codespring.listist.backend.model.Song;

import java.util.List;

public interface SongService {

    void register(Song song);
    void update(Song song);
    void delete(Long id);
    Song getById(Long id);
    List<Song> getByTitle(String title);
    Song getByTitleAndArtist(String title, String artist);
    List<Song> getAll();
    List<Song> getAllFromArtist(String artist);
}
