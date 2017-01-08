package com.rohidekar.imageblog.client;

import java.util.List;

import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;

public class MainWidget extends UIObject {

	private static MainWidgetUiBinder uiBinder = GWT.create(MainWidgetUiBinder.class);

	interface MainWidgetUiBinder extends UiBinder<Element, MainWidget> {
	}

	@UiField
	DivElement verticalPanel;
	private final GreetingServiceAsync greentingService;

	public MainWidget(String firstName, GreetingServiceAsync iGreetingService) {
		this.greentingService = iGreetingService;
		setElement(uiBinder.createAndBindUi(this));
		greentingService.getImages(new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
			}

			public void onSuccess(List<String> result) {
				for (String path : result) {
					Image i = new Image();
					// TODO: if the image height is greater than the browser height, put it on the same row with a height equal to the previous image
					i.setWidth("30%");
					i.setUrl(path);
					verticalPanel.appendChild(i.getElement());
					Label br = new HTML("<br>");
					verticalPanel.appendChild(br.getElement());
				}
			}
		});
	}
}
