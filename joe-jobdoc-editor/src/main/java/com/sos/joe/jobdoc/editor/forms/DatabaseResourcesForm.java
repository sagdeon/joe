package com.sos.joe.jobdoc.editor.forms;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_DBResources_Apply;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_DBResources_New;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_DBResources_Notes;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_DBResources_Remove;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_Cbo_DBResources_Type;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_G_DBResources_Resources;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_L_DBResources_Type;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_L_Name;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_M_ApplyChanges;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_TCl_DBResources_Name;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_TCl_DBResources_Type;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_T_DBResources_Name;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_Tbl_DBResources_Resources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

import com.sos.dialog.classes.SOSGroup;
import com.sos.dialog.classes.SOSLabel;
import com.sos.joe.globals.messages.ErrorLog;
import com.sos.joe.jobdoc.editor.listeners.DatabaseResourceListener;
import com.sos.joe.xml.Utils;
import com.sos.joe.xml.jobdoc.DocumentationDom;
 
public class DatabaseResourcesForm extends JobDocBaseForm<DatabaseResourceListener> {
	private Group								group1		= null;
	@SuppressWarnings("unused") private Label	label1		= null;
	private Text								tName		= null;
	private Label								label2		= null;
	private Combo								cType		= null;
	private Button								bNotes		= null;
	private Table								tResources	= null;
	private Button								bApply	= null;
	private Button								bNewRes		= null;
	private Button								bRemoveRes	= null;
	private Label								label4		= null;
	private Label								label5		= null;
	private NoteForm							fNote		= null;

	public DatabaseResourcesForm(Composite parent, int style, DocumentationDom dom, Element parentElement) {
		super(parent, style);
		this.dom = dom;
		listener = new DatabaseResourceListener(dom, parentElement);
		initialize();
		//listener.fillDatabases(tDatabases);
	}

	private void initialize() {
		createGroup();
//		setSize(new Point(636, 477));
//		setLayout(new FillLayout());
		//sashForm.setWeights(new int[] { 65, 35 });
		//Options.loadSash("databases", sashForm);
		cType.setItems(listener.getTypes());
		// bRemove.setEnabled(false);
		setDatabaseStatus(true);
	}

	/**
	 * This method initializes group
	 */
	private void createGroup() {
		//group = new SOSGroup(this, SWT.NONE);
		//group.setText("Databases"); // Generated
		//group.setLayout(new FillLayout()); // Generated
		createSashForm();
	}

	/**
	 * This method initializes group1
	 */
	private void createGroup1() {
		GridData gridData14 = new GridData();
		gridData14.horizontalAlignment = GridData.FILL; // Generated
		gridData14.verticalAlignment = GridData.BEGINNING; // Generated
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.FILL; // Generated
		gridData13.verticalAlignment = GridData.CENTER; // Generated
		GridData gridData12 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		gridData12.widthHint = 90;
		GridData gridData10 = new GridData(GridData.FILL, GridData.FILL, true, true, 4, 4);
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.FILL; // Generated
		gridData9.verticalAlignment = GridData.CENTER; // Generated
		GridData gridData5 = new GridData(GridData.FILL, GridData.CENTER, false, false, 5, 1);
		GridData gridData11 = new GridData(GridData.FILL, GridData.FILL, true, true);
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL; // Generated
		gridData3.grabExcessHorizontalSpace = true; // Generated
		gridData3.verticalAlignment = GridData.CENTER; // Generated
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 5; // Generated
		group1 = JOE_G_DBResources_Resources.Control(new SOSGroup(this, SWT.NONE));
		group1.setLayout(gridLayout1); // Generated
		group1.setLayoutData(gridData11); // Generated
		label1 = JOE_L_Name.Control(new SOSLabel(group1, SWT.NONE));
		tName = JOE_T_DBResources_Name.Control(new Text(group1, SWT.BORDER));
		tName.setLayoutData(gridData3); // Generated
		tName.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			@Override public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				Utils.setBackground(tName, true);
				setApplyResStatus();
			}
		});
		createCType();
		bApply = JOE_B_DBResources_Apply.Control(new Button(group1, SWT.NONE));
		bApply.setLayoutData(gridData12); // Generated
		bApply.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				applyResource();
			}
		});
		label5 = new SOSLabel(group1, SWT.SEPARATOR | SWT.HORIZONTAL);
		//        label5.setText("Label"); // Generated
		label5.setLayoutData(gridData5); // Generated
		tResources = JOE_Tbl_DBResources_Resources.Control(new Table(group1, SWT.FULL_SELECTION | SWT.BORDER));
		tResources.setHeaderVisible(true); // Generated
		tResources.setLayoutData(gridData10); // Generated
		tResources.setLinesVisible(true); // Generated
		tResources.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (tResources.getSelectionCount() > 0) {
					listener.setResource(tResources.getSelectionIndex());
					if (fNote.isUnsaved()) {
						int ok = ErrorLog.message(JOE_M_ApplyChanges.label(), //$NON-NLS-1$
								SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
						if (ok == SWT.CANCEL)
							return;
						if (ok == SWT.NO)
							return;//return false;
						else
							if (ok == SWT.YES) {
								fNote.apply();
								//return false;
							}
					}
					fNote.setEnabled(true);
					fNote.setParams(dom, listener.getResource(), "note", true, true);
					fNote.setTitle("Note");
					setResourceStatus(true);
					bRemoveRes.setEnabled(true);
				}
			}
		});
		TableColumn tableColumn = JOE_TCl_DBResources_Name.Control(new TableColumn(tResources, SWT.NONE));
		tableColumn.setWidth(250);
		TableColumn tableColumn1 = JOE_TCl_DBResources_Type.Control(new TableColumn(tResources, SWT.NONE));
		tableColumn1.setWidth(60);
		bNewRes = JOE_B_DBResources_New.Control(new Button(group1, SWT.NONE));
		bNewRes.setLayoutData(gridData13); // Generated
		bNewRes.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				listener.setNewResource();
				fNote.setParams(dom, listener.getResource(), "note", true, true);
				fNote.setTitle("Note");
				fNote.setEnabled(false);
				setResourceStatus(true);
				tResources.deselectAll();
			}
		});
		label4 = new SOSLabel(group1, SWT.SEPARATOR | SWT.HORIZONTAL);
		//        label4.setText("Label"); // Generated
		label4.setLayoutData(gridData9); // Generated
		bRemoveRes = JOE_B_DBResources_Remove.Control(new Button(group1, SWT.NONE));
		bRemoveRes.setLayoutData(gridData14); // Generated
		bRemoveRes.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (tResources.getSelectionCount() > 0) {
					listener.removeResource(tResources.getSelectionIndex());
					setResourceStatus(false);
					listener.fillResources(tResources);
				}
				bRemoveRes.setEnabled(false);
			}
		});
		bNotes = JOE_B_DBResources_Notes.Control(new Button(group1, SWT.NONE));
		bNotes.setLayoutData(new GridData());
		bNotes.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				//                String tip = Messages.getTooltip("doc.note.text.databases");
				//                DocumentationForm.openNoteDialog(dom, listener.getResource(), "note", tip, true, !listener
				//                        .isNewDatabase(),"Resource Note");
				DocumentationForm.openNoteDialog(dom, listener.getResource(), "note", null, true, !listener.isNewDatabase(), JOE_B_DBResources_Notes.label());
			}
		});
		fNote = new NoteForm(group1, SWT.NONE);
		//        fNote.setTitle("Note");
		fNote.setTitle(JOE_B_DBResources_Notes.label());
		fNote.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 5, 1));
		fNote.setEnabled(false);
	}

	/**
	 * This method initializes cType
	 */
	private void createCType() {
		GridData gridData4 = new GridData();
		gridData4.widthHint = 100; // Generated
		label2 = JOE_L_DBResources_Type.Control(new SOSLabel(group1, SWT.NONE));
		label2.setLayoutData(new GridData());
		cType = JOE_Cbo_DBResources_Type.Control(new Combo(group1, SWT.READ_ONLY));
		cType.setLayoutData(gridData4); // Generated
		cType.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			@Override public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				setApplyResStatus();
			}

			@Override public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
	}

	/**
	 * This method initializes sashForm
	 */
	private void createSashForm() {
		createComposite();
		createComposite1();
	}

	/**
	 * This method initializes composite
	 */
	private void createComposite() {
		createGroup1();
	}

	/**
	 * This method initializes composite1
	 */
	private void createComposite1() {
	}

	@Override public void apply() {
		if (bApply.isEnabled())
			applyResource();
	}

	@Override public boolean isUnsaved() {
		return false;
	}


	private void setDatabaseStatus(boolean enabled) {
		tResources.setEnabled(enabled);
		bNewRes.setEnabled(enabled);
		setResourceStatus(false);
		if (enabled) {
			listener.fillResources(tResources);
		}
	}

	private void setResourceStatus(boolean enabled) {
		tName.setEnabled(enabled);
		cType.setEnabled(enabled);
		bNotes.setEnabled(enabled);
		bRemoveRes.setEnabled(false);
		if (enabled) {
			tName.setText(listener.getName());
			cType.select(cType.indexOf(listener.getType()));
			tName.setFocus();
		}
		bApply.setEnabled(false);
	}

	private void setApplyResStatus() {
		bApply.setEnabled(tName.getText().length() > 0);
		getShell().setDefaultButton(bApply);
	}

	private void applyResource() {
		listener.applyResource(tName.getText(), cType.getText());
		listener.fillResources(tResources);
		bRemoveRes.setEnabled(tResources.getSelectionCount() > 0);
		bApply.setEnabled(false);
		setResourceStatus(false);
		fNote.setEnabled(true);
		fNote.setParams(dom, listener.getResource(), "note", true, true);
		fNote.setTitle("Note");
	}
	/* private void applyDatabase() {
	 }*/

	@Override
	public void openBlank() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void applySetting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean applyChanges() {
		// TODO Auto-generated method stub
		return false;
	}
} // @jve:decl-index=0:visual-constraint="10,10"
