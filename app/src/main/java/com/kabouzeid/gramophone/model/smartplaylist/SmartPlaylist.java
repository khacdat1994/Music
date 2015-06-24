package com.kabouzeid.gramophone.model.smartplaylist;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kabouzeid.gramophone.R;
import com.kabouzeid.gramophone.adapter.songadapter.smartplaylist.SmartPlaylistSongAdapter;
import com.kabouzeid.gramophone.interfaces.CabHolder;
import com.kabouzeid.gramophone.model.Playlist;
import com.kabouzeid.gramophone.model.Song;

import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public abstract class SmartPlaylist extends Playlist {
    private static final long serialVersionUID = 3013701295356403681L;

    @DrawableRes
    public final int iconRes;

    public SmartPlaylist(final String name, final int iconRes) {
        super(-1, name);
        this.iconRes = iconRes;
    }

    public SmartPlaylist() {
        super();
        this.iconRes = R.drawable.ic_queue_music_white_24dp;
    }

    public abstract ArrayList<Song> getSongs(Context context);

    public abstract SmartPlaylistSongAdapter createAdapter(AppCompatActivity activity, @Nullable CabHolder cabHolder);

    public abstract void clear(Context context);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + iconRes;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SmartPlaylist other = (SmartPlaylist) obj;
            return iconRes == other.iconRes;
        }
        return false;
    }
}