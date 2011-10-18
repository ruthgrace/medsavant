package org.ut.biolab.medsavant.controller;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ut.biolab.medsavant.db.util.DBUtil;
import org.ut.biolab.medsavant.db.format.AnnotationFormat;
import org.ut.biolab.medsavant.db.model.structure.CustomTables;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;
import org.ut.biolab.medsavant.db.util.query.AnnotationQueryUtil;
import org.ut.biolab.medsavant.db.util.query.ProjectQueryUtil;
import org.ut.biolab.medsavant.listener.ProjectListener;
import org.ut.biolab.medsavant.listener.ReferenceListener;
import org.ut.biolab.medsavant.view.util.DialogUtils;

/**
 *
 * @author mfiume
 */
public class ProjectController implements ReferenceListener {
    
    private String currentProjectName;
    private int currentProjectId;

    private String currentPatientTable;
    private String currentVariantTable;
    private AnnotationFormat[] currentAnnotationFormats;
    
    private DbTable currentTable;
    private TableSchema currentTableSchema;
    
    private static ProjectController instance;
    
    
    private final ArrayList<ProjectListener> projectListeners;

    public void removeProject(String projectName) {
        try {
        ProjectQueryUtil.removeProject(projectName);
        fireProjectRemovedEvent(projectName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fireProjectRemovedEvent(String projectName) {
        ProjectController pc = getInstance();
        for (ProjectListener l : pc.projectListeners) {
            l.projectRemoved(projectName);
        }
    }

    public void addProject(String projectName, File patientFormatFile) {
        try {
            ProjectQueryUtil.addProject(projectName, patientFormatFile);
            ProjectController.getInstance().fireProjectAddedEvent(projectName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getProjectId(String projectName) throws SQLException {
        return org.ut.biolab.medsavant.db.util.query.ProjectQueryUtil.getProjectId(projectName);
    }

    public void removeVariantTable(int project_id, int ref_id) {
        try {
            org.ut.biolab.medsavant.db.util.query.ProjectQueryUtil.removeReferenceForProject(project_id,ref_id);
            fireProjectTableRemovedEvent(project_id,ref_id);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getProjectName(int projectid) throws SQLException {
        return org.ut.biolab.medsavant.db.util.query.ProjectQueryUtil.getProjectName(projectid);
    }

    public boolean setProject(String projectName) {
        try {
            if (ProjectQueryUtil.containsProject(projectName)) {
                
                if(FilterController.hasFiltersApplied()){
                    if(!DialogUtils.confirmChangeReference(true)){
                        return false;
                    }
                }
                                
                this.currentProjectId = this.getProjectId(projectName);
                this.currentProjectName = projectName;                
                this.fireProjectChangedEvent(projectName);              
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public int getCurrentProjectId() {
        return this.currentProjectId;
    }
    
    public String getCurrentProjectName() {
        return this.currentProjectName;
    }

    public int getNumVariantsInTable(int projectid, int refid) throws SQLException {
        return ProjectQueryUtil.getNumberOfRecordsInVariantTable(projectid,refid);
    }

    private ProjectController() {
        projectListeners = new ArrayList<ProjectListener>();
        
        ReferenceController.getInstance().addReferenceListener(this);
    }
    
    public static ProjectController getInstance() {
        if (instance == null) {
            instance = new ProjectController();
        }
        return instance;
    }
    
    public List<String> getProjectNames() throws SQLException {
        return ProjectQueryUtil.getProjectNames();
    }
    
    public void fireProjectAddedEvent(String projectName) {
        ProjectController pc = getInstance();
        for (ProjectListener l : pc.projectListeners) {
            l.projectAdded(projectName);
        }
    }
    
    public void fireProjectChangedEvent(String projectName) {
        ProjectController pc = getInstance();
        for (ProjectListener l : pc.projectListeners) {
            l.projectChanged(projectName);
        }
    }
    
    public void fireProjectTableRemovedEvent(int projid, int refid) {
        ProjectController pc = getInstance();
        for (ProjectListener l : pc.projectListeners) {
            l.projectTableRemoved(projid, refid);
        }
    }
    
    
    
    public void addProjectListener(ProjectListener l) {
        this.projectListeners.add(l);
    }
    
    public String getCurrentTableName(){
        try {
            return ProjectQueryUtil.getVariantTablename(currentProjectId, ReferenceController.getInstance().getCurrentReferenceId());
        } catch (SQLException ex) {
            Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public DbTable getCurrentVariantTable(){
        return currentTable;
    }
    
    public TableSchema getCurrentVariantTableSchema(){
        return currentTableSchema;
    }
    
    private void setCurrentVariantTable(){
        try {
            this.currentTable = DBUtil.importTable(getCurrentTableName());
            this.currentTableSchema =  CustomTables.getVariantTableSchema(getCurrentTableName());          
        } catch (SQLException ex) {
            Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public AnnotationFormat[] getCurrentAnnotationFormats(){
        if(currentAnnotationFormats == null){
            try {
                int[] annotationIds = AnnotationQueryUtil.getAnnotationIds(this.currentProjectId, ReferenceController.getInstance().getCurrentReferenceId());
                AnnotationFormat[] af = new AnnotationFormat[annotationIds.length+1];
                af[0] = AnnotationFormat.getDefaultAnnotationFormat();
                for(int i = 0; i < annotationIds.length; i++){
                    af[i+1] = AnnotationQueryUtil.getAnnotationFormat(annotationIds[i]);
                }
                currentAnnotationFormats = af;
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return currentAnnotationFormats;
    }
    
    public void setCurrentAnnotationFormats(AnnotationFormat[] formats){
        this.currentAnnotationFormats = formats;
    }

    public void referenceChanged(String referenceName) {
        setCurrentVariantTable();
        setCurrentAnnotationFormats(null);
    }

    public void referenceAdded(String name) {
    }

    public void referenceRemoved(String name) {
    }
    
}
