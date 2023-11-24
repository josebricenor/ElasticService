package com.vicarius.elasticservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;

@Configuration
public class ElasticsearchConfig {

  @Bean
  public Aws4Signer signer() {
    return Aws4Signer.create();
  }

  @Bean
  public StaticCredentialsProvider credentialsProvider() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
        "AKIASDVWZ24TK6KALRWS", "YMoEdsgYk9ckrEWIPfUm8GC3Gv046pqZxr8HaGLS"
    );
    return StaticCredentialsProvider.create(awsCreds);
  }

  @Bean
  public SdkHttpClient httpClient() {
    return UrlConnectionHttpClient.builder().build();
  }
}
