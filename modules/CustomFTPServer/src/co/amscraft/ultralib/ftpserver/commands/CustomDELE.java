package co.amscraft.ultralib.ftpserver.commands;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


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

public class CustomDELE extends AbstractCommand {
    private final Logger LOG = LoggerFactory.getLogger(org.apache.ftpserver.command.impl.DELE.class);

    public CustomDELE() {
    }

    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        session.resetState();
        String fileName = request.getArgument();
        if (fileName == null) {
            session.write(LocalizedFileActionFtpReply.translate(session, request, context, 501, "DELE", (String)null, (FtpFile)null));
        } else {
            FtpFile file = null;

            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception var8) {
                this.LOG.debug("Could not get file " + fileName, var8);
            }

            if (file == null) {
                session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "DELE.invalid", fileName, (FtpFile)null));
            } else {
                fileName = file.getAbsolutePath();
                if (file.isDirectory()) {
                    session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "DELE.invalid", fileName, file));
                } else if (!file.isRemovable() || !FTPServer.canDownload(fileName)) {
                    session.write(LocalizedFileActionFtpReply.translate(session, request, context, 450, "DELE.permission", fileName, file));
                } else {
                    if (file.delete()) {
                        session.write(LocalizedFileActionFtpReply.translate(session, request, context, 250, "DELE", fileName, file));
                        String userName = session.getUser().getName();
                        this.LOG.info("File delete : " + userName + " - " + fileName);
                        ServerFtpStatistics ftpStat = (ServerFtpStatistics)context.getFtpStatistics();
                        ftpStat.setDelete(session, file);
                    } else {
                        session.write(LocalizedFileActionFtpReply.translate(session, request, context, 450, "DELE", fileName, file));
                    }

                }
            }
        }
    }
}
