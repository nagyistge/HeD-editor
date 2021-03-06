package edu.asu.sharpc2b;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ClassPathResource {

    private String      path;
    private String      encoding;
    private ClassLoader classLoader;
    private Class< ? >  clazz;
    private long        lastRead;

    public ClassPathResource() {

    }

    public ClassPathResource(String path) {
        this( path,
              null,
              null,
              null );
    }

    public ClassPathResource(String path,
                             Class<?> clazz) {
        this( path,
              null,
              clazz,
              null );
    }

    public ClassPathResource(String path,
                             ClassLoader classLoader) {
        this( path,
              null,
              null,
              classLoader );
    }

    public ClassPathResource(String path,
                             String encoding) {
        this( path,
              encoding,
              null,
              null );
    }

    public ClassPathResource(String path,
                             String encoding,
                             Class<?> clazz) {
        this( path,
              encoding,
              clazz,
              null );
    }

    public ClassPathResource(String path,
                             String encoding,
                             ClassLoader classLoader) {
        this( path,
              encoding,
              null,
              classLoader );
    }

    public ClassPathResource(String path,
                             String encoding,
                             Class<?> clazz,
                             ClassLoader classLoader) {
        if ( path == null ) {
            throw new IllegalArgumentException( "path cannot be null" );
        }
        this.path = path;
        this.encoding = encoding;
        this.clazz = clazz;
        this.classLoader = (classLoader) == null ? retrieveClassLoader() : classLoader;
    }

    private ClassLoader retrieveClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader currentClassLoader = ClassPathResource.class.getClassLoader();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        if ( contextClassLoader != null ) {
            return contextClassLoader;
        }

        if ( currentClassLoader != null ) {
            return currentClassLoader;
        }

        if ( systemClassLoader != null ) {
            return systemClassLoader;
        }

        throw new IllegalStateException( "No classloader available to retrieve resources, fatal" );
    }


    /**
     * This implementation opens an InputStream for the given class path resource.
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     * @see java.lang.Class#getResourceAsStream(String)
     */
    public InputStream getInputStream() throws IOException {
        //update the lastRead field
        this.lastRead = this.getLastModified();

        //Some ClassLoaders caches the result of getResourceAsStream() this is
        //why we get the Input Stream from the URL of the resource
        //@see JBRULES-2960
        return this.getURL().openStream();
    }

    /**
     * This implementation returns a URL for the underlying class path resource.
     * @see java.lang.ClassLoader#getResource(String)
     * @see java.lang.Class#getResource(String)
     */
    public URL getURL() throws IOException {
        URL url = null;
        if ( this.clazz != null ) {
            url = this.clazz.getResource( this.path );
        }

        if ( url == null ) {
            url = this.classLoader.getResource( this.path );
        }

        if ( url == null ) {
            throw new FileNotFoundException( "'" + this.path + "' cannot be opened because it does not exist" );
        }
        return url;
    }

    public boolean hasURL() {
        return true;
    }

    public long getLastModified() {
        URLConnection conn = null;
        try {
            conn = getURL().openConnection();
            if (conn instanceof JarURLConnection ) {
                // There is a bug in sun's jar url connection that causes file handle leaks when calling getLastModified()
                // Since the time stamps of jar file contents can't vary independent from the jar file timestamp, just use
                // the jar file timestamp
                URL jarURL = ((JarURLConnection)conn).getJarFileURL();
                if (jarURL.getProtocol().equals("file")) {
                    // Return the last modified time of the underlying file - saves some opening and closing
                    return new File(jarURL.getFile()).lastModified();
                } else {
                    // Use the URL mechanism
                    URLConnection jarConn = null;
                    try {
                        jarConn = jarURL.openConnection();
                        return jarConn.getLastModified();
                    } catch (IOException e) {
                        return -1;
                    } finally {
                        try {
                            if (jarConn!=null) jarConn.getInputStream().close();
                        } catch (IOException e) { }
                    }
                }
            } else {
                return conn.getLastModified();
            }
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get LastModified for ClasspathResource", e );
        } finally {
            if (conn != null) {
                try {
                    conn.getInputStream().close();
                } catch (IOException e) { }
            }
        }
    }

    public long getLastRead() {
        return this.lastRead;
    }

    public String getEncoding() {
        return encoding;
    }

    public Reader getReader() throws IOException {
        if ( this.encoding != null ) {
            return new InputStreamReader( getInputStream(), encoding );
        } else {
            return new InputStreamReader( getInputStream() );
        }
    }


    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public String getPath() {
        return path;
    }

    protected void setLastRead(long lastRead) {
        this.lastRead = lastRead;
    }

    public boolean equals(Object object) {
        if (!(object instanceof ClassPathResource)) {
            return false;
        }

        ClassPathResource other = (ClassPathResource) object;
        return this.path.equals(other.path) && this.clazz == other.clazz && this.classLoader == other.classLoader;
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public String toString() {
        return "[ClassPathResource path='" + this.path + "']";
    }

    public boolean exists() {
        URL url = null;
        if ( this.clazz != null ) {
            url = this.clazz.getResource( this.path );
        }

        if ( url == null ) {
            url = this.classLoader.getResource( this.path );
        }

        if ( url == null ) {
            return false;
        }
        return true;
    }

}
