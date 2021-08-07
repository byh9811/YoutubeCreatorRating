package wgstudy.back.service;

import wgstudy.back.domain.ChannelInfo;

public interface ChannelInfoProvider {
	ChannelInfo run(String channelId); 
}
