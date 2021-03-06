package com.sos.joe.wizard.forms;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.sos.joe.xml.Utils;
import sos.scheduler.editor.conf.container.JobDocumentation;
import sos.scheduler.editor.conf.listeners.JobsListener;
import sos.scheduler.editor.conf.listeners.ParameterListener;
import sos.scheduler.editor.conf.listeners.SortTreeListener;

import com.sos.dialog.swtdesigner.SWTResourceManager;
import com.sos.joe.globals.JOEConstants;
import com.sos.joe.globals.interfaces.ISchedulerUpdate;
import com.sos.joe.globals.messages.ErrorLog;
import com.sos.joe.globals.messages.SOSJOEMessageCodes;
import com.sos.joe.globals.misc.ResourceManager;
import com.sos.joe.globals.options.Options;
import com.sos.joe.objects.job.JobListener;
import com.sos.joe.objects.job.forms.JobDocumentationForm;
import com.sos.joe.objects.job.forms.ScriptJobMainForm;
import com.sos.joe.xml.jobscheduler.SchedulerDom;


/**
 * Job Wizzard.
 * 
 * Liste der Standalone Jobs bzw. Auftragsgesteuerte Jobs.
 *
 * Es werden alle Standalone Jobs oder Auftragsgesteuerte Jobs zur Auswahl gestellt.
 *
 * Die Kriterien stehen in der Job Dokumentation.
 * Das bedeutet alle Job Dokumentationen aus der Verzeichnis <SCHEDULER_DATA>/jobs/*.xml werden parsiert.
 *
 * Folgen Funktionen k�nnen hier ausgef�hrt werden:
 *
 *
 * show:
 * 		zeigt den Job mit den Informationen aus der ausgew�hlten Jobdokumentation aus der Liste im seperaten Fenster als XML.
 *
 * next:
 * 		geht in das n�chste Wizzard Formular Parameter.
 * 		Hier werden alle Parameter der ausgew�hlten Jobdokumentation aus der Liste �bergeben.
 *
 * finish:
 * 		Generiert einen Job. �bernimmt die Einstellungen der ausgew�hlten Job aus der Liste.
 *      Alle Defaulteinstellungen des Jobs werden hier mit �bernommen.
 *
 * Help Button:
 * 		�ffnet einen Dialog mit Hilfetext
 *
 * Description:
 * 		�ffnet einen neuen IE mit der ausgew�hlten JobDocumentation
 *
 * Back:
 * 		geht einen Formular zur�ck
 *
 * Cancel:
 * 		beendet den Wizzard
 *
 * Der Aufbau eines Jobs kann aus der Dokumentation <SCHEDULER_>\config\html\doc\de\xml.xml entnommen werden.
 *
 * @author mueruevet.oeksuez@sos-berlin.com
 *
 * @version $Id: JobAssistentImportJobsForm.java 25898 2014-06-20 14:36:54Z kb $
 */

public class JobAssistentImportJobsForm { 
	private Shell													shell				= null;
	private Display													display				= null;
	private static final String										EMPTY_STRING		= "";
	private Text													searchField			= null;
	public Timer													inputTimer;
	private Text													txtTitle			= null;
	private Text													txtPath				= null;
	private Tree													tree				= null;
	private String													xmlPaths			= null;
	private Text													txtJobname			= null;
	private JobsListener											listener			= null;
	private JobListener												joblistener			= null;
	private SchedulerDom											dom					= null;
	private ISchedulerUpdate										update				= null;
	/** Parameter: Tabelle aus der JobForm*/
	private Table													tParameter			= null;
	private Button													butImport			= null;
	private Button													butParameters		= null;
	private Button								 					butdescription		= null;
	private Button													butCancel			= null;
	private Button													butShow				= null;
	private Button													butBack				= null;
	private String													jobType				= "";
	/** Wer hat ihn aufgerufen, der Job assistent oder job_chain assistent*/
	private int														assistentType		= -1;
	private Combo													jobname				= null;
	private Element													jobBackUp			= null;
	private ScriptJobMainForm										jobForm				= null;
	private sos.scheduler.editor.conf.listeners.ParameterListener	paramListener		= null;
	private Text													refreshDetailsText	= null;
	/** Hilfsvariable f�r das Schliessen des Dialogs.
	 * Das wird gebraucht wenn das Dialog �ber den "X"-Botten (oben rechts vom Dialog) geschlossen wird .*/
	private boolean													closeDialog			= false;
	private boolean													flagBackUpJob		= true;
	private final JobDocumentationForm								jobDocForm			= null;

	/**
	 * Konstruktor
	 *
	 * @param dom_ SchedulerDom
	 * @param update_ ISchedulerUpdate
	 * @param assistentType_ int
	 */
	public JobAssistentImportJobsForm(final SchedulerDom dom_, final ISchedulerUpdate update_, final int assistentType_) {
		dom = dom_;
		inputTimer = new Timer();
		update = update_;
		assistentType = assistentType_;
		listener = new JobsListener(dom, update);
	}

	/**
	 * Konstruktor
	 *
	 * @param listener_
	 * @param assistentType_
	 */
	public JobAssistentImportJobsForm(final JobListener listener_, final int assistentType_) {
		jobBackUp = (Element) listener_.getJob().clone();
		joblistener = listener_;
		dom = joblistener.get_dom();
		inputTimer = new Timer();
		update = joblistener.get_main();
		listener = new JobsListener(dom, update);
		assistentType = assistentType_;
		paramListener = new ParameterListener(dom, joblistener.getJob(), update, assistentType);
	}

	/**
	 * Konstruktor
	 *
	 * @param listener_
	 * @param tParameter_
	 * @param assistentType_
	 */
	public JobAssistentImportJobsForm(final JobListener listener_, final Table tParameter_, final int assistentType_) {
		jobBackUp = (Element) listener_.getJob().clone();
		joblistener = listener_;
		dom = joblistener.get_dom();
		inputTimer = new Timer();
		update = joblistener.get_main();
		listener = new JobsListener(dom, update);
		tParameter = tParameter_;
		assistentType = assistentType_;
		paramListener = new ParameterListener(dom, joblistener.getJob(), update, assistentType);
	}
	class JobListComparator implements Comparator<Map<String, String>> {
		private final String	key;

		public JobListComparator(final String key) {
			this.key = key;
		}

		@Override public int compare(final Map<String, String> first, final Map<String, String> second) {
			String firstValue = first.get(key);
			String secondValue = second.get(key);
			if (firstValue == null) {
				firstValue = "";
			}
			if (secondValue == null) {
				secondValue = "";
			}
			return firstValue.compareTo(secondValue);
		}
	}
	public class InputTask extends TimerTask {
		@Override public void run() {
			if (display == null) {
				display = Display.getDefault();
			}
			display.syncExec(new Runnable() {
				@Override public void run() {
					if (!searchField.equals(EMPTY_STRING)) {
						try {
							createTreeItems();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						inputTimer.cancel();
					}
				};
			});
		}
	}

	private void resetInputTimer() {
		inputTimer.cancel();
		inputTimer = new Timer();
		inputTimer.schedule(new InputTask(), 1 * 1000, 1 * 1000);
	}

	/**
	 * Jobname setzen
	 * @param jobname
	 */
	public void setJobname(final Combo jobname) {
		this.jobname = jobname;
	}

	private String getJobsDirectoryName() {
		String s = Options.getSchedulerData();
		// TODO jobs customizable
		s = s.endsWith("/") || s.endsWith("\\") ? s.concat("jobs") : s.concat("/jobs");
		return s;
	}

	/**
	 * Alle vorhandenen Job Dokumentation aus der <SCHEDULER_DATA>/jobs/*.xml
	 * parsieren und in die Tabelle Schreiben. Folgende Informationen werden bei der Parsierung ausgelesen:
	 * Name, Title, Filename, Job-Meta-Element
	 *
	 * @return ArrayList - Liste aller Jobs. EIn Eintrag der Liste entspricht einen HashMap. Der HasMap hat die
	 * Informationen wie Name, Title, Filename und Job Element
	 */
	public ArrayList parseDocuments() {
		String xmlFilePath = "";
		String xmlFileName = "";
		xmlPaths = getJobsDirectoryName();
		ArrayList listOfDoc = null;
		try {
			listOfDoc = new ArrayList();
			if (!new File(xmlPaths).exists()) {
				//				 ErrorLog.message(shell, "Missing Directory for Job Description: " + xmlPaths, SWT.ICON_WARNING | SWT.OK);
				ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_MissingDirectory.params(xmlPaths), SWT.ICON_WARNING | SWT.OK);
				return listOfDoc;
			}
			java.util.Vector filelist = sos.util.SOSFile.getFilelist(xmlPaths, "^.*\\.xml$", java.util.regex.Pattern.CASE_INSENSITIVE, true);
			Iterator fileIterator = filelist.iterator();
			while (fileIterator.hasNext()) {
				try {
					xmlFilePath = fileIterator.next().toString();
					xmlFileName = new File(xmlFilePath).getName();
					SAXBuilder builder = new SAXBuilder();
					Document doc = builder.build(new File(xmlFilePath));
					Element root = doc.getRootElement();
					List listMainElements = root.getChildren();
					HashMap h = null;
					for (int i = 0; i < listMainElements.size(); i++) {
						Element elMain = (Element) listMainElements.get(i);
						if (elMain.getName().equalsIgnoreCase("job")) {
							h = new HashMap();
							h.put("name", elMain.getAttributeValue("name"));
							h.put("title", elMain.getAttributeValue("title"));
							h.put("filepath", xmlFilePath);
							h.put("filename", xmlFileName);
							h.put("job", elMain);
							listOfDoc.add(h);
						}
					}
				}
				catch (Exception e) {
					// Damit die n�chste Datei verarbeitet wird, hier keine weitere Behandlung. Kaputte Dateien sind uns egal.
				}
			}
		}
		catch (Exception ex) {
			try {
				// new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), ex);
				new ErrorLog(SOSJOEMessageCodes.JOE_E_0002.params(sos.util.SOSClassUtil.getMethodName()), ex);
			}
			catch (Exception ee) {
				// tu nichts
			}
			ex.printStackTrace();
		}
		return listOfDoc;
	}

	public void showAllImportJobs(final String type_) {
		jobType = type_;
		showAllImportJobs();
	}

	public void showAllImportJobs(final JobListener joblistener_) {
		joblistener = joblistener_;
		jobBackUp = (Element) joblistener_.getJob().clone();
		jobType = joblistener.getOrder() ? "order" : "standalonejob";
		showAllImportJobs();
	}

	public void showAllImportJobs() {
		try {
			shell = new Shell(ErrorLog.getSShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			shell.addShellListener(new ShellAdapter() {
				@Override public void shellClosed(final ShellEvent e) {
					if (!closeDialog)
						close();
					e.doit = shell.isDisposed();
				}
			});
			shell.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/editor.png"));
			final GridLayout gridLayout = new GridLayout();
			gridLayout.marginHeight = 0;
			shell.setLayout(gridLayout);
			String step = " ";
			if (jobType.equalsIgnoreCase("order"))
				step += SOSJOEMessageCodes.JOE_M_JobAssistent_Step2of9.label();
			else
				step += SOSJOEMessageCodes.JOE_M_JobAssistent_Step2of8.label();
			// shell.setText("Import Jobs" + step);
			shell.setText(SOSJOEMessageCodes.JOE_M_JobAssistent_ImportJobs.params(step));
			final Group jobGroup = SOSJOEMessageCodes.JOE_G_JobAssistent_JobGroup.Control(new Group(shell, SWT.NONE));
			final GridLayout gridLayout_3 = new GridLayout();
			gridLayout_3.marginWidth = 10;
			gridLayout_3.marginTop = 5;
			gridLayout_3.marginBottom = 10;
			gridLayout_3.marginHeight = 10;
			gridLayout_3.marginLeft = 10;
			gridLayout_3.numColumns = 3;
			jobGroup.setLayout(gridLayout_3);
			final GridData gridData_6 = new GridData(GridData.FILL, GridData.CENTER, true, false);
			gridData_6.minimumWidth = 400;
			jobGroup.setLayoutData(gridData_6);
			Composite composite;
			final Label jobnameLabel_1 = SOSJOEMessageCodes.JOE_L_JobAssistent_JobName.Control(new Label(jobGroup, SWT.NONE));
			jobnameLabel_1.setLayoutData(new GridData());
			{
				txtJobname = SOSJOEMessageCodes.JOE_T_JobAssistent_JobName.Control(new Text(jobGroup, SWT.BORDER));
				txtJobname.setFocus();
				final GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
				txtJobname.setLayoutData(gridData);
				if (listener != null)
					txtJobname.setBackground(Options.getRequiredColor());
				if (joblistener != null) {
					if (joblistener.getJob().getName().equals("start_job")) {
						txtJobname.setText(Utils.getAttributeValue("job", joblistener.getJob()));
					}
					else
						if (joblistener.getJob().getName().equals("order")) {
							txtJobname.setText(" ");
						}
						else
							txtJobname.setText(joblistener.getJobName());
				}
				else {
					txtJobname.setText("");
				}
			}
			new Label(jobGroup, SWT.NONE);
			final Label titelLabel = SOSJOEMessageCodes.JOE_L_JobAssistent_Title.Control(new Label(jobGroup, SWT.NONE));
			titelLabel.setLayoutData(new GridData());
			txtTitle = SOSJOEMessageCodes.JOE_T_JobAssistent_Title.Control(new Text(jobGroup, SWT.BORDER));
			final GridData gridData = new GridData(GridData.FILL, GridData.CENTER, false, false);
			gridData.widthHint = 420;
			txtTitle.setLayoutData(gridData);
			if (joblistener != null) {
				txtTitle.setText(joblistener.getTitle());
			}
			new Label(jobGroup, SWT.NONE);
			final Label pathLabel = SOSJOEMessageCodes.JOE_L_JobAssistent_PathLabel.Control(new Label(jobGroup, SWT.NONE));
			pathLabel.setLayoutData(new GridData());
			txtPath = SOSJOEMessageCodes.JOE_T_JobAssistent_Path.Control(new Text(jobGroup, SWT.BORDER));
			txtPath.setEditable(false);
			if (joblistener != null) {
				txtPath.setText(joblistener.getInclude());
			}
			final GridData gridData_1 = new GridData(GridData.FILL, GridData.CENTER, false, false);
			gridData_1.widthHint = 420;
			txtPath.setLayoutData(gridData_1);
			new Label(jobGroup, SWT.NONE);
			final Composite composite_3 = SOSJOEMessageCodes.JOE_Composite1.Control(new Composite(jobGroup, SWT.NONE));
			final GridData gridData_7 = new GridData(103, SWT.DEFAULT);
			composite_3.setLayoutData(gridData_7);
			final GridLayout gridLayout_4 = new GridLayout();
			gridLayout_4.marginWidth = 0;
			composite_3.setLayout(gridLayout_4);
			butCancel = SOSJOEMessageCodes.JOE_B_JobAssistent_Cancel.Control(new Button(composite_3, SWT.NONE));
			butCancel.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(final SelectionEvent e) {
					close();
				}
			});
			composite = SOSJOEMessageCodes.JOE_Composite2.Control(new Composite(jobGroup, SWT.NONE));
			final GridData gridData_8 = new GridData(GridData.END, GridData.CENTER, false, false);
			composite.setLayoutData(gridData_8);
			final GridLayout gridLayout_2 = new GridLayout();
			gridLayout_2.marginWidth = 0;
			gridLayout_2.verticalSpacing = 0;
			gridLayout_2.numColumns = 6;
			composite.setLayout(gridLayout_2);
			{
				butdescription = SOSJOEMessageCodes.JOE_B_JobAssistent_Description.Control(new Button(composite, SWT.NONE));
				butdescription.addSelectionListener(new SelectionAdapter() {
					@Override public void widgetSelected(final SelectionEvent e) {
						try {
							if (txtPath.getText() != null && txtPath.getText().length() > 0) {
								Program prog = Program.findProgram("html");
								if (prog != null)
									prog.execute(new File(txtPath.getText()).toURL().toString());
								else {
									Runtime.getRuntime().exec(Options.getBrowserExec(new File(txtPath.getText()).toURL().toString(), Options.getLanguage()));
								}
							}
							else {
								ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_NoJobDescription.label(), SWT.ICON_WARNING | SWT.OK);
							}
						}
						catch (Exception ex) {
							try {
								// new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " ;could not open description " +
								// txtJobname.getText(), ex);
								new ErrorLog(SOSJOEMessageCodes.JOE_E_0002.params(sos.util.SOSClassUtil.getMethodName()) + " "
										+ SOSJOEMessageCodes.JOE_E_0009.params(txtJobname.getText(), ex));
							}
							catch (Exception ee) {
								// tu nichts
							}
							// System.out.println("..could not open description " + txtJobname.getText() + " " + ex);
							System.out.println(SOSJOEMessageCodes.JOE_E_0009.params(txtJobname.getText(), ex));
						}
					}
				});
			}
			butShow = SOSJOEMessageCodes.JOE_B_JobAssistent_Show.Control(new Button(composite, SWT.NONE));
			butShow.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			butShow.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(final SelectionEvent e) {
					HashMap attr = getJobFromDescription();
					JobAssistentImportJobParamsForm defaultParams = new JobAssistentImportJobParamsForm();
					ArrayList listOfParams = defaultParams.parseDocuments(txtPath.getText(), "");
					attr.put("params", listOfParams);
					Element job = null;
					if (flagBackUpJob) {
						if (assistentType == JOEConstants.JOB_WIZARD) {
							// Starten des Wizzards f�r bestehenden Job. Die Einstzellungen im Jobbeschreibungen mergen mit backUpJob wenn
							// assistentype = JOEConstants.Job_Wizzard
							Element currJob = (Element) joblistener.getJob().clone();
							job = listener.createJobElement(attr, currJob);
						}
						else {
							job = listener.createJobElement(attr);
						}
					}
					else {
						job = (Element) jobBackUp.clone();
					}
					Utils.showClipboard(Utils.getElementAsString(job), shell, false, null, false, null, false);
					job.removeChildren("param");
				}
			});
			{
				butImport = SOSJOEMessageCodes.JOE_B_JobAssistent_Import.Control(new Button(composite, SWT.NONE));
				butImport.addSelectionListener(new SelectionAdapter() {
					@Override public void widgetSelected(final SelectionEvent e) {
						try {
							if (!check()) {
								return;
							}
							HashMap h = getJobFromDescription();
							if (jobname != null) {
								jobname.setText(txtJobname.getText());
							}
							JobAssistentImportJobParamsForm defaultParams = new JobAssistentImportJobParamsForm();
							// OTRS: http://www.sos-berlin.com/otrs/index.pl?Action=AgentZoom&TicketID=76
							// http://www.sos-berlin.com/jira/browse/JS-852
							// If the user is pressing "finish" then no parameters should be copied/moved to the object.
							// The Parameterlist should be empty in this case.
							ArrayList listOfParams = new ArrayList<HashMap<String, Object>>();
							// ArrayList listOfParams = defaultParams.parseDocuments(txtPath.getText(), "required");
							// h.put("params", listOfParams);
							if (assistentType == JOEConstants.JOB_WIZARD) {
								// Starten des Wizzards f�r bestehenden Job. Die Einstzellungen im Jobbeschreibungen mergen mit backUpJob
								// wenn
								// assistentype = JOEConstants.Job_Wizzard
								Element job = joblistener.getJob();
								job = job.setContent(listener.createJobElement(h, joblistener.getJob()).cloneContent());
								if (jobForm != null) {
									jobForm.initForm();
								}
								if (jobDocForm != null)
									jobDocForm.initForm();
							}
							else
								if (assistentType == JOEConstants.PARAMETER) {
									// Starten des Wizzards f�r bestehenden Job. Die Einstzellungen im Jobbeschreibungen mergen mit
									// backUpJob
									// wenn assistentype = JOEConstants.Job_Wizzard
									// joblistener.getJob().setContent(listener.createJobElement(h, joblistener.getJob()).cloneContent());
									Element job = joblistener.getJob();
									if (job.getName().equals("job")) {
										job = job.setContent(listener.createJobElement(h, joblistener.getJob()).cloneContent());
										paramListener.fillParams(tParameter);
									}
									else
										paramListener.fillParams(listOfParams, tParameter, false);
								}
								else {
									if (listener.existJobname(txtJobname.getText())) {
										ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_JobNameExists.label(), SWT.OK);
										txtJobname.setFocus();
										return;
									}
									Element job = null;
									if (flagBackUpJob) {
										job = listener.createJobElement(h);
									}
									else {
										job = joblistener.getJob();
										job = job.setContent(jobBackUp.cloneContent());
									}
									listener.newImportJob(job, assistentType);
									if (Options.getPropertyBoolean("editor.job.show.wizard"))
										Utils.showClipboard(Utils.getElementAsString(job), shell, false, null, false, null, true);
								}
							closeDialog = true;
							// Event ausl�sen
							if (refreshDetailsText != null)
								refreshDetailsText.setText("X");
							shell.dispose();
						}
						catch (Exception ex) {
							try {
								// new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), ex);
								new ErrorLog(SOSJOEMessageCodes.JOE_E_0002.params(sos.util.SOSClassUtil.getMethodName()), ex);
							}
							catch (Exception ee) {
								// tu nichts
							}
							System.err.print(ex.getMessage());
						}
					}
				});
			}
			butBack = SOSJOEMessageCodes.JOE_B_JobAssistent_Back.Control(new Button(composite, SWT.NONE));
			butBack.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(final SelectionEvent e) {
					JobAssistentTypeForms typeForms = new JobAssistentTypeForms(dom, update);
					typeForms.showTypeForms(jobType, jobBackUp, assistentType);
					closeDialog = true;
					shell.dispose();
				}
			});
			butParameters = SOSJOEMessageCodes.JOE_B_JobAssistent_Next.Control(new Button(composite, SWT.NONE));
			butParameters.setFont(SWTResourceManager.getFont("", 8, SWT.BOLD));
			butParameters.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			butParameters.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(final SelectionEvent e) {
					Utils.startCursor(shell);
					if (!check())
						return;
					HashMap attr = getJobFromDescription();
					if (assistentType == JOEConstants.JOB_WIZARD || assistentType == JOEConstants.JOB) {
						Element job = listener.createJobElement(attr, joblistener.getJob());
						JobAssistentImportJobParamsForm paramsForm = new JobAssistentImportJobParamsForm(joblistener.get_dom(), joblistener.get_main(), job,
								assistentType);
						paramsForm.setBackUpJob(jobBackUp, jobForm);
						paramsForm.setJobForm(jobForm);
						paramsForm.showAllImportJobParams(txtPath.getText());
					}
					else
						if (assistentType == JOEConstants.PARAMETER) {
							JobAssistentImportJobParamsForm paramsForm = new JobAssistentImportJobParamsForm(joblistener.get_dom(), joblistener.get_main(),
									joblistener, tParameter, assistentType);
							paramsForm.showAllImportJobParams(txtPath.getText());
						}
						else {
							if (assistentType != JOEConstants.JOB_WIZARD && listener.existJobname(txtJobname.getText())) {
								ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_JobNameExists.label(), SWT.OK);
								txtJobname.setFocus();
								return;
							}
							Element job = null;
							if (flagBackUpJob) {
								if (jobBackUp != null && assistentType != JOEConstants.JOB_WIZARD) {
									int cont = ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_DiscardChanges.label(), SWT.ICON_QUESTION
											| SWT.YES | SWT.NO | SWT.CANCEL);
									if (cont == SWT.CANCEL) {
										return;
									}
									else
										if (cont != SWT.YES) {
											job = joblistener.getJob().setContent(jobBackUp.cloneContent());
										}
								}
							}
							else {
								// der backUpJob wurde nicht ver�ndert
								job = joblistener.getJob().setContent(jobBackUp.cloneContent());
							}
							if (job == null) {
								job = listener.createJobElement(attr);
							}
							JobAssistentImportJobParamsForm paramsForm = null;
							if (assistentType == JOEConstants.JOB_WIZARD) {
								paramsForm = new JobAssistentImportJobParamsForm(dom, update, joblistener, assistentType);
							}
							else {
								paramsForm = new JobAssistentImportJobParamsForm(dom, update, job, assistentType);
							}
							paramsForm.showAllImportJobParams(txtPath.getText());
							if (jobname != null)
								paramsForm.setJobname(jobname);
							paramsForm.setBackUpJob(jobBackUp, jobForm);
						}
					closeDialog = true;
					Utils.stopCursor(shell);
					shell.dispose();
				}
			});
			Utils.createHelpButton(composite, "JOE_M_JobAssistentImportJobsForm_Help.label", shell);
			if (assistentType == JOEConstants.JOB) {
				butImport.setVisible(true);
				butParameters.setText(SOSJOEMessageCodes.JOE_M_JobAssistent_ImportParams.label());
			}
			if (assistentType == JOEConstants.JOB_WIZARD) {
				txtJobname.setEnabled(false);
				txtTitle.setEnabled(true);
				butShow.setEnabled(true);
				butBack.setEnabled(true);
			}
			else
				if (assistentType == JOEConstants.JOB) {
					txtJobname.setEnabled(false);
					txtTitle.setEnabled(false);
					butShow.setEnabled(false);
					butBack.setEnabled(false);
				}
				else
					if (assistentType == JOEConstants.JOB_CHAINS) {
						txtJobname.setEnabled(true);
						txtTitle.setEnabled(true);
						butShow.setEnabled(true);
						butBack.setEnabled(false);
					}
					else {
						txtJobname.setEnabled(true);
						txtTitle.setEnabled(true);
						butShow.setEnabled(true);
						butBack.setEnabled(true);
					}
			if (joblistener != null) {
				if (joblistener.getJob().getName().equals("start_job") || joblistener.getJob().getName().equals("process")
						|| joblistener.getJob().getName().equals("order") || joblistener.getJob().getName().equals("config")) {
					txtJobname.setEnabled(false);
				}
			}
			java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			shell.setBounds((screen.width - shell.getBounds().width) / 2, (screen.height - shell.getBounds().height) / 2, shell.getBounds().width,
					shell.getBounds().height);
			final Group jobnamenGroup = SOSJOEMessageCodes.JOE_G_JobAssistent_JobsGroup.Control(new Group(shell, SWT.NONE));
			final GridLayout gridLayout_1 = new GridLayout();
			gridLayout_1.marginTop = 5;
			gridLayout_1.marginRight = 5;
			gridLayout_1.marginLeft = 5;
			jobnamenGroup.setLayout(gridLayout_1);
			final GridData gridData_3 = new GridData(GridData.FILL, GridData.FILL, true, true);
			gridData_3.heightHint = 154;
			jobnamenGroup.setLayoutData(gridData_3);
			jobnamenGroup.getBounds().height = 100;
			searchField = new Text(jobnamenGroup, SWT.BORDER);
			searchField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			searchField.addModifyListener(new ModifyListener() {
				@Override public void modifyText(final ModifyEvent e) {
					if (searchField != null) {
						resetInputTimer();
					}
				}
			});
			tree = SOSJOEMessageCodes.JOE_JobAssistent_JobTree.Control(new Tree(jobnamenGroup, SWT.FULL_SELECTION | SWT.BORDER));
			tree.setHeaderVisible(true);
			tree.getBounds().height = 100;
			tree.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(final SelectionEvent e) {
					String strT = txtTitle.getText();
					//if (strT.trim().equalsIgnoreCase("")) {
					txtTitle.setText(tree.getSelection()[0].getText(1));
					//}
					txtPath.setText(getJobsDirectoryName() + "/" + tree.getSelection()[0].getText(2));
					txtJobname.setFocus();
					flagBackUpJob = true;
				}
			});
			final GridData gridData_2 = new GridData(GridData.FILL, GridData.FILL, true, true);
			tree.setLayoutData(gridData_2);
			TreeColumn column1 = SOSJOEMessageCodes.JOE_JobAssistent_NameTreeColumn.Control(new TreeColumn(tree, SWT.LEFT));
			column1.setWidth(165);
			column1.addSelectionListener(new SortTreeListener());
			TreeColumn column2 = SOSJOEMessageCodes.JOE_JobAssistent_TitleTreeColumn.Control(new TreeColumn(tree, SWT.LEFT));
			column2.setWidth(200);
			column2.addSelectionListener(new SortTreeListener());
			TreeColumn column3 = SOSJOEMessageCodes.JOE_JobAssistent_FilenameTreeColumn.Control(new TreeColumn(tree, SWT.LEFT));
			column3.setWidth(209);
			column3.addSelectionListener(new SortTreeListener());
			try {
				createTreeItems();
			}
			catch (Exception e) {
				try {
					// new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), e);
					new ErrorLog(SOSJOEMessageCodes.JOE_E_0002.params(sos.util.SOSClassUtil.getMethodName()), e);
				}
				catch (Exception ee) {
					// tu nichts
				}
				System.err.print(e.getMessage());
			}
			if (joblistener != null) {
				selectTree();
			}
			setToolTipText();
			shell.layout();
			shell.pack();
			shell.open();
		}
		catch (Exception e) {
			try {
				// new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), e);
				new ErrorLog(SOSJOEMessageCodes.JOE_E_0002.params(sos.util.SOSClassUtil.getMethodName()), e);
				// System.err.println("error in JobAssistentImportJobsForm.showAllImportJobs(): " + e.getMessage());
				System.err.println(SOSJOEMessageCodes.JOE_M_0010.params(sos.util.SOSClassUtil.getMethodName(), e.getMessage()));
			}
			catch (Exception ee) {
				// tu nichts
			}
		}
	}

	private void createTreeItems() throws Exception {
		try {
			tree.removeAll();
			final TreeItem newItemTreeItem_ = new TreeItem(tree, SWT.NONE);
			//          erm�glicht das Starten des Wizards ohne vorhandene Jobbeschreibung
/*            newItemTreeItem_.setText(0, SOSJOEMessageCodes.JOE_M_JobAssistent_NoJobDoc.label());
			newItemTreeItem_.setText(1, "..");
			newItemTreeItem_.setText(2, "..");
			Element j = new Element("job");
			Utils.setAttribute("order", (jobType.equals("order") ? "yes" : "no"), j);
			newItemTreeItem_.setData(j);*/
			ArrayList listOfDoc = parseDocuments();
			Collections.sort(listOfDoc, new JobListComparator("name"));
			String filename = "";
			String lastParent = "";
			TreeItem parentItemTreeItem = null;
			boolean insertJobInTree = true;
			for (int i = 0; i < listOfDoc.size(); i++) {
				HashMap h = (HashMap) listOfDoc.get(i);
				insertJobInTree = searchField.getText().toLowerCase().equals(EMPTY_STRING)
						|| h.get("filename").toString().toLowerCase().contains(searchField.getText().toLowerCase())
						|| h.get("name").toString().toLowerCase().contains(searchField.getText().toLowerCase())
						|| h.get("title").toString().toLowerCase().contains(searchField.getText().toLowerCase());
				if (jobType != null && jobType.equals("order")) {
					Element job = (Element) h.get("job");
					if (!(Utils.getAttributeValue("order", job).equals("yes") || Utils.getAttributeValue("order", job).equals("both"))) {
						insertJobInTree = false;
					}
				}
				else
					if (jobType != null && jobType.equals("standalonejob")) {
						Element job = (Element) h.get("job");
						if (!(Utils.getAttributeValue("order", job).equals("no") || Utils.getAttributeValue("order", job).equals("both"))) {
							insertJobInTree = false;
						}
					}
				if (insertJobInTree) {
					filename = h.get("filepath").toString();
					if (new File(filename).getParentFile().equals(new File(xmlPaths))) {
						final TreeItem newItemTreeItem = new TreeItem(tree, SWT.NONE);
						newItemTreeItem.setText(0, h.get("name").toString());
						newItemTreeItem.setText(1, h.get("title").toString());
						newItemTreeItem.setText(2, h.get("filename").toString());
						newItemTreeItem.setData(h.get("job"));
					}
					else {
						if (!lastParent.equalsIgnoreCase(new File(filename).getParentFile().getPath())) {
							if (!new File(lastParent).getName().equals(tree.getItems()[tree.getItems().length - 1].getText())) {
								parentItemTreeItem = new TreeItem(tree, SWT.NONE);
								parentItemTreeItem.setText(0, new File(filename).getParentFile().getName());
								parentItemTreeItem.setData(h.get("job"));
								lastParent = new File(filename).getParentFile().getPath();
							}
							else {
								parentItemTreeItem = new TreeItem(parentItemTreeItem, SWT.NONE);
								parentItemTreeItem.setText(0, new File(filename).getParentFile().getName());
								parentItemTreeItem.setData(h.get("job"));
								lastParent = new File(filename).getParentFile().getPath();
							}
						}
						final TreeItem newItemTreeItem = new TreeItem(parentItemTreeItem, SWT.NONE);
						newItemTreeItem.setText(0, h.get("name").toString());
						newItemTreeItem.setText(1, h.get("title").toString());
						newItemTreeItem.setText(2, filename);
						newItemTreeItem.setData(h.get("job"));
					}
				}
			}
		}
		catch (Exception e) {
			try {
				//				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), e);
				new ErrorLog(SOSJOEMessageCodes.JOE_E_0002.params(sos.util.SOSClassUtil.getMethodName()), e);
			}
			catch (Exception ee) {
				// tu nichts
			}
			//			System.out.println("error in JobAssistentImportJobsForm.createTreeItems(): " + e.getMessage());
			System.out.println(SOSJOEMessageCodes.JOE_M_0010.params(sos.util.SOSClassUtil.getMethodName(), e.getMessage()));
		}
	}

	public void setToolTipText() {
		// butImport.setToolTipText(Messages.getTooltip("butImport"));
		// butParameters.setToolTipText(Messages.getTooltip("butParameters"));
		// butdescription.setToolTipText(Messages.getTooltip("butdescription"));
		// tree.setToolTipText(Messages.getTooltip("tree"));
		// txtJobname.setToolTipText(Messages.getTooltip("jobname"));
		// txtTitle.setToolTipText(Messages.getTooltip("jobtitle"));
		// txtPath.setToolTipText(Messages.getTooltip("jobdescription"));
		// butBack.setToolTipText(Messages.getTooltip("butBack"));
		// if (butCancel != null)
		// butCancel.setToolTipText(Messages.getTooltip("assistent.cancel"));
		// if (butShow != null)
		// butShow.setToolTipText(Messages.getTooltip("assistent.show"));
	}

	/**
	 * Felder und Attribute werden aus der Jobdokumnetation genommen und in eine hashMap gepackt.
	 * @return HashMap
	 */
	private HashMap getJobFromDescription() {
		HashMap h = new HashMap();
		try {
			// elMain ist ein Job Element der Jobbeschreibung
			if (tree.getSelection().length == 0)
				return h;
			Element elMain = (Element) tree.getSelection()[0].getData();
			// Attribute der Job bestimmen
			if (jobType != null && jobType.trim().length() > 0)
				h.put("order", jobType.equalsIgnoreCase("order") ? "yes" : "no");
			h.put("tasks", elMain.getAttributeValue("tasks"));
			h.put("name", txtJobname.getText());
			h.put("title", txtTitle.getText());
			// relativen pfad bestimmen
			File sData = new File(Options.getSchedulerData());
			File currPathFile = new File(txtPath.getText());
			File currPathParent = new File(currPathFile.getParent());
			if (currPathFile.getPath().indexOf(sData.getPath()) > -1) {
				h.put("filename", currPathParent.getName() + "/" + currPathFile.getName());
			}
			else {
				h.put("filepath", txtPath.getText());
			}
			// Element script
			Element script = elMain.getChild("script", elMain.getNamespace());
			if (script != null) {
				// hilfsvariable: es gibt script informationen
				h.put("script", "script");
				if (script.getAttributeValue("language") != null)
					h.put("script_language", script.getAttributeValue("language"));
				if (script.getAttributeValue("java_class") != null)
					h.put("script_java_class", script.getAttributeValue("java_class"));
				if (script.getAttributeValue("com_class") != null)
					h.put("script_com_class", script.getAttributeValue("com_class"));
				if (script.getAttributeValue("filepath") != null)
					h.put("script_filename", script.getAttributeValue("filepath"));
				if (script.getAttributeValue("use_engine") != null)
					h.put("script_use_engine", script.getAttributeValue("use_engine"));
				// script includes bestimmen
				List comClassInclude = script.getChildren("include", elMain.getNamespace());
				ArrayList listOfIncludeFilename = new ArrayList();
				for (int i = 0; i < comClassInclude.size(); i++) {
					Element inc = (Element) comClassInclude.get(i);
					listOfIncludeFilename.add(inc.getAttribute("file").getValue());
				}
				h.put("script_include_file", listOfIncludeFilename);
				// welche Library wurde hier verwendet? interne verwendung
				if (script.getAttributeValue("resource") != null) {
					String lib = script.getAttributeValue("resource");
					if (lib.length() > 0) {
						Element rese = elMain.getParentElement().getChild("resources", elMain.getNamespace());
						if (rese != null) {
							List r = rese.getChildren("file", elMain.getNamespace());
							if (r != null) {
								for (int i = 0; i < r.size(); i++) {
									Element res = (Element) r.get(i);
									if (Utils.getAttributeValue("id", res) != null && Utils.getAttributeValue("id", res).equals(lib)) {
										if (Utils.getAttributeValue("file", res) != null)
											h.put("library", Utils.getAttributeValue("file", res));
										JobListener.setLibrary(Utils.getAttributeValue("file", res));
									}
								}
							}
						}
					}
				}
			}
			// Element monitor
			Element monitor = elMain.getChild("monitor", elMain.getNamespace());
			if (monitor != null) {
				// hilfsvariable: es gibt Monitor Informationen
				h.put("monitor", "monitor");
				Element mon_script = monitor.getChild("script", elMain.getNamespace());
				if (mon_script != null) {
					if (mon_script.getAttributeValue("language") != null)
						h.put("monitor_script_language", mon_script.getAttributeValue("language"));
					if (mon_script.getAttributeValue("java_class") != null)
						h.put("monitor_script_java_class", mon_script.getAttributeValue("java_class"));
					if (mon_script.getAttributeValue("com_class") != null)
						h.put("monitor_script_com_class", mon_script.getAttributeValue("com_class"));
					if (mon_script.getAttributeValue("filepath") != null)
						h.put("monitor_script_filename", mon_script.getAttributeValue("filepath"));
					if (mon_script.getAttributeValue("use_engine") != null)
						h.put("monitor_script_use_engine", mon_script.getAttributeValue("use_engine"));
					// script monitor includes bestimmen
					List comClassInclude = mon_script.getChildren("include", elMain.getNamespace());
					ArrayList listOfIncludeFilename = new ArrayList();
					for (int i = 0; i < comClassInclude.size(); i++) {
						Element inc = (Element) comClassInclude.get(i);
						listOfIncludeFilename.add(inc.getAttribute("file").getValue());
					}
					h.put("monitor_script_include_file", listOfIncludeFilename);
				}
			}
			// Element process aus der Dokumentation zu Execute aus der Konfiguration
			Element process = elMain.getChild("process", elMain.getNamespace());
			if (process != null) {
				h.put("process", "process"); // hilfsvariable: es gibt proces Informationen
				if (process.getAttributeValue("file") != null)
					h.put("process_file", process.getAttributeValue("file"));
				if (process.getAttributeValue("param") != null)
					h.put("process_param", process.getAttributeValue("param"));
				if (process.getAttributeValue("log") != null)
					h.put("process_log", process.getAttributeValue("log"));
				// environment Variablen bestimmen
				Element environment = process.getChild("environment", elMain.getNamespace());
				if (environment != null) {
					List listOfEnvironment = environment.getChildren("variable", elMain.getNamespace());
					ArrayList listOfIncludeFilename = new ArrayList();
					for (int i = 0; i < listOfEnvironment.size(); i++) {
						HashMap hEnv = new HashMap();
						Element env = (Element) listOfEnvironment.get(i);
						hEnv.put("name", env.getAttribute("name") != null ? env.getAttribute("name").getValue() : "");
						hEnv.put("value", env.getAttribute("value") != null ? env.getAttribute("value").getValue() : "");
						listOfIncludeFilename.add(hEnv);
					}
					h.put("process_environment", listOfIncludeFilename);
				}
			}
		}
		catch (Exception e) {
			try {
				//				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), e);
				new ErrorLog(SOSJOEMessageCodes.JOE_E_0002.params(sos.util.SOSClassUtil.getMethodName()), e);
			}
			catch (Exception ee) {
				// tu nichts
			}
			//			System.out.println("..error in JobAssistentImportJobsForm.getJobFromDescription() " + e.getMessage());
			System.out.println(SOSJOEMessageCodes.JOE_M_0010.params("getJobFromDescription()", e.getMessage()));
		}
		return h;
	}

	private void close() {
		int cont = ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_CancelWizard.label(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		if (cont == SWT.OK) {
			if (jobBackUp != null)
				joblistener.getJob().setContent(jobBackUp.cloneContent());
			shell.dispose();
		}
	}

	private void selectTree() {
		if (joblistener != null && (joblistener.getInclude() == null || joblistener.getInclude().length() == 0)) {
			TreeItem[] si = new TreeItem[1];
			si[0] = tree.getItem(0);
			tree.setSelection(si);
			return;
		}
		if (tree != null) {
			for (int i = 0; i < tree.getItemCount(); i++) {
				TreeItem item = tree.getItem(i);
				if (item.getText(2) != null) {
					String it = new File(item.getText(2)).getName();
					String in = new File(joblistener.getInclude()).getName();
					if (it.endsWith(in)) {
						TreeItem[] si = new TreeItem[1];
						si[0] = item;
						tree.setSelection(si);
						flagBackUpJob = false;
						break;
					}
				}
			}
		}
	}

	/**
	 * Der Wizzard wurde f�r ein bestehende Job gestartet.
	 * Beim verlassen der Wizzard ohne Speichern, muss der bestehende Job ohne �nderungen wieder zur�ckgesetz werden.
	 * @param backUpJob
	 */
	public void setBackUpJob(final Element backUpJob, final ScriptJobMainForm jobForm_) {
		if (backUpJob != null)
			jobBackUp = (Element) backUpJob.clone();
		if (jobForm_ != null)
			jobForm = jobForm_; 
		if (backUpJob != null)
			selectTree();
	}

	public void setJobForm(final ScriptJobMainForm jobForm_) {
		jobForm = jobForm_;
	}

	public void setJobForm(final JobDocumentation jobForm_) {
		// jobForm = jobForm_;
	}

	public void setJobForm(JobDocumentationForm jobDocForm_) {
		jobDocForm_ = jobDocForm;
	}

	private boolean check() {
		if (tree.getSelectionCount() == 0) {
			ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_NoJobSelected.label(), SWT.ICON_WARNING | SWT.OK);
			txtJobname.setFocus();
			return false;
		}
		if (assistentType != JOEConstants.JOB && joblistener != null && !joblistener.getJob().getName().equals("config")) {
			if (txtJobname.isEnabled()) {
				if (txtJobname.getText() == null || txtJobname.getText().length() == 0) {
					ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_NoJobName.label(), SWT.ICON_WARNING | SWT.OK);
					txtJobname.setFocus();
					return false;
				}
			}
			if (txtJobname.getText().concat(".xml").equalsIgnoreCase(new File(txtPath.getText()).getName())) {
				int cont = ErrorLog.message(shell, SOSJOEMessageCodes.JOE_M_JobAssistent_EditJobName.label(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				if (cont == SWT.YES) {
					txtJobname.setFocus();
					return false;
				}
			}
		}
		return true;
	}

	// Details hat einen anderen Aufbau der Parameter Description.
	// Beim generieren der Parameter mit Wizzard m�ssen die Parameterdescriptchen anders aufgebaut werden.
	public void setDetailsRefresh(final Text refreshDetailsText_) {
		refreshDetailsText = refreshDetailsText_;
	}
}
