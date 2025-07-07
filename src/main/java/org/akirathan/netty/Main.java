package org.akirathan.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpHeadersFactory;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.ssl.SslProvider;
import io.netty.internal.tcnative.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private volatile static Class<?> libClass;
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    LOGGER.info("Starting netty playground...");
    //AzureConnector.listBlob();
    loadNativeLibEntrypoint();
  }

  private static void ensureClassesInNI() {
    libClass = Library.class;
  }

  private static void loadNativeLibEntrypoint() {
    addCurDirToLibPath();
    loadNativeLib();
  }

  private static void addCurDirToLibPath() {
    String curDir;
    try {
      curDir = new File(".").getCanonicalPath();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    var propName = "java.library.path";
    var libPath = System.getProperty(propName);
    var newLibPath = libPath + ":" + curDir;
    System.setProperty(propName, newLibPath);
    LOGGER.info("Updated {} to [{}]", propName, newLibPath);
  }

  private static void loadNativeLib() {
    var libName = "netty_tcnative_linux_x86_64";
    try {
      System.loadLibrary(libName);
    } catch (Throwable t) {
      System.err.println("Failed to load native library: " + t.getMessage());
      t.printStackTrace();
      throw new RuntimeException("Failed to load native library", t);
    }
    System.out.println("Successfully loaded native library: " + libName);
  }

  /**
   * {@code isAlpnSupported} is called from {@code AzureStorage.listBlob}.
   * And it fails in Enso.
   */
  private static void tryLoadSslProvider() {
    var isAlpnSupported = SslProvider.isAlpnSupported(SslProvider.OPENSSL);
    System.out.println("isAlpnSupported on OpenSSL = " + isAlpnSupported);
  }

  /**
   * Works fine in both JVM and NI.
   */
  private static void createHttpHeader() {
    var headerFac = DefaultHttpHeadersFactory.headersFactory();
    var header = headerFac.newHeaders();
    header.add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
    header.add(HttpHeaderNames.CONTENT_LENGTH, "0");
    System.out.println("header.toString() = " + header);
  }
}
