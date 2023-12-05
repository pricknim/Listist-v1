package edu.codespring.listist.backend.repository;

import edu.codespring.listist.backend.model.Song;

import java.util.List;

public interface SongDAO {

    Song create(Song song);
    void update(Song song);
    void delete(Long id);
    Song getById(Long id);
    Song getByTitleAndArtist(String title, String artist);
    List<Song> getByTitle(String title);
    List<Song> getAll();
    List<Song> getAllFromArtist(String artist);
}
