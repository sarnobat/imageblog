package com.rohidekar.imageblog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;

/** Entry point classes define <code>onModuleLoad()</code>. */
public class ImageBlog implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public void onModuleLoad() {
		// Add the image, label, and clip/restore buttons to the root panel.
		Document.get().getBody()
				.appendChild(new MainWidget("Sridhar", greetingService).getElement());
	}
}
