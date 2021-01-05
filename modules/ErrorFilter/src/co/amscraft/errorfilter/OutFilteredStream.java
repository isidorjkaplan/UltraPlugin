package co.amscraft.errorfilter;

import java.io.PrintStream;

public class OutFilteredStream extends PrintStream {
    public OutFilteredStream(PrintStream stream) {
        super(stream);
    }

    @Override
    public void println(Object x) {
        //super.println("Printing: " + x);
        String s = x + "";
        for (String blocked : ErrorFilter.blockedMessages) {
            if (s.contains(blocked)) {
                return;
            }
        }
        super.println(x);
    }

    @Override
    public void println(String s) {
        for (String blocked : ErrorFilter.blockedMessages) {
            if (s.contains(blocked)) {
                return;
            }
        }
        super.println(s);
    }
}
