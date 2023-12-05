package edu.codespring.listist.frontend;

import edu.codespring.listist.backend.model.Playlist;
import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.model.User;
import edu.codespring.listist.backend.service.PlaylistService;
import edu.codespring.listist.backend.service.ServiceFactory;
import edu.codespring.listist.backend.service.SongService;
import edu.codespring.listist.backend.service.UserService;

public class Main {

    public static void main(String[] args) {
        ServiceFactory sf = ServiceFactory.getInstance();
        UserService us = sf.getUserService();
        SongService ss = sf.getSongService();
        PlaylistService ps = sf.getPlaylistService();

        User user = us.getByEmail("saelee@tacorn.co");
        Song s = ss.getByTitleAndArtist("Paradise", "Lee Siyeon");
        Song s2 = ss.getByTitleAndArtist("Starry Night", "fromis_9");

        System.out.println(ps.getAll());

        Playlist p = ps.getByName(user.getId(), "Loml songs");
        ps.addSong(p, s2);

        System.out.println(p);

        ps.removeSong(p, s);
        System.out.println(p);

    }
}
