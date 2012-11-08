package net.sf.dltableau.client;

import java.util.ArrayList;
import java.util.List;

import net.sf.dltableau.shared.DLTableauBean;
import net.sf.dltableau.shared.DLTableauOptions;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class DLTableau implements EntryPoint {
	private final DLTableauServiceAsync tableauService = GWT.create(DLTableauService.class);
	private final DLTableauOptions tableauOptions = new DLTableauOptions();
	
	final TextBox tboxEdit = new TextBox();
	final Button tboxAddButton = new Button("Add");
	final ListBox tboxField = new ListBox(true);
	final Button tboxEditButton = new Button("Edit");
	final Button tboxRemoveButton = new Button("Remove");
	
	final TextBox formulaField = new TextBox();
	final Button goButton = new Button("Go");
	final Button astButton = new Button("AST");
	
	final CheckBox useUnicode = new CheckBox("Use UNICODE symbols");
	final CheckBox interactiveMode = new CheckBox("Interactive mode");
	final Label errorLabel = new Label();
	final HTML outputLabel = new HTML();
	
	final HasEnabled lockableUiItems[] = new HasEnabled[]{
		tboxEdit, tboxAddButton,
		tboxField, tboxEditButton, tboxRemoveButton,
		formulaField, goButton, astButton,
		useUnicode, interactiveMode
	};
	
	private List<String> lastExpansionSequence = new ArrayList<String>();

	public void onModuleLoad() {
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				computeTableauIncr(event.getValue());
			}
		});
		
		addTBOXDef("D subsumed-by exists R. C");
		addTBOXDef("C = D");
		setConceptString("exists R. D and forall R. (not C)");
				
		tboxField.addStyleName("tbox");
		tboxField.addStyleName("formula");
		tboxEdit.addStyleName("formula");
		formulaField.addStyleName("formula");

		tboxAddButton.addStyleName("button");
		tboxEditButton.addStyleName("button");
		tboxRemoveButton.addStyleName("button");
		
		goButton.addStyleName("button");
		astButton.addStyleName("button");

		// Use RootPanel.get() to get the entire body element
		RootPanel.get("tboxEditorContainer").add(tboxEdit);
		RootPanel.get("tboxAddButtonContainer").add(tboxAddButton);
		RootPanel.get("tboxFieldContainer").add(tboxField);
		RootPanel.get("tboxEditButtonContainer").add(tboxEditButton);
		RootPanel.get("tboxRemoveButtonContainer").add(tboxRemoveButton);
		
		RootPanel.get("conceptFieldContainer").add(formulaField);
		RootPanel.get("sendButtonContainer").add(goButton);
		RootPanel.get("astButtonContainer").add(astButton);
		
		RootPanel.get("optionsContainer").add(useUnicode);
		RootPanel.get("optionsContainer").add(interactiveMode);
		
		RootPanel.get("errorLabelContainer").add(errorLabel);
		RootPanel.get("output").add(outputLabel);

		useUnicode.setValue(tableauOptions.isUseUnicodeSymbols());
		
		formulaField.setFocus(true);
		formulaField.selectAll();
		
		tboxEdit.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					addTBOXDef(tboxEdit.getText());
					tboxEdit.setText("");
				}
			}
		});
		
		tboxAddButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addTBOXDef(tboxEdit.getText());
				tboxEdit.setText("");
			}
		});
		
		tboxField.addDoubleClickHandler(new DoubleClickHandler() {
			public void onDoubleClick(DoubleClickEvent event) {
				int i = tboxField.getSelectedIndex();
				if(i >= 0) {
					tboxEdit.setText(tboxField.getItemText(i));
					tboxField.removeItem(i);
				}
			}
		});
		
		tboxField.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE ||
						event.getNativeKeyCode() == KeyCodes.KEY_DELETE) {
					int i = tboxField.getSelectedIndex();
					if(i >= 0) {
						tboxField.removeItem(i);
					}
				}
			}
		});
		
		tboxEditButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int i = tboxField.getSelectedIndex();
				if(i >= 0) {
					tboxEdit.setText(tboxField.getItemText(i));
					tboxField.removeItem(i);
				}
			}
		});
		
		tboxRemoveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int i = tboxField.getSelectedIndex();
				if(i >= 0) {
					tboxField.removeItem(i);
				}
			}
		});
		
		formulaField.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					computeTableau();
				}
			}
		});

		goButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				computeTableau();
			}
		});
		
		astButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				computeSyntaxTree();
			}
		});
		
		useUnicode.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				tableauOptions.setUseUnicodeSymbols(event.getValue());
			}
		});
	}
	
	private void addTBOXDef(String s) {
		tboxField.addItem(s.trim());
	}
	
	private void setConceptString(String s) {
		formulaField.setText(s.trim());
	}
	
	private void lockUi(boolean lock) {
		for(HasEnabled item : lockableUiItems) item.setEnabled(!lock);
	}
	
	private List<String> getTBOXDefsStringList() {
		List<String> tboxDefs = new ArrayList<String>();
		for(int i = 0; i < tboxField.getItemCount(); i++) tboxDefs.add(tboxField.getItemText(i));
		return tboxDefs;
	}
	
	private String getConceptString() {
		return formulaField.getText();
	}
	
	private void computeTableau() {
		if(interactiveMode.getValue() == true) {
			lastExpansionSequence = new ArrayList<String>();
			computeTableauIncr(null);
		} else {
			computeTableauOneShot();
		}
	}
	
	private void computeTableauIncr(String exp1) {
		errorLabel.setText("");
		lockUi(true);
		List<String> exp = new ArrayList<String>(lastExpansionSequence);
		if(exp1 != null) exp.add(exp1);
		tableauService.incrSolve(
			getTBOXDefsStringList(), getConceptString(), exp, tableauOptions,
			new AsyncCallback<DLTableauBean>() {
				public void onFailure(Throwable e) {
					Window.alert("Remote Procedure Call - Failure:\n\n" + e.getMessage());
					lockUi(false);
				}
				
				public void onSuccess(DLTableauBean result) {
					lastExpansionSequence = result.expansionSequence;
					outputLabel.setHTML(result.toHTML());
					lockUi(false);
				}
			}
		);
	}

	private void computeTableauOneShot() {
		errorLabel.setText("");
		lockUi(true);
		tableauService.solve(
			getTBOXDefsStringList(), getConceptString(), tableauOptions,
			new AsyncCallback<DLTableauBean>() {
				public void onFailure(Throwable e) {
					Window.alert("Remote Procedure Call - Failure:\n\n" + e.getMessage());
					lockUi(false);
				}
				
				public void onSuccess(DLTableauBean result) {
					lastExpansionSequence = result.expansionSequence;
					outputLabel.setHTML(result.toHTML());
					lockUi(false);
				}
			}
		);
	}
	
	private void computeSyntaxTree() {
		lockUi(true);
		tableauService.syntaxTree(
			getConceptString(),
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
