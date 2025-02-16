package com.spring.ai.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Indian constitution will be passed as source of truth
 **/
@RestController
@RequestMapping("/openai")
public class IndianConstitution {

	private final ChatClient chatClient;
	private final VectorStore vectorStore;

	private String prompt = """
	        Your task is to answer the questions about Indian Constitution. Use the information from the DOCUMENTS
	        section to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section,
	        simply state that you don't know the answer.
	        QUESTION:
	        {input}
	        DOCUMENTS:
	        {documents}
	        """;

	public IndianConstitution(ChatClient.Builder builder, VectorStore vectorStore) {
		this.chatClient = builder.build();
		this.vectorStore = vectorStore;
	}

	private String findSimilarData(String q) {
		List<Document> documents = this.vectorStore.similaritySearch(SearchRequest.builder().query(q).topK(5).build());
		return Optional.ofNullable(documents) // Handle null documents list
		        .orElse(Collections.emptyList()) // If null, use an empty list
		        .stream().map(document -> Optional.ofNullable(document.getText()).orElse("")) // Handle null document
		                                                                                      // text
		        .collect(Collectors.joining());
	}

	@GetMapping("/ics")
	public String icsQuestion(@RequestParam String question) {
		return this.chatClient.prompt().user(question).call().content();
	}

	@GetMapping("/ic")
	public String simplifyIC(@RequestParam String q) {
		PromptTemplate template = new PromptTemplate(this.prompt);
		Map<String, Object> promptParams = new HashMap<>();
		promptParams.put("input", q);
		promptParams.put("documents", findSimilarData(q));
		return this.chatClient.prompt(template.create(promptParams)).call().content();
	}
}
