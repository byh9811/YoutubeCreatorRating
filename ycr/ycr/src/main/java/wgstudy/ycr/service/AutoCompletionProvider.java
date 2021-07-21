package wgstudy.ycr.service;

import wgstudy.ycr.domain.AutoCompletion;

public interface AutoCompletionProvider {
	AutoCompletion run(String name);
}