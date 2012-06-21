package sos.scheduler.editor.conf.forms;

//import org.eclipse.draw2d.*;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;


import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import sos.scheduler.editor.app.ContextMenu;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
 
import org.jdom.Element;

import sos.scheduler.editor.app.Editor;
import sos.scheduler.editor.app.ErrorLog;
import sos.scheduler.editor.app.IUpdateLanguage;
import sos.scheduler.editor.app.MainWindow;
import sos.scheduler.editor.app.Options;

import sos.scheduler.editor.conf.ISchedulerUpdate;
import sos.scheduler.editor.conf.SchedulerDom;
import sos.scheduler.editor.conf.composites.PreProcessingComposite;
 
public class ScriptFormPreProcessing extends ScriptForm implements IUpdateLanguage {

    @SuppressWarnings("unused")
    private final String conSVNVersion = "$Id$";

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ScriptJobMainForm.class);
    @SuppressWarnings("unused")
    private final String conClassName = "PreProcessingForm";
    private PreProcessingComposite headerComposite=null;
    private HashMap <String,String> favorites = null;

    public ScriptFormPreProcessing(Composite parent, int style, SchedulerDom dom, Element job, ISchedulerUpdate main) {
        super(parent, style, dom, job, main);
        objDataProvider._languages = objDataProvider._languagesMonitor;
        initialize();
   }
 
    protected void createGroup() {
        
        GridLayout gridLayoutMainOptionsGroup = new GridLayout();
        gridLayoutMainOptionsGroup.numColumns = 1;
        objMainOptionsGroup = new Group(this, SWT.NONE);
        objMainOptionsGroup.setText(objDataProvider.getJobNameAndTitle());

        objMainOptionsGroup.setLayout(gridLayoutMainOptionsGroup);
        objMainOptionsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 1;
        gridLayout.numColumns = 1;

        headerComposite = new PreProcessingComposite(objMainOptionsGroup, SWT.NONE,objDataProvider);
        headerComposite.setLayout(gridLayout);
        headerComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        
        createLanguageSelector(headerComposite.getgMain());
        if (objDataProvider.getLanguage() < 0){ 
          objDataProvider.setLanguage(0);
          languageSelector.selectLanguageItem(0);
        }
        createScriptTabForm(objMainOptionsGroup);

        getFavoriteNames();

        headerComposite.getButFavorite().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                Options.setProperty("monitor_favorite_" + objDataProvider.getLanguage(objDataProvider.getLanguage()) + "_" + headerComposite.getTxtName().getText(), getFavoriteValue());
                Options.saveProperties();
                headerComposite.getCboFavorite().setItems(normalized(Options.getPropertiesWithPrefix("monitor_favorite_")));
            }
        });
        
        headerComposite.getCboFavorite().addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(final SelectionEvent e) {
            getDataFromFavorite();
          }
    });
    }
    
    protected String getPredefinedFunctionNames(){
        return "spooler_task_before;spooler_task_after;spooler_process_before;spooler_process_after";
    }
    
    protected String[] getScriptLanguages(){
        return objDataProvider._languagesMonitor;
    }
    
    private void getFavoriteNames(){
        headerComposite.getCboFavorite().setData("favorites", favorites);
        headerComposite.getCboFavorite().setMenu(new ContextMenu(headerComposite.getCboFavorite(), objDataProvider.getDom(), Editor.SCRIPT).getMenu());
     }

    
    private String getFavoriteValue() {
        if (objDataProvider.isJava()) {
            return this.getObjJobJAPI().getTbxClassName().getText();
        }
        else {
            return objDataProvider.getIncludesAsString();
        }
    }

    
    private String getPrefix() {
        if (favorites != null && headerComposite.getCboFavorite().getText().length() > 0 && favorites.get(headerComposite.getCboFavorite().getText()) != null)
            return "monitor_favorite_" + favorites.get(headerComposite.getCboFavorite().getText()) + "_";
        if (objDataProvider.getLanguage() == 0)
            return "";
        return "monitor_favorite_" + objDataProvider.getLanguage(objDataProvider.getLanguage()) + "_";
    }

    private String[] normalized(String[] str) {
        String[] retVal = new String[] { "" };
        try {
            favorites = new HashMap<String,String>();
            if (str == null)
                return new String[0];

            String newstr = "";
            retVal = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                String s = str[i];
                int idx = s.indexOf("_");
                if (idx > -1) {
                    String lan = s.substring(0, idx);
                    String name = s.substring(idx + 1);
                    if (name == null || lan == null)
                        System.out.println(name);
                    else
                        favorites.put(name, lan);
                    newstr = name + ";" + newstr;
                }
            }
            retVal = newstr.split(";");
            return retVal;
        }
        catch (Exception e) {
            System.out.println(e.toString());
            try {
                new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), e);
            }
            catch (Exception ee) {
              
            }
            return retVal;
        }
    }

     
    private void getDataFromFavorite(){
        
        if (headerComposite.getCboFavorite().getText().length() > 0) {
            if (Options.getProperty(getPrefix() + headerComposite.getCboFavorite().getText()) != null) {

                if (this.getObjJobJAPI() != null && this.getObjJobJAPI().getTbxClassName().getText().length() > 0 ||  this.getObjJobIncludeFile() != null  && this.getObjJobIncludeFile().getTableIncludes().isEnabled() && this.getObjJobIncludeFile().getTableIncludes().getItemCount() > 0) {
                    int c = MainWindow.message(getShell(), "Overwrite this Monitor?", SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    if (c != SWT.YES)
                        return;
                    else {
                        if (this.getObjJobJAPI() != null){
                            this.getObjJobJAPI().getTbxClassName().setText("");
                        }
                        if (this.getObjJobIncludeFile() != null){
                           this.getObjJobIncludeFile().getTableIncludes().clearAll();
                        }
                        objDataProvider.removeIncludes();
                    }
                }

                if (favorites != null && favorites.get(headerComposite.getCboFavorite().getText()) != null && favorites.get(headerComposite.getCboFavorite().getText()).toString().length() > 0) {

                    objDataProvider.setLanguage(objDataProvider.languageAsInt(favorites.get(headerComposite.getCboFavorite().getText()).toString()));
                    languageSelector.setText(objDataProvider.getLanguageAsString(objDataProvider.getLanguage()));
                    
                    if (objDataProvider.isJava()) {
                        this.getObjJobJAPI().getTbxClassName().setText(Options.getProperty(getPrefix() + headerComposite.getCboFavorite().getText()));
                    }
                    else {
                        tabFolder.setSelection(this.getTabItemIncludedFiles());
                        String[] split = Options.getProperty(getPrefix() + headerComposite.getCboFavorite().getText()).split(";");
                        for (int i = 0; i < split.length; i++) {
                            objDataProvider.addInclude(split[i]);
                        }
                    }

                    fillForm();
                }
            }
        }

    }


    @Override protected void initForm() {

        headerComposite.init();
        if (normalized(Options.getPropertiesWithPrefix("monitor_favorite_")) != null
                && normalized(Options.getPropertiesWithPrefix("monitor_favorite_"))[0] != null) {
            headerComposite.getCboFavorite().setItems(normalized(Options.getPropertiesWithPrefix("monitor_favorite_")));
        }
        
    }
}  