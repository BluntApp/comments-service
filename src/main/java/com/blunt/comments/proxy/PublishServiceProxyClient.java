package com.blunt.comments.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "publish-service")
public interface PublishServiceProxyClient {
  @GetMapping("/api/v1/publish/post/{postId}")
  public ResponseEntity<Object> getPost(
      @RequestHeader(name = "BLUNT-ID", required = true) String bluntId,
      @PathVariable String postId) ;

  @GetMapping("/api/v1/publish/content/{contentId}")
  public ResponseEntity<Object> getContent(
      @RequestHeader(name = "BLUNT-ID", required = true) String bluntId,
      @PathVariable String contentId) ;

}
