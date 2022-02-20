package com.example.posturalassessment;

import java.util.List;

public class EvaluatePosture {

    Boolean Sitting;
    Boolean Good_Posture;
    int sitting_timemargin; // time margin for considering if someone is sitting (in seconds)
    int posture_timemargin; // time margin for measuring posture
    int sittingtimelimit; // should be sitting for one hour maximum

    private List<LoadCell> LoadCells;
    int sampling_freq = 1; // number of signals measured for each second

    public Boolean getSitting() {
        return Sitting;
    }

    public Boolean getGood_Posture() {
        return Good_Posture;
    }

    public EvaluatePosture(List<LoadCell> LoadCells) {
        this.sitting_timemargin = 10;
        this.posture_timemargin = 15;
        this.sittingtimelimit = 60*60;
        this.LoadCells = LoadCells;
        Sitting = isSitting();
        Good_Posture = isGoodPosture();
    }

    private Boolean isSitting(){
        LoadCell current_LoadCell = LoadCells.get(0);
        boolean current_sitting_status = current_LoadCell.isSitting();
        double matching_status_counter = 0;

        int n_cells = sampling_freq * sitting_timemargin; // number of cells to be read each time

        // Read Cells corresponding to 10 seconds
        if(LoadCells.size() < n_cells) return false; // when starting app, consider true
        else {
            for (int i = 0; i < n_cells; i++) {
                if (LoadCells.get(LoadCells.size() - 1 - i).isSitting() == current_sitting_status)
                    matching_status_counter += 1;

            }

            if (matching_status_counter / n_cells > 0.6) Sitting = current_sitting_status;
            if (matching_status_counter / n_cells < 0.4) Sitting = !current_sitting_status;
            else Sitting = false;
        }
        return Sitting;
    }

    private Boolean isGoodPosture(){
        LoadCell current_LoadCell = LoadCells.get(LoadCells.size()-1);
        boolean current_posture = current_LoadCell.EvaluatePostureCM();
        double matching_status_counter = 0;

        int n_cells = sampling_freq * posture_timemargin; // number of cells to be read each time

        if(LoadCells.size() < n_cells) return false; // when starting app, consider true

        else{

        // Read Cells corresponding to 15 seconds
        for (int i = 0; i < n_cells; i++) {
            if (LoadCells.get(LoadCells.size() - 1 - i).EvaluatePostureCM() == current_posture)
                matching_status_counter += 1;

        }

        if( matching_status_counter/n_cells > 0.6) Good_Posture = current_posture;
        if (matching_status_counter/n_cells < 0.4) Good_Posture = !current_posture;
        else Good_Posture = false;

        return Good_Posture;}
    }

    // Has the person been sitting for too long?
    public Boolean TimeToGetUp(){
        LoadCell current_LoadCell = LoadCells.get(LoadCells.size()-1);
        boolean current_sitting_status = current_LoadCell.isSitting();
        double matching_status_counter = 0;

        double n_cells = sampling_freq * sittingtimelimit; // number of cells to be read each time

        if (LoadCells.size() < n_cells) return false;

        // Read Cells corresponding to 1 hour
        for (int i = 0; i < n_cells; i++) {
            if (LoadCells.get(LoadCells.size() - 1 - i).isSitting() == current_sitting_status)
                matching_status_counter += 1;

        }

        if( matching_status_counter/n_cells > 0.6) return current_sitting_status;
        if (matching_status_counter/n_cells < 0.4) return !current_sitting_status;
        else return false;

    }
    
}
