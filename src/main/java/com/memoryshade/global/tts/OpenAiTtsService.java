package com.memoryshade.global.tts;

import com.memoryshade.domain.chat.exception.ChatErrorCode;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class OpenAiTtsService implements TtsService {

  private static final String OPENAI_SPEECH_URL = "https://api.openai.com/v1/audio/speech";

  @Value("${spring.ai.openai.api-key}")
  private String apiKey;

  @Value("${openai.tts.model:gpt-4o-mini-tts}")
  private String model;

  @Value("${openai.tts.voice:alloy}")
  private String voice;

  private final RestClient.Builder restClientBuilder;



  @Override
  public byte[] synthesize(String text) {
    if (text == null || text.isBlank()) {
      throw new ExceptionList(ChatErrorCode.TTS_FAILED);
    }

    try {
      return restClientBuilder.build()
          .post()
          .uri(OPENAI_SPEECH_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer " + apiKey)
          .body(new OpenAiSpeechRequest(
              model,
              text,
              voice,
              "mp3"
          ))
          .retrieve()
          .body(byte[].class);
    } catch (Exception e) {
      throw new ExceptionList(ChatErrorCode.TTS_FAILED);
    }
  }

  private record OpenAiSpeechRequest(
      String model,
      String input,
      String voice,
      String response_format
  ) {
  }
}