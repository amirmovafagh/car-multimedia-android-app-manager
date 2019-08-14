package com.hooshmandkhodro.carservice.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import eu.chainfire.libsuperuser.Shell;

/**
 * manage cpu frequency , get temperature
 * and initializing/deinitializing touchscreen driver
 *
 * @author Amir Movafagh
 * */

public class CpuManager {
    private static final String tag = CpuManager.class.getSimpleName();
    private boolean rootPermission = false;
    private Shell.ThreadedAutoCloseable shell;

    public CpuManager() {
        rootPermission = Shell.SU.available();
        if(rootPermission){
            try {
            shell = Shell.Pool.SU.get().ac();
        } catch (Shell.ShellDiedException e) {
            e.printStackTrace();
        }}

    }

    public float getTemperature() {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                float temp = Float.parseFloat(line);
                return temp;
            } else {
                return 51.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public void cpuMaxFrequency(int maxFrq) {

        String[] cmd = {
                "echo " + maxFrq + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq",
                "echo " + maxFrq + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_max_freq",
                "echo " + maxFrq + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_max_freq",
                "echo " + maxFrq + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_max_freq"};
        /*try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", cmd[0], "-c", cmd[1], "-c", cmd[2], "-c", cmd[3]});
        } catch (Exception e) {
            Log.e(tag, e.getMessage());
        }*/

        shellExe(new String[]{cmd[0], "-c", cmd[1], "-c", cmd[2], "-c", cmd[3]});
    }

    public void cpuMinFrequency(int minFrq) {

        String[] cmd = {
                "echo " + minFrq + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq",
                "echo " + minFrq + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_min_freq",
                "echo " + minFrq + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_min_freq",
                "echo " + minFrq + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_min_freq"};
        /*try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", cmd[0], "-c", cmd[1], "-c", cmd[2], "-c", cmd[3]});
        } catch (Exception e) {
            Log.e(tag, e.getMessage());
        }*/

        shellExe(new String[]{cmd[0], "-c", cmd[1], "-c", cmd[2], "-c", cmd[3]});
    }

    public void cpuGoverner(String governerType) {

        String[] cmd = {
                "echo " + governerType + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor",
                "echo " + governerType + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor",
                "echo " + governerType + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor",
                "echo " + governerType + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor",};
        /*try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", cmd[0], "-c", cmd[1], "-c", cmd[2], "-c", cmd[3]});
        } catch (Exception e) {
            Log.e(tag, e.getMessage());
        }*/

        shellExe(new String[]{cmd[0], "-c", cmd[1], "-c", cmd[2], "-c", cmd[3]});
    }
    public void deInitTouchConfig(){
        shellExe(new String[]{"modprobe -rf gt9xxf_ts"});
    }
    public void InitTouchConfig(){
        shellExe(new String[]{"modprobe gt9xxf_ts"});
    }

    public void cpuOnlineStateConfig(int CoreNum,int state){
        shellExe(new String[]{"echo "+state+" > /sys/devices/system/cpu/cpu"+CoreNum+"/online"});
    }

    private void shellExe(String[] cmd) {
        if (rootPermission) {
            try {

                // get an instance from the pool
                //shell = Shell.Pool.SU.get();
                try {

                    // this is very useful

                    shell.run(cmd);


                } finally {
                    // return the instance to the pool
                    //shell.close();
                }

            } catch (Shell.ShellDiedException e) {
                // su isn't present, access was denied, or the shell terminated while 'run'ing
            }
        }
    }

    /**
     * adb shell echo "userspace" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
     * #Set governor CPU 0 to performance and set min-max freq to 1200 MHz
     * echo "performance" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor;
     * echo "1200000" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq;
     * echo "1200000" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq;
     *
     * #Set governor CPU 1 to performance and set min-max freq to 1200 MHz
     * echo "performance" > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor;
     * echo "1200000" > /sys/devices/system/cpu/cpu1/cpufreq/scaling_min_freq;
     * echo "1200000" > /sys/devices/system/cpu/cpu1/cpufreq/scaling_max_freq;
     *
     * #Set governor CPU 2 to performance and set min-max freq to 1200 MHz
     * echo "performance" > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor;
     * echo "1200000" > /sys/devices/system/cpu/cpu2/cpufreq/scaling_min_freq;
     * echo "1200000" > /sys/devices/system/cpu/cpu2/cpufreq/scaling_max_freq;
     *
     * #Set governor CPU 3 to performance and set min-max freq to 1200 MHz
     * echo "performance" > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor;
     * echo "1200000" > /sys/devices/system/cpu/cpu3/cpufreq/scaling_min_freq;
     * echo "1200000" > /sys/devices/system/cpu/cpu3/cpufreq/scaling_max_freq;
     * */

}
