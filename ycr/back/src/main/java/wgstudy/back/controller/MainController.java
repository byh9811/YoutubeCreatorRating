package wgstudy.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import wgstudy.back.domain.AutoCompletion;
import wgstudy.back.domain.ChannelInfo;
import wgstudy.back.service.AutoCompletionProvider;
import wgstudy.back.service.ChannelInfoProvider;

@RestController
public class MainController {
	private AutoCompletionProvider autoCompletionProvider;
	private ChannelInfoProvider channelInfoProvider;
	
	@Autowired
	public MainController(AutoCompletionProvider autoCompletionProvider, ChannelInfoProvider channelInfoProvider) {
		this.autoCompletionProvider = autoCompletionProvider;
		this.channelInfoProvider = channelInfoProvider;
	}
	
	@GetMapping("autoCompletion")
	public AutoCompletion lightSearch(String name) {
		return autoCompletionProvider.run(name);
	}
	
	@GetMapping("channelInfo")
	public ChannelInfo heavySearch(String id) {
		return channelInfoProvider.run(id);
	}
}