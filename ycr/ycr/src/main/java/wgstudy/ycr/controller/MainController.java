package wgstudy.ycr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import wgstudy.ycr.domain.AutoCompletion;
import wgstudy.ycr.service.AutoCompletionProvider;

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