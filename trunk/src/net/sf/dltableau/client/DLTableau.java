package net.sf.dltableau.client;

import net.sf.dltableau.shared.DLTableauBean;
import net.sf.dltableau.shared.DLTableauOptions;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class DLTableau implements EntryPoint {
	private final DLTableauServiceAsync tableauService = GWT.create(DLTableauService.class);
	private final DLTableauOptions tableauOptions = new DLTableauOptions();
	
	final Button goButton = new Button("Go");
	final Button astButton = new Button("AST");
	final TextArea tboxField = new TextArea();
	final TextBox formulaField = new TextBox();
	final Label errorLabel = new Label();
	final HTML outputLabel = new HTML();
	final CheckBox useUnicode = new CheckBox("Use UNICODE symbols");

	public void onModuleLoad() {
		tboxField.setText("D subsumed-by exists R. C;\nC = D;");
		formulaField.setText("exists R. D and forall R. (not C)");
		
		tboxField.addStyleName("tbox");
		formulaField.addStyleName("formula");
		goButton.addStyleName("button");
		astButton.addStyleName("button");

		// Use RootPanel.get() to get the entire body element
		RootPanel.get("tboxFieldContainer").add(tboxField);
		RootPanel.get("nameFieldContainer").add(formulaField);
		RootPanel.get("sendButtonContainer").add(goButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);
		RootPanel.get("output").add(outputLabel);
		RootPanel.get("astButtonContainer").add(astButton);
		RootPanel.get("optionsContainer").add(useUnicode);

		useUnicode.setValue(tableauOptions.isUseUnicodeSymbols());
		
		formulaField.setFocus(true);
		formulaField.selectAll();
		
		useUnicode.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				tableauOptions.setUseUnicodeSymbols(event.getValue());
			}
		});

		goButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				computeTableau();
			}
		});
		
		formulaField.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					computeTableau();
			}
		});
		
		astButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				computeSyntaxTree();
			}
		});
	}
	
	private void lockUi(boolean lock) {
		goButton.setEnabled(!lock);
		astButton.setEnabled(!lock);
		formulaField.setEnabled(!lock);
		useUnicode.setEnabled(!lock);
	}

	private void computeTableau() {
		errorLabel.setText("");
		lockUi(true);
		tableauService.solve(
			tboxField.getText() + formulaField.getText(),
			tableauOptions,
			new AsyncCallback<DLTableauBean>() {
				public void onFailure(Throwable e) {
					Window.alert("Remote Procedure Call - Failure:\n\n" + e.getMessage());
					lockUi(false);
				}
				
				public void onSuccess(DLTableauBean result) {
					outputLabel.setHTML(result.toHTML());
					lockUi(false);
				}
			}
		);
	}
	
	private void computeSyntaxTree() {
		lockUi(true);
		tableauService.syntaxTree(
			formulaField.getText(),
			tableauOptions,
			new AsyncCallback<String>() {
				public void onFailure(Throwable e) {
					Window.alert("Remote Procedure Call - Failure:\n\n" + e.getMessage());
					lockUi(false);
				}
				
				public void onSuccess(String result) {
					outputLabel.setHTML("<pre>" + result + "</pre>");
					lockUi(false);
				}
			}
		);
	}
}
