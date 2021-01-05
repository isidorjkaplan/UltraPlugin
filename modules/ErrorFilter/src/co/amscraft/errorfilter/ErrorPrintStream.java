package co.amscraft.errorfilter;

import java.io.PrintStream;

public class ErrorPrintStream extends OutFilteredStream {
    private boolean block = false;

    public ErrorPrintStream(PrintStream stream) {
        super(stream);
    }

    //private static final String EXCEPTION = "java.lang.IllegalArgumentException";
    @Override
    public void println(Object x) {
        //System.out.println("Println: " + x);
        String s = x + "";
        if (ErrorFilter.errors != null) {
            for (String error : ErrorFilter.errors) {
                if (s.contains(error)) {
                    block = true;
                    break;
                } else if (block && !s.contains("at ")) {
                    block = false;
                }
            }
            if (!block) {
                super.println(x);
            }
        }
    }
}
