package com.game.util;

import javax.sound.midi.*;
import java.io.IOException;
import java.net.URL;

public class MidiSequence {
	private Sequencer seq;
	private Sequence song;
	private String fileName;
	private boolean looping = false;
	private int repeat = 0;
	
	public Sequence getSong() { return song; }
	public String getFilename() { return fileName; }
	public boolean isLooping() { return looping; }
	public void setLooping(boolean _looping) { looping = _looping; }
	public int getRepeat() { return repeat; }
	public void setRepeat(int _repeat) { repeat = _repeat; }
	
	public boolean isLoaded() {
		return (seq.isOpen());
	}
	
	public MidiSequence() {
		try {
			seq = MidiSystem.getSequencer();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public MidiSequence(String midi) {
		this();
		load(midi);
	}
	
	private URL getURL(String fileName) {
		URL url = null;
		try {
			url = this.getClass().getResource(fileName);
		} catch (Exception e) {}
		return url;
	}
	
	public boolean load(String midi) {
		if (seq == null) return false;
		try {
			fileName = midi;
			song = MidiSystem.getSequence(getURL(fileName));
			seq.setSequence(song);
			seq.open();
			return true;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void play() {
		if (!isLoaded()) return;
		
		if (looping)
			seq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
		else
			seq.setLoopCount(repeat);
		seq.start();
	}
	
	public void stop() {
		if (!isLoaded()) return;

		seq.stop();
	}
}
