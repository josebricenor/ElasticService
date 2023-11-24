package com.vicarius.elasticservice.controller;

import com.vicarius.elasticservice.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ElasticsearchController {

  private final ElasticsearchService elasticsearchService;

  @Autowired
  public ElasticsearchController(ElasticsearchService elasticsearchService) {
    this.elasticsearchService = elasticsearchService;
  }

  @GetMapping("/docs")
  public ResponseEntity<String> root() {
    return ResponseEntity.ok("Service is up and running!");
  }

  @PostMapping("/api/createIndex")
  public String createIndex(@RequestParam String indexName) {
    return elasticsearchService.createIndex(indexName);
  }

  @PostMapping("/api/createDocument")
  public String createDocument(@RequestParam String indexName, @RequestBody String document) {
    return elasticsearchService.createDocument(indexName, document);
  }

  @GetMapping("/api/getDocument/{index}/{id}")
  public String getDocument(@PathVariable String index, @PathVariable String id) {
    return elasticsearchService.getDocumentById(index, id);
  }
}

