package co.amscraft.ultralib.ftpserver.commands;
import java.io.IOException;
import co.amscraft.ultralib.ftpserver.FTPServer;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFileActionFtpReply;
import org.apache.ftpserver.impl.ServerFtpStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomMKD extends AbstractCommand {
    private final Logger LOG = LoggerFactory.getLogger(org.apache.ftpserver.command.impl.MKD.class);

    public CustomMKD() {
    }

    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        session.resetState();
        String fileName = request.getArgument();
        if (fileName == null) {
            session.write(LocalizedFileActionFtpReply.translate(session, request, context, 501, "MKD", (String)null, (FtpFile)null));
        } else {
            FtpFile file = null;

            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception var8) {
                this.LOG.debug("Exception getting file object", var8);
            }

            if (file == null) {
                session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "MKD.invalid", fileName, file));
            } else {
                fileName = file.getAbsolutePath();
                if (!file.isWritable() || !FTPServer.canDownload(fileName)) {
                    session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "MKD.permission", fileName, file));
                } else if (file.doesExist()) {
                    session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "MKD.exists", fileName, file));
                } else {
                    if (file.mkdir()) {
                        session.write(LocalizedFileActionFtpReply.translate(session, request, context, 257, "MKD", fileName, file));
                        String userName = session.getUser().getName();
                        this.LOG.info("Directory create : " + userName + " - " + fileName);
                        ServerFtpStatistics ftpStat = (ServerFtpStatistics)context.getFtpStatistics();
                        ftpStat.setMkdir(session, file);
                    } else {
                        session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "MKD", fileName, file));
                    }

                }
            }
        }
    }
}
