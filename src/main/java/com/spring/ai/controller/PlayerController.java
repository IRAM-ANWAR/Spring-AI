package com.spring.ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.ai.model.Achievement;
import com.spring.ai.model.Player;

@RestController
@RequestMapping("/openai")
public class PlayerController {

	private final ChatClient chatClient;

	public PlayerController(ChatClient.Builder chatClient) {
		this.chatClient = chatClient.build();
	}

	@GetMapping("/achievement/player")
	public List<Achievement> getAchievement(@RequestParam String name) {

		String message = "Provide a List of Achievements for {player}";
		PromptTemplate template = new PromptTemplate(message);
		Prompt prompt = template.create(Map.of("player", name));
		return this.chatClient.prompt(prompt).call().entity(new ParameterizedTypeReference<List<Achievement>>() {
		});
	}

	@GetMapping("/player")
	public List<Player> getPlayerAchievement(@RequestParam String name) {

		BeanOutputConverter<List<Player>> converter = new BeanOutputConverter<>(
		        new ParameterizedTypeReference<List<Player>>() {// ParameterizedTypeReference - bcz it is a list
		        });
		String message = """
		        Generate a list of Career achievements for the sportsperson {sports}.
		        Include the Player as the key and achievements as the value for it
		        {format}
		        """;
		PromptTemplate template = new PromptTemplate(message);
		Prompt prompt = template.create(Map.of("sports", name, "format", converter.getFormat()));

		/*
		 * ChatResponse response = chatClient .prompt(prompt) .call() .chatResponse();
		 *
		 * return response.getResult().getOutput().getContent();
		 */
		Generation result = this.chatClient.prompt(prompt).call().chatResponse().getResult();
		return converter.convert(result.getOutput().getContent());
	}

}
