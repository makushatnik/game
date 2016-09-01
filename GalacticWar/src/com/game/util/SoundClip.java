package com.game.util;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class SoundClip {
    private AudioInputStream sample;
    private Clip clip;
    private boolean looping = false;
    private int repeat = 0;
    private String fileName;

    public Clip getClip() { return clip; }

    public boolean isLooping() { return looping; }
    public void setLooping(boolean _looping) { looping = _looping; }

    public int getRepeat() { return repeat; }
    public void setRepeat(int _repeat) { repeat = _repeat; }

    public String getFilename() { return fileName; }
    public void setFilename(String _fileName) { fileName = _fileName; }

    public boolean isLoaded() {
        return (sample != null);
    }

    public SoundClip() {
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public SoundClip(String audio) {
        this();
        load(audio);
    }

    private URL getURL(String fileName) {
        URL url = null;
        try {
            url = this.getClass().getResource(fileName);
        } catch (Exception e) {}
        return url;
    }

    public boolean load(String audio) {
        try {
            setFilename(audio);
            sample = AudioSystem.getAudioInputStream(getURL(fileName));
            clip.open(sample);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void play() {
        if (!isLoaded()) return;

        clip.setFramePosition(0);
        if (looping)
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        else
            clip.loop(repeat);
    }

    public void stop() {
        clip.stop();
    }
}
