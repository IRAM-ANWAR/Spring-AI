package com.spring.ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openai")
public class AIController {

	private final ChatClient chatClient;

	@Value("classpath:/prompt/celeb-details.st") // using resource for prompt
	private Resource celebPrompts;

	public AIController(ChatClient.Builder chatClient) {
		this.chatClient = chatClient.build();
	}

	@GetMapping("/celeb")
	public String getCelebDetails(@RequestParam String name) {
		PromptTemplate template = new PromptTemplate(this.celebPrompts);
		Prompt prompt = template.create(Map.of("name", name));
		return this.chatClient.prompt(prompt).call().content();
	}

	@GetMapping("/sport")
	public String getSportDetails(@RequestParam String name) {
		String message = "List the details of the Sport %s along with their Rules and Regulations.";
		String systemMsg = "You are a smart Virtual Assistant. Your task is to give the details about the Sports.If someone ask about something else and you do not know the answer, just say that you do not know the answer. ";
		UserMessage userMessage = new UserMessage(String.format(message, name));
		SystemMessage systemMessage = new SystemMessage(systemMsg);
		Prompt prompt = new Prompt(List.of(userMessage, systemMessage));
		return this.chatClient.prompt(prompt).call().content();
	}

	@GetMapping("/chat")
	public String prompt(@RequestParam String message) {
		return this.chatClient.prompt(message).call().content();
	}

}
