//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package co.amscraft.ultralib.ftpserver.commands;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;

import co.amscraft.ultralib.ftpserver.FTPServer;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.command.impl.RETR;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DataType;
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

public class CustomRETR extends AbstractCommand {
    private final Logger LOG = LoggerFactory.getLogger(RETR.class);

    public CustomRETR() {
    }

    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {

        try {
            long skipLen = session.getFileOffset();
            String fileName = request.getArgument();
            if (fileName == null) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 501, "RETR", (String)null, (FtpFile)null));
            } else {
                FtpFile file = null;

                try {
                    file = session.getFileSystemView().getFile(fileName);
                } catch (Exception var32) {
                    this.LOG.debug("Exception getting file object", var32);
                }

                if (file == null) {
                    session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 550, "RETR.missing", fileName, file));
                } else {
                    fileName = file.getAbsolutePath();
                    if (!file.doesExist()) {
                        session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 550, "RETR.missing", fileName, file));
                    } else if (!file.isFile()) {
                        session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 550, "RETR.invalid", fileName, file));
                    } else if (!file.isReadable() || !FTPServer.canDownload(file.getAbsolutePath())) {
                        session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 550, "RETR.permission", fileName, file));
                    } else {
                        DataConnectionFactory connFactory = session.getDataConnection();
                        if (connFactory instanceof IODataConnectionFactory) {
                            InetAddress address = ((IODataConnectionFactory)connFactory).getInetAddress();
                            if (address == null) {
                                session.write(new DefaultFtpReply(503, "PORT or PASV must be issued first"));
                                return;
                            }
                        }

                        session.write(LocalizedFtpReply.translate(session, request, context, 150, "RETR", (String)null));
                        boolean failure = false;
                        InputStream is = null;

                        DataConnection dataConnection;
                        try {
                            dataConnection = session.getDataConnection().openConnection();
                        } catch (Exception var33) {
                            this.LOG.debug("Exception getting the output data stream", var33);
                            session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 425, "RETR", (String)null, file));
                            return;
                        }

                        long transSz = 0L;

                        try {
                            is = this.openInputStream(session, file, skipLen);
                            transSz = dataConnection.transferToClient(session.getFtpletSession(), is);
                            if (is != null) {
                                is.close();
                            }

                            this.LOG.info("File downloaded {}", fileName);
                            ServerFtpStatistics ftpStat = (ServerFtpStatistics)context.getFtpStatistics();
                            if (ftpStat != null) {
                                ftpStat.setDownload(session, file, transSz);
                            }
                        } catch (SocketException var29) {
                            this.LOG.debug("Socket exception during data transfer", var29);
                            failure = true;
                            session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 426, "RETR", fileName, file, transSz));
                        } catch (IOException var30) {
                            this.LOG.debug("IOException during data transfer", var30);
                            failure = true;
                            session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 551, "RETR", fileName, file, transSz));
                        } finally {
                            IoUtils.close(is);
                        }

                        if (!failure) {
                            session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 226, "RETR", fileName, file, transSz));
                        }

                    }
                }
            }
        } finally {
            session.resetState();
            session.getDataConnection().closeDataConnection();
        }
    }

    public InputStream openInputStream(FtpIoSession session, FtpFile file, long skipLen) throws IOException {
        Object in;
        if (session.getDataType() == DataType.ASCII) {
            long offset = 0L;
            in = new BufferedInputStream(file.createInputStream(0L));

            while(offset++ < skipLen) {
                int c;
                if ((c = ((InputStream)in).read()) == -1) {
                    throw new IOException("Cannot skip");
                }

                if (c == 10) {
                    ++offset;
                }
            }
        } else {
            in = file.createInputStream(skipLen);
        }

        return (InputStream)in;
    }
}
