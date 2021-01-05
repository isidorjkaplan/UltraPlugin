package co.amscraft.ultralib.ftpserver;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class CustomPermission implements Authority {

    public AuthorizationRequest authorize(AuthorizationRequest request) {
        if (request instanceof WriteRequest) {
            String file = ((WriteRequest) request).getFile();
            return FTPServer.canDownload(file) ? (WriteRequest) request : null;
        }
        return null;

    }

    public boolean canAuthorize(AuthorizationRequest request) {
        return request instanceof WriteRequest;
    }
}
