package ir.dev_roid.testusb.app;

import static ir.dev_roid.testusb.MyHandler.steeringWheelData;

public class ObservableInteger {
    public interface OnIntegerChangeListener
    {
        public void onIntegerChanged(int newValue);
    }

    private OnIntegerChangeListener listener;

    private int value;

    public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
    {
        this.listener = listener;
    }

    public int get()
    {
        return value;
    }

    public void set(int value)
    {
        this.value = value;

        if(listener != null)
        {
            listener.onIntegerChanged(value);
        }
    }
}
