package stark.a.is.zhang.tcptest.server;

public class Constants {
    public static class ServerServiceMsg {
        static final int SET_ACTIVITY_MESSENGER = 0;
        public static final int STOP_BROADCAST = 1;
        static final int QUIT = 2;
    }

    static class ServerActivityMsg {
        static final int CLIENT_CONNECT = 0;
    }
}
