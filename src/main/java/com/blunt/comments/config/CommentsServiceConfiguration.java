package com.blunt.comments.config;

import brave.sampler.Sampler;
import com.blunt.comments.mapper.CommentsMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentsServiceConfiguration {

  @Bean
  public Sampler defaultSampler() {
    return Sampler.ALWAYS_SAMPLE;
  }

  @Bean
  public CommentsMapper bluntMapper(){
    CommentsMapper mapper = Mappers.getMapper(CommentsMapper.class);
    return mapper;
  }

}
