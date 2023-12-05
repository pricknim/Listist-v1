package edu.codespring.listist.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist extends BaseEntity {
    private Long userId;
    private String name;
    private List<Song> songs = new ArrayList<>();

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public void setSongs(List<Song> songs) { this.songs.addAll(songs); }

    public void addSong(Song song) { songs.add(song); }

    public void removeSong(Song song) { songs.remove(song); }

    public List<Song> getSongs() { return songs; }

    @Override
    public String toString() {
        return "Playlist{" +
                "user id= '" + userId + '\'' +
                ", name= '" + name + '\'' +
                ", id= '" + getId() + '\'' +
                ", uuid= '" + getUuid() + '\'' +
                "\nSongs:" + getSongs() +
                '}';
    }
}
