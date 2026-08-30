package com.joshuawilliams.ims.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        // Credentials are picked up automatically from the AWS_ACCESS_KEY_ID /
        // AWS_SECRET_ACCESS_KEY environment variables via the SDK's default
        // credential provider chain - nothing hardcoded here. On EC2 later,
        // this same code will instead pick up the instance's IAM role with
        // zero changes needed.
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}
