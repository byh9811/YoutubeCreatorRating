package wgstudy.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import wgstudy.back.domain.AutoCompletion;
import wgstudy.back.service.AutoCompletionProvider;

@RestController
public class MainController {
	private AutoCompletionProvider autoCompletionProvider;
	
	@Autowired
	public MainController(AutoCompletionProvider autoCompletionProvider) {
		this.autoCompletionProvider = autoCompletionProvider;
	}
	
	@GetMapping("autoCompletion")
	public AutoCompletion lightSearch(String name) {
		return autoCompletionProvider.run(name);
	}
}