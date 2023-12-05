package edu.codespring.listist.backend.service;

import edu.codespring.listist.backend.service.impl.PlaylistServiceImpl;
import edu.codespring.listist.backend.service.impl.SongServiceImpl;
import edu.codespring.listist.backend.service.impl.UserServiceImpl;

public class ServiceFactory {
    private ServiceFactory() {}

    public UserService getUserService() { return new UserServiceImpl(); }

    public SongService getSongService() { return new SongServiceImpl(); }

    public PlaylistService getPlaylistService() { return new PlaylistServiceImpl(); }

    public static ServiceFactory getInstance() {
        return new ServiceFactory();
    }
}
