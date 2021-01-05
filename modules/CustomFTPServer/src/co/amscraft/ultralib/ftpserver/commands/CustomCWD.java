package co.amscraft.ultralib.ftpserver.commands;

import java.io.IOException;

import co.amscraft.ultralib.ftpserver.FTPServer;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFileActionFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomCWD extends AbstractCommand {
    private final Logger LOG = LoggerFactory.getLogger(org.apache.ftpserver.command.impl.CWD.class);

    public CustomCWD() {
    }

    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        session.resetState();
        String dirName = "/";
        if (request.hasArgument()) {
            dirName = request.getArgument();
        }

        FileSystemView fsview = session.getFileSystemView();
        boolean success = false;

        try {
            success = FTPServer.canDownload(dirName) && fsview.changeWorkingDirectory(dirName);
        } catch (Exception var8) {
            this.LOG.debug("Failed to change directory in file system", var8);
        }

        FtpFile cwd = fsview.getWorkingDirectory();
        if (success) {
            dirName = cwd.getAbsolutePath();
            session.write(LocalizedFileActionFtpReply.translate(session, request, context, 250, "CWD", dirName, cwd));
        } else {
            session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "CWD", (String)null, cwd));
        }

    }
}
