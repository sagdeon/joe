package sos.scheduler.editor.app;

import java.io.File;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import sos.scheduler.editor.actions.ActionsDom;
import sos.scheduler.editor.actions.forms.ActionsForm;
import sos.scheduler.editor.app.MainListener;
import sos.scheduler.editor.app.IContainer;
import sos.scheduler.editor.app.TabbedContainer;
import sos.scheduler.editor.conf.DetailDom;
import sos.scheduler.editor.conf.SchedulerDom;
import sos.scheduler.editor.doc.DocumentationDom;
import sos.scheduler.editor.conf.forms.JobAssistentForm;
import sos.scheduler.editor.conf.forms.JobChainConfigurationForm;
import sos.scheduler.editor.conf.forms.HotFolderDialog;
import java.util.ArrayList;
import sos.scheduler.editor.conf.forms.SchedulerForm;
import sos.scheduler.editor.doc.forms.DocumentationForm;
import sos.util.SOSString;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;

public class MainWindow  {


	private static Shell                 sShell             = null; // @jve:decl-index=0:visual-constraint="3,1"

	private        MainListener          listener           = null;

	private static IContainer            container          = null;

	private        Menu                  menuBar            = null;

	private static Menu                  mFile              = null;

	private        Menu                  submenu            = null;

	private        Menu                  menuLanguages      = null;

	private        Menu                  submenu1           = null;

	private        MainWindow            main               = null;

	private        Composite             groupmain          = null;

	private static ToolItem              butSave            = null;

	private static ToolItem              butShowAsSML       = null; 

	private static SOSString             sosString          = new SOSString();

	/**  */
	private static boolean flag = true;//hilfsvariable


	public MainWindow() {
		super();	
	}
	/**
	 * This method initializes composite
	 */
	private void createContainer() {
		container = new TabbedContainer(sShell);
		sShell.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		//TODO: Ausserhalb des Job Editors ver�nderte Files sollten mit Hilfe einer "Aktualisieren" Funktion neu eingelesen werden k�nnen.


		sShell.addShellListener(new ShellListener() {

			public void shellActivated(ShellEvent event) {

				shellActivated_();
				/*System.out.println("activated");
				if(!flag) {
					System.out.println("ignore");
					return;
				}


				if(MainWindow.getContainer().getCurrentEditor() == null)
					return;

				DomParser dom = getSpecifiedDom();
				if(dom.getFilename() != null) {

					File f = new File(dom.getFilename());

					if(dom.getFileLastModified() > 0 && 
                		   f.lastModified() != dom.getFileLastModified()) {

						flag = false;
                	   int c = MainWindow.message(sShell, "This file " + dom.getFilename()+ " has been modified outside.\nDo you want to reload it?",  SWT.ICON_QUESTION | SWT.YES | SWT.NO );
						if(c == SWT.YES) {

							System.out.println("hier neu laden");
							try {

							dom.read(dom.getFilename());

							if (container.getCurrentEditor() instanceof SchedulerForm) {
								SchedulerForm form =(SchedulerForm)container.getCurrentEditor();
								form.updateTree("main");
								form.update();
							}
							} catch (Exception e) {
								System.out.println(e.toString());
							}
						}


                   }
				}
				flag = true;
				 */
			}

			public void shellClosed(ShellEvent arg0) {
				//System.out.println("close");
			}

			public void shellDeactivated(ShellEvent arg0) {
				//System.out.println("deactivated");
			}

			public void shellDeiconified(ShellEvent arg0) {
				//System.out.println("deicon");
			}

			public void shellIconified(ShellEvent arg0) {
				//System.out.println("icon");
			}
		});

		main = this;
	}


	/**
	 * This method initializes sShell
	 * @wbp.parser.entryPoint
	 */
	public void createSShell() {

		sShell = new Shell();
		final GridLayout gridLayout_1 = new GridLayout();
		sShell.setLayout(gridLayout_1);
		sShell.setText("Job Scheduler Editor");
		sShell.setData(sShell.getText());		
		sShell.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/editor.png"));

		groupmain = new Composite(sShell, SWT.NONE);
		groupmain.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		groupmain.setLayout(gridLayout);

		createToolBar();
		createContainer();

		listener = new MainListener(this, container);
		sShell.setSize(new org.eclipse.swt.graphics.Point(940, 600));
		sShell.setMinimumSize(940, 600);

		// load resources
		listener.loadOptions();
		listener.loadMessages();
		listener.loadJobTitels();
		listener.loadHolidaysTitel();
		Options.loadWindow(sShell, "editor");

		menuBar = new Menu(sShell, SWT.BAR);
		MenuItem submenuItem2 = new MenuItem(menuBar, SWT.CASCADE);
		submenuItem2.setText("&File");
		mFile = new Menu(submenuItem2);

		MenuItem open = new MenuItem(mFile, SWT.PUSH);
		open.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.openQuick() != null)
					setSaveStatus();
			}
		});
		open.setText("Open                                  \tCtrl+O");
		open.setAccelerator(SWT.CTRL | 'O');

		MenuItem mNew = new MenuItem(mFile, SWT.CASCADE);				
		mNew.setText("New                           ");


		Menu pmNew = new Menu(mNew);
		MenuItem pNew = new MenuItem(pmNew, SWT.PUSH);
		pNew.setText("Configuration                  \tCtrl+I");
		pNew.setAccelerator(SWT.CTRL | 'I');
		pNew.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newScheduler() != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		mNew.setMenu(pmNew);

		MenuItem push1 = new MenuItem(pmNew, SWT.PUSH);
		push1.setText("Documentation            \tCtrl+P"); // Generated
		push1.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newDocumentation() != null)
					setSaveStatus();
			}


			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		//new event handler
		MenuItem pNewActions = new MenuItem(pmNew, SWT.PUSH);
		pNewActions.setText("Event Handler \tCTRL+X");
		pNewActions.setAccelerator(SWT.CTRL | 'X');
		pNewActions.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newActions() != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});


		MenuItem mpLife = new MenuItem(pmNew, SWT.CASCADE);				
		mpLife.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
			}
		});
		mpLife.setText("Hot Folder Element   \tCtrl+L");
		mpLife.setAccelerator(SWT.CTRL | 'L');

		Menu mLife = new Menu(mpLife);
		MenuItem mLifeJob = new MenuItem(mLife, SWT.PUSH);
		mLifeJob.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {

				if (container.newScheduler(SchedulerDom.LIFE_JOB) != null)
					setSaveStatus();
			}
		});
		mLifeJob.setText("Job           \tCtrl+J");
		mLifeJob.setAccelerator(SWT.CTRL | 'J');
		mpLife.setMenu(mLife);

		MenuItem mLifeJobChain = new MenuItem(mLife, SWT.PUSH);
		mLifeJobChain.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_JOB_CHAIN) != null)
					setSaveStatus();
			}
		});
		mLifeJobChain.setText("Job Chain     \tCtrl+K");
		mLifeJobChain.setAccelerator(SWT.CTRL | 'K');

		MenuItem mLifeProcessClass = new MenuItem(mLife, SWT.PUSH);
		mLifeProcessClass.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_PROCESS_CLASS) != null)
					setSaveStatus();
			}
		});
		mLifeProcessClass.setText("Process Class \tCtrl+R");
		mLifeProcessClass.setAccelerator(SWT.CTRL | 'R');

		MenuItem mLifeLock = new MenuItem(mLife, SWT.PUSH);
		mLifeLock.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_LOCK) != null)
					setSaveStatus();
			}
		});
		mLifeLock.setText("Lock          \tCtrl+M");
		mLifeLock.setAccelerator(SWT.CTRL | 'M');

		MenuItem mLifeOrder= new MenuItem(mLife, SWT.PUSH);
		mLifeOrder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_ORDER) != null)
					setSaveStatus();
			}
		});
		mLifeOrder.setText("Order         \tCtrl+W");
		mLifeOrder.setAccelerator(SWT.CTRL | 'W');

		MenuItem mLifeSchedule= new MenuItem(mLife, SWT.PUSH);
		mLifeSchedule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_SCHEDULE) != null)
					setSaveStatus();
			}
		});
		mLifeSchedule.setText("Schedule      \tCtrl+K");
		mLifeSchedule.setAccelerator(SWT.CTRL | 'K');

		new MenuItem(mFile, SWT.SEPARATOR);

		MenuItem openDir = new MenuItem(mFile, SWT.PUSH);
		openDir.setText("Open Hot Folder               \tCtrl+D");		
		openDir.setAccelerator(SWT.CTRL | 'D');
		openDir.setEnabled(true);
		openDir.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.openDirectory(null) != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		//open remote configuration
		MenuItem mORC = new MenuItem(mFile, SWT.CASCADE);
		mORC.setText("Open Remote Configuration");

		Menu pMOpenGlobalScheduler = new Menu(mORC);

		MenuItem pOpenGlobalScheduler = new MenuItem(pMOpenGlobalScheduler, SWT.PUSH);
		pOpenGlobalScheduler.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				Utils.startCursor(getSShell());
				String globalSchedulerPath = Options.getSchedulerData().endsWith("/") || Options.getSchedulerData().endsWith("\\") ? Options.getSchedulerData() : Options.getSchedulerData() + "/";
				globalSchedulerPath = globalSchedulerPath + "config/remote/_all";
				File f = new java.io.File(globalSchedulerPath); 
				if(!f.exists()) {
					if(!f.mkdirs()) {						
						MainWindow.message("could not create Global Scheduler Configurations: " + globalSchedulerPath, SWT.ICON_WARNING);
						Utils.stopCursor(getSShell());
						return;
					}
				}

				if (container.openDirectory(globalSchedulerPath) != null)
					setSaveStatus();
				Utils.stopCursor(getSShell());
			}
		});
		pOpenGlobalScheduler.setText("Open Global Scheduler");	

		MenuItem pOpenSchedulerCluster = new MenuItem(pMOpenGlobalScheduler, SWT.PUSH);
		pOpenSchedulerCluster.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				HotFolderDialog dialog = new HotFolderDialog();
				dialog.showForm(HotFolderDialog.SCHEDULER_CLUSTER);
			}
		});
		pOpenSchedulerCluster.setText("Open Cluster Configuration");


		MenuItem pOpenSchedulerHost = new MenuItem(pMOpenGlobalScheduler, SWT.PUSH);
		pOpenSchedulerHost.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				HotFolderDialog dialog = new HotFolderDialog();
				dialog.showForm(HotFolderDialog.SCHEDULER_HOST);
			}
		});
		pOpenSchedulerHost.setText("Open Remote Scheduler Configuration");


		mORC.setMenu(pMOpenGlobalScheduler);

		new MenuItem(mFile, SWT.SEPARATOR);

		MenuItem pSaveFile = new MenuItem(mFile, SWT.PUSH);
		pSaveFile.setText("Save                                    \tCtrl+S");
		pSaveFile.setAccelerator(SWT.CTRL | 'S');
		pSaveFile.setEnabled(false);
		pSaveFile.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				save();				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			  
			}
		
		});
		MenuItem pSaveAs = new MenuItem(mFile, SWT.PUSH);
		pSaveAs.setText("Save As                            ");		
		pSaveAs.setEnabled(false);
		pSaveAs.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.getCurrentEditor() != null && container.getCurrentEditor().applyChanges()) {
					if(container.getCurrentTab().getData("ftp_title") != null) {
						container.getCurrentTab().setData("ftp_title", null);
						container.getCurrentTab().setData("ftp_profile_name", null);
						container.getCurrentTab().setData("ftp_remote_directory", null);
						container.getCurrentTab().setData("ftp_hot_folder_elements", null);
						container.getCurrentTab().setData("ftp_profile", null);
					}
					container.getCurrentEditor().saveAs();
					setSaveStatus();
				}
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem pSaveAsHotFolderElement = new MenuItem(mFile, SWT.PUSH);
		pSaveAsHotFolderElement.setText("Save As Hot Folder Elements   \tCtrl+B");		
		pSaveAsHotFolderElement.setAccelerator(SWT.CTRL | 'B');
		pSaveAsHotFolderElement.setEnabled(false);
		pSaveAsHotFolderElement.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

				if (container.getCurrentEditor() != null && container.getCurrentEditor().applyChanges()) {
					SchedulerForm form =(SchedulerForm)container.getCurrentEditor();
					SchedulerDom currdom = (SchedulerDom)form.getDom();
					if(IOUtils.saveDirectory(currdom, true, SchedulerDom.DIRECTORY, null, container)) {
						Element root = currdom.getRoot();
						if(root != null) {
							Element config = root.getChild("config");
							if(config != null) {
								config.removeChildren("jobs");								
								config.removeChildren("job_chains");
								config.removeChildren("locks");
								Utils.removeChildrensWithName(config, "process_classes");
								config.removeChildren("schedules");
								config.removeChildren("commands");
								form.updateTree("main");
								form.update();
							}
						}
					}
					container.getCurrentEditor().save();
					setSaveStatus();
				}
			}

			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});


		new MenuItem(mFile, SWT.SEPARATOR);

		//		FTP
		MenuItem mFTP = new MenuItem(mFile, SWT.CASCADE);				
		mFTP.setText("FTP");
		Menu pmFTP = new Menu(mNew);

		MenuItem pOpenFTP = new MenuItem(pmFTP, SWT.PUSH);
		pOpenFTP.setText("Open By FTP");

		pOpenFTP.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {	
				FTPDialog ftp = new FTPDialog(main);
				ftp.showForm(FTPDialog.OPEN);
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem pOpenHotFolderFTP = new MenuItem(pmFTP, SWT.PUSH);
		pOpenHotFolderFTP.setText("Open Hot Folder By FTP");
		pOpenHotFolderFTP.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {	
				FTPDialog ftp = new FTPDialog(main);
				ftp.showForm(FTPDialog.OPEN_HOT_FOLDER);
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		new MenuItem(pmFTP, SWT.SEPARATOR);

		MenuItem pSaveFTP = new MenuItem(pmFTP, SWT.PUSH);
		pSaveFTP.setText("Save By FTP");

		pSaveFTP.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {				
				saveByFTP();				
			}

			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		mFTP.setMenu(pmFTP);
		new MenuItem(mFile, SWT.SEPARATOR);

		//		WebDav		
		boolean existwebDavLib = existLibraries();
		MenuItem mWebDav = new MenuItem(mFile, SWT.CASCADE);				
		mWebDav.setText("WebDav");
		mWebDav.setAccelerator(SWT.CTRL | 'N');
		mWebDav.setEnabled(existwebDavLib);
		Menu pmWebDav = new Menu(mNew);

		MenuItem pOpenWebDav = new MenuItem(pmWebDav, SWT.PUSH);
		pOpenWebDav.setText("Open By WebDav");
		pOpenWebDav.setEnabled(existwebDavLib);
		pOpenWebDav.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					if(existLibraries()) {
						WebDavDialog webdav = new WebDavDialog();
						webdav.showForm(WebDavDialog.OPEN);
					}
				} catch(Exception ex) {
					try {
						new sos.scheduler.editor.app.ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " ; could not open file on Webdav Server", ex);
					} catch(Exception ee) {
						//tu nichts
					}
					MainWindow.message("could not open file on Webdav Server, cause: "  + ex.getMessage(), SWT.ICON_WARNING);

				}
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem pOpenHotFolderWebDav = new MenuItem(pmWebDav, SWT.PUSH);
		pOpenHotFolderWebDav.setText("Open Hot Folder By WebDav");
		pOpenHotFolderWebDav.setEnabled(existwebDavLib);
		pOpenHotFolderWebDav.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if(existLibraries()) {
					WebDavDialog webdav = new WebDavDialog();
					webdav.showForm(WebDavDialog.OPEN_HOT_FOLDER);
				}
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		new MenuItem(pmWebDav, SWT.SEPARATOR);

		MenuItem pSaveWebDav = new MenuItem(pmWebDav, SWT.PUSH);
		pSaveWebDav.setText("Save By WebDav");
		pSaveWebDav.setEnabled(existwebDavLib);
		pSaveWebDav.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {	
				if(existLibraries()) {

					WebDavDialog webdav = new WebDavDialog();
					DomParser currdom = getSpecifiedDom();
					if(currdom == null)
						return;

					if( currdom instanceof SchedulerDom && ((SchedulerDom)currdom).isDirectory()) {				
						webdav.showForm(WebDavDialog.SAVE_AS_HOT_FOLDER);
					} else
						webdav.showForm(WebDavDialog.SAVE_AS);
				}
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		mWebDav.setMenu(pmWebDav);
		new MenuItem(mFile, SWT.SEPARATOR);

		submenuItem2.setMenu(mFile);
		MenuItem pExit = new MenuItem(mFile, SWT.PUSH);
		pExit.setText("Exit\tCtrl+E");
		pExit.setAccelerator(SWT.CTRL | 'E');
		pExit.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					sShell.close();
				} catch(Exception es) {
					try {
						new ErrorLog("error: " + sos.util.SOSClassUtil.getMethodName(), es);
					} catch (Exception ee){
						//tu nichts
					}
				}

			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem submenuItem = new MenuItem(menuBar, SWT.CASCADE);
		submenuItem.setText("Options");
		MenuItem submenuItem3 = new MenuItem(menuBar, SWT.CASCADE);
		submenuItem3.setText("&Help");
		submenu1 = new Menu(submenuItem3);

		MenuItem pHelS = new MenuItem(submenu1, SWT.PUSH);
		pHelS.setText("Scheduler Editor Help");		
		pHelS.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {				
				listener.openHelp(Options.getHelpURL("index"));				
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem pHelp = new MenuItem(submenu1, SWT.PUSH);
		pHelp.setText("Help\tF1");		
		pHelp.setAccelerator(SWT.F1);
		pHelp.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.getCurrentEditor() != null) {
					listener.openHelp(container.getCurrentEditor().getHelpKey());					
				}else {
					String msg = "Help is available after documentation or configuration is opened";
					MainWindow.message(msg, SWT.ICON_INFORMATION);
				}
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem pAbout = new MenuItem(submenu1, SWT.PUSH);
		pAbout.setText("About");
		pAbout.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				listener.showAbout();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		submenuItem3.setMenu(submenu1);
		submenu = new Menu(submenuItem);
		MenuItem submenuItem1 = new MenuItem(submenu, SWT.CASCADE);
		submenuItem1.setText("Help Language");
		menuLanguages = new Menu(submenuItem1);

		// create languages menu
		listener.setLanguages(menuLanguages);

		submenuItem1.setMenu(menuLanguages);
		submenuItem.setMenu(submenu);

		MenuItem submenuItemInfo = new MenuItem(submenu, SWT.PUSH);
		submenuItemInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				listener.resetInfoDialog();	
				Options.setPropertyBoolean("editor.job.show.wizard", true);
				Options.saveProperties();
			}
		});
		submenuItemInfo.setText("Reset Dialog");
		sShell.setMenuBar(menuBar);
		sShell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				e.doit = container.closeAll();
				setSaveStatus();
				Options.saveWindow(sShell, "editor");
				listener.saveOptions();
				ResourceManager.dispose();
			}
			public void shellActivated(org.eclipse.swt.events.ShellEvent e) {
				setSaveStatus();
			}
		});




	}


	public static Shell getSShell() {
		return sShell;
	}


	public static void setSaveStatus() {
		setMenuStatus();
		container.setStatusInTitle();
	}


	public static boolean setMenuStatus() {
		boolean saved = true;
		if (container.getCurrentEditor() != null) {
			saved = !container.getCurrentEditor().hasChanges();
			butShowAsSML.setEnabled(true);	
			butSave.setEnabled(container.getCurrentEditor().hasChanges());

		} else {
			butShowAsSML.setEnabled(false);
			butSave.setEnabled(false);
		}

		MenuItem[] items = mFile.getItems();
		int index = 0;
		for (int i =0; i < items.length; i++){
			MenuItem item = items[i];
			if(item.getText().startsWith("Save")) {
				index = i;
				break;
			} 
		}

		items[index].setEnabled(container.getCurrentEditor() != null);
		items[index+1].setEnabled(container.getCurrentEditor() != null);

		if(container.getCurrentEditor() instanceof sos.scheduler.editor.conf.forms.SchedulerForm)  {
			sos.scheduler.editor.conf.forms.SchedulerForm form =(sos.scheduler.editor.conf.forms.SchedulerForm)container.getCurrentEditor();
			SchedulerDom dom = (SchedulerDom)form.getDom(); 
			if(dom.isDirectory()) {
				items[index+1].setEnabled(false);			
			} 
			if(!dom.isLifeElement() && !dom.isDirectory()) {
				items[index+2].setEnabled(true);
				butSave.setEnabled(true);
			} else {
				items[index+2].setEnabled(false);			
			}

		} else {
			items[index+2].setEnabled(false);

		}
		return saved;
	}


	public static int message(String message, int style) {
		return message(getSShell(), message, style);		
	}


	public static int message(Shell shell, String message, int style) {
		MessageBox mb = new MessageBox(shell, style);
		mb.setMessage(message);

		String title = "Message";
		if ((style & SWT.ICON_ERROR) != 0)
			title = "Error";
		else if ((style & SWT.ICON_INFORMATION) != 0)
			title = "Information";
		else if ((style & SWT.ICON_QUESTION) != 0)
			title = "Question";
		else if ((style & SWT.ICON_WARNING) != 0)
			title = "Warning";
		mb.setText(title);

		return mb.open();
	}


	public static IContainer getContainer() {
		return container;
	}


	private void save() {
		Utils.startCursor(getSShell());
		HashMap changes = new HashMap();

		if(container.getCurrentEditor() instanceof sos.scheduler.editor.conf.forms.SchedulerForm) {
			sos.scheduler.editor.conf.forms.SchedulerForm form =(sos.scheduler.editor.conf.forms.SchedulerForm)container.getCurrentEditor();
			SchedulerDom currdom = (SchedulerDom)form.getDom();
			changes = (java.util.HashMap)((SchedulerDom)currdom).getChangedJob().clone()	;
		}

		if (container.getCurrentEditor().applyChanges()) {
			container.getCurrentEditor().save();
			saveJobChainNoteParameter();
			saveFTP(changes);
			saveWebDav(changes);			
			setSaveStatus();

		}		
		Utils.stopCursor(getSShell());
	}

	private void createToolBar() {

		final ToolBar toolBar = new ToolBar(groupmain, SWT.NONE);
		toolBar.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, true, false));
		final ToolItem butNew = new ToolItem(toolBar, SWT.NONE);
		butNew.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/icon_new.gif"));	
		final Menu menu = new Menu(toolBar);
		butNew.setToolTipText("New Configuration");
		MenuItem itemConfig = new MenuItem(menu, SWT.PUSH);
		itemConfig.setText("Configuration");
		itemConfig.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newScheduler() != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		MenuItem itemDoc = new MenuItem(menu, SWT.PUSH);
		itemDoc.setText("Documentation");
		itemDoc.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newDocumentation() != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		/*MenuItem itemDetails = new MenuItem(menu, SWT.PUSH);
		itemDetails.setText("Details");
		itemDetails.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newDetails() != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		 */
		MenuItem itemActions = new MenuItem(menu, SWT.PUSH);
		itemActions.setText("Event Handler");
		itemActions.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newActions() != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem itemHFEJob = new MenuItem(menu, SWT.PUSH);
		itemHFEJob.setText("Hot Folder Element - Job");
		itemHFEJob.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_JOB) != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem itemHFEJobChain = new MenuItem(menu, SWT.PUSH);
		itemHFEJobChain.setText("Hot Folder Element - Job Chain");
		itemHFEJobChain.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_JOB_CHAIN) != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem itemHFEProcessClass = new MenuItem(menu, SWT.PUSH);
		itemHFEProcessClass.setText("Hot Folder Element - Process Class");
		itemHFEProcessClass.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_PROCESS_CLASS) != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem itemHFELock = new MenuItem(menu, SWT.PUSH);
		itemHFELock.setText("Hot Folder Element - Lock");
		itemHFELock.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_LOCK) != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem itemHFEOrder = new MenuItem(menu, SWT.PUSH);
		itemHFEOrder.setText("Hot Folder Element - Order");
		itemHFEOrder.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_ORDER) != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		MenuItem itemHFEScheduler = new MenuItem(menu, SWT.PUSH);
		itemHFEScheduler.setText("Hot Folder Element - Schedule");
		itemHFEScheduler.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (container.newScheduler(SchedulerDom.LIFE_SCHEDULE) != null)
					setSaveStatus();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		addDropDown(butNew, menu);

		final ToolItem butOpen = new ToolItem(toolBar, SWT.PUSH);
		butOpen.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.openQuick() != null)
					setSaveStatus();
			}
		});
		butOpen.setImage(ResourceManager
				.getImageFromResource("/sos/scheduler/editor/icon_open.gif"));
		butOpen.setToolTipText("Open Configuration File");


		final ToolItem butOpenHotFolder = new ToolItem(toolBar, SWT.PUSH);
		butOpenHotFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.openDirectory(null) != null)
					setSaveStatus();
			}
		});
		butOpenHotFolder.setImage(ResourceManager
				.getImageFromResource("/sos/scheduler/editor/icon_open_hot_folder.gif"));
		butOpenHotFolder.setToolTipText("Open Hot Folder");


		butSave = new ToolItem(toolBar, SWT.PUSH);
		butSave.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				save();
			}
		});
		butSave.setImage(ResourceManager
				.getImageFromResource("/sos/scheduler/editor/save.gif"));	
		butSave.setToolTipText("Save Configuration");

		butShowAsSML = new ToolItem(toolBar, SWT.PUSH);
		butShowAsSML.setEnabled(container != null && container.getCurrentEditor() instanceof SchedulerForm);
		butShowAsSML.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(final SelectionEvent e) {
				try {
					if(container.getCurrentEditor()== null)
						return;
					DomParser currDomParser = getSpecifiedDom();				
					Utils.showClipboard(Utils.getElementAsString(currDomParser.getRoot()), getSShell(), false, null, false, null, false); 
				} catch (Exception ex) {
					try {
						new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " cause: " + ex.toString(), ex);
					} catch(Exception ee) {
						//tu nichts
					}
				}
			}
		});
		butShowAsSML.setImage(ResourceManager
				.getImageFromResource("/sos/scheduler/editor/icon_view_as_xml.gif"));	
		butShowAsSML.setToolTipText("Show Configuration as XML");

		final ToolItem butFTP = new ToolItem(toolBar, SWT.NONE);

		final Menu menuFTP = new Menu(toolBar);
		addDropDown(butFTP, menuFTP);

		butFTP.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/icon_open_ftp.gif"));	
		butFTP.setToolTipText("FTP");

		MenuItem itemFTPOpen = new MenuItem(menuFTP, SWT.PUSH);
		itemFTPOpen.setText("Open By FTP");
		itemFTPOpen.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(final SelectionEvent e) {
				FTPDialog ftp = new FTPDialog(main);
				ftp.showForm(FTPDialog.OPEN);
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});


		MenuItem itemFTPOpenHotFolder = new MenuItem(menuFTP, SWT.PUSH);
		itemFTPOpenHotFolder.setText("Open Hot Folder By FTP");
		itemFTPOpenHotFolder.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(final SelectionEvent e) {
				FTPDialog ftp = new FTPDialog(main);
				ftp.showForm(FTPDialog.OPEN_HOT_FOLDER);
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});


		MenuItem itemFTPSave = new MenuItem(menuFTP, SWT.PUSH);
		itemFTPSave.setText("Save As By FTP");
		itemFTPSave.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(final SelectionEvent e) {
				saveByFTP();				
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		final ToolItem itemReset = new ToolItem(toolBar, SWT.PUSH);
		//itemReset.setEnabled(container != null && (container.getCurrentEditor() instanceof sos.scheduler.editor.actions.forms.ActionsForm || container.getCurrentEditor() instanceof SchedulerForm ));
		itemReset.setImage(ResourceManager
				.getImageFromResource("/sos/scheduler/editor/icon_reset.gif"));

		itemReset.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(final SelectionEvent e) {
				int c = MainWindow.message("Do you want to reload the configuration and loose the changes?", SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				if(c != SWT.YES)
					return;
				if(container.getCurrentEditor() instanceof SchedulerForm) {					
					SchedulerForm form =(SchedulerForm)container.getCurrentEditor();
					SchedulerDom currdom = (SchedulerDom)form.getDom();
					if(currdom.isLifeElement())
						sos.scheduler.editor.app.Utils.reset( currdom.getRoot(), form, currdom);
					else
						sos.scheduler.editor.app.Utils.reset( currdom.getRoot().getChild("config"), form, currdom);

				} else if(container.getCurrentEditor() instanceof sos.scheduler.editor.actions.forms.ActionsForm) {
					sos.scheduler.editor.actions.forms.ActionsForm form =( sos.scheduler.editor.actions.forms.ActionsForm)container.getCurrentEditor();
					sos.scheduler.editor.actions.ActionsDom currdom = ( sos.scheduler.editor.actions.ActionsDom)form.getDom();
					sos.scheduler.editor.app.Utils.reset( currdom.getRoot(),form, currdom);
				} else if(container.getCurrentEditor() instanceof sos.scheduler.editor.doc.forms.DocumentationForm) {
					sos.scheduler.editor.doc.forms.DocumentationForm form =( sos.scheduler.editor.doc.forms.DocumentationForm)container.getCurrentEditor();
					sos.scheduler.editor.doc.DocumentationDom currdom = ( sos.scheduler.editor.doc.DocumentationDom)form.getDom();
					sos.scheduler.editor.app.Utils.reset( currdom.getRoot(),form, currdom);
				}

			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});


		final ToolItem butWizzard = new ToolItem(toolBar, SWT.PUSH);
		butWizzard.setToolTipText("Wizzard");
		butWizzard.setImage(ResourceManager
				.getImageFromResource("/sos/scheduler/editor/icon_wizzard.gif"));
		butWizzard.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(final SelectionEvent e) {
				startWizzard();
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});


		final ToolItem butHelp = new ToolItem(toolBar, SWT.PUSH);
		butHelp.setImage(ResourceManager
				.getImageFromResource("/sos/scheduler/editor/icon_help.gif"));
		butHelp.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(final SelectionEvent e) {
				if (container.getCurrentEditor() != null) {
					listener.openHelp(container.getCurrentEditor().getHelpKey());					
				}else {
					String msg = "Help is available after documentation or configuration is opened";
					MainWindow.message(msg, SWT.ICON_INFORMATION);
				}
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});


	}


	private static void addDropDown(final ToolItem item, final Menu menu) {
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				Rectangle rect = item.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = item.getParent().toDisplay(pt);
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			}
		});	
	}


	/**
	 * �berpr�fen, ob job Chain namen ver�ndert wurden. Wenn ja, dann die job chain note parameter anpassen
	 * Job Chain Note Parameter
	 */
	public void saveJobChainNoteParameter() {
		try {


			if(container.getCurrentTab().getData("details_parameter") != null) {
				HashMap h = new HashMap();
				h = (HashMap)container.getCurrentTab().getData("details_parameter");
				Iterator it = h.keySet().iterator();
				while(it.hasNext()) {
					Element jobChain = (Element)it.next();
					String configFilename = h.get(jobChain).toString();
					File configFile = new File(configFilename);
					if(configFile.exists()) {
						String newConfigFilename = configFile.getParent();
						newConfigFilename = newConfigFilename != null ? newConfigFilename : "";
						newConfigFilename = new File(newConfigFilename, Utils.getAttributeValue("name", jobChain) + ".config.xml").getCanonicalPath();
						File newConfigFile = new File(newConfigFilename);

						//Attribute anpassem
						DomParser currdom = getSpecifiedDom();
						String oldname = configFile.getName().replaceAll(".config.xml", "");
						String newName = newConfigFile.getName().replaceAll(".config.xml", "");
						sos.scheduler.editor.conf.listeners.DetailsListener.changeDetailsJobChainname(newName, oldname, (SchedulerDom)currdom);
						//

						if(!newConfigFile.exists() &&  !configFile.renameTo(newConfigFile)) {
							MainWindow.message("could not rename job chain node configuration file [" + configFilename + "] in [" + newConfigFilename+ "].\n" +
									"Please try later by Hand."
									, SWT.ICON_WARNING);
						} else {
							//Attribute in der config.xml Datei vder�ndern

						}
					}

				}
				container.getCurrentTab().setData("details_parameter", new HashMap());
			}
		} catch (Exception e) {

		}
	}

	public static void saveFTP(java.util.HashMap changes)  {
		try {
			if(container.getCurrentTab().getData("ftp_title") != null && 
					container.getCurrentTab().getData("ftp_title").toString().length()>0) {

				DomParser currdom = getSpecifiedDom();
				if(currdom == null)
					return;			

				String remoteDir = container.getCurrentTab().getData("ftp_remote_directory").toString();
				ArrayList ftpHotFolderElements = new ArrayList();
				if(container.getCurrentTab().getData("ftp_hot_folder_elements") != null)
					ftpHotFolderElements = (ArrayList)container.getCurrentTab().getData("ftp_hot_folder_elements");

				sos.ftp.profiles.FTPProfile profile = (sos.ftp.profiles.FTPProfile)container.getCurrentTab().getData("ftp_profile");

				Text txtLog = new Text(getSShell(), SWT.NONE);
				txtLog.setVisible(false);

				final GridData gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
				gridData.widthHint = 0;
				gridData.heightHint = 0;
				txtLog.setLayoutData(gridData);
				txtLog.setSize(0, 0);				


				profile.setLogText(txtLog);

				profile.connect();

				if(profile.isLoggedIn()) {
					if( currdom instanceof SchedulerDom && 
							sosString.parseToString(container.getCurrentTab().getData("ftp_details_parameter_file")).length() > 0) {

						//Details Parameter speichern						
						File source = new File(container.getCurrentTab().getData("ftp_details_parameter_file").toString());
						String remoteDir_ = remoteDir; //remoteDir nicht ver�ndern, da es unten weiterverarbeitet wird
						remoteDir_ = new File(remoteDir_).getCanonicalPath().endsWith(".xml") ? new File(remoteDir_).getParent() : remoteDir_;
						remoteDir_ = remoteDir_ != null ? remoteDir_.replaceAll("\\\\", "/") : "";
						if (source.exists()) {
							profile.saveAs( container.getCurrentTab().getData("ftp_details_parameter_file").toString(), remoteDir_ + "/" + source.getName());
						}						container.getCurrentTab().setData("ftp_details_parameter_file", "");

						if(sosString.parseToString(container.getCurrentTab().getData("ftp_details_parameter_remove_file")).length() > 0) {
							//Alte Jobkettenname wurde gel�scht.. Deshalb den alten Job Node Parametern auch l�schen.
							String removeOldFilename = container.getCurrentTab().getData("ftp_details_parameter_remove_file").toString();							
							profile.removeFile(remoteDir_ + "/" + removeOldFilename);
							container.getCurrentTab().setData("ftp_details_parameter_remove_file", "");
						}
					} 
					if( currdom instanceof SchedulerDom && ((SchedulerDom)currdom).isLifeElement()) {
						String filename = container.getCurrentEditor().getFilename();
						//if(!new File(remoteDir).getName().equalsIgnoreCase(new File(filename).getName())){
						if(!new File(remoteDir).getName().equalsIgnoreCase(new File(filename).getName())){
							//Attribute "name" wurde ge�ndert: Das bedeutet auch �nderungen der life Datei namen.
							profile.removeFile(remoteDir);
							try {
								String newName = sosString.parseToString(new File(remoteDir).getParent()) + "/" + new File(filename).getName();
								newName = newName.replaceAll("\\\\", "/");
								container.getCurrentTab().setData("ftp_remote_directory", newName);
							} catch(Exception e) {
								System.out.println("could not save per ftp, cause: " + e.toString());

							} //tu nichts 
						}
						remoteDir = new File(remoteDir).getParent() + "/" + new File(filename).getName();

						profile.saveAs( filename, remoteDir);

					} else if( currdom instanceof SchedulerDom && ((SchedulerDom)currdom).isDirectory()) {

						profile.saveHotFolderAs(container.getCurrentEditor().getFilename(), remoteDir, ftpHotFolderElements, changes);

					} else {

						profile.saveAs( container.getCurrentEditor().getFilename(), remoteDir );

					}
					profile.disconnect();

				} else {
					MainWindow.message("could not save file on ftp Server", SWT.ICON_WARNING);
				}

				if(profile.hasError()) {
					String text = sos.scheduler.editor.app.Utils.showClipboard(txtLog.getText(), getSShell(), false, "");
					if(text != null)
						txtLog.setText(text);
				} 
			}
		} catch (Exception e) {
			MainWindow.message("could not save per ftp, cause: " + e.toString(), SWT.ICON_WARNING);

			try {
				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() , e);
			} catch(Exception ee) {
				//tu nichts
			}
		}
	}


	private void saveWebDav(java.util.HashMap changes) {

		WebDavDialogListener webdavListener = null;
		Text txtLog = null;

		if(container.getCurrentTab().getData("webdav_title") != null && 
				container.getCurrentTab().getData("webdav_title").toString().length()>0) {

			DomParser currdom = getSpecifiedDom();
			if(currdom == null)
				return;

			String profilename = container.getCurrentTab().getData("webdav_profile_name").toString();
			String remoteDir = container.getCurrentTab().getData("webdav_remote_directory").toString();
			ArrayList webdavHotFolderElements = new ArrayList();
			if(container.getCurrentTab().getData("webdav_hot_folder_elements") != null)
				webdavHotFolderElements = (ArrayList)container.getCurrentTab().getData("webdav_hot_folder_elements");

			java.util.Properties profile = (java.util.Properties)container.getCurrentTab().getData("webdav_profile");

			txtLog = new Text(getSShell(), SWT.NONE);
			txtLog.setVisible(false);
			final GridData gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
			gridData.widthHint = 0;
			gridData.heightHint = 0;
			txtLog.setLayoutData(gridData);
			txtLog.setSize(0, 0);
			webdavListener = new WebDavDialogListener(profile, profilename);
			webdavListener.setLogText(txtLog);

			if( currdom instanceof SchedulerDom && ((SchedulerDom)currdom).isLifeElement()) {

				String filename = container.getCurrentEditor().getFilename();
				if(!new File(remoteDir).getName().equalsIgnoreCase(new File(filename).getName())){
					//Attribute "name" wurde ge�ndert: Das bedeutet auch �nderungen der life Datei namen.
					webdavListener.removeFile(remoteDir);
				}

				remoteDir = remoteDir.substring(0, remoteDir.lastIndexOf("/"))+ "/" + new File(filename).getName();


				webdavListener.saveAs( filename, remoteDir);

			} else if( currdom instanceof SchedulerDom && ((SchedulerDom)currdom).isDirectory()) {

				webdavListener.saveHotFolderAs(container.getCurrentEditor().getFilename(), remoteDir, webdavHotFolderElements, changes);

			} else {

				webdavListener.saveAs( container.getCurrentEditor().getFilename(), remoteDir );

			}

			if(webdavListener.hasError()) {
				String text = sos.scheduler.editor.app.Utils.showClipboard(txtLog.getText(), getSShell(), false, "");
				if(text != null)
					txtLog.setText(text);
			} 

		} 
	}


	private void saveByFTP() {

		FTPDialog ftp = new FTPDialog(main);

		DomParser currdom = getSpecifiedDom();
		if(currdom == null)
			return;

		if( currdom instanceof SchedulerDom && ((SchedulerDom)currdom).isDirectory()) {
			ftp.showForm(FTPDialog.SAVE_AS_HOT_FOLDER);
		} else
			ftp.showForm(FTPDialog.SAVE_AS);

	}

	private boolean existLibraries() { 
		boolean libExist = false;
		try{
			try {
				Class.forName("org.apache.commons.logging.LogFactory");
			} catch (Exception e) {
				throw e;
			}
			try {
				Class.forName("org.apache.commons.httpclient.HttpState");
			} catch (Exception e) {
				throw e;
			}
			try {
				Class.forName("org.apache.commons.codec.DecoderException");
			} catch (Exception e) {
				throw e;
			}
			try {
				Class.forName("org.apache.webdav.lib.WebdavResource");
			} catch (Exception e) {
				throw e;
			}


			libExist = true;
		} catch (Exception e){
			/*
			String msg = "Missing libraries to open connection to Webdav Server. \n\n " +
			"\t- webdavclient4j-core-0.92.jar\n" +
			"\t- commons-logging.jar\n" +								
			"\t- commons-codec-1.3.jar\n" +
			"\t- commons-httpclient-3.0.1.jar\n\n" +
			"for more information see https://sourceforge.net/projects/webdavclient4j/";

			MainWindow.message(msg, SWT.ICON_WARNING);

			try {
				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " cause: " + msg, e);
			} catch(Exception ee) {
				//tu nichts
			}
			 */
		}
		return libExist;

	}

	public static DomParser getSpecifiedDom() {

		DomParser currdom = null;
		if(MainWindow.getContainer().getCurrentEditor() instanceof SchedulerForm) {
			SchedulerForm form =(SchedulerForm)MainWindow.getContainer().getCurrentEditor();			
			currdom = (SchedulerDom)form.getDom();
		} else if(MainWindow.getContainer().getCurrentEditor() instanceof DocumentationForm) {
			DocumentationForm form =(DocumentationForm)MainWindow.getContainer().getCurrentEditor();			
			currdom = (DocumentationDom)form.getDom();
		} else if(MainWindow.getContainer().getCurrentEditor() instanceof JobChainConfigurationForm) {
			JobChainConfigurationForm form =(JobChainConfigurationForm)MainWindow.getContainer().getCurrentEditor();
			currdom = (DetailDom)form.getDom();
		}else if(MainWindow.getContainer().getCurrentEditor() instanceof ActionsForm) {
			ActionsForm form =(ActionsForm)MainWindow.getContainer().getCurrentEditor();
			currdom = (ActionsDom)form.getDom();
		} else {
			MainWindow.message("Could not save FTP File. <unspecified type>  ", SWT.ICON_WARNING);
		}
		return currdom;
	}

	private void startWizzard() {

		try {						
			Utils.startCursor(sShell);						
			SchedulerForm _scheduler = container.newScheduler(SchedulerDom.LIFE_JOB);
			if (_scheduler  != null)
				setSaveStatus();
			JobAssistentForm assitent = new JobAssistentForm(_scheduler.getDom(), _scheduler);
			assitent.startJobAssistant();
			setSaveStatus();
		} catch (Exception ex) {
			try {
				new sos.scheduler.editor.app.ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " ; could not start assistent." , ex);
			} catch(Exception ee) {
				//tu nichts
			}
			System.out.println("..error " + ex.getMessage());
		} finally {
			Utils.stopCursor(sShell);
		}

	}


	/**
	 * �berpr�ft beim wieder Aktivieren des Editor, ob sich eine 
	 * im Editor ge�ffnete Konfigurationsdatei ausserhalb sich ge�ndert hat.
	 *  TODO:
	 */
	public static void shellActivated_() {

		try {
			//System.out.println("activated");


			if(MainWindow.getContainer().getCurrentEditor() == null || !flag) {
				//System.out.println("ignore");
				return;
			}



			DomParser dom = getSpecifiedDom();
			if(dom.getFilename() != null) {

				File f = new File(dom.getFilename());
				ArrayList<File> changeFiles = new ArrayList<File>();//gilt f�r Hot Folder Dateien, die von einer anderen Process ver�ndert wurden
				ArrayList<File> newFFiles = new ArrayList<File>();
				ArrayList<File> delFFiles = new ArrayList<File>();
				HashMap<String, Long> hFFiles = new HashMap<String, Long>();
				//System.out.println("file     = " + dom.getLastModifiedFile());
				//System.out.println("dom file = " + f.lastModified() );

				//Hot Folder. Hat sich ein Holt Folder Datei ausserhalb ver�ndert?
				long lastmod = 0;
				if(dom.getFilename() != null) {
					hFFiles = ((SchedulerDom)dom).getHotFolderFiles();
					if(f.isDirectory() ) {				
						ArrayList<File> listOfhotFolderFiles = ((SchedulerDom)dom).getHoltFolderFiles(new File(dom.getFilename()));
						//wurden Ve�nderungen ausserhalb durchgef�hrt
						for(int i = 0; i < listOfhotFolderFiles.size(); i++) {           		    	
							File fFile = listOfhotFolderFiles.get(i);								
							

							try {								
								long current = fFile.lastModified();//aktuelle �nderungs Zeitstempel
								if(hFFiles.containsKey(fFile.getName())) {
									long domc = Long.parseLong((hFFiles.get(fFile.getName()).toString()));//gespeicherte Zeitstempel
									if(current != domc)
										changeFiles.add(fFile);
								} else {
									//sind neue HotFolder Dateien ausserhalb zustande gekommen?
									//("jobname" + "_" + name, what)
									String fName = fFile.getName();
									int pos1 = fName.indexOf(".");
									int pos2 = fName.lastIndexOf(".");
									String n = fName.substring(pos1, pos2) + "_" + fName.substring(0, pos1);
									if(!( ((SchedulerDom)dom).getChangedJob().get(n) != null && ((SchedulerDom)dom).getChangedJob().get(n).equals(SchedulerDom.DELETE) ))
										newFFiles.add(fFile);
								}

								

							} catch (Exception e) {
								//tu nichts
								e.printStackTrace();
							}
							lastmod = lastmod + fFile.lastModified();
						}

						//�berpr�fen, ob Dateien ausserhalb gel�scht wurden
						Iterator<String> it = hFFiles.keySet().iterator();
						while(it.hasNext()) {
							String fName = it.next();
							if(!new File(dom.getFilename(), fName).exists()) {
								delFFiles.add(new File(dom.getFilename(), fName));
							}
						}

					} else {
						//if(!new File(dom.getFilename()).exists())
						//	delFFiles.add(new File(dom.getFilename()));
						lastmod = f.lastModified(); 
					}
				}
				if(dom.getFilename() != null && 						
						lastmod != dom.getLastModifiedFile()) {

					flag = false;

					String msg = "";
					if(f.isDirectory()) {

						msg = "This directory " + dom.getFilename()+ " has been modified outside.";
						if(newFFiles.size() > 0) {
							msg = msg + "\nNew Files: ";

							for(int i = 0; i < newFFiles.size(); i++) {
								if(i == 0)
									msg = msg + "n\t" + 	 newFFiles.get(i).getName();
								else
									msg = msg + "\n\t" + newFFiles.get(i).getName();
							}
						}
						if(changeFiles.size() > 0) {
							msg = msg + "\nChange Files: ";

							for(int i = 0; i < changeFiles.size(); i++) {
								if(i == 0)
									msg = msg + "n\t" + 	 changeFiles.get(i);
								else
									msg = msg + "\n\t" + changeFiles.get(i);
							}
						}						

						if(delFFiles.size() > 0) {
							msg = msg + "\nRemove Files: ";

							for(int i = 0; i < delFFiles.size(); i++) {
								if(i == 0)
									msg = msg + "\n\t" + delFFiles.get(i);
								else
									msg = msg + "\n\t" + delFFiles.get(i);
							}
							
						}
						
						msg = msg + "\nDo you want to reload it?";
					} else {
						if(!new File(dom.getFilename()).exists()){
							msg = "This file " + dom.getFilename()+ " has been deleted outside.\nDo you want to close the Editor?";
							delFFiles.add(new File(dom.getFilename()));
							
						} else 
							msg = "This file " + dom.getFilename()+ " has been modified outside.\nDo you want to reload it?";
					}


					int c = MainWindow.message(sShell, msg,  SWT.ICON_QUESTION | SWT.YES | SWT.NO );
					if(c == SWT.YES) {

						//System.out.println("hier neu laden");
						try {

							if(f.isDirectory()) {
								for(int i = 0; i < changeFiles.size(); i++) {
									File hFfile = changeFiles.get(i);
									String sXPATH= getXPathString(hFfile, false);

									XPath x1 = XPath.newInstance(sXPATH);
									List<Element> listOfElement = x1.selectNodes(dom.getDoc());
									if(!listOfElement.isEmpty()){
										Element e = listOfElement.get(0);
										Element pe = e.getParentElement();
										e.detach();
										Element n = MergeAllXMLinDirectory.readElementFromHotHolderFile(hFfile);
										pe.addContent((Element)n.clone());

									}

								}


								//Es wurden ausserhalb vom Editor neue Hot Folder dateien hinzugef�gt. In diesem Fall soll der Editor aktualisiert werden 
								for(int i = 0; i < newFFiles.size(); i++) {
									File newHFFile = newFFiles.get(i);

									String sXPATH= getXPathString(newHFFile, true);

									XPath x1 = XPath.newInstance(sXPATH);
									List<Element> listOfElement = x1.selectNodes(dom.getDoc());
									if(!listOfElement.isEmpty()) {
										Element pe = listOfElement.get(0);
										Element n = MergeAllXMLinDirectory.readElementFromHotHolderFile(newHFFile);
										pe.addContent((Element)n.clone());
									} else {

										Element pe = new Element(sXPATH);							
										dom.getRoot().addContent(pe);
										Element n = MergeAllXMLinDirectory.readElementFromHotHolderFile(newHFFile);
										pe.addContent((Element)n.clone());

									}
								}
												
								
								for(int i = 0; i < delFFiles.size(); i++){
									File delFile = delFFiles.get(i);
									String sXPATH= getXPathString(delFile, false);

									XPath x1 = XPath.newInstance(sXPATH);
									List<Element> listOfElement = x1.selectNodes(dom.getDoc());
									if(!listOfElement.isEmpty()) {
										Element pe = listOfElement.get(0);
										pe.detach();
										((SchedulerDom)dom).getHotFolderFiles().remove(delFile.getName());
									}									
								}

								if(changeFiles.size() > 0 || newFFiles.size() > 0 || delFFiles.size() > 0) {
									SchedulerForm form =(SchedulerForm)container.getCurrentEditor();
									form.updateTree("main");
									//form.updateCommands();
									form.update();
									dom.readFileLastModified();
								}

							} else {

								if(delFFiles.size() > 0) {
									//current Tabraiter  soll geschlossen werden weil die Kpnfigurationsdatei ausserhalb gel�scht wurden
									MainWindow.getContainer().getCurrentTab().dispose();
									return;
								}
								
								dom.read(dom.getFilename());
								

								if (container.getCurrentEditor() instanceof SchedulerForm) {							
									SchedulerForm form =(SchedulerForm)container.getCurrentEditor();
									form.updateTree("main");
									form.update();
								} else if (container.getCurrentEditor() instanceof DocumentationForm) {
									DocumentationForm form =(DocumentationForm)container.getCurrentEditor();
									form.updateTree("main");
									form.update();
								} else if (container.getCurrentEditor() instanceof ActionsForm) {
									ActionsForm form =(ActionsForm)container.getCurrentEditor();
									form.updateTree("main");
									form.update();
								}
								//dom.setFileLastModified(f.lastModified());

								//System.out.println("neu= " + f.lastModified());
								//System.out.println("neu= " + dom.getFileLastModified());
							}
						} catch (Exception e) {
							System.out.println(e.toString());
						}
					} else {
						
						if(!f.isDirectory()) {
							if(delFFiles.size() > 0) {
								dom.setFilename(null);
								dom.setChanged(true);
								
							}
						}
						
					}

			

				}
			}

		} catch(Exception e) {
			try {
				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() , e);
			} catch(Exception ee) {
				//tu nichts
			}
		} 
		flag = true;

	}

	/**

	 * @param hFfile
	 * @return
	 */
	private static String  getXPathString(File hFfile, boolean onlyParentPath) {
		String aName = "";
		String eName = "";
		String parentElementname = "";
		String attributName = "name";


		int pos1 = hFfile.getName().indexOf(".");
		int pos2 = hFfile.getName().lastIndexOf(".");
		aName = hFfile.getName().substring(0, pos1);
		eName = hFfile.getName().substring(pos1 + 1, pos2);
		if(eName.equalsIgnoreCase("order") || eName.equalsIgnoreCase("add_order") ) {
			parentElementname = "commands";
			aName = aName.substring(aName.indexOf(",")+ 1);
			attributName = "id";
		} else if(eName.equalsIgnoreCase("process_class")) {
			parentElementname = eName.concat("es");
		} else {
			parentElementname = eName.concat("s");
		}	

		if(onlyParentPath)
			return "//" + parentElementname;
		else
			return "//"+ parentElementname +"/"+ eName+"[@"+attributName+"='"+ aName + "']";
	}


}