package co.amscraft.ultralib.ftpserver.commands;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//



import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;

import co.amscraft.ultralib.ftpserver.FTPServer;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.IODataConnectionFactory;
import org.apache.ftpserver.impl.LocalizedDataTransferFtpReply;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerFtpStatistics;
import org.apache.ftpserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSTOR extends AbstractCommand {
    private final Logger LOG = LoggerFactory.getLogger(org.apache.ftpserver.command.impl.STOR.class);

    public CustomSTOR() {
    }

    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        try {
            long skipLen = session.getFileOffset();
            String fileName = request.getArgument();
            if (fileName == null) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 501, "STOR", (String)null, (FtpFile)null));
            } else {
                DataConnectionFactory connFactory = session.getDataConnection();
                if (connFactory instanceof IODataConnectionFactory) {
                    InetAddress address = ((IODataConnectionFactory)connFactory).getInetAddress();
                    if (address == null) {
                        session.write(new DefaultFtpReply(503, "PORT or PASV must be issued first"));
                        return;
                    }
                }

                FtpFile file = null;

                try {
                    file = session.getFileSystemView().getFile(fileName);
                } catch (Exception var32) {
                    this.LOG.debug("Exception getting file object", var32);
                }

                if (file == null) {
                    session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 550, "STOR.invalid", fileName, file));
                } else {
                    fileName = file.getAbsolutePath();
                    if (!file.isWritable() || !FTPServer.canDownload(fileName)) {
                        session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 550, "STOR.permission", fileName, file));
                    } else {
                        session.write(LocalizedFtpReply.translate(session, request, context, 150, "STOR", fileName)).awaitUninterruptibly(10000L);
                        DataConnection dataConnection;
                        try {
                            dataConnection = session.getDataConnection().openConnection();
                        } catch (Exception var33) {
                            this.LOG.debug("Exception getting the input data stream", var33);
                            session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 425, "STOR", fileName, file));
                            return;
                        }

                        boolean failure = false;
                        OutputStream outStream = null;
                        long transSz = 0L;

                        try {
                            outStream = file.createOutputStream(skipLen);
                            transSz = dataConnection.transferFromClient(session.getFtpletSession(), outStream);
                            if (outStream != null) {
                                outStream.close();
                            }

                            this.LOG.info("File uploaded {}", fileName);
                            ServerFtpStatistics ftpStat = (ServerFtpStatistics)context.getFtpStatistics();
                            ftpStat.setUpload(session, file, transSz);
                        } catch (SocketException var29) {
                            this.LOG.debug("Socket exception during data transfer", var29);
                            failure = true;
                            session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 426, "STOR", fileName, file));
                        } catch (IOException var30) {
                            this.LOG.debug("IOException during data transfer", var30);
                            failure = true;
                            session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 551, "STOR", fileName, file));
                        } finally {
                            IoUtils.close(outStream);
                        }

                        if (!failure) {
                            session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 226, "STOR", fileName, file, transSz));
                        }

                    }
                }
            }
        } finally {
            session.resetState();
            session.getDataConnection().closeDataConnection();
        }
    }
}
