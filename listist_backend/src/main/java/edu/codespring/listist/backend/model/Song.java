package edu.codespring.listist.backend.model;

public class Song extends BaseEntity{
    private String title;
    private String artist;

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }

    public void setArtist(String artist) { this.artist = artist; }

   @Override
    public String toString() {
        return "Song{" +
                "title= '" + title + '\'' +
                ", artist= '" + artist + '\'' +
                ", id= " + getId() + '\'' +
                ", uuid= " + getUuid() + '\'' +
                '}';
    }


}
