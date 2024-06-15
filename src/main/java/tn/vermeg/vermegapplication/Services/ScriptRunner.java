package tn.vermeg.vermegapplication.Services;

import java.io.IOException;

public class ScriptRunner  {
    public static void runScript(String scriptPath) throws IOException {
  try{
      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder.command(scriptPath);
      processBuilder.start();
  }catch (IOException ex){
      System.out.println("exception Error" + ex.getMessage());
  }
    }
}
