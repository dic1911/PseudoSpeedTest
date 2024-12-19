package moe.dic1911.test4speed;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QSTileService extends TileService {
    boolean listening = false, running = false;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        getQsTile().setState(Tile.STATE_ACTIVE);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.d("030-st", "start " + listening);
        listening = true;
        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Log.d("030-st", "stop " + listening);
        listening = false;
    }

    @Override
    public void onClick() {
        super.onClick();
        if (running) return;
        running = true;

        boolean err = false;
        Log.d("030-st", "tile triggered - poke speedtest " + listening);

        Tile tile = getQsTile();
        Icon ic = Icon.createWithResource(this,
                getResources().getIdentifier("stat_notify_sync", "drawable", "android"));
        tile.setIcon(ic);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.setSubtitle(getString(R.string.running));
        }
        tile.updateTile();

        long t = System.currentTimeMillis(), elapsed;

        try {
            NetworkUtils.pokeSpeedTest();
        } catch (Exception e) {
            Log.e("030-st", "err", e);
            err = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.setSubtitle("Error");
            }
        }

        if ((elapsed = t - System.currentTimeMillis()) < 1000) {
            try {
                Thread.sleep(1000 - elapsed);
            } catch (InterruptedException ignored) {}
        }

        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_foreground));
        if (!err && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
            tile.setSubtitle("");
        }
        tile.updateTile();
        running = false;
    }
}
