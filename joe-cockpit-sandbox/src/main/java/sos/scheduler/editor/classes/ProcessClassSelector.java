package sos.scheduler.editor.classes;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_JobMainComposite_BrowseProcessClass;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_JobMainComposite_ShowProcessClass;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_Cbo_JobMainComposite_ProcessClass;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_L_JobMainComposite_ProcessClass;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import sos.scheduler.editor.app.ContextMenu;

import com.sos.joe.globals.JOEConstants;
import com.sos.joe.globals.messages.ErrorLog;
import com.sos.joe.objects.job.JobListener;
import com.sos.joe.xml.IOUtils;
import com.sos.joe.xml.jobscheduler.MergeAllXMLinDirectory;

/**
* \class LanguageSelector
*
* \brief LanguageSelector -
*
* \details
*
* \code
*   .... code goes here ...
* \endcode
*
* <p style="text-align:center">
* <br />---------------------------------------------------------------------------
* <br /> APL/Software GmbH - Berlin
* <br />##### generated by ClaviusXPress (http://www.sos-berlin.com) #########
* <br />---------------------------------------------------------------------------
* </p>
* \author Uwe Risse
* \version 25.08.2011
* \see reference
*
* Created on 25.08.2011 13:54:32
 */
public class ProcessClassSelector /* extends SOSJOEMessageCodes */ {
	@SuppressWarnings("unused")
	private final String		conClassName		= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion		= "$Id$";
	@SuppressWarnings("unused")
	private final Logger		logger				= Logger.getLogger(this.getClass());
	protected JobListener		objDataProvider		= null;
	private Composite			objParent			= null;
	@SuppressWarnings("unused")
	private Label			lblProcessClass		= null;
	private SOSComboBox	cProcessClass		= null;
	private Button		butBrowse			= null;
	private Button		butShowProcessClass	= null;
	private boolean init = false;
	
	public ProcessClassSelector(final Composite parent, final int style, final JobListener pobjDataProvider) {
//		super(parent, style);
		try {
			parent.setRedraw(false);
			objDataProvider = pobjDataProvider;
			objParent = parent;
			createGroup(parent);
		}
		catch (Exception e) {
			new ErrorLog(e.getLocalizedMessage(), e);
		}
		finally {
			parent.setRedraw(true);
			parent.layout();
		}
	}

	public ProcessClassSelector(final Composite pobjComposite, final int arg1) {
//		super(pobjComposite, arg1);
	}

	private void createGroup(final Composite pobjParent) {
		pobjParent.setParent(pobjParent);
		lblProcessClass = JOE_L_JobMainComposite_ProcessClass.Control(new Label(pobjParent, SWT.NONE));
		butShowProcessClass = JOE_B_JobMainComposite_ShowProcessClass.Control(new Button(pobjParent, SWT.ARROW | SWT.DOWN));
		butShowProcessClass.setVisible(objDataProvider.get_dom() != null && !objDataProvider.get_dom().isLifeElement());
		butShowProcessClass.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(final SelectionEvent e) {
				String strT = cProcessClass.getText();
				if (strT.length() > 0) {
					ContextMenu.goTo(strT, objDataProvider.get_dom(), JOEConstants.PROCESS_CLASSES);
				}
			}
		});
		butShowProcessClass.setAlignment(SWT.RIGHT);
		butShowProcessClass.setVisible(true);
		//
		cProcessClass = new SOSComboBox(pobjParent, JOE_Cbo_JobMainComposite_ProcessClass);
		cProcessClass.setEditable(false);
		cProcessClass.setMenu(new ContextMenu(cProcessClass, objDataProvider.get_dom(), JOEConstants.PROCESS_CLASSES).getMenu());
		cProcessClass.addModifyListener(new ModifyListener() {
			@Override public void modifyText(final ModifyEvent e) {
				if (init) {
					return;
				}
				objDataProvider.setProcessClass(cProcessClass.getText());
				butShowProcessClass.setVisible(true);
			}
		});
		cProcessClass.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 3, 1));
		cProcessClass.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(final SelectionEvent e) {
				if (init) {
					return;
				}
				objDataProvider.setProcessClass(cProcessClass.getText());
			}
		});
		cProcessClass.addKeyListener(new KeyListener() {
			@Override public void keyPressed(final KeyEvent event) {
				if (event.keyCode == SWT.F1) {
					objDataProvider.openXMLAttributeDoc("job", "process_class");
				}
				if (event.keyCode == SWT.F10) {
					objDataProvider.openXMLDoc("job");
				}
			}

			@Override public void keyReleased(final KeyEvent arg0) {
			}
		});
		cProcessClass.addMouseListener(new MouseListener() {
			@Override public void mouseUp(final MouseEvent arg0) {
			}

			@Override public void mouseDown(final MouseEvent arg0) {
			}

			@Override public void mouseDoubleClick(final MouseEvent arg0) {
				String strT = cProcessClass.getText();
				if (strT.length() > 0) {
					ContextMenu.goTo(strT, objDataProvider.get_dom(), JOEConstants.PROCESS_CLASSES);
				}
			}
		});
		//
		butBrowse = JOE_B_JobMainComposite_BrowseProcessClass.Control(new Button(pobjParent, SWT.NONE));
		butBrowse.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(final SelectionEvent e) {
				String name = IOUtils.getJobschedulerObjectPathName(MergeAllXMLinDirectory.MASK_PROCESS_CLASS);
				if (name != null && name.length() > 0)
					cProcessClass.setText(name);
			}
		});
	}

	public void init() {
		init = true;
		cProcessClass.setItems(objDataProvider.getProcessClasses());
		String process_class = objDataProvider.getProcessClass();
		cProcessClass.setText(process_class);
		init = false;
	}

//	@Override protected void checkSubclass() {
//		// Disable the check that prevents subclassing of SWT components
//	}
}