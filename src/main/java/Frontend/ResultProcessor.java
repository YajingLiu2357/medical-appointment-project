package Frontend;

import java.util.List;

public class ResultProcessor {

    private static ResultProcessor instance = null;

    public static ResultProcessor getInstance() {
        if(instance == null){
            instance = new ResultProcessor();
        }
        return instance;
    }

    public String ResultsErrorProcess(List<String> results){
        FrontEndHelper.setResults(results);
        String majorityResult = FrontEndHelper.getMajority();
        FrontEndHelper.checkSoftwareFailure(majorityResult);
        return majorityResult;
    }


    public int ResultsErrorChecker(List<String> results){
        int resultsLength = results.size();
        int[] resultsEvaluation = new int[resultsLength];
        //initialize
        for(int i =0; i < resultsLength; ++i){
            resultsEvaluation[i] = 0;
        }

        //bucket algorithm
        for(int i =0; i < results.size(); ++i){
            for(int j =0; j < results.size(); ++j){
                if(i != j && results.get(i).equals(results.get(j))){
                    resultsEvaluation[i] ++;
                }
            }
        }

        int minIndex = -1;
        int minValue = resultsLength - 1;
        for(int i =0; i < resultsLength; ++i){
            if(minValue > resultsEvaluation[i]){
                minValue = resultsEvaluation[i];
                minIndex = i;
            }
        }

        //return the min similarity replica index
        return minIndex;
    }


    public String RegisterUserResultsProcess(List<String> results){
        return ResultsErrorProcess(results);
    }

    public boolean IsValidUserName(List<String> results){
        return Boolean.parseBoolean(ResultsErrorProcess(results));
    }

    public String BookAppointmentResultsProcess(List<String> results){
        return ResultsErrorProcess(results);
    }


    public String CancelAppointmentResultsProcess(List<String> results){
        return ResultsErrorProcess(results);
    }

    public String ViewBookedAppointmentsResultsProcess(List<String> results){
        return ResultsErrorProcess(results);
    }

    public String AddAppointmentResultsProcess(List<String> results){
        return ResultsErrorProcess(results);
    }

    public String RemoveAppointmentResultsProcess(List<String> results){
        return ResultsErrorProcess(results);
    }

    public String ViewAvailableAppointmentsResultsProcess(List<String> results){
        return ResultsErrorProcess(results);
    }

    public String SwapAppointmentResultsProcess(List<String> results){
        return ResultsErrorProcess(results);
    }
}
