package edu.codespring.listist.backend.repository;

import edu.codespring.listist.backend.model.Playlist;
import edu.codespring.listist.backend.model.Song;

import java.util.List;

public interface PlaylistDAO {
    Playlist create(Playlist playlist);
    void update(Playlist playlist);
    void delete(Long id);
    void addSong(Playlist p, Song s);
    void removeSong(Playlist p, Song s);
    Playlist getById(Long id);
    Playlist getByName(Long userId, String name);
    List<Playlist> getByName(String name);
    List<Playlist> getByUser(Long userId);
    List<Playlist> getAll();
    List<Song> getNotInPlaylist(Playlist p);
}
