package com.gcgenome.rms.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI
import java.time.Duration

@Configuration
class S3Config(
    @Value("\${aws.credentials.access-key}")
    val accessKey: String,
    @Value("\${aws.credentials.secret-key}")
    val secretKey: String,
    @Value("\${aws.s3.endpoint}")
    val endpoint: String
) {
    @Bean
    fun s3Client(): S3AsyncClient {
        return S3AsyncClient.builder()
            .httpClient(sdkAsyncHttpClient())
            .region(Region.US_EAST_1)
            .credentialsProvider { AwsBasicCredentials.create(accessKey, secretKey) }
            .endpointOverride(URI.create(endpoint))
            .forcePathStyle(true)
            .serviceConfiguration(s3Configuration()).build();
    }
    private fun sdkAsyncHttpClient(): SdkAsyncHttpClient {
        return NettyNioAsyncHttpClient.builder()
            .writeTimeout(Duration.ZERO)
            .maxConcurrency(64)
            .build()
    }

    private fun s3Configuration(): S3Configuration {
        return S3Configuration.builder()
            .checksumValidationEnabled(false)
            .chunkedEncodingEnabled(true)
            .build()
    }
}