package sos.scheduler.editor.conf.forms;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import sos.scheduler.editor.app.MainWindow;
import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.conf.listeners.JobListener;
import sos.scheduler.editor.conf.listeners.ParameterListener;
import sos.util.SOSString;

import com.sos.dialog.swtdesigner.SWTResourceManager;
import com.sos.joe.globals.JOEConstants;
import com.sos.joe.globals.interfaces.ISchedulerUpdate;
import com.sos.joe.globals.interfaces.IUnsaved;
import com.sos.joe.globals.messages.ErrorLog;
import com.sos.joe.globals.messages.SOSJOEMessageCodes;
import com.sos.joe.globals.misc.ResourceManager;
import com.sos.joe.globals.options.Options;
import com.sos.joe.wizard.forms.JobAssistentImportJobParamsForm;
import com.sos.joe.wizard.forms.JobAssistentImportJobsForm;
import com.sos.joe.xml.IOUtils;
import com.sos.joe.xml.jobscheduler.SchedulerDom;

public class ParameterForm extends SOSJOEMessageCodes implements IUnsaved {

    private Button butDown_1 = null;
    private Button butUp_1 = null;
    private Button butImport_1 = null;
    private Label label4_3 = null;
    private Label label4_1 = null;
    private ParameterListener listener = null;
    private Group gJobParameter = null;
    private Table tParameter = null;
    private Button bRemove = null;
    private Label label2 = null;
    private Text tParaName = null;
    private Label label6 = null;
    private Text tParaValue = null;
    private Button bApply = null;
    private Label label4 = null;
    private Text txtParameterDescription = null;
    private Table tableEnvironment = null;
    private Text txtEnvName = null;
    private Text txtEnvValue = null;
    private Button butEnvApply = null;
    private Button butEnvRemove = null;
    private Text txtIncludeFilename = null;
    private Text txtIncludeNode = null;
    private Table tableIncludeParams = null;
    private Button butIncludesApply = null;
    private Button butImport = null;
    private Button butOpenInclude = null;
    private Button butRemoveInclude = null;
    private CTabFolder tabFolder = null;
    private SOSString sosString = null;
    private SchedulerDom dom = null;
    private CTabItem includeParameterTabItem = null;
    private CTabItem parameterTabItem = null;
    private CTabItem environmentTabItem = null;
    private int type = -1;
    private Combo cSource = null;
    private CTabItem parameterJobCmdTabItem = null;
    private Group group = null;
    private String includeFile = null;
    private Button butNewIncludes = null;
    private Button butIsLifeFile = null;
    private Button butDown = null;
    private Button butUp = null;
    private Button butNewParam = null;
    private Button butNewEnvironment = null;
    private Button butoIncludeSave = null;
    private boolean isRemoteConnection = false;
    public static String WIZARD = "Wizard";

    public ParameterForm(Composite parent, int style, SchedulerDom _dom, Element parentElem, ISchedulerUpdate main, int type_) throws JDOMException {
        super(parent, style);
        dom = _dom;
        type = type_;
        listener = new ParameterListener(dom, parentElem, main, type_);
        initialize();
    }

    public ParameterForm(Composite parent, int style, SchedulerDom _dom, Element parentElem, ISchedulerUpdate main, int type_, String jobname)
            throws JDOMException {
        super(parent, style);
        dom = _dom;
        type = type_;
        listener = new ParameterListener(dom, parentElem, main, type_);
        listener.setJobname(jobname);
        initialize();
    }

    private void initialize() {
        sosString = new SOSString();
        try {
            isRemoteConnection = !sosString.parseToString(MainWindow.getContainer().getCurrentTab().getData("ftp_title")).isEmpty();
        } catch (Exception e) {
        }
        this.setLayout(new GridLayout());
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 1;
        gJobParameter = JOE_G_ParameterForm_JobParameter.control(new Group(this, SWT.NONE));
        gJobParameter.setLayout(gridLayout2);
        final GridData gridData_1 = new GridData(GridData.FILL, GridData.FILL, true, true);
        gJobParameter.setLayoutData(gridData_1);
        createParameterGroup();
        getDescription();
        initForm();
    }

    @Override
    public void apply() {
        if (isUnsaved()) {
            addParam();
        }
    }

    @Override
    public boolean isUnsaved() {
        return bApply.isEnabled();
    }

    public void createParameterGroup() {
        tabFolder = new CTabFolder(gJobParameter, SWT.BORDER);
        final GridData gridData_2 = new GridData(GridData.FILL, GridData.FILL, true, true);
        gridData_2.heightHint = 203;
        gridData_2.widthHint = 760;
        tabFolder.setLayoutData(gridData_2);
        if (type == JOEConstants.JOB_COMMANDS) {
            createJobCommandParameter();
        } else {
            createParameter();
        }
        if (type == JOEConstants.JOB || type == JOEConstants.JOB_COMMANDS) {
            createEnvironment();
        }
        if ((type != JOEConstants.WEBSERVICE) && (type != JOEConstants.CONFIG)) {
            createIncludes();
        }
        tabFolder.setSelection(0);
        if (tParaName != null) {
            tParaName.setFocus();
        }
    }

    private void addParam() {
        if (!"".equals(tParaName.getText())) {
            listener.saveParameter(tParameter, tParaName.getText().trim(), tParaValue.getText());
        }
        tParaName.setText("");
        tParaValue.setText("");
        bRemove.setEnabled(false);
        tParameter.deselectAll();
        tParaName.setFocus();
    }

    private void addEnvironment() {
        listener.saveEnvironment(tableEnvironment, txtEnvName.getText().trim(), txtEnvValue.getText());
        txtEnvName.setText("");
        txtEnvValue.setText("");
        butEnvRemove.setEnabled(false);
        butEnvApply.setEnabled(false);
        tableEnvironment.deselectAll();
        txtEnvName.setFocus();
    }

    private void addInclude() {
        listener.saveIncludeParams(tableIncludeParams, txtIncludeFilename.getText().trim(), txtIncludeNode.getText(), type == JOEConstants.JOB
                || type == JOEConstants.COMMANDS || type == JOEConstants.JOB_COMMANDS && butIsLifeFile.getSelection() ? butIsLifeFile.getSelection()
                : false);
        txtIncludeFilename.setText("");
        txtIncludeNode.setText("");
        butIncludesApply.setEnabled(false);
        butRemoveInclude.setEnabled(false);
        butOpenInclude.setEnabled(false);
        tableIncludeParams.deselectAll();
        txtIncludeFilename.setFocus();
        if (type == JOEConstants.JOB || type == JOEConstants.COMMANDS || type == JOEConstants.JOB_COMMANDS) {
            butIsLifeFile.setSelection(false);
        }
    }

    public void initForm() {
        tParameter.removeAll();
        if (includeFile != null
                && !includeFile.trim().isEmpty()
                && (new File(Options.getSchedulerData().endsWith("/") || Options.getSchedulerData().endsWith("\\") ? Options.getSchedulerData()
                        : Options.getSchedulerData() + "/" + includeFile).exists())) {
            listener.getAllParameterDescription();
        }
        listener.fillParams(tParameter);
        listener.fillEnvironment(tableEnvironment);
        listener.fillIncludeParams(tableIncludeParams);
    }

    private void startWizzard() {
        Utils.startCursor(getShell());
        if (includeFile != null && !includeFile.trim().isEmpty()) {
            JobAssistentImportJobParamsForm paramsForm =
                    new JobAssistentImportJobParamsForm(listener.get_dom(), listener.get_main(), new JobListener(dom, listener.getParent(),
                            listener.get_main()), tParameter, JOEConstants.PARAMETER);
            paramsForm.showAllImportJobParams(includeFile);
            listener.fillIncludeParams(tableIncludeParams);
        } else {
            JobAssistentImportJobsForm importParameterForms =
                    new JobAssistentImportJobsForm(new JobListener(dom, listener.getParent(), listener.get_main()), tParameter,
                            JOEConstants.PARAMETER);
            importParameterForms.showAllImportJobs();
        }
        Utils.stopCursor(getShell());
    }

    public Table getTParameter() {
        return tParameter;
    }

    private void createParameterTabItem() {
        Element params = null;
        final String node = txtIncludeNode.getText();
        try {
            String f = txtIncludeFilename.getText();
            boolean fNotExist = false;
            if (!new File(f).exists()) {
                String data = ".";
                if ((dom.isDirectory() || dom.isLifeElement()) && butIsLifeFile.getSelection()) {
                    if (f.startsWith("/") || f.startsWith("\\")) {
                        data = Options.getSchedulerHotFolder();
                    } else if (dom.getFilename() != null) {
                        if (dom.isLifeElement()) {
                            data = new File(dom.getFilename()).getParent();
                        } else {
                            data = new File(dom.getFilename()).getPath();
                        }
                    }
                } else {
                    if (butIsLifeFile != null && butIsLifeFile.getSelection()) {
                        data = Options.getSchedulerHotFolder();
                    } else {
                        data = Options.getSchedulerData();
                    }
                }
                f = ((data.endsWith("/") || data.endsWith("\\") ? data : data + "/") + f);
                if (!new File(f).exists()) {
                    fNotExist = true;
                }
            }
            if (fNotExist) {
                MainWindow.message(JOE_M_ParameterForm_FileNotExist.params(f), SWT.ICON_WARNING);
                return;
            }
            final String filename = f;
            for (int i = 0; i < tabFolder.getItemCount(); i++) {
                if (sosString.parseToString(tabFolder.getItem(i).getData("filename")).equals(filename)
                        && sosString.parseToString(tabFolder.getItem(i).getData("node")).equals(node)) {
                    tabFolder.setSelection(tabFolder.getItem(i));
                    return;
                }
            }
            SAXBuilder builder = new SAXBuilder();
            final Document doc = builder.build(filename);
            java.util.List listOfElement = null;
            if (txtIncludeNode.getText() != null && !txtIncludeNode.getText().isEmpty()) {
                XPath x = XPath.newInstance(txtIncludeNode.getText());
                listOfElement = x.selectNodes(doc);
            } else {
                listOfElement = new java.util.ArrayList();
                params = doc.getRootElement();
                if (params != null) {
                    listOfElement = params.getChildren("param");
                }
            }
            java.util.HashMap hash = new java.util.HashMap();
            for (int i = 0; i < listOfElement.size(); i++) {
                Element param = (Element) listOfElement.get(i);
                if (hash.containsKey(Utils.getAttributeValue("name", param))) {
                    MainWindow.message(JOE_M_ParameterForm_NoDistinctParam.params(Utils.getAttributeValue("name", param)), SWT.ICON_WARNING);
                    return;
                }
                hash.put(Utils.getAttributeValue("name", param), "");
            }
            includeParameterTabItem = new CTabItem(tabFolder, SWT.CLOSE);
            includeParameterTabItem.setText(new File(filename).getName());
            includeParameterTabItem.setData("filename", filename);
            includeParameterTabItem.setData("node", node);
            includeParameterTabItem.setData("doc", doc);
            includeParameterTabItem.setData("params", listOfElement);
            final Group group_1 = new Group(tabFolder, SWT.NONE);
            group_1.setText(txtIncludeFilename.getText());
            final GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 5;
            group_1.setLayout(gridLayout);
            includeParameterTabItem.setControl(group_1);
            label2 = JOE_L_Name.control(new Label(group_1, SWT.NONE));
            final Text txtIncludeParameter = new Text(group_1, SWT.BORDER);
            final GridData gridData_4 = new GridData(GridData.FILL, GridData.CENTER, true, false);
            txtIncludeParameter.setLayoutData(gridData_4);
            txtIncludeParameter.addModifyListener(new org.eclipse.swt.events.ModifyListener() {

                @Override
                public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                    butoIncludeSave.setEnabled(!"".equals(txtIncludeParameter.getText()));
                }
            });
            label6 = JOE_L_ParameterForm_Value.control(new Label(group_1, SWT.NONE));
            label6.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
            final Text txtIncludeParameterValue = new Text(group_1, SWT.BORDER);
            final GridData gridData_9 = new GridData(GridData.FILL, GridData.CENTER, true, false);
            txtIncludeParameterValue.setLayoutData(gridData_9);
            txtIncludeParameterValue.addModifyListener(new org.eclipse.swt.events.ModifyListener() {

                @Override
                public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                    butoIncludeSave.setEnabled(!"".equals(txtIncludeParameter.getText()));
                }
            });
            butoIncludeSave = JOE_B_ParameterForm_IncludeSave.control(new Button(group_1, SWT.NONE));
            butoIncludeSave.setEnabled(false);
            final GridData gridData_7 = new GridData(GridData.FILL, GridData.CENTER, false, false);
            gridData_7.widthHint = 36;
            butoIncludeSave.setLayoutData(gridData_7);
            label4 = new Label(group_1, SWT.SEPARATOR | SWT.HORIZONTAL);
            label4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 5, 1));
            final Table tableIncludeParameter = new Table(group_1, SWT.BORDER | SWT.FULL_SELECTION);
            final GridData gridData_1 = new GridData(GridData.FILL, GridData.FILL, true, true, 4, 3);
            gridData_1.heightHint = 85;
            tableIncludeParameter.setLayoutData(gridData_1);
            tableIncludeParameter.setHeaderVisible(true);
            tableIncludeParameter.setLinesVisible(true);
            TableColumn tcName = JOE_TCl_ParameterForm_Name.control(new TableColumn(tableIncludeParameter, SWT.NONE));
            tcName.setWidth(132);
            TableColumn tcValue = JOE_TCl_ParameterForm_Value.control(new TableColumn(tableIncludeParameter, SWT.NONE));
            tcValue.setWidth(450);
            for (int i = 0; i < listOfElement.size(); i++) {
                Element param = (Element) listOfElement.get(i);
                TableItem item = new TableItem(tableIncludeParameter, SWT.NONE);
                item.setText(0, Utils.getAttributeValue("name", param));
                item.setText(1, Utils.getAttributeValue("value", param));
                item.setData("param", param);
            }
            final Button newButton = JOE_B_ParameterForm_New.control(new Button(group_1, SWT.NONE));
            newButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
            final Button butIncludeRemove = JOE_B_ParameterForm_IncludeRemove.control(new Button(group_1, SWT.NONE));
            final GridData gridData_8 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
            butIncludeRemove.setLayoutData(gridData_8);
            butIncludeRemove.setEnabled(false);
            butIncludeRemove.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

                @Override
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                    updateIncludeParam(includeParameterTabItem, false, tableIncludeParameter, txtIncludeParameter, txtIncludeParameterValue,
                            butIncludeRemove);
                }
            });
            if (type == JOEConstants.JOB) {
                butImport = JOE_B_ParameterForm_Wizard.control(new Button(group_1, SWT.NONE));
                butImport.setVisible(false);
                butImport.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
                butImport.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent e) {
                        JobAssistentImportJobsForm importParameterForms =
                                new JobAssistentImportJobsForm(new JobListener(dom, listener.getParent(), listener.get_main()),
                                        tableIncludeParameter, JOEConstants.JOB);
                        importParameterForms.showAllImportJobs();
                    }
                });
            }
            txtIncludeParameterValue.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {

                @Override
                public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
                    if (e.keyCode == SWT.CR && !"".equals(txtIncludeParameter.getText().trim())) {
                        updateIncludeParam(includeParameterTabItem, true, tableIncludeParameter, txtIncludeParameter, txtIncludeParameterValue,
                                butIncludeRemove);
                    }
                }
            });
            txtIncludeParameter.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {

                @Override
                public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
                    if (e.keyCode == SWT.CR && !"".equals(txtIncludeParameter)) {
                        updateIncludeParam(includeParameterTabItem, true, tableIncludeParameter, txtIncludeParameter, txtIncludeParameterValue,
                                butIncludeRemove);
                    }
                }
            });
            butoIncludeSave.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

                @Override
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                    updateIncludeParam(includeParameterTabItem, true, tableIncludeParameter, txtIncludeParameter, txtIncludeParameterValue,
                            butIncludeRemove);
                }
            });
            tableIncludeParameter.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

                @Override
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                    TableItem item = (TableItem) e.item;
                    if (item == null) {
                        return;
                    }
                    txtIncludeParameter.setText(item.getText(0));
                    txtIncludeParameterValue.setText(item.getText(1));
                    butIncludeRemove.setEnabled(tableIncludeParameter.getSelectionCount() > 0);
                    butoIncludeSave.setEnabled(false);
                }
            });
            newButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    txtIncludeParameter.setText("");
                    txtIncludeParameterValue.setText("");
                    butIncludeRemove.setEnabled(false);
                    tableIncludeParameter.deselectAll();
                    txtIncludeParameter.setFocus();
                }
            });
            boolean hasXPathExpression = txtIncludeNode.getText().trim().isEmpty();
            butoIncludeSave.setVisible(hasXPathExpression);
            butIncludeRemove.setVisible(hasXPathExpression);
            txtIncludeParameter.setEnabled(hasXPathExpression);
            txtIncludeParameterValue.setEnabled(hasXPathExpression);
            newButton.setEnabled(hasXPathExpression);
            tabFolder.setSelection(includeParameterTabItem);
        } catch (Exception e) {
            new ErrorLog(JOE_E_0002.params(sos.util.SOSClassUtil.getMethodName()), e);
            MainWindow.message(JOE_E_0010.label() + e.getMessage(), SWT.ICON_WARNING);
        }
    }

    private void updateIncludeParam(CTabItem includeParameterTabItem, boolean add, Table tableIncludeParameter, Text txtIncludeParameter,
            Text txtIncludeParameterValue, Button butIncludeRemove) {
        Document doc = (Document) includeParameterTabItem.getData("doc");
        String filename = (String) includeParameterTabItem.getData("filename");
        List listOfElement = (List) includeParameterTabItem.getData("params");
        if (add) {
            boolean found = false;
            for (int i = 0; i < tableIncludeParameter.getItemCount(); i++) {
                TableItem item = tableIncludeParameter.getItem(i);
                if (item.getText(0).equals(txtIncludeParameter.getText())) {
                    found = true;
                    item.setText(0, txtIncludeParameter.getText());
                    item.setText(1, txtIncludeParameterValue.getText());
                    Element param = (Element) item.getData("param");
                    param.setAttribute("name", item.getText(0));
                    param.setAttribute("value", item.getText(1));
                }
            }
            if (!found) {
                TableItem item = new TableItem(tableIncludeParameter, SWT.NONE);
                item.setText(0, txtIncludeParameter.getText());
                item.setText(1, txtIncludeParameterValue.getText());
                Element param = new Element("param");
                param.setAttribute("name", item.getText(0));
                param.setAttribute("value", item.getText(1));
                item.setData("param", param);
                listOfElement.add(param);
                includeParameterTabItem.setData("params", listOfElement);
            }
        } else if (tableIncludeParameter.getSelectionCount() > 0) {
            Element param = (Element) tableIncludeParameter.getSelection()[0].getData("param");
            Element params = ((Element) listOfElement.get(0)).getParentElement();
            params.removeContent(param);
            listOfElement = params.getChildren("param");
            tableIncludeParameter.remove(tableIncludeParameter.getSelectionIndex());
            includeParameterTabItem.setData("params", listOfElement);
        }
        IOUtils.saveXML(doc, filename);
        txtIncludeParameter.setText("");
        txtIncludeParameterValue.setText("");
        butIncludeRemove.setEnabled(false);
        tableIncludeParameter.deselectAll();
        txtIncludeParameter.setFocus();
    }

    private void createParameter() {
        parameterTabItem = JOE_TI_ParameterForm_TabItemParameter.control(new CTabItem(tabFolder, SWT.BORDER));
        final Group Group = new Group(tabFolder, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        Group.setLayout(gridLayout);
        parameterTabItem.setControl(Group);
        label2 = JOE_L_Name.control(new Label(Group, SWT.NONE));
        tParaName = JOE_T_ParameterForm_ParamName.control(new Text(Group, SWT.BORDER));
        final GridData gridData_4 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        tParaName.setLayoutData(gridData_4);
        tParaName.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {

            @Override
            public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(tParaName.getText())) {
                    addParam();
                }
            }
        });
        tParaName.addModifyListener(new org.eclipse.swt.events.ModifyListener() {

            @Override
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                bApply.setEnabled(!"".equals(tParaName.getText().trim()));
            }
        });
        label6 = JOE_L_ParameterForm_Value.control(new Label(Group, SWT.NONE));
        label6.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        tParaValue = JOE_T_ParameterForm_ParamValue.control(new Text(Group, SWT.BORDER));
        final GridData gridData_9 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        tParaValue.setLayoutData(gridData_9);
        tParaValue.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {

            @Override
            public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(tParaName.getText().trim())) {
                    addParam();
                }
            }
        });
        tParaValue.addModifyListener(new org.eclipse.swt.events.ModifyListener() {

            @Override
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                bApply.setEnabled(!"".equals(tParaName.getText()));
            }
        });
        final Button button = JOE_B_ParameterForm_Comment.control(new Button(Group, SWT.NONE));
        final GridData gridDatax = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
        gridDatax.widthHint = 28;
        button.setLayoutData(gridDatax);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                String text = sos.scheduler.editor.app.Utils.showClipboard(tParaValue.getText(), getShell(), true, "");
                if (text != null) {
                    tParaValue.setText(text);
                }
            }
        });
        button.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/icon_edit.gif"));
        bApply = JOE_B_ParameterForm_Apply.control(new Button(Group, SWT.NONE));
        bApply.setEnabled(false);
        final GridData gridData_7 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        gridData_7.widthHint = 36;
        bApply.setLayoutData(gridData_7);
        bApply.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                addParam();
            }
        });
        label4 = new Label(Group, SWT.SEPARATOR | SWT.HORIZONTAL);
        label4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 6, 1));
        tParameter = JOE_Tbl_ParameterForm_Parameter.control(new Table(Group, SWT.FULL_SELECTION | SWT.BORDER));
        tParameter.setLinesVisible(true);
        final GridData gridData_1 = new GridData(GridData.FILL, GridData.FILL, true, true, 5, 4);
        tParameter.setLayoutData(gridData_1);
        tParameter.setHeaderVisible(true);
        tParameter.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                if (bApply.isEnabled()) {
                    int ok = MainWindow.message(JOE_M_ApplyChanges.label(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
                    if (ok == SWT.YES) {
                        addParam();
                        return;
                    }
                }
                TableItem item = (TableItem) e.item;
                if (item == null) {
                    return;
                }
                tParaName.setText(item.getText(0));
                tParaValue.setText(item.getText(1));
                bRemove.setEnabled(tParameter.getSelectionCount() > 0);
                if (type == JOEConstants.JOB) {
                    try {
                        String s = sosString.parseToString(item.getData("parameter_description_" + Options.getLanguage()));
                        s = s.replaceAll("\\<.*?>", "");
                        txtParameterDescription.setText(s);
                    } catch (Exception ew) {
                    }
                }
                bApply.setEnabled(false);
            }
        });
        TableColumn tcName = JOE_TCl_ParameterForm_Name.control(new TableColumn(tParameter, SWT.NONE));
        tcName.setWidth(262);
        TableColumn tcValue = JOE_TCl_ParameterForm_Value.control(new TableColumn(tParameter, SWT.NONE));
        tcValue.setWidth(500);
        butNewParam = JOE_B_ParameterForm_NewParam.control(new Button(Group, SWT.NONE));
        butNewParam.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                tParaName.setText("");
                tParaValue.setText("");
                bRemove.setEnabled(false);
                tParameter.deselectAll();
                tParaName.setFocus();
            }
        });
        butNewParam.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        final Composite composite = new Composite(Group, SWT.NONE);
        final GridData gridData_2 = new GridData(GridData.CENTER, GridData.CENTER, false, false);
        gridData_2.heightHint = 67;
        composite.setLayoutData(gridData_2);
        composite.setLayout(new GridLayout());
        butUp = JOE_B_Up.control(new Button(composite, SWT.NONE));
        butUp.setText("");
        butUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                listener.changeUp(tParameter);
            }
        });
        butUp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        butUp.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/icon_up.gif"));
        butDown = JOE_B_Down.control(new Button(composite, SWT.NONE));
        butDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                listener.changeDown(tParameter);
            }
        });
        butDown.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
        butDown.setText("");
        butDown.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/icon_down.gif"));
        butImport = JOE_B_ParameterForm_Wizard.control(new Button(Group, SWT.NONE));
        butImport.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        butImport.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                startWizzard();
                tParaName.setFocus();
            }
        });
        bRemove = JOE_B_ParameterForm_Remove.control(new Button(Group, SWT.NONE));
        final GridData gridData_8 = new GridData(GridData.FILL, GridData.BEGINNING, false, true);
        bRemove.setLayoutData(gridData_8);
        bRemove.setEnabled(false);
        bRemove.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                int index = tParameter.getSelectionIndex();
                listener.deleteParameter(tParameter, index);
                tParaName.setText("");
                tParaValue.setText("");
                tParameter.deselectAll();
                bRemove.setEnabled(false);
                bApply.setEnabled(false);
                if (index >= tParameter.getItemCount()) {
                    index--;
                }
                if (index >= 0) {
                    tParameter.select(index);
                    tParameter.setSelection(index);
                    setParams(tParameter.getItem(index));
                }
            }
        });
        if (type == JOEConstants.JOB) {
            txtParameterDescription =
                    JOE_T_ParameterForm_ParamDescription.control(new Text(Group, SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP
                            | SWT.H_SCROLL));
            final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 5, 1);
            gridData.minimumHeight = 100;
            txtParameterDescription.setLayoutData(gridData);
            txtParameterDescription.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(final FocusEvent e) {
                    tParaName.setFocus();
                }
            });
            txtParameterDescription.setEditable(false);
            txtParameterDescription.setBackground(SWTResourceManager.getColor(247, 247, 247));
            new Label(Group, SWT.NONE);
            tParaName.setFocus();
        }
    }

    private void createEnvironment() {
        environmentTabItem = JOE_TI_ParameterForm_TabItemEnvironment.control(new CTabItem(tabFolder, SWT.BORDER));
        final Group group_2 = new Group(tabFolder, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 5;
        group_2.setLayout(gridLayout_1);
        environmentTabItem.setControl(group_2);
        final Label nameLabel = JOE_L_Name.control(new Label(group_2, SWT.NONE));
        txtEnvName = JOE_T_ParameterForm_EnvName.control(new Text(group_2, SWT.BORDER));
        txtEnvName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                butEnvApply.setEnabled(!"".equals(txtEnvName.getText().trim()));
            }
        });
        txtEnvName.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(txtEnvName)) {
                    addEnvironment();
                }
            }
        });
        final GridData gridData_5 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        txtEnvName.setLayoutData(gridData_5);
        final Label valueLabel = JOE_L_ParameterForm_Value.control(new Label(group_2, SWT.NONE));
        valueLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        txtEnvValue = JOE_T_ParameterForm_EnvValue.control(new Text(group_2, SWT.BORDER));
        txtEnvValue.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                butEnvApply.setEnabled(!"".equals(txtEnvName.getText().trim()));
            }
        });
        txtEnvValue.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(txtEnvName)) {
                    addEnvironment();
                }
            }
        });
        txtEnvValue.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        butEnvApply = JOE_B_ParameterForm_EnvApply.control(new Button(group_2, SWT.NONE));
        butEnvApply.setEnabled(false);
        butEnvApply.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                addEnvironment();
            }
        });
        final GridData gridData_6 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        butEnvApply.setLayoutData(gridData_6);
        label4_1 = new Label(group_2, SWT.HORIZONTAL | SWT.SEPARATOR);
        label4_1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 5, 1));
        tableEnvironment = JOE_Tbl_ParameterForm_Environment.control(new Table(group_2, SWT.FULL_SELECTION | SWT.BORDER));
        tableEnvironment.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                TableItem item = (TableItem) e.item;
                if (item == null) {
                    return;
                }
                setEnvironment(item);
            }
        });
        tableEnvironment.setLinesVisible(true);
        tableEnvironment.setHeaderVisible(true);
        tableEnvironment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 4, 2));
        final TableColumn colEnvName = JOE_TCl_ParameterForm_Name.control(new TableColumn(tableEnvironment, SWT.NONE));
        colEnvName.setWidth(250);
        final TableColumn colEnvValue = JOE_TCl_ParameterForm_Value.control(new TableColumn(tableEnvironment, SWT.NONE));
        colEnvValue.setWidth(522);
        butNewEnvironment = JOE_B_ParameterForm_NewEnv.control(new Button(group_2, SWT.NONE));
        butNewEnvironment.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                txtEnvName.setText("");
                txtEnvValue.setText("");
                butEnvRemove.setEnabled(false);
                tableEnvironment.deselectAll();
                txtEnvName.setFocus();
            }
        });
        butNewEnvironment.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        butEnvRemove = JOE_B_ParameterForm_RemoveEnv.control(new Button(group_2, SWT.NONE));
        butEnvRemove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                int index = tableEnvironment.getSelectionIndex();
                listener.deleteEnvironment(tableEnvironment, index);
                txtEnvName.setText("");
                txtEnvValue.setText("");
                tableEnvironment.deselectAll();
                butEnvApply.setEnabled(false);
                butEnvRemove.setEnabled(false);
                txtEnvName.setFocus();
                if (index >= tableEnvironment.getItemCount()) {
                    index--;
                }
                if (index >= 0) {
                    tableEnvironment.setSelection(index);
                    tableEnvironment.select(index);
                    setEnvironment(tableEnvironment.getItem(index));
                }
            }
        });
        final GridData gridData_3 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        butEnvRemove.setLayoutData(gridData_3);
        txtEnvName.setFocus();
    }

    private void createIncludes() {
        final CTabItem includesTabItem = JOE_TI_ParameterForm_TabItemIncludes.control(new CTabItem(tabFolder, SWT.BORDER));
        final Group group_3 = new Group(tabFolder, SWT.NONE);
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 5;
        group_3.setLayout(gridLayout_2);
        includesTabItem.setControl(group_3);
        if (type == JOEConstants.JOB || type == JOEConstants.COMMANDS || type == JOEConstants.JOB_COMMANDS) {
            butIsLifeFile = JOE_B_ParameterForm_LifeFile.control(new Button(group_3, SWT.CHECK));
            butIsLifeFile.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    butIncludesApply.setEnabled(!"".equals(txtIncludeFilename.getText().trim()));
                }
            });
        } else {
            final Label lblNode_ = JOE_L_ParameterForm_File.control(new Label(group_3, SWT.NONE));
        }
        txtIncludeFilename = JOE_T_ParameterForm_IncludeFilename.control(new Text(group_3, SWT.BORDER));
        txtIncludeFilename.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                butIncludesApply.setEnabled(!"".equals(txtIncludeFilename.getText().trim()));
                if (type == JOEConstants.JOB || type == JOEConstants.COMMANDS || type == JOEConstants.JOB_COMMANDS) {
                    butIsLifeFile.setEnabled(!"".equals(txtIncludeFilename.getText().trim()));
                }
            }
        });
        txtIncludeFilename.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(txtIncludeFilename)) {
                    addInclude();
                }
            }
        });
        txtIncludeFilename.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        final Label lblNode = JOE_L_ParameterForm_Node.control(new Label(group_3, SWT.NONE));
        txtIncludeNode = JOE_T_ParameterForm_IncludeNode.control(new Text(group_3, SWT.BORDER));
        txtIncludeNode.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                butIncludesApply.setEnabled(!"".equals(txtIncludeFilename.getText().trim()));
                if (type == JOEConstants.JOB || type == JOEConstants.COMMANDS || type == JOEConstants.JOB_COMMANDS) {
                    butIsLifeFile.setEnabled(!"".equals(txtIncludeFilename.getText().trim()));
                }
            }
        });
        txtIncludeNode.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(txtIncludeFilename)) {
                    addInclude();
                }
            }
        });
        txtIncludeNode.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        butIncludesApply = JOE_B_ParameterForm_IncludesApply.control(new Button(group_3, SWT.NONE));
        butIncludesApply.setEnabled(false);
        butIncludesApply.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                addInclude();
            }
        });
        butIncludesApply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        label4_3 = new Label(group_3, SWT.HORIZONTAL | SWT.SEPARATOR);
        label4_3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 5, 1));
        tableIncludeParams = JOE_Tbl_ParameterForm_IncludeParams.control(new Table(group_3, SWT.FULL_SELECTION | SWT.BORDER));
        tableIncludeParams.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(final MouseEvent e) {
                if (!isRemoteConnection) {
                    createParameterTabItem();
                }
            }
        });
        tableIncludeParams.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                TableItem item = (TableItem) e.item;
                if (item == null) {
                    return;
                }
                setInclude(item);
            }
        });
        tableIncludeParams.setLinesVisible(true);
        tableIncludeParams.setHeaderVisible(true);
        tableIncludeParams.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 4, 3));
        final TableColumn colParamColums = JOE_TCl_ParameterForm_File.control(new TableColumn(tableIncludeParams, SWT.NONE));
        colParamColums.setWidth(250);
        final TableColumn newColumnTableColumn_1 = JOE_TCl_ParameterForm_Node.control(new TableColumn(tableIncludeParams, SWT.NONE));
        newColumnTableColumn_1.setWidth(400);
        final TableColumn newColumnTableColumn = JOE_TCl_ParameterForm_LiveFile.control(new TableColumn(tableIncludeParams, SWT.NONE));
        newColumnTableColumn.setWidth(100);
        if (type != JOEConstants.JOB && type != JOEConstants.COMMANDS && type != JOEConstants.JOB_COMMANDS) {
            newColumnTableColumn.setWidth(200);
            newColumnTableColumn.setResizable(false);
        }
        butNewIncludes = JOE_B_ParameterForm_NewIncludes.control(new Button(group_3, SWT.NONE));
        butNewIncludes.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                tableIncludeParams.deselectAll();
                txtIncludeFilename.setText("");
                txtIncludeNode.setText("");
                if (type == JOEConstants.JOB || type == JOEConstants.COMMANDS || type == JOEConstants.JOB_COMMANDS) {
                    butIsLifeFile.setSelection(false);
                }
                butIncludesApply.setEnabled(false);
                butOpenInclude.setEnabled(false);
                butRemoveInclude.setEnabled(false);
                txtIncludeFilename.setFocus();
            }
        });
        butNewIncludes.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        butOpenInclude = JOE_B_ParameterForm_OpenInclude.control(new Button(group_3, SWT.NONE));
        butOpenInclude.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                createParameterTabItem();
            }
        });
        butOpenInclude.setEnabled(false);
        butOpenInclude.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        butRemoveInclude = JOE_B_ParameterForm_RemoveInclude.control(new Button(group_3, SWT.NONE));
        butRemoveInclude.setEnabled(false);
        butRemoveInclude.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                int index = tableIncludeParams.getSelectionIndex();
                listener.deleteIncludeParams(tableIncludeParams, tableIncludeParams.getSelectionIndex());
                txtIncludeFilename.setText("");
                txtIncludeNode.setText("");
                tableIncludeParams.deselectAll();
                butIncludesApply.setEnabled(false);
                butRemoveInclude.setEnabled(false);
                txtIncludeFilename.setFocus();
                if (index >= tableIncludeParams.getItemCount()) {
                    index--;
                }
                if (index >= 0) {
                    tableIncludeParams.setSelection(index);
                    setInclude(tableIncludeParams.getItem(index));
                }
            }
        });
        butRemoveInclude.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
        tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {

            @Override
            public void close(final CTabFolderEvent e) {
                if (e.item.equals(parameterTabItem) || e.item.equals(environmentTabItem) || e.item.equals(includesTabItem)) {
                    e.doit = false;
                }
            }
        });
        tabFolder.setSelection(0);
        txtIncludeFilename.setFocus();
    }

    public void createJobCommandParameter() {
        parameterJobCmdTabItem = JOE_TI_ParameterForm_Parameter.control(new CTabItem(tabFolder, SWT.BORDER));
        group = new Group(tabFolder, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        group.setLayout(gridLayout);
        parameterJobCmdTabItem.setControl(group);
        label2 = JOE_L_Name.control(new Label(group, SWT.NONE));
        label2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        tParaName = JOE_T_ParameterForm_ParaName.control(new Text(group, SWT.BORDER));
        final GridData gridData_9 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData_9.widthHint = 200;
        tParaName.setLayoutData(gridData_9);
        tParaName.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {

            @Override
            public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(tParaName)) {
                    addParam();
                }
            }
        });
        tParaName.addModifyListener(new org.eclipse.swt.events.ModifyListener() {

            @Override
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                bApply.setEnabled(!"".equals(tParaName.getText()));
                if ("<from>".equals(tParaName.getText())) {
                    cSource.setVisible(true);
                    tParaValue.setVisible(false);
                } else {
                    cSource.setVisible(false);
                    tParaValue.setVisible(true);
                }
            }
        });
        label6 = JOE_L_Value.control(new Label(group, SWT.NONE));
        label6.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        final Composite composite = new Composite(group, SWT.NONE);
        composite.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(final ControlEvent e) {
                cSource.setBounds(0, 2, composite.getBounds().width, tParaName.getBounds().height);
                tParaValue.setBounds(0, 2, composite.getBounds().width, tParaName.getBounds().height);
            }
        });
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        cSource = new Combo(composite, SWT.READ_ONLY);
        cSource.setItems(new String[] { "order", "task" });
        cSource.setBounds(0, 0, 250, 21);
        cSource.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                tParaValue.setText(cSource.getText());
            }
        });
        cSource.setVisible(false);
        tParaValue = JOE_T_ParameterForm_ParaValue.control(new Text(composite, SWT.BORDER));
        tParaValue.setBounds(0, 0, 250, 21);
        tParaValue.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {

            @Override
            public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(tParaName)) {
                    addParam();
                }
            }
        });
        tParaValue.addModifyListener(new org.eclipse.swt.events.ModifyListener() {

            @Override
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                bApply.setEnabled(!"".equals(tParaName.getText()));
            }
        });
        final Button button = JOE_B_ParameterForm_Comment.control(new Button(group, SWT.NONE));
        final GridData gridDatax = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
        gridDatax.widthHint = 28;
        button.setLayoutData(gridDatax);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                String text = sos.scheduler.editor.app.Utils.showClipboard(tParaValue.getText(), getShell(), true, "");
                if (text != null) {
                    tParaValue.setText(text);
                }
            }
        });
        button.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/icon_edit.gif"));
        bApply = JOE_B_ParameterForm_Apply.control(new Button(group, SWT.NONE));
        final GridData gridData_5 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        bApply.setLayoutData(gridData_5);
        bApply.setEnabled(false);
        bApply.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                addParam();
            }
        });
        tParameter = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
        final GridData gridData_3 = new GridData(GridData.FILL, GridData.FILL, false, true, 5, 5);
        gridData_3.widthHint = 342;
        gridData_3.heightHint = 140;
        tParameter.setLayoutData(gridData_3);
        tParameter.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(final PaintEvent e) {
                //
            }
        });
        tParameter.setHeaderVisible(true);
        tParameter.setLinesVisible(true);
        tParameter.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                TableItem item = (TableItem) e.item;
                if (item == null) {
                    return;
                }
                setParams(item);
            }
        });
        TableColumn tcName = JOE_TCl_ParameterForm_Name.control(new TableColumn(tParameter, SWT.NONE));
        tcName.setWidth(252);
        TableColumn tcValue = JOE_TCl_ParameterForm_Value.control(new TableColumn(tParameter, SWT.NONE));
        tcValue.setWidth(500);
        butNewParam = JOE_B_ParameterForm_NewParam.control(new Button(group, SWT.NONE));
        butNewParam.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                tParaName.setText("");
                tParaValue.setText("");
                bRemove.setEnabled(false);
                tParameter.deselectAll();
                tParaName.setFocus();
            }
        });
        butNewParam.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        final Composite composite_2 = new Composite(group, SWT.NONE);
        final GridData gridData_2_1 = new GridData(GridData.CENTER, GridData.CENTER, false, false);
        gridData_2_1.heightHint = 67;
        composite_2.setLayoutData(gridData_2_1);
        composite_2.setLayout(new GridLayout());
        butUp_1 = JOE_B_Up.control(new Button(composite_2, SWT.NONE));
        butUp_1.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                listener.changeUp(tParameter);
            }
        });
        butUp_1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        butUp_1.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/icon_up.gif"));
        butDown_1 = JOE_B_Down.control(new Button(composite_2, SWT.NONE));
        butDown_1.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                listener.changeDown(tParameter);
            }
        });
        butDown_1.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
        butDown_1.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/icon_down.gif"));
        butImport_1 = JOE_B_ParameterForm_Wizard.control(new Button(group, SWT.NONE));
        butImport_1.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                startWizzard();
            }
        });
        butImport_1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        bRemove = JOE_B_ParameterForm_Remove.control(new Button(group, SWT.NONE));
        bRemove.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
        bRemove.setEnabled(false);
        bRemove.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                int index = tParameter.getSelectionIndex();
                listener.deleteParameter(tParameter, index);
                tParaName.setText("");
                tParaValue.setText("");
                tParameter.deselectAll();
                bRemove.setEnabled(false);
                bApply.setEnabled(false);
                tParaName.setFocus();
                if (index >= tParameter.getItemCount()) {
                    index--;
                }
                if (index >= 0) {
                    tParameter.setSelection(index);
                    tParameter.select(index);
                    setParams(tParameter.getItem(index));
                }
            }
        });
        final Composite composite_1 = new Composite(group, SWT.NONE);
        final GridData gridData = new GridData(GridData.FILL, GridData.FILL, false, true);
        gridData.widthHint = 87;
        composite_1.setLayoutData(gridData);
        composite_1.setLayout(new GridLayout());
        final Button paramButton = JOE_B_ParameterForm_Param.control(new Button(composite_1, SWT.RADIO));
        paramButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
        paramButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                tParaName.setText("");
                tParaValue.setText("");
                tParaName.setFocus();
            }
        });
        paramButton.setSelection(true);
        final Button fromTaskButton = JOE_B_ParameterForm_FromTask.control(new Button(composite_1, SWT.RADIO));
        fromTaskButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        fromTaskButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                tParaName.setText(JOE_M_ParameterForm_From.label());
                cSource.setText(JOE_M_ParameterForm_Task.label());
                bApply.setFocus();
            }
        });
        final Button fromOrderButton = JOE_B_ParameterForm_FromOrder.control(new Button(composite_1, SWT.RADIO));
        final GridData gridData_2 = new GridData(GridData.FILL, GridData.BEGINNING, false, true);
        fromOrderButton.setLayoutData(gridData_2);
        fromOrderButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                tParaName.setText(JOE_M_ParameterForm_From.label());
                cSource.setText(JOE_M_ParameterForm_Order.label());
                bApply.setFocus();
            }
        });
    }

    private void getDescription() {
        Element desc = listener.getParent().getChild("description");
        if (desc != null) {
            Element include = desc.getChild("include");
            includeFile = Utils.getAttributeValue("file", include);
        }
    }

    private void setParams(TableItem item) {
        tParaName.setText(item.getText(0));
        if ("<from>".equals(tParaName.getText())) {
            cSource.setText(item.getText(1));
        }
        tParaValue.setText(item.getText(1));
        bRemove.setEnabled(tParameter.getSelectionCount() > 0);
        bApply.setEnabled(false);
        tParaName.setFocus();
    }

    private void setEnvironment(TableItem item) {
        txtEnvName.setText(item.getText(0));
        txtEnvValue.setText(item.getText(1));
        butEnvRemove.setEnabled(tableEnvironment.getSelectionCount() > 0);
        butEnvApply.setEnabled(false);
    }

    private void setInclude(TableItem item) {
        txtIncludeFilename.setText(item.getText(0));
        txtIncludeNode.setText(item.getText(1));
        if (type == JOEConstants.JOB || type == JOEConstants.COMMANDS || type == JOEConstants.JOB_COMMANDS) {
            butIsLifeFile.setSelection("live_file".equalsIgnoreCase(item.getText(2)));
        }
        butRemoveInclude.setEnabled(tableIncludeParams.getSelectionCount() > 0);
        butIncludesApply.setEnabled(false);
        butOpenInclude.setEnabled(true && !isRemoteConnection);
    }

}