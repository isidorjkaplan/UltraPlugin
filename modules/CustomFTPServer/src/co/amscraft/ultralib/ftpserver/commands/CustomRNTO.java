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
import org.apache.ftpserver.impl.LocalizedRenameFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRNTO extends AbstractCommand {
    private final Logger LOG = LoggerFactory.getLogger(org.apache.ftpserver.command.impl.RNTO.class);

    public CustomRNTO() {
    }

    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        try {
            String toFileStr = request.getArgument();
            if (toFileStr == null) {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 501, "RNTO", (String)null, (FtpFile)null, (FtpFile)null));
                return;
            }

            FtpFile frFile = session.getRenameFrom();
            if (frFile == null) {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 503, "RNTO", (String)null, (FtpFile)null, (FtpFile)null));
                return;
            }

            FtpFile toFile = null;

            try {
                toFile = session.getFileSystemView().getFile(toFileStr);
            } catch (Exception var11) {
                this.LOG.debug("Exception getting file object", var11);
            }

            if (toFile == null) {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 553, "RNTO.invalid", (String)null, frFile, toFile));
                return;
            }

            toFileStr = toFile.getAbsolutePath();
            if (!toFile.isWritable() || !FTPServer.canDownload(toFileStr) || !FTPServer.canDownload(frFile.getAbsolutePath())) {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 553, "RNTO.permission", (String)null, frFile, toFile));
                return;
            }

            if (!frFile.doesExist()) {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 553, "RNTO.missing", (String)null, frFile, toFile));
                return;
            }

            String logFrFileAbsolutePath = frFile.getAbsolutePath();
            if (frFile.move(toFile)) {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 250, "RNTO", toFileStr, frFile, toFile));
                this.LOG.info("File rename from \"{}\" to \"{}\"", logFrFileAbsolutePath, toFile.getAbsolutePath());
            } else {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 553, "RNTO", toFileStr, frFile, toFile));
            }
        } finally {
            session.resetState();
        }

    }
}
