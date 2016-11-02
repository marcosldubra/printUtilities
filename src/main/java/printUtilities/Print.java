package printUtilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class Print {
	
	// TODO: if we don't print in default printer, the programs print as many times as printers has
	public static void printJob(String filePath) throws FileNotFoundException, IOException, PrintException {
		//try {
		DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
		FileInputStream inputStream = new FileInputStream(filePath);
        Doc document = new SimpleDoc(inputStream, docFormat, null);
        DocPrintJob printJob;
        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        PrintService defaultPrintService;
        PrintService[] availablePrinters;
        int printerNumber;
        JobCompletedMonitor monitor = new JobCompletedMonitor();
        
        attributeSet.add(MediaSizeName.ISO_A4); 
                
        defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
        
        if (defaultPrintService != null) {
            printJob = defaultPrintService.createPrintJob();
            printJob.print(document, attributeSet);
        } else {
        	availablePrinters = PrintServiceLookup.lookupPrintServices(docFormat, attributeSet);
                    	
        	for (printerNumber = 0; printerNumber < availablePrinters.length; printerNumber++) {
        		printJob = availablePrinters[printerNumber].createPrintJob();
        		printJob.addPrintJobListener(monitor);

        		printJob.print(document, attributeSet);
        		
        		monitor.waitForJobCompletion();
        	}
        	
        	/*if (!jobCompleted) {
        		throw new JobNotPrintedException(e);
        	}*/
        }
        
        inputStream.close();
		
        /*} catch (FileNotFoundException, IOException, PrintException e) {
			throw new JobNotPrintedException(e);
		}*/
        
	}
		
	private static class JobCompletedMonitor extends PrintJobAdapter {
		private boolean completed = false;
		
		@Override
	    public void printJobCanceled(PrintJobEvent jobEvent) {
			signalCompletion();
	    }
		
		@Override
	    public void printJobCompleted(PrintJobEvent jobEvent) {
			signalCompletion();
	    }
		
	    @Override
	    public void printJobFailed(PrintJobEvent jobEvent) {
	    	signalCompletion();
	    }
	    
	    @Override
	    public void printJobNoMoreEvents(PrintJobEvent jobEvent) {
	    	signalCompletion();
	    }
	    
	    private void signalCompletion() {
	    	synchronized (JobCompletedMonitor.this) {
	    		completed = true;
	    		
	    		JobCompletedMonitor.this.notify();
	    	}
	    }
	    
	    public synchronized void waitForJobCompletion() {
	    	try {
	    		while (!completed) {
	    			wait();
	    		}
	    	} catch (InterruptedException e) {
	    		
	    	}
	    }
	}

}
