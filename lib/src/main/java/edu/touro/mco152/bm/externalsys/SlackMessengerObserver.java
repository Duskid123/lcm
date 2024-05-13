package edu.touro.mco152.bm.externalsys;

import Observer.ObserveFinishedRun;
import edu.touro.mco152.bm.persist.DiskRun;


public class SlackMessengerObserver implements ObserveFinishedRun {
    @Override
    public void observeRun(DiskRun diskRun) {
        if (diskRun.getIoMode() == DiskRun.IOMode.READ){
            SlackManager slackmgr = new SlackManager("BadBM");
            System.out.println(diskRun.getRunMax()/diskRun.getRunAvg());
            if (diskRun.getRunMax()/diskRun.getRunAvg() > 1.03){
                String percentage = String.format("%.2f", 100*(diskRun.getRunMax()/diskRun.getRunAvg()-1));
                String message = "The max cost of the Read run exceeds the average cost of the run by a percentage of: " + percentage +"%";
                System.out.println(message);
                slackmgr.postMsg2OurChannel("WARNING " + message);

            }
        }

    }
}
