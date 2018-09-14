package HeartBeats;


import java.net.MalformedURLException;
import java.rmi.*;

public class HeartBeatsGame implements Runnable,HeartBeats {
    final static String serviceDefaultGame = "remoteT";
    boolean cont = true;
    private Thread thread = null;
    private IHeartBeats beat = null;
    public boolean DEBUG = false;
    private IHeartBeatsGameParent parent;

    static public HeartBeatsGame startHeartBeatsGame(IHeartBeatsGameParent server, String registry, String serviceStr) {

        try {
            String registration = "rmi://" + registry + "/" + serviceStr;
            Remote service = Naming.lookup(registration);
            HeartBeatsGame heartBeatsGame = new HeartBeatsGame();
            heartBeatsGame.beat = (IHeartBeats) service;
            heartBeatsGame.parent = server;
            heartBeatsGame.thread = new Thread(heartBeatsGame);
            heartBeatsGame.thread.start();
            return heartBeatsGame;
        } catch (ConnectException e) {
            System.out.println("Nenhum servidor encontrado em <" + registry + "> com o nome de seviço <" + serviceStr + ">");
            System.exit(0);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public HeartBeatsGame startHeartBeatsGame(IHeartBeatsGameParent server, String registry) {
        return startHeartBeatsGame(server, registry, serviceDefaultGame);
    }

    @Override
    public void run() {
        while (cont) {
            try {
                String[] databaseInf = beat.heartBeat().split(":");

                parent.setDataBaseIP(databaseInf[0]);
                parent.setDataBasePort(Integer.parseInt(databaseInf[1]));
                if (DEBUG && databaseInf.length == 2)
                    System.out.println(databaseInf[0] + ":" + databaseInf[1]);
                Thread.sleep(delay);
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }
    }

    public void stop() {
        cont = false;
        thread = null;
    }
}
