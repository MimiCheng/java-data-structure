package com.firstpackage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;


class Song implements Comparable<Song> {
    int priority;
    String song_name;
    String singer;
    int duration;

    Song(int priority, String song_name, String singer, int duration) {
        this.priority = priority;
        this.singer = singer;
        this.song_name = song_name;
        this.duration = duration;
    }

    @Override
    public int compareTo(Song o) {
        return Integer.compare(priority, o.priority);
    }
}

class Temp implements Comparable<Temp> {
    String song_name;
    int priority;

    Temp(String song_name, int priority) {
        this.song_name = song_name;
        this.priority = priority;
    }

    @Override
    public int compareTo(Temp o) {
        if (priority != o.priority) {
            return Integer.compare(priority, o.priority);
        } else {
            return song_name.compareTo(o.song_name);
        }
    }
}

class MusicPlayer {
    ArrayList<String> play_sequence;
    ArrayList<Integer> play_duration;
    ArrayList<Song> musiclist;
    ArrayList<String> playlist;
    List<Integer> accum_duration;
    Hashtable<String, Integer> songs_dict;
    Hashtable<String, String> artist_dict;
    HashMap<String, Integer> count_play_dict;
    TreeSet<Temp> tempmusic;

    int total_Song_Count = 0;
    int capacity;
    int track_refill = 0;

    MusicPlayer(int capacity) {
        System.out.println("<<<<<< Initialize Music Player >>>>>>");
        this.capacity = capacity;
        musiclist = new ArrayList<Song>();
        play_sequence = new ArrayList<String>();
        play_duration = new ArrayList<Integer>();
        playlist = new ArrayList<String>();
        accum_duration = new ArrayList<Integer>();
        tempmusic = new TreeSet<Temp>();
        songs_dict = new Hashtable<>();
        artist_dict = new Hashtable<>();
        count_play_dict = new HashMap<>();
    }


    void addInitSong(int prior, String song_name, String singer, int duration) {
        musiclist.add(total_Song_Count, new Song(prior, song_name, singer, duration));
        total_Song_Count++;
    }

    void readFromFile(Scanner scanner) {
        //add songs from ASCII file to Array
        String line = scanner.nextLine();
        String[] splitted = line.split("\t", 3);
        String artist = splitted[0];
        String songname = splitted[1];
        int duration = Integer.parseInt(splitted[2]);
        addInitSong(0, songname, artist, duration);
    }

    void topk(int num_play, int top_k) {
        System.out.println("\n------------------------SHOW TOP K-------------------------");
        int last_val = 0;
        int add_seq = play_sequence.size() - num_play;
        LinkedHashMap<String, Integer> accum_dict = new LinkedHashMap<String, Integer>();
        accum_duration = (ArrayList<Integer>) play_duration.clone();

        // calculate lapse time for each song that has already played
        for (int i = 0; i < accum_duration.size() - 1; i++) {
            int accum_val = accum_duration.get(i + 1) + accum_duration.get(i);
            accum_duration.set(i + 1, accum_val);
            last_val = accum_duration.get(accum_duration.size() - 1);
        }

        for (int i = 0; i < accum_duration.size(); i++) {
            String played_song = play_sequence.get(i);
            if (!accum_dict.containsKey(played_song)) {
                accum_dict.put(played_song, last_val - accum_duration.get(i));
            }
            if (i >= add_seq) {
                if (!count_play_dict.containsKey(played_song)) {
                    count_play_dict.put(played_song, 1);
                } else {
                    count_play_dict.put(played_song, count_play_dict.get(played_song) + 1);
                }
            }
        }

        // sort total number of songs played by descending order
        Map<String, Integer> count_play_desc = count_play_dict.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        // get top played song name
        List<String> most_played_songs = new ArrayList<>(count_play_desc.keySet());
        // handle when number of top k is more than actual songs in the queue
        int show_top_items = Math.min(top_k, most_played_songs.size());
        // show top songs and its lapse time
        for (int k = 0; k < show_top_items; k++) {
            String most_played_song = most_played_songs.get(k);
            int meta_duration = songs_dict.get(most_played_song);

            int lapse = accum_dict.get(most_played_song);
            int num_occur = count_play_dict.get(most_played_song);
            int wait_time = (lapse - (meta_duration * (num_occur - 1))) / num_occur;
            System.out.println(artist_dict.get(most_played_song) + " " + most_played_song + ": " + wait_time);
        }
        System.out.println("-----------------------------------------------------------\n");
    }

    void get_metadata() {
        // get pairs of song name + duration, and song name + artist name
        for (Song m : musiclist) {
            if (!songs_dict.containsKey(m.song_name)) {
                songs_dict.put(m.song_name, m.duration);
            }

            if (!artist_dict.containsKey(m.song_name)) {
                artist_dict.put(m.song_name, m.singer);
            }
        }
    }

    void play(int percent_limit, int refill, int topk_item) {
        int limit_size = Math.round((percent_limit * capacity) / 100f);
        // iterlate through all songs in the file
        for (Song m : musiclist) {
            if (tempmusic.size() == capacity || track_refill == (refill - 1)) {
                track_refill++; // track number of refill
                int num_play = playlist.size() - limit_size;
                // remove first song in the playlist and show as current playing song
                for (int i = 0; i < num_play; i++) {
                    String play_song = tempmusic.first().song_name;
                    int current_play_duration = songs_dict.get(play_song);
                    String now_playing_song = playlist.remove(0);
                    System.out.println("Playing... " + artist_dict.get(now_playing_song) + " " + now_playing_song + " " + songs_dict.get(now_playing_song));
                    //add current playing song namd and its duration for calculating lapse time afterward in topk()
                    play_sequence.add(play_song);
                    play_duration.add(current_play_duration);
                    //remove songs in the playlist (queue)
                    tempmusic.removeIf(value -> value.song_name.equals(play_song));
                }
                // show Top-k items
                if (num_play != 0)
                    topk(num_play, topk_item);

            } else {
                //verify if the next song is already in the playlist.
                // if yes, don't add it in the playlist, but increase the priority.
                // If no, add the song in the playlist, and initialize its priority to 0
                if (!playlist.contains(m.song_name)) {
                    tempmusic.add(new Temp(m.song_name, 0));
                    playlist.add(m.song_name);
                } else {
                    for (Temp t : tempmusic) {
                        if (t.song_name.equals(m.song_name)) {
                            t.priority -= 1; //increase priority of song
                        }
                    }
                }
            }
            if ((track_refill == refill) && playlist.size() == capacity) {
                // show top k for last refill
                play_sequence.addAll(playlist);
                for (int p = 0; p < capacity - limit_size; p++) {
                    String now_playing_song = playlist.get(p);
                    System.out.println("Playing... " + artist_dict.get(now_playing_song) + " " + now_playing_song + " " + songs_dict.get(now_playing_song));
                    play_duration.add(songs_dict.get(now_playing_song));
                }
                topk(capacity, topk_item);
                break;
            }
        }
    }
}

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        String requests = args[0];
        String capacity = args[1];
        int percent_limit = Integer.parseInt(args[2]);
        int refill = Integer.parseInt(args[3]);
        int topk = Integer.parseInt(args[4]);

        Scanner scanner = new Scanner(new FileInputStream(requests));
        MusicPlayer mp = new MusicPlayer(Integer.parseInt(capacity));

        while (scanner.hasNext()) {
            mp.readFromFile(scanner);
        }
        mp.get_metadata();
        mp.play(percent_limit, refill, topk);
    }
}


