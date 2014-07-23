package com.android.projecte.townportal.rss;

import java.io.Serializable;

/*
 * Feed item represents a single item in the feed storing data with various setters and getters
 * Feeditem and the rest of the rss code was from http://javatechig.com/android/json-feed-reader-in-android
 * which was tweaked and modified.
 */

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
