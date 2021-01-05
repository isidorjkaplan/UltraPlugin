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

public class CustomRMD extends AbstractCommand {
    private final Logger LOG = LoggerFactory.getLogger(org.apache.ftpserver.command.impl.RMD.class);

    public CustomRMD() {
    }

    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        session.resetState();
        String fileName = request.getArgument();
        if (fileName == null) {
            session.write(LocalizedFileActionFtpReply.translate(session, request, context, 501, "RMD", (String)null, (FtpFile)null));
        } else {
            FtpFile file = null;

            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception var9) {
                this.LOG.debug("Exception getting file object", var9);
            }

            if (file == null || !FTPServer.canDownload(fileName)) {
                session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "RMD.permission", fileName, file));
            } else {
                fileName = file.getAbsolutePath();
                if (!file.isDirectory()) {
                    session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "RMD.invalid", fileName, file));
                } else {
                    FtpFile cwd = session.getFileSystemView().getWorkingDirectory();
                    if (file.equals(cwd)) {
                        session.write(LocalizedFileActionFtpReply.translate(session, request, context, 450, "RMD.busy", fileName, file));
                    } else if (!file.isRemovable()) {
                        session.write(LocalizedFileActionFtpReply.translate(session, request, context, 550, "RMD.permission", fileName, file));
                    } else {
                        if (file.delete()) {
                            session.write(LocalizedFileActionFtpReply.translate(session, request, context, 250, "RMD", fileName, file));
                            String userName = session.getUser().getName();
                            this.LOG.info("Directory remove : " + userName + " - " + fileName);
                            ServerFtpStatistics ftpStat = (ServerFtpStatistics)context.getFtpStatistics();
                            ftpStat.setRmdir(session, file);
                        } else {
                            session.write(LocalizedFileActionFtpReply.translate(session, request, context, 450, "RMD", fileName, file));
                        }

                    }
                }
            }
        }
    }
}
