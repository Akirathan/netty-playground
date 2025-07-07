package org.akirathan.netty;

import io.netty.internal.tcnative.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {
  private volatile static Class<?> libClass;
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    LOGGER.info("Starting netty playground...");
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
}
