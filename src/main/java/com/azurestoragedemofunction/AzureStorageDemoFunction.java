package com.azurestoragedemofunction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class AzureStorageDemoFunction {

   /**
    * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
    * using "curl" command in bash: 1. curl -d "HTTP Body" {your
    * host}/api/HttpExample 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
    * 
    * @throws URISyntaxException
    * @throws InvalidKeyException
    * @throws StorageException
    */
   @FunctionName("AzureStorageDemoFunction")
   public void run(
         @BlobTrigger(name = "file", dataType = "binary", path = "files/{name}", connection = "AzureStorageDemoConnectionStringSrc") byte[] content,
         @BindingName("name") String filename, final ExecutionContext context)
         throws InvalidKeyException, URISyntaxException, StorageException {
      context.getLogger().info("Name: " + filename + " Size: " + content.length + " bytes");

      CloudStorageAccount storageAccountDest;
      CloudBlobClient blobClientDest = null;
      CloudBlobContainer containerDest = null;
      
      String storageConnectionStringDest = System.getenv("AzureStorageDemoConnectionStringDest");

      // Parse the connection string and create a blob client to interact with Blob
      // storage
      storageAccountDest = CloudStorageAccount.parse(storageConnectionStringDest);
      blobClientDest = storageAccountDest.createCloudBlobClient();
      containerDest = blobClientDest.getContainerReference("files2");

      // Create the container if it does not exist with public access.
      context.getLogger().info("Creating container: " + containerDest.getName());
      containerDest.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(),
            new OperationContext());

      CloudBlob blobDest = containerDest.getBlockBlobReference(filename);
      try {
         
         context.getLogger().info("Start Uploading blob: " + filename);
         blobDest.uploadFromByteArray(content, 0, content.length);
         context.getLogger().info("Finished Uploading blob: " + filename);

      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
		
    }
}
