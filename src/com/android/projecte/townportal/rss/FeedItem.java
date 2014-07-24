/*
 * House Droidaryen!
 *FeedItem represents a single item in the feed storing various data along with the appropriate setters and getters to move the data.
 * * 
 * FeedListActivity was developed using the tutorial http://javatechig.com/android/json-feed-reader-in-android
 * along with the documentation for JSON and Google Feeds api
 */
package com.android.projecte.townportal.rss;

import java.io.Serializable;

public class FeedItem implements Serializable {

	private static final long serialVersionUID = 4670282224321790916L;
	private String title;
	private String date;
	private String attachmentUrl;
	private String content;
	private String url;
	private String contentPreview;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentPreview() {
		return contentPreview;
	}

	public void setContentPreview(String contentPreview) {
		this.contentPreview = contentPreview;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	@Override
	public String toString() {
		return "[ title=" + title + ", date=" + date + "]";
	}
}
