package sos.scheduler.editor.conf.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sos.scheduler.editor.app.MainWindow;
import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.conf.listeners.DateListener;
import sos.scheduler.editor.conf.listeners.DaysListener;

import com.sos.joe.globals.interfaces.ISchedulerUpdate;
import com.sos.joe.globals.messages.SOSJOEMessageCodes;
import com.sos.joe.globals.options.Options;
import com.sos.joe.xml.jobscheduler.SchedulerDom;

public class DateForm extends SOSJOEMessageCodes {

    private Table tableIncludes = null;
    private Button butIsLifeFile = null;
    private Button bRemove = null;
    private Label label_1 = null;
    private Button bAdd = null;
    private Combo tInclude = null;
    private Group gInclude = null;
    private Button bRemoveDate = null;
    private List lDates = null;
    private Label label3 = null;
    private Button bAddDay = null;
    private Spinner sDay = null;
    private Label label2 = null;
    private Spinner sMonth = null;
    private Label label1 = null;
    private Spinner sYear = null;
    private Label yearLabel = null;
    private DateListener listener = null;
    private int type = -1;
    private SchedulerDom dom = null;
    private ISchedulerUpdate main = null;
    private static String[] groupLabel = { "Holidays", "Specific dates" };
    private Group gDates = null;
    private Button butOpenInclude = null;

    public DateForm(Composite parent, int style, int type) {
        super(parent, style);
        this.type = type;
        initialize();
    }

    public DateForm(Composite parent, int style, int type, SchedulerDom dom, Element element, ISchedulerUpdate main) {
        this(parent, style, type);
        setObjects(dom, element, main);
        setNow();
        this.gDates.setEnabled(Utils.isElementEnabled("job", dom, element) && !Utils.hasSchedulesElement(dom, element));
        bAddDay.setFocus();
    }

    public void setObjects(SchedulerDom dom, Element element, ISchedulerUpdate main) {
        listener = new DateListener(dom, element, type);
        listener.fillList(lDates);
        if (type == 0) {
            listener.fillTable(tableIncludes);
        }
        this.main = main;
        this.dom = dom;
        if (type == 0) {
            tInclude.setItems(listener.getHolidayDescription());
        }
        setNow();
    }

    private void setNow() {
        int[] now = listener.getNow();
        sYear.setSelection(now[0]);
        sMonth.setSelection(now[1]);
        sDay.setSelection(now[2]);
    }

    private void initialize() {
        this.setLayout(new FillLayout());
        createGroup();
        setSize(new org.eclipse.swt.graphics.Point(380, 232));
    }

    private void createGroup() {
        GridLayout gridLayout = new GridLayout();
        gDates = new Group(this, SWT.NONE);
        String strT = "";
        switch (type) {
        case 0:
            strT = JOE_G_DateForm_Holiday.label();
            break;
        case 1:
            strT = JOE_G_DateForm_Specific.label();
            break;
        default:
            strT = JOE_G_DateForm_Holiday.label();
            break;
        }
        gDates.setText(strT);
        gDates.setLayout(gridLayout);
        final Group group = JOE_G_DateForm_Dates.control(new Group(gDates, SWT.NONE));
        group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 7;
        group.setLayout(gridLayout_1);
        yearLabel = JOE_L_DateForm_Year.control(new Label(group, SWT.NONE));
        yearLabel.setLayoutData(new GridData());
        sYear = JOE_Sp_DateForm_Year.control(new Spinner(group, SWT.BORDER));
        final GridData gridData2 = new GridData(40, SWT.DEFAULT);
        sYear.setLayoutData(gridData2);
        sYear.setMinimum(1900);
        sYear.setMaximum(10000);
        label1 = JOE_L_DateForm_Month.control(new Label(group, SWT.NONE));
        sMonth = JOE_Sp_DateForm_Month.control(new Spinner(group, SWT.BORDER));
        final GridData gridData21 = new GridData(20, SWT.DEFAULT);
        sMonth.setLayoutData(gridData21);
        sMonth.setMinimum(1);
        sMonth.setMaximum(12);
        label2 = JOE_L_DateForm_Day.control(new Label(group, SWT.NONE));
        sDay = JOE_Sp_DateForm_Day.control(new Spinner(group, SWT.BORDER));
        final GridData gridData31 = new GridData(20, SWT.DEFAULT);
        sDay.setLayoutData(gridData31);
        sDay.setMinimum(1);
        sDay.setMaximum(31);
        bAddDay = JOE_B_DateForm_AddDate.control(new Button(group, SWT.NONE));
        bAddDay.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                int year = sYear.getSelection();
                int month = sMonth.getSelection();
                int day = sDay.getSelection();
                String sDate = listener.asStr(year) + "-" + listener.asStr(month) + "-" + listener.asStr(day);
                try {
                    sos.util.SOSDate.getDate(sDate);
                } catch (Exception ex) {
                    MainWindow.message(JOE_M_0015.params(sDate, ex.getMessage()), SWT.ICON_ERROR);
                    return;
                }
                if (listener.exists(year, month, day)) {
                    MessageBox mb = JOE_M_0014.control(new MessageBox(getShell(), SWT.ICON_INFORMATION));
                    mb.open();
                    if (main != null && dom.isChanged()) {
                        main.dataChanged();
                    }
                } else {
                    listener.addDate(year, month, day);
                    listener.fillList(lDates);
                    bRemoveDate.setEnabled(false);
                    if (main != null && type == DateListener.DATE) {
                        main.updateDays(DaysListener.SPECIFIC_DAY);
                    }
                    if (type == DateListener.DATE && main != null) {
                        main.updateFont();
                    }
                }
            }
        });
        final GridData gridData3 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        bAddDay.setLayoutData(gridData3);
        label3 = new Label(group, SWT.HORIZONTAL | SWT.SEPARATOR);
        final GridData gridData32 = new GridData(GridData.FILL, GridData.CENTER, false, false, 7, 1);
        gridData32.heightHint = 10;
        label3.setLayoutData(gridData32);
        lDates = JOE_Lst_DateForm_DatesList.control(new List(group, SWT.BORDER));
        lDates.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                bRemoveDate.setEnabled(lDates.getSelectionCount() > 0);
            }
        });
        final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 6, 2);
        lDates.setLayoutData(gridData);
        bRemoveDate = JOE_B_DateForm_RemoveDate.control(new Button(group, SWT.NONE));
        bRemoveDate.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                if (lDates.getSelectionCount() > 0) {
                    int index = lDates.getSelectionIndex();
                    listener.removeDate(index);
                    listener.fillList(lDates);
                    if (index >= lDates.getItemCount()) {
                        index--;
                    }
                    if (lDates.getItemCount() > 0) {
                        lDates.select(index);
                    }
                    bRemoveDate.setEnabled(lDates.getSelectionCount() > 0);
                    if (main != null && type == 1) {
                        main.updateDays(DaysListener.SPECIFIC_DAY);
                    }
                    if (type == DateListener.DATE && main != null) {
                        main.updateFont();
                    }
                }
            }
        });
        final GridData gridData1 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        bRemoveDate.setLayoutData(gridData1);
        bRemoveDate.setEnabled(false);
        if (type == 0) {
            createGroupForIncludes();
        }
    }

    private void applyFile() {
        listener.addInclude(tableIncludes, tInclude.getText(), butIsLifeFile.getSelection());
        listener.fillTable(tableIncludes);
        tInclude.setText("");
        tInclude.setFocus();
        tableIncludes.deselectAll();
        butOpenInclude.setEnabled(false);
        bRemove.setEnabled(false);
        butIsLifeFile.setSelection(false);
    }

    private void createGroupForIncludes() {
        gInclude = JOE_G_DateForm_IncludeFiles.control(new Group(gDates, SWT.NONE));
        gInclude.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 3;
        gInclude.setLayout(gridLayout_2);
        butIsLifeFile = JOE_B_DateForm_IsLifeFile.control(new Button(gInclude, SWT.CHECK));
        butIsLifeFile.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                //
            }
        });
        butIsLifeFile.setLayoutData(new GridData());
        tInclude = JOE_Cbo_DateForm_Include.control(new Combo(gInclude, SWT.BORDER));
        tInclude.setCapture(true);
        tInclude.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                butIsLifeFile.setSelection(false);
            }
        });
        tInclude.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                bAdd.setEnabled(!"".equals(tInclude.getText()));
            }
        });
        tInclude.addKeyListener(new KeyAdapter() {

            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.CR && !"".equals(tInclude.getText())) {
                    listener.addInclude(tableIncludes, tInclude.getText(), butIsLifeFile.getSelection());
                    listener.fillTable(tableIncludes);
                    tInclude.setText("");
                }
            }
        });
        final GridData gridData6 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        tInclude.setLayoutData(gridData6);
        bAdd = JOE_B_DateForm_AddFile.control(new Button(gInclude, SWT.NONE));
        bAdd.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                applyFile();
            }
        });
        final GridData gridData7 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        bAdd.setLayoutData(gridData7);
        bAdd.setEnabled(false);
        label_1 = JOE_Sep_DateForm_S2.control(new Label(gInclude, SWT.HORIZONTAL | SWT.SEPARATOR));
        final GridData gridData1_1 = new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 1);
        label_1.setLayoutData(gridData1_1);
        tableIncludes = JOE_Tbl_DateForm_Includes.control(new Table(gInclude, SWT.FULL_SELECTION | SWT.BORDER));
        tableIncludes.addMouseListener(new MouseAdapter() {

            public void mouseDoubleClick(final MouseEvent e) {
                openInclude();
            }
        });
        tableIncludes.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                if (tableIncludes.getSelectionCount() > 0) {
                    bRemove.setEnabled(true);
                    butOpenInclude.setEnabled(true);
                } else {
                    bRemove.setEnabled(false);
                    butOpenInclude.setEnabled(false);
                    return;
                }
                if (tableIncludes.getSelection()[0].getText(2) != null && !tableIncludes.getSelection()[0].getText(2).isEmpty()) {
                    tInclude.setText(tableIncludes.getSelection()[0].getText(2));
                } else {
                    tInclude.setText(tableIncludes.getSelection()[0].getText(0));
                }
                butIsLifeFile.setSelection("live_file".equalsIgnoreCase(tableIncludes.getSelection()[0].getText(1)));
            }
        });
        tableIncludes.setLinesVisible(true);
        tableIncludes.setHeaderVisible(true);
        final GridData gridData_2 = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 3);
        tableIncludes.setLayoutData(gridData_2);
        final TableColumn newColumnTableColumn = JOE_TCl_DateForm_NameColumn.control(new TableColumn(tableIncludes, SWT.NONE));
        newColumnTableColumn.setWidth(200);
        final Button butIncludeNew = JOE_B_DateForm_NewButton.control(new Button(gInclude, SWT.NONE));
        butIncludeNew.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                butOpenInclude.setEnabled(false);
                butIsLifeFile.setSelection(false);
                tInclude.setText("");
                tableIncludes.deselectAll();
                bRemove.setEnabled(false);

            }
        });
        butIncludeNew.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        butOpenInclude = JOE_B_DateForm_OpenButton.control(new Button(gInclude, SWT.NONE));
        butOpenInclude.setEnabled(false);
        butOpenInclude.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                openInclude();

            }
        });
        butOpenInclude.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        bRemove = JOE_B_DateForm_RemoveFile.control(new Button(gInclude, SWT.NONE));
        bRemove.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                removeInclude();
            }
        });
        final GridData gridData5 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        bRemove.setLayoutData(gridData5);
        bRemove.setEnabled(false);
        butOpenInclude.setEnabled(false);
    }

    private void removeInclude() {
        if (tableIncludes.getSelectionCount() > 0) {
            int index = tableIncludes.getSelectionIndex();
            listener.removeInclude(index);
            listener.fillTable(tableIncludes);
            if (index >= tableIncludes.getItemCount()) {
                index--;
            }
            if (tableIncludes.getItemCount() > 0) {
                tableIncludes.setSelection(index);
            }
            tInclude.setText("");
        }
    }

    private void openInclude() {
        try {
            if (tableIncludes.getSelectionCount() == 0) {
                return;
            }
            String filename = tableIncludes.getSelection()[0].getText(0);
            if (butIsLifeFile.getSelection()) {
                filename = Options.getSchedulerNormalizedHotFolder() + filename;
            }
            if (!(new java.io.File(filename).exists())) {
                filename = Options.getSchedulerNormalizedHome() + "config/" + filename;
            }
            if (!(new java.io.File(filename).exists())) {
                return;
            }
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(filename);
            String xml = Utils.getElementAsString(doc.getRootElement());
            Utils.showClipboard(xml, getShell(), false, null, false, null, false);
        } catch (Exception ex) {
            MainWindow.message(JOE_E_0001.params(ex.getMessage()), SWT.ICON_ERROR);
            return;
        }
    }

}