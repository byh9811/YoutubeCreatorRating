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
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;

import wgstudy.back.domain.AutoCompletion;
import wgstudy.back.domain.AutoCompletionContent;
import wgstudy.back.domain.ChannelInfo;

@Service
public class ChannelInfoService implements ChannelInfoProvider {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static YouTube youtube;
	private final long NUMBER_OF_VIDEOS_RETURNED_BY_ID = 10;
	
	@Override
	public ChannelInfo run(String id) {
		ChannelInfo ci = new ChannelInfo();
		
		try {
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException { }
			}).setApplicationName("youtube-video-duration-get").build();
			
			getChannelInfoById(id, ci);
			// getCategoryById(id, ci);
			String videoList = getRecentVideoIdByChannelId(id);
			getVideoInfoById(videoList, ci);
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
	
	private void getVideoInfoById(String videoList, ChannelInfo ci) throws IOException {
		int views = 0;
		int penalty = 1;
		int likes = 0;
		int dislikes = 0;
		int comments = 0;
		
		YouTube.Videos.List videos = youtube.videos().list("statistics");
		videos.setKey("AIzaSyCjOlrNkkzNNTkA8ZqkKXfY7n9OA-CkLIE");
		videos.setId(videoList);
		Iterator<Video> iterator = videos.execute().getItems().iterator();
		if (!iterator.hasNext()) {
		      System.out.println(" There aren't any results for your query.");
		}
		
		while (iterator.hasNext()) {
			Video singleVideo = iterator.next();
			VideoStatistics statistics= singleVideo.getStatistics();
			long viewCount;
			long likeCount;
			long dislikeCount;
			long commentCount;
		    
		    try {
		    	viewCount = statistics.getViewCount().longValue();
		    }
		    catch (NullPointerException e) {
		    	viewCount = 0;
	    	}
		    
	    	try {
	    		likeCount = statistics.getLikeCount().longValue();
	    	}
	    	catch (NullPointerException e) {
	    		likeCount = 0;
	    	}
	    	
	    	try {
	    		dislikeCount = statistics.getDislikeCount().longValue();
	    	}
	    	catch (NullPointerException e) {
	    		dislikeCount = 0;
	    	}
	    	
	    	try {
	    		commentCount = statistics.getCommentCount().longValue();
	    	}
	    	catch (NullPointerException e) {
	    		commentCount = 0;
	    	}
		    		
	    	System.out.println(" ViewCount: " + viewCount);
	    	System.out.println(" LikeCount: " + likeCount);
	    	System.out.println(" DislikeCount: " + dislikeCount);
	    	System.out.println(" CommentCount: " + commentCount);
	    	System.out.println("\n-------------------------------------------------------------\n");
	    	views += viewCount;
	    	likes += likeCount;
	    	dislikes += dislikeCount;
	    	comments += commentCount;
		}
    	views /= 10;
    	likes /= 10;
    	dislikes /= 10;
    	comments /= 10;
    	
    	long subs = ci.getSubscribers();
    	ci.setATK(views);
    	ci.setDEF((int)(subs/2));
    	if(likes > views/100)
    		ci.getItems().add("좋아요 아이템");
    	if(likes/dislikes > 30)
    		ci.getItems().add("싫어요 아이템");
    	if(views/comments > 400)
    		ci.getItems().add("댓글 아이템");
    	if(views<ci.getDEF())
    		penalty = 5;
    	int lv = ci.getATK()/5 + ci.getDEF()/penalty;
    	for(@SuppressWarnings("unused") String temp : ci.getItems())
    		lv *= 1.1;
    	ci.setLV(lv);
	}

	private String getRecentVideoIdByChannelId(String id) throws IOException {
		YouTube.Search.List search = youtube.search().list("id");
		search.setKey("AIzaSyCjOlrNkkzNNTkA8ZqkKXfY7n9OA-CkLIE");
		search.setChannelId(id);
		search.setType("video");
		search.setOrder("date");
		search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED_BY_ID);
		
		Iterator<SearchResult> iterator = search.execute().getItems().iterator();
		StringBuilder sb = new StringBuilder();
		while(true) {
			sb.append(iterator.next().getId().getVideoId().toString());
			if(iterator.hasNext())
				sb.append(",");
			else
				break;
		}
		System.out.println("VideoIds: " + sb);
		
		return sb.toString();
	}

	private void getChannelInfoById(String id, ChannelInfo ci) throws IOException {
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
