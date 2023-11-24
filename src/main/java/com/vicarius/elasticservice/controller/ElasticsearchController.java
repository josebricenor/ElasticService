package com.vicarius.elasticservice.controller;
import com.vicarius.elasticservice.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class ElasticsearchController {

  private final ElasticsearchService elasticsearchService;

  @Autowired
  public ElasticsearchController(ElasticsearchService elasticsearchService) {
    this.elasticsearchService = elasticsearchService;
  }

  @GetMapping("/")
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

