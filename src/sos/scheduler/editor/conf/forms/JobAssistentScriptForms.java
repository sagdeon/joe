package sos.scheduler.editor.conf.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;
import sos.scheduler.editor.app.Editor;
import sos.scheduler.editor.app.MainWindow;
import sos.scheduler.editor.app.Messages;
import sos.scheduler.editor.app.Options;
import sos.scheduler.editor.app.ResourceManager;
import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.conf.ISchedulerUpdate;
import sos.scheduler.editor.conf.SchedulerDom;
import sos.scheduler.editor.conf.listeners.JobsListener;
import sos.scheduler.editor.conf.listeners.JobListener;
import sos.scheduler.editor.conf.listeners.ScriptListener;
import org.eclipse.swt.widgets.Combo;
import com.swtdesigner.SWTResourceManager;

public class JobAssistentScriptForms {

	private SchedulerDom      dom            = null;

	private ISchedulerUpdate  update         = null;

	private ScriptListener    scriptlistener = null;	

	private Button            butFinish      = null;

	private Button            butCancel      = null;

	private Button            butNext        = null;

	private Button            butShow        = null;		

	private Button            butBack        = null; 

	private Table             tableInclude   = null;

	private Text              txtLanguage    = null;  

	private Text              txtJavaClass   = null; 

	private Label             lblClass       = null;

	/** Wer hat ihn aufgerufen, der Job assistent oder job_chain assistent*/
	private int               assistentType  = -1; 

	private Shell             scriptShell    = null;

	private Combo             jobname        = null;

	private Element           jobBackUp      = null;  

	private ScriptJobMainForm           jobForm        = null;

	/** Hilsvariable f�r das Schliessen des Dialogs. 
	 * Das wird gebraucht wenn das Dialog �ber den "X"-Botten (oben rechts vom Dialog) geschlossen wird .*/
	private boolean               closeDialog   = false;  


	public JobAssistentScriptForms(SchedulerDom dom_, ISchedulerUpdate update_, Element job_, int assistentType_) {
		dom = dom_;
		update = update_;
		assistentType = assistentType_;
		scriptlistener = new ScriptListener(dom, job_, Editor.SCRIPT, update);			
	}


	public void showScriptForm() {

		scriptShell = new Shell(MainWindow.getSShell(), SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL | SWT.BORDER);
		scriptShell.addShellListener(new ShellAdapter() {
			public void shellClosed(final ShellEvent e) {
				if(!closeDialog)
					close();
				e.doit = scriptShell.isDisposed();
			}
		});
		scriptShell.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/editor.png"));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginTop = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		gridLayout.marginBottom = 5;
		gridLayout.numColumns = 3;
		scriptShell.setLayout(gridLayout);
		scriptShell.setSize(521, 322);
		String step = "  ";
		if (Utils.getAttributeValue("order", scriptlistener.getParent()).equalsIgnoreCase("yes"))
			step = step + " [Step 5 of 9]";
		else 
			step = step + " [Step 5 of 8]";
		scriptShell.setText("Script" + step); //TODO lang "Script"

		{
			final Group jobGroup = new Group(scriptShell, SWT.NONE);
			jobGroup.setText( "Job: " + Utils.getAttributeValue("name", scriptlistener.getParent())); //TODO lang "Job: " 
			final GridData gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 3, 1);
			gridData.widthHint = 490;
			gridData.heightHint = 217;
			jobGroup.setLayoutData(gridData);
			final GridLayout gridLayout_1 = new GridLayout();
			gridLayout_1.verticalSpacing = 10;
			gridLayout_1.horizontalSpacing = 10;
			gridLayout_1.marginWidth = 10;
			gridLayout_1.marginTop = 10;
			gridLayout_1.marginRight = 10;
			gridLayout_1.marginLeft = 10;
			gridLayout_1.marginHeight = 10;
			gridLayout_1.marginBottom = 10;
			gridLayout_1.numColumns = 2;
			jobGroup.setLayout(gridLayout_1);


			{
				final Label lblLanguage = new Label(jobGroup, SWT.NONE);
				lblLanguage.setText("Language"); //TODO lang "Language"
			}
			txtLanguage = new Text(jobGroup, SWT.BORDER);
			txtLanguage.addFocusListener(new FocusAdapter() {
				public void focusLost(final FocusEvent e) {
					if(txtLanguage.getEnabled()) {
						if(txtLanguage.getText() != null && txtLanguage.getText().length() > 0) {
							if(!(txtLanguage.getText().equalsIgnoreCase("") || 
									txtLanguage.getText().equalsIgnoreCase("java") ||
									txtLanguage.getText().equalsIgnoreCase("javascript") ||
									txtLanguage.getText().equalsIgnoreCase("perlScript") ||
									txtLanguage.getText().equalsIgnoreCase("VBScript") ||
									txtLanguage.getText().equalsIgnoreCase("shell"))) {
								MainWindow.message(scriptShell, sos.scheduler.editor.app.Messages.getString("assistent.script.unknown_language"), SWT.ICON_WARNING | SWT.OK );
								txtLanguage.setFocus();
								return;
							}

							scriptlistener.setLanguage( scriptlistener.languageAsInt(txtLanguage.getText()) );

						}
					}
				}
			});

			txtLanguage.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
			txtLanguage.setFont(SWTResourceManager.getFont("", 8, SWT.BOLD));
			txtLanguage.setEditable(false);
			txtLanguage.setFocus();

			String lan = scriptlistener.getLanguage(scriptlistener.getLanguage()); 
			if(lan != null && lan.trim().length() > 0 && scriptlistener.getParent().getChild("description") != null ) {
				txtLanguage.setText(lan);
			} else {
				//txtlanguage ist immer editierbar, wenn eine Sprache angegeben ist bzw. Wizzard ohne Jobbeschreibung gestartet wurde
				txtLanguage.setEditable(true);
				txtLanguage.setText(lan);
			}
			{
				lblClass = new Label(jobGroup, SWT.NONE);
				lblClass.setText("Java Class"); //TODO lang "Java Class"
				if (scriptlistener.getComClass() != null && scriptlistener.getComClass().length() > 0) {
					lblClass.setText("Com Class"); //TODO lang "Com Class"
				}
			}
			txtJavaClass = new Text(jobGroup, SWT.BORDER);
			txtJavaClass.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent e) {
					if(txtJavaClass.getEnabled()) {
						if(txtJavaClass.getText() != null && txtJavaClass.getText().trim().length() > 0)
							if(lblClass.getText().equals("Java Class")) {				
								scriptlistener.setJavaClass(txtJavaClass.getText());			
							} else {				
								//scriptlistener.setComClass(txtJavaClass.getText());
							}
					}
				}

			});
			txtJavaClass.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
			txtJavaClass.setFont(SWTResourceManager.getFont("", 8, SWT.BOLD));
			txtJavaClass.setEditable(false);

			if(scriptlistener.getJavaClass() != null && scriptlistener.getJavaClass().trim().length() > 0) {
				txtJavaClass.setText(scriptlistener.getJavaClass());
			}

			if(txtJavaClass.getText()!= null && txtJavaClass.getText().length() == 0 
					&& scriptlistener.getParent().getChild("description") == null && txtLanguage.getText().equals("java")) {
				//ist immer editierbar, wenn eine Sprache angegeben ist bzw. Wizzard ohne Jobbeschreibung gestartet wurde
				txtJavaClass.setEditable(true);
			}
			{				
				final Label lblRessources = new Label(jobGroup, SWT.NONE);
				if(lblClass != null && lblClass.getText().equals("Com Class")) {
					lblRessources.setText("Filename"); //TODO lang "Filename"
				} else {
					lblRessources.setText("Resource"); //TODO lang "Resource"
				}


			}

			final Text txtResource = new Text(jobGroup, SWT.BORDER);
			txtResource.setEditable(false);
			txtResource.setFont(SWTResourceManager.getFont("", 8, SWT.BOLD));
			txtResource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			if(lblClass != null && lblClass.getText().equals("Java Class")) {
				if(JobListener.getLibrary() != null && JobListener.getLibrary().length() > 0) {
					txtResource.setText(JobListener.getLibrary());
				} 
			} else {				
				if(scriptlistener.getFilename() != null && scriptlistener.getFilename().trim().length()>0)
					txtResource.setText(scriptlistener.getFilename() );
			}

			{
				final Label lblInclude = new Label(jobGroup, SWT.NONE);
				lblInclude.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true));
				lblInclude.setText("Include"); //TODO lang "Include"
			}
			tableInclude = new Table(jobGroup, SWT.BORDER);
			final GridData gridData_1 = new GridData(GridData.FILL, GridData.FILL, true, true);
			gridData_1.widthHint = 322;
			gridData_1.heightHint = 55;
			tableInclude.setLayoutData(gridData_1);
			tableInclude.setLinesVisible(true);
			tableInclude.setHeaderVisible(true);
			tableInclude.setEnabled(false);

			String[] iElem = scriptlistener.getIncludes();
			for(int i =0; i < iElem.length; i++) {
				String in = iElem[i];
				TableItem item = new TableItem(tableInclude, SWT.NONE);
				item.setText(in);	
			}
		}

		java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();		
		scriptShell.setBounds((screen.width - scriptShell.getBounds().width) /2, 
				(screen.height - scriptShell.getBounds().height) /2, 
				scriptShell.getBounds().width, 
				scriptShell.getBounds().height);

		scriptShell.open();

		{
			final Composite composite = new Composite(scriptShell, SWT.NONE);
			final GridLayout gridLayout_2 = new GridLayout();
			gridLayout_2.marginWidth = 0;
			gridLayout_2.horizontalSpacing = 0;
			composite.setLayout(gridLayout_2);
			{
				butCancel = new Button(composite, SWT.NONE);
				butCancel.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						close();
					}
				});
				butCancel.setText("Cancel"); //TODO lang "Cancel"
			}
		}

		{
			final Composite composite = new Composite(scriptShell, SWT.NONE);
			composite.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 2, 1));
			final GridLayout gridLayout_2 = new GridLayout();
			gridLayout_2.marginWidth = 0;
			gridLayout_2.numColumns = 5;
			composite.setLayout(gridLayout_2);

			{
				butShow = new Button(composite, SWT.NONE);
				butShow.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {											
						Utils.showClipboard(Utils.getElementAsString(scriptlistener.getParent()), scriptShell, false, null, false, null, false); 
					}
				});
				butShow.setText("Show"); //TODO lang "Show"
			}

			{
				butFinish = new Button(composite, SWT.NONE);
				butFinish.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						doFinish();											
					}
				});
				butFinish.setText("Finish"); //TODO lang "Finish"
			}

			butBack = new Button(composite, SWT.NONE);
			butBack.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(final SelectionEvent e) {
					doBack();					
				}
			});
			butBack.setText("Back"); //TODO lang "Back"
			{
				butNext = new Button(composite, SWT.NONE);
				butNext.setFont(SWTResourceManager.getFont("", 8, SWT.BOLD));
				butNext.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						Utils.startCursor(scriptShell);
						if(Utils.getAttributeValue("order", scriptlistener.getParent()).equals("yes")) {
							JobAssistentTimeoutOrderForms timeout = new JobAssistentTimeoutOrderForms(dom, update, scriptlistener.getParent(), assistentType);
							timeout.showTimeOutForm();	
							if(jobname != null) 													
								timeout.setJobname(jobname);							
							timeout.setBackUpJob(jobBackUp, jobForm);
						} else {
							JobAssistentTimeoutForms timeout = new JobAssistentTimeoutForms(dom, update, scriptlistener.getParent(), assistentType);
							timeout.showTimeOutForm();						
							timeout.setBackUpJob(jobBackUp, jobForm);
						}
						Utils.stopCursor(scriptShell);
						closeDialog = true;
						scriptShell.dispose();
					}
				});
				butNext.setText("Next"); //TODO lang "Next"
			}
			Utils.createHelpButton(composite, "assistent.script.java", scriptShell);			
		}
		setToolTipText();
		scriptShell.layout();		
	}

	public void setToolTipText() {
		butCancel.setToolTipText(Messages.getTooltip("assistent.cancel"));
		butNext.setToolTipText(Messages.getTooltip("assistent.next"));
		butShow.setToolTipText(Messages.getTooltip("assistent.show"));
		butFinish.setToolTipText(Messages.getTooltip("assistent.finish"));
		butBack.setToolTipText(Messages.getTooltip("butBack"));
		txtJavaClass.setToolTipText(Messages.getTooltip("assistent.java_class"));
		txtLanguage.setToolTipText(Messages.getTooltip("assistent.language"));
		if(tableInclude != null ) tableInclude.setToolTipText(Messages.getTooltip("assistent.script_include"));
	}

	private void close() {
		int cont = MainWindow.message(scriptShell, sos.scheduler.editor.app.Messages.getString("assistent.cancel"), SWT.ICON_WARNING | SWT.OK |SWT.CANCEL );
		if(cont == SWT.OK) {
			if(jobBackUp != null)
				scriptlistener.getParent().setContent(jobBackUp.cloneContent());
			scriptShell.dispose();
		}
	}

	public void setJobname(Combo jobname) {
		this.jobname = jobname;
	}

	private void doBack() {
		if(scriptlistener.getParent().getChild("description") == null) {			
			//Wizzard ohne Jobbeschreibung wurde aufgerufen.
			JobAssistentExecuteForms execute = new JobAssistentExecuteForms(dom, update, scriptlistener.getParent(), assistentType);
			execute.showExecuteForm();
			if(jobname != null) 													
				execute.setJobname(jobname);

			execute.setBackUpJob(jobBackUp, jobForm);
		} else {
			JobAssistentTasksForm tasks = new JobAssistentTasksForm(dom, update,  scriptlistener.getParent(), assistentType);											
			tasks.showTasksForm();	
			if(jobname != null) 													
				tasks.setJobname(jobname);

			tasks.setBackUpJob(jobBackUp, jobForm);
		}
		closeDialog = true;
		scriptShell.dispose();
	}

	/**
	 * Der Wizzard wurde f�r ein bestehende Job gestartet. 
	 * Beim verlassen der Wizzard ohne Speichern, muss der bestehende Job ohne �nderungen wieder zur�ckgesetz werden.
	 * @param backUpJob
	 */
	public void setBackUpJob(Element backUpJob, ScriptJobMainForm jobForm_) {
		if(backUpJob != null)
			jobBackUp = (Element)backUpJob.clone();	
		jobForm = jobForm_;
	}


	private void doFinish(){

		if(jobname != null)
			jobname.setText(Utils.getAttributeValue("name",scriptlistener.getParent()));

		if(assistentType == Editor.JOB_WIZARD) {															
			jobForm.initForm();	

		} else {

			JobsListener listener = new JobsListener(dom, update);
			listener.newImportJob(scriptlistener.getParent(), assistentType);

		}

		if(Options.getPropertyBoolean("editor.job.show.wizard"))
			Utils.showClipboard(Messages.getString("assistent.finish") + "\n\n" + Utils.getElementAsString(scriptlistener.getParent()), scriptShell, false, null, false, null, true); 

		closeDialog = true;
		scriptShell.dispose();	
	}

}
