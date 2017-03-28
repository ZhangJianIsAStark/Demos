package stark.a.is.zhang.tcptest.server;

class Constants {
    static class ServerServiceMsg {
        static final int SET_ACTIVITY_MESSENGER = 0;
        static final int STOP_BROADCAST = 1;
        static final int QUIT = 2;
    }

    static class ServerActivityMsg {
        static final int CLIENT_CONNECT = 0;
    }
}
