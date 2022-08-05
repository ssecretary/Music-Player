package com.music.mp3player.audio.mediaplayer.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.music.mp3player.audio.mediaplayer.AddToPlaylistClass;
import com.music.mp3player.audio.mediaplayer.DataBase.DbHelper;
import com.music.mp3player.audio.mediaplayer.ModelClass.AudioModel;
import com.music.mp3player.audio.mediaplayer.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.music.mp3player.audio.mediaplayer.activities.MainActivity.mainActivity;

//import static com.example.musicplayer.Service.MusicService.mediaPlayer;


public class GenreSongsAdapter extends RecyclerView.Adapter<GenreSongsAdapter.ViewHolder> {

    private Context context;
    private List<AudioModel> genreSongList;
    //private albumSongListAdapter.playingSong song;
    private DbHelper DB;

    public GenreSongsAdapter(Context context, List<AudioModel> genreSongList) {
        this.context = context;
        this.genreSongList = genreSongList;
        //this.song = song;
        DB = new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.playlist_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String name = genreSongList.get(position).getName();
        String artist = genreSongList.get(position).getArtist();
        String album = genreSongList.get(position).getAlbum();
        String duration = genreSongList.get(position).getDuration();
        String id = genreSongList.get(position).getId();
        String url = genreSongList.get(position).getPath();
        Uri albumArtUri = genreSongList.get(position).getAlbumArt();

        //Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Albums.Al})
        Bitmap bitmap = null;

        //ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), albumArtUri);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
            // bitmap = ImageDecoder.decodeBitmap(source);
        } catch (FileNotFoundException exception) {
        } catch (IOException e) {

            e.printStackTrace();
        }

        if (bitmap != null) {
            holder.ivalbumSongListItem.setImageBitmap(bitmap);
        }
        else
        {
            holder.ivalbumSongListItem.setImageResource(R.drawable.music_photo);
        }

        holder.tvalbumSongListCount.setText(artist);
        holder.tvalbumSongListName.setText(name);

        holder.rlalbumSongListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(context, MainActivity.class);
                i.putExtra("ActivityName", "Genre");
                i.putExtra("Position", position);
                context.startActivity(i);*/
                mainActivity.onClickSong(position, "Genre");

                //song.onPlaying(position, name, artist);
            }
        });

        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.ivMore);
                popupMenu.inflate(R.menu.popup_menu);
                Menu opt = popupMenu.getMenu();

                Cursor checkFav = DB.getFavouritesFromId(name);
                if(checkFav.getCount() > 0)
                {
                    opt.getItem(0).setTitle("Remove From Favourites");
                }
                else
                {
                    opt.getItem(0).setTitle("Add to Favourites");
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if(menuItem.getTitle().equals("Add to Favourites") || menuItem.getTitle().equals("Remove From Favourites")) {
                            addSongsToFavourites(position);
                        }
                        else
                        {
                            AddToPlaylistClass add = new AddToPlaylistClass(context, position, genreSongList);
                            add.showBottomSheetDialog();
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return genreSongList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivalbumSongListItem, ivMore;
        private TextView tvalbumSongListName, tvalbumSongListCount;
        private View valbumSongListDivider;
        private RelativeLayout rlalbumSongListItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivalbumSongListItem = itemView.findViewById(R.id.ivPlayListItem);
            ivMore = itemView.findViewById(R.id.ivMoreOptionPlaylistItem);
            tvalbumSongListName = itemView.findViewById(R.id.tvPlaylistName);
            tvalbumSongListCount = itemView.findViewById(R.id.tvPlaylistSongCount);
            valbumSongListDivider = itemView.findViewById(R.id.vPlaylistDivider);
            rlalbumSongListItem = itemView.findViewById(R.id.rlPlaylistItem);
        }
    }

    public void addSongsToFavourites(int position)
    {
        Cursor checkUser = DB.getFavouritesFromId(genreSongList.get(position).getName());

        if(checkUser.getCount() <= 0) {
            boolean checkAdd = DB.addToFavourites(genreSongList.get(position).getPath(), genreSongList.get(position).getName(), genreSongList.get(position).getArtist(), genreSongList.get(position).getAlbum(), genreSongList.get(position).getDuration(), genreSongList.get(position).getId(), genreSongList.get(position).getAlbumArt().toString());
            if(checkAdd)
            {
                Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
                //ivFavouritesSong.setColorFilter(ContextCompat.getColor(PlayingSongActivity.this, R.color.SelectedTab), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
        else
        {
            boolean checkDelFav = DB.removeFromFavourites(genreSongList.get(position).getName());
            if(checkDelFav) {
                Toast.makeText(context, "Remove from Favourites", Toast.LENGTH_SHORT).show();
                //ivFavouritesSong.setColorFilter(ContextCompat.getColor(PlayingSongActivity.this, R.color.countTxt), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
    }
}
