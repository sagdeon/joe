package com.sos.joe.jobdoc.editor.forms;

import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_ParamsForm_Apply;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_ParamsForm_NewParam;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_ParamsForm_Notes;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_ParamsForm_ParamsNote;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_ParamsForm_RemoveParam;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_B_ParamsForm_Required;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_Cbo_ParamsForm_Reference;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_Cbo_ParamsForm_Reference2;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_G_ParamsForm_ParamValues;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_G_ParamsForm_Parameter;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_L_Name;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_L_ParamsForm_DefaultValue;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_L_ParamsForm_ID;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_L_ParamsForm_Reference;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_L_ParamsForm_Required;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_TCl_ParamsForm_Default;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_TCl_ParamsForm_ID;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_TCl_ParamsForm_Name;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_TCl_ParamsForm_Reference;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_TCl_ParamsForm_Required;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_T_ParamsForm_DefaultValue;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_T_ParamsForm_ID;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_T_ParamsForm_ID2;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_T_ParamsForm_Name;
import static com.sos.joe.globals.messages.SOSJOEMessageCodes.JOE_Tbl_ParamsForm_Params;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

import com.sos.dialog.classes.SOSGroup;
import com.sos.dialog.classes.SOSLabel;
import com.sos.dialog.components.WaitCursor;
import com.sos.joe.globals.messages.SOSMsgJOE;
import com.sos.joe.jobdoc.editor.listeners.DocumentationListener;
import com.sos.joe.jobdoc.editor.listeners.ParamsListener;
import com.sos.joe.xml.Utils;
import com.sos.joe.xml.jobdoc.DocumentationDom;

public class ParamsForm extends JobDocBaseForm<ParamsListener> {

    private SOSGroup group = null;
    private Label label = null;
    private Text tParamsID = null;
    private Label label1 = null;
    private Combo cParamsReference = null;
    private Button bParamsNotes = null;
    private Group group1 = null;
    private Label label2 = null;
    private Label label3 = null;
    private Label label4 = null;
    private Text tName = null;
    private Text tDefault = null;
    private Button cRequired = null;
    private Label label5 = null;
    private Text tID = null;
    private Label label6 = null;
    private Combo cReference = null;
    private Combo cboDataType = null;
    private Label label7 = null;
    private Table tParams = null;
    private Button bNew = null;
    private Label label8 = null;
    private Button bRemove = null;
    private Button bNotes = null;

    public ParamsForm(Composite parent, int style) {
        super(parent, style);
        initialize();
    }

    public ParamsForm(Composite parent, int style, DocumentationDom dom1, Element parentElement) {
        super(parent, style);
        initialize();
        setParams(dom1, parentElement);
    }

    public void setParams(DocumentationDom dom1, Element parentElement) {
        this.dom = dom1;
        listener = new ParamsListener(dom, parentElement);
        listener.fillParams(tParams);
        bRemove.setEnabled(false);
        tParamsID.setText(listener.getID());
        DocumentationListener.setCheckbox(cParamsReference, listener.getReferences(listener.getID()), listener.getReference());
    }

    private void initialize() {
        createGroup();
        setParamStatus(false);
        tParamsID.setFocus();
    }

    public void setParamNote(boolean enabled) {
        bParamsNotes.setVisible(enabled);
    }

    private void createGroup() {
        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        GridLayout gridLayout = new GridLayout(5, false);
        group = JOE_G_ParamsForm_Parameter.Control(new SOSGroup(this, SWT.NONE));
        group.setLayout(gridLayout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        label = JOE_L_ParamsForm_ID.control(new SOSLabel(group, SWT.NONE));
        tParamsID = JOE_T_ParamsForm_ID.control(new Text(group, SWT.BORDER));
        tParamsID.setLayoutData(gridData);
        tParamsID.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                listener.setID(tParamsID.getText());
            }
        });
        label1 = JOE_L_ParamsForm_Reference.control(new SOSLabel(group, SWT.NONE));
        createCParamsReference();
        bParamsNotes = JOE_B_ParamsForm_ParamsNote.control(new Button(group, SWT.NONE));
        bParamsNotes.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String tip = "";
                DocumentationForm.openNoteDialog(dom, listener.getParamsElement(), "note", tip, true, JOE_B_ParamsForm_ParamsNote.label());
            }
        });
        createGroup1();
    }

    private void createCParamsReference() {
        GridData gridData1 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        cParamsReference = JOE_Cbo_ParamsForm_Reference.control(new Combo(group, SWT.NONE));
        cParamsReference.setLayoutData(gridData1);
        cParamsReference.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                listener.setReference(cParamsReference.getText());
            }
        });
    }

    private void createGroup1() {
        GridData gridData14 = new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1);
        GridData gridData13 = new GridData();
        GridData gridData12 = new GridData(GridData.FILL, GridData.FILL, true, true, 4, 3);
        GridData gridData11 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        GridData gridData10 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        GridData gridData9 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        GridData gridData8 = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 4);
        GridData gridData7 = new GridData(GridData.FILL, GridData.CENTER, false, false, 5, 1);
        GridData gridData5 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        GridData gridData4 = new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1);
        GridData gridData3 = new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1);
        GridData gridData2 = new GridData(GridData.FILL, GridData.FILL, true, true, 5, 1);
        GridLayout gridLayout1 = new GridLayout(5, false);
        group1 = JOE_G_ParamsForm_ParamValues.Control(new SOSGroup(group, SWT.NONE));
        group1.setLayout(gridLayout1);
        group1.setLayoutData(gridData2);
        label2 = JOE_L_Name.control(new SOSLabel(group1, SWT.NONE));
        tName = JOE_T_ParamsForm_Name.control(new Text(group1, SWT.BORDER));
        tName.setLayoutData(gridData3);
        tName.addModifyListener(modifyTextListener);
        bApply = JOE_B_ParamsForm_Apply.control(new Button(group1, SWT.NONE));
        bApply.setLayoutData(gridData8);
        bApply.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                applyParam();
                newParam();
            }
        });
        Label label = new SOSMsgJOE("JOE_L_ParamsForm_DataType").control(new SOSLabel(group1, SWT.NONE));
        cboDataType = new SOSMsgJOE("JOE_Cbo_ParamsForm_DataType").control(new Combo(group1, SWT.NONE));
        cboDataType.setLayoutData(gridData4);
        cboDataType.addModifyListener(modifyTextListener);
        label3 = JOE_L_ParamsForm_DefaultValue.control(new SOSLabel(group1, SWT.NONE));
        tDefault = JOE_T_ParamsForm_DefaultValue.control(new Text(group1, SWT.BORDER));
        tDefault.setLayoutData(gridData4);
        tDefault.addModifyListener(modifyTextListener);
        label6 = JOE_L_ParamsForm_Reference.control(new SOSLabel(group1, SWT.NONE));
        createCReference();
        label4 = JOE_L_ParamsForm_Required.control(new SOSLabel(group1, SWT.NONE));
        cRequired = JOE_B_ParamsForm_Required.control(new Button(group1, SWT.CHECK));
        cRequired.setLayoutData(gridData13);
        cRequired.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setApplyStatus();
            }
        });
        label5 = JOE_L_ParamsForm_ID.control(new SOSLabel(group1, SWT.NONE));
        tID = JOE_T_ParamsForm_ID2.control(new Text(group1, SWT.BORDER));
        tID.setLayoutData(gridData5);
        tID.addModifyListener(modifyTextListener);
        bNotes = JOE_B_ParamsForm_Notes.control(new Button(group1, SWT.NONE));
        bNotes.setEnabled(false);
        bNotes.setLayoutData(gridData14);
        bNotes.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String tip = "";
                if ((listener.getParamElement() == null)
                        || ((listener.getParamElement() != null) && listener.getParamElement().getParentElement() == null)) {
                    applyParam();
                }
                DocumentationForm.openNoteDialog(dom, listener.getParamElement(), "note", tip, true, !listener.isNewParam(),
                        JOE_B_ParamsForm_ParamsNote.label());
            }
        });
        label7 = new SOSLabel(group1, SWT.SEPARATOR | SWT.HORIZONTAL);
        label7.setText("Label");
        label7.setLayoutData(gridData7);
        tParams = JOE_Tbl_ParamsForm_Params.control(new Table(group1, SWT.BORDER));
        tParams.setHeaderVisible(true);
        tParams.setLayoutData(gridData12);
        tParams.setLinesVisible(true);
        tParams.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tParams.getSelectionCount() > 0 && listener.selectParam(tParams.getSelectionIndex())) {
                    bRemove.setEnabled(true);
                    setParamStatus(true);
                }
                bApply.setEnabled(false);
            }
        });
        TableColumn tableColumn = JOE_TCl_ParamsForm_Name.control(new TableColumn(tParams, SWT.NONE));
        tableColumn.setWidth(200);
        TableColumn tableColumn21 = JOE_TCl_ParamsForm_Default.control(new TableColumn(tParams, SWT.NONE));
        tableColumn21.setWidth(150);
        TableColumn tableColumn3 = JOE_TCl_ParamsForm_Required.control(new TableColumn(tParams, SWT.NONE));
        tableColumn3.setWidth(70);
        TableColumn tableColumn2 = JOE_TCl_ParamsForm_Reference.control(new TableColumn(tParams, SWT.NONE));
        tableColumn2.setWidth(120);
        TableColumn tableColumn1 = JOE_TCl_ParamsForm_ID.control(new TableColumn(tParams, SWT.NONE));
        tableColumn1.setWidth(120);
        TableColumn tableColumnD = new SOSMsgJOE("JOE_TCl_ParamsForm_DataType").control(new TableColumn(tParams, SWT.NONE));
        tableColumnD.setWidth(120);
        bNew = JOE_B_ParamsForm_NewParam.control(new Button(group1, SWT.NONE));
        bNew.setLayoutData(gridData11);
        bNew.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                listener.setNewParam();
                setParamStatus(true);
                tParams.deselectAll();
            }
        });
        label8 = new SOSLabel(group1, SWT.SEPARATOR | SWT.HORIZONTAL);
        bRemove = JOE_B_ParamsForm_RemoveParam.control(new Button(group1, SWT.NONE));
        bRemove.setLayoutData(gridData9);
        bRemove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tParams.getSelectionCount() > 0) {
                    listener.removeParam(tParams.getSelectionIndex());
                    setParamStatus(false);
                    bApply.setEnabled(false);
                    bRemove.setEnabled(false);
                    tParams.deselectAll();
                    listener.fillParams(tParams);
                    DocumentationListener.setCheckbox(cParamsReference, listener.getReferences(listener.getID()), listener.getReference());
                }
            }
        });
    }

    private void createCReference() {
        GridData gridData6 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        cReference = JOE_Cbo_ParamsForm_Reference2.control(new Combo(group1, SWT.NONE));
        cReference.setLayoutData(gridData6);
        cReference.addModifyListener(modifyTextListener);
    }

    private void setParamStatus(boolean enabled) {
        tName.setEnabled(enabled);
        tDefault.setEnabled(enabled);
        tID.setEnabled(enabled);
        cRequired.setEnabled(enabled);
        cReference.setEnabled(enabled);
        cboDataType.setEnabled(enabled);
        bNotes.setEnabled(enabled);
        if (enabled) {
            tName.setText(listener.getParamName());
            cboDataType.setText(listener.getDataType());
            tDefault.setText(listener.getParamDefaultValue());
            tID.setText(listener.getParamID());
            cRequired.setSelection(listener.getParamRequired());
            DocumentationListener.setCheckbox(cReference, listener.getReferences(listener.getParamID()), listener.getParamReference());
            tName.setFocus();
        }
        bApply.setEnabled(false);
    }

    @Override
    protected void setApplyStatus() {
        try (WaitCursor objWC = new WaitCursor()) {
            bApply.setEnabled(!tName.getText().isEmpty());
            bNotes.setEnabled(!tName.getText().isEmpty());
            Utils.setBackground(tName, true);
            getShell().setDefaultButton(bApply);
        } catch (Exception e) {
            //
        }
    }

    private void newParam() {
        listener.setNewParam();
        setParamStatus(true);
        tParams.deselectAll();
    }

    private void applyParam() {
        listener.applyParam(tName.getText(), tDefault.getText(), tID.getText(), cboDataType.getText(), cReference.getText(), cRequired.getSelection());
        DocumentationListener.setCheckbox(cParamsReference, listener.getReferences(listener.getID()), listener.getReference());
        bApply.setEnabled(false);
        listener.fillParams(tParams);
        bRemove.setEnabled(tParams.getSelectionCount() > 0);
    }

    @Override
    public void apply() {
        if (isUnsaved()) {
            applyParam();
        }
    }

    @Override
    public boolean isUnsaved() {
        listener.checkParams();
        return bApply.isEnabled();
    }

    public void checkParams() {
        listener.checkParams();
    }

    @Override
    public void openBlank() {
        //
    }

    @Override
    protected void applySetting() {
        apply();
    }

    @Override
    public boolean applyChanges() {
        apply();
        return false;
    }

}