/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.ut.biolab.medsavant.shared.util;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.samtools.util.SeekableBufferedStream;
import net.sf.samtools.util.SeekableFileStream;
import net.sf.samtools.util.SeekableHTTPStream;
import net.sf.samtools.util.SeekableStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPFile;


/**
 * Some useful methods for performing network-related functions.
 *
 * @author vwilliams, tarkvara
 */
public class NetworkUtils {

    private static final Log LOG = LogFactory.getLog(NetworkUtils.class);    
    public static final int CONNECT_TIMEOUT = 30000; // 30s timeout for making connection
    public static final int READ_TIMEOUT = 30000;    // 30s timeout for reading data
    public static final int NONCRITICAL_CONNECT_TIMEOUT = 5000; //For non-critical network i/o, use shorter 5s timeouts.
    public static final int NONCRITICAL_READ_TIMEOUT = 5000;
    public static final int BUF_SIZE = 8192;         // 8kB buffer
    public static final boolean ALLOW_URL_REDIRECTS = true;
    static {
        // Create a trust manager that does not validate certificate chains.
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception x) {
        }
    }

    /**
     * Open a stream for the given URL with the CONNECT_TIMEOUT and READ_TIMEOUT.
     * @throws IOException
     */
    public static InputStream openStream(URL url) throws IOException {
        return openStream(url, CONNECT_TIMEOUT, READ_TIMEOUT);
    }

    /**
     * Open a stream for the given URL with custom timeouts
     * @throws IOException
     */
    public static InputStream openStream(URL url, int connectTimeout, int readTimeout) throws IOException, SocketTimeoutException {
        //URLConnection conn = url.openConnection();
       // conn.setConnectTimeout(connectTimeout);
        //conn.setReadTimeout(readTimeout);
        
       HttpURLConnection huc = (HttpURLConnection) url.openConnection();
       HttpURLConnection.setFollowRedirects(ALLOW_URL_REDIRECTS);
       huc.setConnectTimeout(connectTimeout);
       huc.setReadTimeout(readTimeout);
       huc.setRequestMethod("GET");
       huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
       huc.connect();
       InputStream input = huc.getInputStream();        
       return input;
    }

    /**
     * Create a URL object from a string which we know to be a valid URL.  Avoids having
     * to catch a MalformedURLException which we know will never be thrown.  Intended
     * as the URL equivalent to <code>URL.create()</code>.
     */
    public static URL getKnownGoodURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ignored) {
            throw new IllegalArgumentException();
        }
    }


    /**
     * Create a URL object from an existing URL and a string which we know to be a valid path.  Avoids having
     * to catch a MalformedURLException which we know will never be thrown.  Intended
     * as the URL equivalent to <code>URL.create()</code>.
     */
    public static URL getKnownGoodURL(URL base, String spec) {
        try {
            String baseStr = base.toString();
            if (!baseStr.endsWith("/")) {
                baseStr += "/";
            }
            return new URL(baseStr + spec);
        } catch (MalformedURLException ignored) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Synchronously download the given URL to the given destination directory.
     *
     * @param u the URL to be downloaded
     * @param destDir the destination directory
     * @param fileName the destination file within <code>destDir</code>; use <code>null</code> to infer the name from the URL
     * @return the downloaded file
     */
    public static File downloadFile(URL u, File destDir, String fileName) throws IOException {
        File f = new File(destDir, fileName != null ? fileName : MiscUtils.getFilenameFromPath(u.getPath()));
        LOG.info("Downloading file "+u+" into directory "+f.getAbsolutePath());

        InputStream in = NetworkUtils.openStream(u);
        OutputStream out = new FileOutputStream(f);
        byte[] buf = new byte[BUF_SIZE];
        int bytesRead;
        int totalRead = 0;
        while ((bytesRead = in.read(buf)) != -1) {
            out.write(buf, 0, bytesRead);
            totalRead += bytesRead;
        }
        in.close();
        out.close();
        LOG.info("Downloaded "+totalRead+" bytes");
        return f;
    }

    /**
     * Synchronously download a (small) file and read its contents to a String.
     *
     * @param u the URL to be downloaded
     * @return a string containing the contents of the URL
     */
    public static String downloadFile(URL u) throws IOException {

        StringBuilder result = new StringBuilder();

        InputStream in = NetworkUtils.openStream(u);
        byte[] buf = new byte[BUF_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buf)) != -1) {
            char [] r = (new String(buf)).toCharArray();
            result.append(r, 0, bytesRead);
        }

        return result.toString();
    }

    /**
     * Get a unique hash representing the contents of this file.  For an HTTP server,
     * this will be the ETag returned in the header; for FTP servers, we create a
     * hash based on the size and modification time.
     *
     * @param url   URL of the file to be hashed.
     * @return  a hash-value "unique" to this file.
     *
     * @throws IOException
     */
    public static String getHash(URL url) throws IOException {
        String proto = url.getProtocol().toLowerCase();
        if (proto.equals("http") || proto.equals("https")) {
            URLConnection conn = null;
            try {
                conn = url.openConnection();
                return conn.getHeaderField("ETag");
            }
            finally {
                if ((conn != null) && (conn instanceof HttpURLConnection)) {
                    ((HttpURLConnection)conn).disconnect();
                }
            }
        } else if (proto.equals("ftp")) {
            SeekableFTPStream ftp = new SeekableFTPStream(url, "anonymous", "");

            try {
                // List the files.  We should only get one match.
                FTPFile[] files = ftp.listFiles(url.getFile());
                if (files.length > 0) {
                    return String.format("%016x-%016x", files[0].getTimestamp().getTimeInMillis(), files[0].getSize());
                } else {
                    throw new IOException("URL not found: " + url);
                }
            } finally {
                ftp.close();
            }
        } else if (proto.equals("file")) {
            // Cheesy fake hash-code based on the modification time and size.
            try {
                File f = new File(url.toURI());
                return String.format("%016x-%016x", f.lastModified(), f.length());
            } catch (URISyntaxException x) {
                throw new IllegalArgumentException("Invalid argument; cannot parse " + url + " as a file.");
            }
        } else {
            throw new IllegalArgumentException("Invalid argument; cannot get hash for " + proto + " URLs.");
        }
    }

    /**
     * Given a URI, return a SeekableStream of the appropriate type.
     *
     * @param uri an ftp:, http:, or file: URI
     * @param allowCaching if true, remote streams will be wrapped in a CachedSeekableStream
     * @return a SeekableStream which can be passed to SavantROFile or BAMDataSource
     */
    public static SeekableStream getSeekableStreamForURI(URI uri) throws IOException {
        String proto = uri.getScheme().toLowerCase();
        SeekableStream result;
        if (proto.equals("file")) {
            result = new SeekableBufferedStream(new SeekableFileStream(new File(uri)));
        } else {
            if (proto.equals("http") || proto.equals("https")) {
                result = new SeekableHTTPStream(uri.toURL());
            } else {
                throw new IOException("Unknown URI scheme " + uri.toString());
            }
            result = new SeekableBufferedStream(result);
        }
        return result;
    }
}
