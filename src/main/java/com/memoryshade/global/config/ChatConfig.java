package com.memoryshade.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;

@Configuration
public class ChatConfig {

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
  }
}