package de.gaffa.vescom.vesdroid;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;

@ReportsCrashes(formKey = "dDB5RE1DRnpIajJ3OU05b0U4Z3Y1eFE6MQ") 
public class VesDroidApplication extends Application {
  @Override
  public void onCreate() {
    // The following line triggers the initialization of ACRA
    ACRA.init(this);
    super.onCreate();
  }
}			