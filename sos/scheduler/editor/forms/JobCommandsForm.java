package sos.scheduler.editor.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import sos.scheduler.editor.app.DomParser;

import sos.scheduler.editor.app.IUpdateLanguage;
import sos.scheduler.editor.app.Messages;
import sos.scheduler.editor.app.IUpdate;

import sos.scheduler.editor.listeners.JobCommandsListener;
import sos.scheduler.editor.listeners.MainListener;

import org.eclipse.swt.widgets.Label;
import org.jdom.Element;

public class JobCommandsForm extends Composite implements  IUpdateLanguage {

	private JobCommandsListener listener;

	private MainListener mainListener;

	private Group commandsGroup = null;

	private Table table = null;

	private Button bNewCommands = null;

	private Button bRemoveCommand = null;

	private Label label = null;

	public JobCommandsForm(Composite parent, int style, DomParser dom, Element job, IUpdate update,
			MainListener mainListener) {
		super(parent, style);
		this.mainListener = mainListener;
		listener = new JobCommandsListener(dom,job,update);
		initialize();
		setToolTipText();		
		listener.fillTable(table);
	}

	private void initialize() {
		this.setLayout(new FillLayout());
		createGroup();
		setSize(new org.eclipse.swt.graphics.Point(656, 400));
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup() {
		GridData gridData4 = new org.eclipse.swt.layout.GridData();
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		commandsGroup = new Group(this, SWT.NONE);
		commandsGroup.setText("Commands");
		commandsGroup.setLayout(gridLayout);
		createTable();
		bNewCommands = new Button(commandsGroup, SWT.NONE);
		bNewCommands.setText("&New Commands");
		bNewCommands.setLayoutData(gridData);
		getShell().setDefaultButton(bNewCommands);
		
		label = new Label(commandsGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setText("Label");
		label.setLayoutData(gridData4);
		bNewCommands
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						listener.newCommands(table);
						bRemoveCommand.setEnabled(true);
					}
				});
		bRemoveCommand = new Button(commandsGroup, SWT.NONE);
		bRemoveCommand.setText("Remove Commands");
		bRemoveCommand.setEnabled(false);
		bRemoveCommand.setLayoutData(gridData1);
		bRemoveCommand
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						bRemoveCommand.setEnabled(listener.deleteCommands(table));
					}
				});
	}

	/**
	 * This method initializes table
	 * 
	 */
	private void createTable() {
		GridData gridData2 = new org.eclipse.swt.layout.GridData(GridData.FILL, GridData.FILL, true, true, 1, 3);
		gridData2.widthHint = 204;
		table = new Table(commandsGroup, SWT.BORDER | SWT.FULL_SELECTION );
		table.setHeaderVisible(true);
		table.setLayoutData(gridData2);
		table.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(240);
		tableColumn.setText("Exitcode");
		table
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
							bRemoveCommand.setEnabled(true);
					}
				});
		
	}
	
	public void setToolTipText(){
		bNewCommands.setToolTipText(Messages.getTooltip("jobs.btn_add_new"));
		bRemoveCommand.setToolTipText(Messages.getTooltip("jobs.btn_remove"));
		table.setToolTipText(Messages.getTooltip("jobs.table"));

	 
  }	

} // @jve:decl-index=0:visual-constraint="10,10"
