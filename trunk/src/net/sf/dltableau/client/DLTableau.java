package net.sf.dltableau.client;

import net.sf.dltableau.shared.DLTableauBean;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class DLTableau implements EntryPoint {

	private final DLTableauServiceAsync tableauService = GWT.create(DLTableauService.class);

	public void onModuleLoad() {
		final Button goButton = new Button("Go");
		final Button astButton = new Button("AST");
		final TextBox formulaField = new TextBox();
		formulaField.setText("exists R. (forall S. C) and forall R. (exists S. not C)");
		final Label errorLabel = new Label();
		final HTML outputLabel = new HTML();
		final CheckBox useUnicode = new CheckBox("Use UNICODE symbols");

		// We can add style names to widgets
		goButton.addStyleName("sendButton");
		formulaField.addStyleName("formula");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(formulaField);
		RootPanel.get("sendButtonContainer").add(goButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);
		RootPanel.get("output").add(outputLabel);
		RootPanel.get("astButtonContainer").add(astButton);
		RootPanel.get("optionsContainer").add(useUnicode);

		useUnicode.setValue(true);
		
		// Focus the cursor on the name field when the app loads
		formulaField.setFocus(true);
		formulaField.selectAll();

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			private void sendNameToServer() {
				errorLabel.setText("");
				goButton.setEnabled(false);
				tableauService.solve(formulaField.getText(),
					useUnicode.getValue(),
					new AsyncCallback<DLTableauBean>() {
						public void onFailure(Throwable e) {
							Window.alert("Remote Procedure Call - Failure:\n\n" + e.getMessage());
							goButton.setEnabled(true);
						}
						
						public void onSuccess(DLTableauBean result) {
							goButton.setEnabled(true);
							outputLabel.setHTML(result.toHTML());
						}
					}
				);
			}
		}

		MyHandler handler = new MyHandler();
		goButton.addClickHandler(handler);
		formulaField.addKeyUpHandler(handler);
		
		astButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				astButton.setEnabled(false);
				tableauService.syntaxTree(formulaField.getText(), new AsyncCallback<String>() {
					public void onFailure(Throwable e) {
						Window.alert("Remote Procedure Call - Failure:\n\n" + e.getMessage());
						astButton.setEnabled(true);
					}
					
					public void onSuccess(String result) {
						astButton.setEnabled(true);
						outputLabel.setHTML("<pre>" + result + "</pre>");
					}
				});
			}
		});
	}
}
