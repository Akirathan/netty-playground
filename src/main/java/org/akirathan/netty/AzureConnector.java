package org.akirathan.netty;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AzureConnector {
  private AzureConnector() {}

  public static void listBlob() {
    var dummySasToken = "sv=2024-11-04&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2025-08-30T19:03:22Z&st=2025-06-24T11:03:22Z&spr=https&sig=z1Gm3CUR896qh1eBfebjkMhsTD4Mw8cB9c3W40kl5z9rzC";
    var containerName = "test-container";
    var storageAccountName = "ensostorage";
    var blobs = listBlob(containerName, null, dummySasToken, storageAccountName);
    System.out.println("blobs = " + blobs);
  }

  private static BlobServiceClient makeClient(
      String storageAccountName,
      String token
  ) {
     var clientBuilder =
         new BlobServiceClientBuilder()
             .endpoint("https://" + storageAccountName + ".blob.core.windows.net/");
     clientBuilder.sasToken(token);
     return clientBuilder.buildClient();
  }

  private static List<String> listBlob(
      String containerName,
      String prefix,
      String token,
      String storageAccountName
  ) {
    var client = makeClient(storageAccountName, token);
    var blobContainer = client.getBlobContainerClient(containerName);
    if (!blobContainer.exists()) {
      throw new IllegalArgumentException("Container does not exist: " + containerName);
    }

    var blobList = blobContainer.listBlobs();

    var result = new ArrayList<String>();
    for (var blob : blobList) {
      var blobName = blob.getName();
      if ((Objects.equals(prefix, "")) || blobName.startsWith(prefix)) {
        result.add(blobName);
      }
    }
    return result;
  }
}
