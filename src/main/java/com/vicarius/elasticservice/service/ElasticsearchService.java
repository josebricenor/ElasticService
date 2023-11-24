package com.vicarius.elasticservice.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

@Service
public class ElasticsearchService {

  private final Aws4Signer signer;
  private final StaticCredentialsProvider credentialsProvider;
  private static final String SERVICE_ENDPOINT = "https://search-elastic-service-domain-bho5awv6dnw6wys3db6qhjj6oy.us-west-2.es.amazonaws.com";
  private static final String REGION = "us-west-2";
  private final SdkHttpClient httpClient;

  @Autowired
  public ElasticsearchService(SdkHttpClient httpClient,
      Aws4Signer signer,
      StaticCredentialsProvider credentialsProvider) {
    this.httpClient = httpClient;
    this.signer = signer;
    this.credentialsProvider = credentialsProvider;
  }

  public String createIndex(String indexName) {
    try {
      URI endpoint = new URI(SERVICE_ENDPOINT + "/" + indexName);

      SdkHttpFullRequest signedRequest = SdkHttpFullRequest.builder()
          .method(SdkHttpMethod.PUT)
          .uri(endpoint)
          .build();

      Aws4SignerParams signerParams = Aws4SignerParams.builder()
          .awsCredentials(credentialsProvider.resolveCredentials())
          .signingName("es")
          .signingRegion(Region.of(REGION))
          .build();

      SdkHttpFullRequest signedSdkRequest = signer.sign(signedRequest, signerParams);

      HttpExecuteRequest executeRequest = HttpExecuteRequest.builder()
          .request(signedSdkRequest)
          .build();

      HttpExecuteResponse executeResponse = httpClient.prepareRequest(executeRequest).call();

      if (executeResponse.httpResponse().isSuccessful()) {
        return "Index created: " + indexName;
      } else {
        return "Failed to create index: " + indexName + ". HTTP Status Code: "
            + executeResponse.httpResponse().statusCode();
      }
    } catch (URISyntaxException e) {
      return "Error in URI syntax: " + e.getMessage();
    } catch (Exception e) {
      return "Error creating index: " + e.getMessage();
    }
  }

  public String createDocument(String indexName, String documentJson) {
    try {
      URI endpoint = new URI(SERVICE_ENDPOINT + "/" + indexName + "/_doc/");
      byte[] contentBytes = documentJson.getBytes(StandardCharsets.UTF_8);

      SdkHttpFullRequest.Builder requestBuilder = SdkHttpFullRequest.builder()
          .method(SdkHttpMethod.POST)
          .uri(endpoint)
          .putHeader("Content-Type", "application/json")
          .putHeader("Content-Length", String.valueOf(contentBytes.length))
          .contentStreamProvider(() -> new ByteArrayInputStream(contentBytes));

      SdkHttpFullRequest preSignedRequest = requestBuilder.build();

      Aws4SignerParams signerParams = Aws4SignerParams.builder()
          .awsCredentials(credentialsProvider.resolveCredentials())
          .signingName("es")
          .signingRegion(Region.of(REGION))
          .build();

      SdkHttpFullRequest signedRequest = signer.sign(preSignedRequest, signerParams);

      HttpURLConnection connection = (HttpURLConnection) endpoint.toURL().openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);

      for (Map.Entry<String, List<String>> entry : signedRequest.headers().entrySet()) {
        String headerKey = entry.getKey();
        for (String headerValue : entry.getValue()) {
          connection.setRequestProperty(headerKey, headerValue);
        }
      }

      try (OutputStream os = connection.getOutputStream()) {
        os.write(contentBytes);
      }

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK
          || responseCode == HttpURLConnection.HTTP_CREATED) {
        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
          String line;
          while ((line = br.readLine()) != null) {
            responseBuilder.append(line);
          }
        }
        return responseBuilder.toString();
      } else {
        return "Failed to create document in index: " + indexName + ". HTTP Status Code: "
            + responseCode;
      }
    } catch (Exception e) {
      return "Error creating document: " + e.getMessage();
    }
  }

  public String getDocumentById(String index, String documentId) {
    try {
      URI endpoint = new URI(SERVICE_ENDPOINT + "/" + index + "/_doc/" + documentId);

      SdkHttpFullRequest signedRequest = SdkHttpFullRequest.builder()
          .method(SdkHttpMethod.GET)
          .uri(endpoint)
          .build();

      Aws4SignerParams signerParams = Aws4SignerParams.builder()
          .awsCredentials(credentialsProvider.resolveCredentials())
          .signingName("es")
          .signingRegion(Region.of(REGION))
          .build();

      SdkHttpFullRequest signedSdkRequest = signer.sign(signedRequest, signerParams);

      HttpExecuteRequest executeRequest = HttpExecuteRequest.builder()
          .request(signedSdkRequest)
          .build();

      HttpExecuteResponse executeResponse = httpClient.prepareRequest(executeRequest).call();

      if (executeResponse.httpResponse().isSuccessful()) {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(executeResponse.responseBody().orElseThrow(),
                StandardCharsets.UTF_8));
        String responseString = reader.lines().collect(Collectors.joining("\n"));
        return "Document retrieved: " + responseString;
      } else {
        return "Document not found in index: " + index + ". HTTP Status Code: "
            + executeResponse.httpResponse().statusCode();
      }
    } catch (URISyntaxException e) {
      return "Error in URI syntax: " + e.getMessage();
    } catch (Exception e) {
      return "Error retrieving document: " + e.getMessage();
    }
  }
}
