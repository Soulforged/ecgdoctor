package edu.uk.dromm.wfdb;

public class WfdbStatus {

  public String message(final String function, final int returnValue){
    String message = "Unknown";
    if(function.equalsIgnoreCase("annopen"))
      message = returnValue == 0 ? "Success" :
        returnValue == -3 ? "Failure: unable to open input annotation file" :
          returnValue == -4 ? "Failure: unable to open output annotation file" :
            returnValue == -5 ? "Failure: illegal stat (in aiarray) specified for annotation file" :
              message;
    return message;
  }
}
