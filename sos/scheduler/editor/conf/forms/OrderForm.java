package sos.scheduler.editor.conf.forms;

import javax.xml.transform.TransformerException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;
import org.jdom.JDOMException;
import sos.scheduler.editor.app.IUnsaved;
import sos.scheduler.editor.app.IUpdateLanguage;
import sos.scheduler.editor.app.Messages;
import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.conf.ISchedulerUpdate;
import sos.scheduler.editor.conf.SchedulerDom;
import sos.scheduler.editor.conf.listeners.OrderListener;
import sos.scheduler.editor.app.MergeAllXMLinDirectory;
import sos.scheduler.editor.app.IOUtils;



public class OrderForm extends Composite implements IUnsaved, IUpdateLanguage {
    
	private OrderListener listener   = null;

    private Group         group      = null;

    private Group         gOrder     = null;

    private Label         label10    = null;

    private Text          tTitle     = null;

    private Text          tState     = null;

    private Text          tPriority  = null;

    private Combo         cJobchain  = null;

    private Button        bReplace   = null;

    private Text          tOrderId   = null;

    private boolean       event      = false;

    private SchedulerDom  dom        = null;

    private ISchedulerUpdate main   = null;
    
    private Element       order     = null;
    
    
    public OrderForm(Composite parent, int style, SchedulerDom _dom, Element _order, ISchedulerUpdate _main)
            throws JDOMException, TransformerException {
        super(parent, style);

        dom = _dom;
        main = _main;
        order = _order;
        listener = new OrderListener(dom, order, main);
        initialize();
        setToolTipText();
        //sashForm.setWeights(new int[] { 25, 75 });

        dom.setInit(true);

        cJobchain.setItems(listener.getJobChains());

        fillOrder();
        //listener.fillParams(tParameter);
        dom.setInit(false);
        event = true;
        
        this.setEnabled(Utils.isElementEnabled("commands", dom, order));
    }


    public void apply() {
    	
        //if (isUnsaved())
        //    addParam();
    }


    public boolean isUnsaved() {
    	return false;
        //return bApply.isEnabled();
    }


    private void initialize() {
        this.setLayout(new FillLayout());
        createGroup();
        setSize(new org.eclipse.swt.graphics.Point(723, 566));
    }


    /**
     * This method initializes group
     */
    private void createGroup() {
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 1;
        group = new Group(this, SWT.NONE);

        group.setLayout(gridLayout2);

        createSashForm();
    }


    /**
     * This method initializes group1
     */
    private void createGroup1() {
        listener.setCommandAttribute("replace", "yes");

    }


    /**
     * This method initializes sashForm
     */
    private void createSashForm() {
        GridData gridData18 = new org.eclipse.swt.layout.GridData();
        gridData18.horizontalSpan = 1;
        gridData18.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData18.grabExcessHorizontalSpace = true;
        gridData18.grabExcessVerticalSpace = true;
        gridData18.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridLayout gridLayout3 = new GridLayout();
        gridLayout3.numColumns = 2;
        gOrder = new Group(group, SWT.NONE);
        final GridData gridData_10 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData_10.widthHint = 577;
        gOrder.setLayoutData(gridData_10);
        gOrder.setText("Order");
        gOrder.setLayout(gridLayout3);
        label10 = new Label(gOrder, SWT.NONE);
        label10.setLayoutData(new GridData());
        label10.setText("Order ID");

        tOrderId = new Text(gOrder, SWT.BORDER);
        tOrderId.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        tOrderId.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                /*if (event) {                	
                	listener.setOrderId(tOrderId.getText(), true, !checkName());
                    group.setText("Order: " + tOrderId.getText());
                }
                */
            	
            	if (event)
                	if(checkName())
                		listener.setCommandAttribute("id", tOrderId.getText());
                		//listener.setOrderId(tOrderId.getText(), true, rem)
            	
                /*if(tOrderId.getText() == null || tOrderId.getText().length() == 0) {
                	tOrderId.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
                } else {
                	tOrderId.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                }*/
            }
        });
        final GridData gridData_3 = new GridData(GridData.FILL, GridData.FILL, true, false);
        gridData_3.widthHint = 319;
        tOrderId.setLayoutData(gridData_3);

        final Composite composite = new Composite(gOrder, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.numColumns = 3;
        composite.setLayout(gridLayout);

        final Label jobchainLabel = new Label(composite, SWT.NONE);
        jobchainLabel.setLayoutData(new GridData(63, SWT.DEFAULT));
        jobchainLabel.setText("Job chain");

        cJobchain = new Combo(composite, SWT.NONE);
        cJobchain.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        cJobchain.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                if (event)
                	if(checkName())
                		listener.setCommandAttribute("job_chain", cJobchain.getText());
            }
        });

        if(!dom.isLifeElement()){
        	Button butBrowse = new Button(composite, SWT.NONE);
        	butBrowse.addSelectionListener(new SelectionAdapter() {
        		public void widgetSelected(final SelectionEvent e) {        		
        			String jobname = IOUtils.openDirectoryFile(MergeAllXMLinDirectory.MASK_JOB_CHAIN);
        			if(jobname != null && jobname.length() > 0) {
        				cJobchain.setText(jobname);
        			}
        		}
        	});
        	butBrowse.setText("Browse");
        }
       
       

        final Label titleLabel = new Label(gOrder, SWT.NONE);
        final GridData gridData_6 = new GridData(47, SWT.DEFAULT);
        titleLabel.setLayoutData(gridData_6);
        titleLabel.setText("Title");

        tTitle = new Text(gOrder, SWT.BORDER);
        tTitle.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                if (event)
                    listener.setCommandAttribute("title", tTitle.getText());
            }
        });

        final GridData gridData_5 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData_5.widthHint = 351;
        tTitle.setLayoutData(gridData_5);

        final Label priorityLabel = new Label(gOrder, SWT.NONE);
        priorityLabel.setLayoutData(new GridData());
        priorityLabel.setText("Priority");

        tPriority = new Text(gOrder, SWT.BORDER);
        tPriority.addVerifyListener(new VerifyListener() {
            public void verifyText(final VerifyEvent e) {
                e.doit = Utils.isOnlyDigits(e.text);
            }
        });
        tPriority.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                if (event)
                    listener.setCommandAttribute("priority", tPriority.getText());
            }
        });
        final GridData gridData_2 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData_2.widthHint = 389;
        tPriority.setLayoutData(gridData_2);

        final Label stateLabel = new Label(gOrder, SWT.NONE);
        stateLabel.setLayoutData(new GridData());
        stateLabel.setText("State");

        tState = new Text(gOrder, SWT.BORDER);
        tState.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                if (event)
                    listener.setCommandAttribute("state", tState.getText());
            }
        });
        final GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = 150;
        tState.setLayoutData(gridData);

        final Label replaceLabel = new Label(gOrder, SWT.NONE);
        final GridData gridData_4 = new GridData(58, SWT.DEFAULT);
        replaceLabel.setLayoutData(gridData_4);
        replaceLabel.setText("Replace");

        bReplace = new Button(gOrder, SWT.CHECK);
        bReplace.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                String r = "no";
                if (bReplace.getSelection())
                    r = "yes";
                if (event)
                    listener.setCommandAttribute("replace", r);
            }
        });
        final GridData gridData_9 = new GridData(28, SWT.DEFAULT);
        bReplace.setLayoutData(gridData_9);

      
        //new ParameterForm(dom, order, main, group, Editor.ORDER);
      
        createGroup1();
        createGroup2();

    }


    /**
     * This method initializes group2
     */
    private void createGroup2() {
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 1;
    }


    private void clearFields() {
        tOrderId.setText("");
        tPriority.setText("");
        cJobchain.setText("");
        tTitle.setText("");
        tState.setText("");
        bReplace.setSelection(true);
    }


    public void fillOrder() {
        clearFields();

        tOrderId.setText(listener.getCommandAttribute("id"));
        tTitle.setText(listener.getCommandAttribute("title"));
        tState.setText(listener.getCommandAttribute("state"));
        cJobchain.setText(listener.getCommandAttribute("job_chain"));
        tPriority.setText(listener.getCommandAttribute("priority"));
        bReplace.setSelection(listener.getCommandReplace());
        checkName();

    }


    public void setToolTipText() {
        tTitle.setToolTipText(Messages.getTooltip("jobcommand.title"));
        tPriority.setToolTipText(Messages.getTooltip("jobcommand.priority"));
        tState.setToolTipText(Messages.getTooltip("jobcommand.state"));
        bReplace.setToolTipText(Messages.getTooltip("jobcommand.replaceorder"));
        cJobchain.setToolTipText(Messages.getTooltip("jobcommand.jobchain"));
        tOrderId.setToolTipText(Messages.getTooltip("order.order_id"));       

    }
    
    private boolean checkName(){
    	if(listener.existName(tOrderId.getText() + "," + cJobchain.getText())) {
    		tOrderId.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
			cJobchain.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
			return false;
	
    	} else if(tOrderId.getText() == null || tOrderId.getText().length() == 0) {
        	tOrderId.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));    
        	return false;
		} else {
			tOrderId.setBackground(null);
			cJobchain.setBackground(null);
			return true;	
    	} 
    	
    	/*java.util.HashMap h = new java.util.HashMap(); 
    	if(dom.isDirectory()) {
    		java.util.List l = listener.getOrder().getParentElement().getChildren("order");
    		for(int i = 0; i < l.size(); i++) {
    			Element e = (Element)l.get(i);
    			String name = Utils.getAttributeValue("id", e) + "," + Utils.getAttributeValue("jobchain", e);
    			if(!h.containsKey(name)) {
    				h.put(name, "");
    				tOrderId.setBackground(null);
    				cJobchain.setBackground(null);
    			} else {
    				tOrderId.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
    				cJobchain.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
    				return false;
    			}
    		}
    	}
    	*/
    	
    }
    		
    	} // @jve:decl-index=0:visual-constraint="10,10"
