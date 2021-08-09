package wgstudy.back.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Channels;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.ChannelStatistics;
import com.google.api.services.youtube.model.SearchResult;

import wgstudy.back.domain.AutoCompletion;
import wgstudy.back.domain.AutoCompletionContent;
import wgstudy.back.domain.ChannelInfo;

@Service
public class ChannelInfoService implements ChannelInfoProvider {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static YouTube youtube;
	
	@Override
	public ChannelInfo run(String id) {
		ChannelInfo ci = new ChannelInfo();
		
		try {
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException { }
			}).setApplicationName("youtube-video-duration-get").build();
			
			getChannelIdById(id, ci);
			// getCategoryById(id, ci);
		}
		catch (GoogleJsonResponseException e) {
			System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return ci;
	}
	
	private void getChannelIdById(String id, ChannelInfo ci) throws IOException {
		YouTube.Channels.List channels = youtube.channels().list("snippet, statistics");
		channels.setKey("AIzaSyCjOlrNkkzNNTkA8ZqkKXfY7n9OA-CkLIE");
		channels.setId(id);
		Iterator<Channel> iterator = channels.execute().getItems().iterator();
		
		if (!iterator.hasNext()) {
		      System.out.println(" There aren't any results for your query.");
		}
		
		while (iterator.hasNext()) {
			Channel singleChannel = iterator.next();

			if (singleChannel.getKind().equals("youtube#channel")) {
				ChannelSnippet snippet = singleChannel.getSnippet();
				String title = snippet.getTitle();
				String chImg = snippet.getThumbnails().getDefault().getUrl();
				String description = snippet.getDescription();
				
			    ChannelStatistics statistics = singleChannel.getStatistics();
				long viewCount;
				long commentCount;
				long subscriberCount;
		    	
		    	try {
		    		viewCount = statistics.getViewCount().longValue();
		    	}
		    	catch (NullPointerException e) {
		    		viewCount = 0;
		    	}

		    	try {
		    		commentCount = statistics.getCommentCount().longValue();
		    	}
		    	catch (NullPointerException e) {
		    		commentCount = 0;
		    	}
		    	
		    	try {
		    		subscriberCount = statistics.getSubscriberCount().longValue();
		    	}
		    	catch (NullPointerException e) {
		    		subscriberCount = 0;
		    	}
			    		
		    	System.out.println(" Title: " + title);
		    	System.out.println(" Profile: " + chImg);
		    	System.out.println(" Description: " + description);
		    	System.out.println(" ViewCount: " + viewCount);
		    	System.out.println(" CommentCount: " + commentCount);
		    	System.out.println(" SubscriberCount: " + subscriberCount);
		    	System.out.println("\n-------------------------------------------------------------\n");
		    	
		    	ci.setTitle(title);
		    	ci.setChannelImg(chImg);
		    	ci.setDescription(description);
		    	ci.setSubscribers(subscriberCount);
		    }
		}
	}
}
