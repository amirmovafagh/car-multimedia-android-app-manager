package com.hooshmandkhodro.carservice.app;

import android.os.Handler;
import android.os.HandlerThread;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import eu.chainfire.libsuperuser.Shell;

    /**
     * read and write serial data fom gpio pins on HeadUnit board
     * using chainfire library because of don't get su permission in every  command running
     *
     * @author  Amir Movafagh
     * */

public class GpioUart {
    public String port;
    public int pin;
    private HandlerThread ht;
    private Handler h;
    StringBuilder cmdReturn;
    private static String command;
    private boolean rootPermission = false;


    private String suVersion = null;
    private String suVersionInternal = null;
    private List<String> suResult = null;
    private Shell.Threaded shell;
    private Shell.ThreadedAutoCloseable getShell;


    /**
     * Constructor
     * set target UART pin for communication
     * and get permission for first time the app install and run
     *
     * @param pin   serial pin number
     * */
    public GpioUart(int pin)  {
        this.pin = pin;
        this.port = "ttyS" + this.pin;
        command = String.format("head -1 /dev/%s", this.port);
        //rootPermission = isRootGiven();

        ht = new HandlerThread("sendDataThread");
        ht.start();
        h = new Handler(ht.getLooper());
        Shell.setRedirectDeprecated(true);

        rootPermission = Shell.SU.available();

        if (rootPermission) {

            suVersion = Shell.SU.version(false);
            suVersionInternal = Shell.SU.version(true);
            try {
                //fix the loopback echo tx in rx problem
                Runtime.getRuntime().exec(new String[]{"su", "-c",  "stty -F /dev/ttyS1 -echo -onlcr"});

                //readProcessBuilder = new ProcessBuilder("su", "-c", command);
               shell = Shell.Pool.SU.get();
                getShell = Shell.Pool.SU.get().ac();
            } catch (Shell.ShellDiedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public String readData() {

        final String[] retour = {null};
        try {

            getShell.run(command, new Shell.OnSyncCommandInputStreamListener() {
                @Override
                public void onInputStream(InputStream inputStream) {
                    try {
                        cmdReturn = new StringBuilder();


                        int c;
                        while ((c = inputStream.read()) !=-1) {
                            cmdReturn.append((char) c);
                        }

                        inputStream.close();


                        if (cmdReturn != null) {
                             retour[0] = cmdReturn.toString();


                        }

                    } catch (IOException e) {
                        // shell is closed during read

                    } catch (Exception e) {
                        // this really shouldn't happen

                        e.printStackTrace();
                    }
                }

                @Override
                public void onSTDERR(String line) {
                    retour[0]=line;
                }
            });
        } catch (Shell.ShellDiedException e) {
            e.printStackTrace();
        }

        return retour[0];
        /*if (rootPermission) {


            Process p ;
            try {
                cmdReturn = new StringBuilder();

                p = readProcessBuilder.start();
                InputStream inputStream = p.getInputStream();
                int c;
                while ((c = inputStream.read()) != -1) {
                    cmdReturn.append((char) c);
                }

                inputStream.close();

                p.waitFor();
                if (cmdReturn != null) {
                    String retour = cmdReturn.toString();

                    return retour;
                } else return "null";

            } catch (IOException e) {
                return "error IO" + e + " 1";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "error IO" + e + " 1";
            } finally {

            }
        }

        return "root permission Error";*/
    }

    //set value of the output
    public void sendData(String value) {

        if (rootPermission) {

            try {

                // get an instance from the pool

                try {
                    // this is very useful
                    shell.run("echo -ne "+value+" > /dev/"+port);
                } finally {
                    // return the instance to the pool
                    //shell.close();
                }

            } catch (Shell.ShellDiedException e) {
                // su isn't present, access was denied, or the shell terminated while 'run'ing
            }


            /*String command1 = String.format("echo %s > /dev/%s", value, this.port);
            h.post(() -> {
                String[] test = new String[]{"su", "-c", command1};
                try {
                    Process p1;
                    sendProcessBuilder = new ProcessBuilder(command1);
                    //sendProcessBuilder.command(command1);


                    p1 = sendProcessBuilder.start();

                    //dos.flush();
                    p1.waitFor();
                    p1.destroy();

                    //p1.destroy();
                    // dos.writeBytes("exit\n");
                    //dos.flush();
                    // dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });*/
        }


    }

    /*public static boolean isRootGiven() {
        if (isRootAvailable()) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = in.readLine();
                if (output != null && output.toLowerCase().contains("uid=0")) {
                    readProcessBuilder = new ProcessBuilder("su", "-c", command);





                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null)
                    process.destroy();
            }
        }

        return false;
    }

    public static boolean isRootAvailable() {
        for (String pathDir : System.getenv("PATH").split(":")) {
            if (new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }*/

}
