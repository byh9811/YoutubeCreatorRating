package wgstudy.back.service;

import wgstudy.back.domain.AutoCompletion;

public interface AutoCompletionProvider {
	AutoCompletion run(String name);
}