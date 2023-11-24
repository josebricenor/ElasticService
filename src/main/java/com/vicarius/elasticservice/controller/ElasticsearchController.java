package com.vicarius.elasticservice.controller;
import com.vicarius.elasticservice.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ElasticsearchController {

  private final ElasticsearchService elasticsearchService;

  @Autowired
  public ElasticsearchController(ElasticsearchService elasticsearchService) {
    this.elasticsearchService = elasticsearchService;
  }

  @PostMapping("/createIndex")
  public String createIndex(@RequestParam String indexName) {
    return elasticsearchService.createIndex(indexName);
  }

  @PostMapping("/createDocument")
  public String createDocument(@RequestParam String indexName, @RequestBody String document) {
    return elasticsearchService.createDocument(indexName, document);
  }

  @GetMapping("/getDocument/{index}/{id}")
  public String getDocument(@PathVariable String index, @PathVariable String id) {
    return elasticsearchService.getDocumentById(index, id);
  }
}

