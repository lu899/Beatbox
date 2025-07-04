import javax.sound.midi.*;
import javax.swing.*;
import static javax.sound.midi.ShortMessage.*;
import java.awt.*;
import java.util.ArrayList;

public class MusicTest1 {
    private ArrayList<JCheckBox> checkBoxList;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    String[] instrumentNames = {"Bass Drum", "Closed H1-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "Hi Tom", "Bi Bingo", "Maracas", "Whistle", "Low Conga", "Cowbell", "VibrasLap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};

    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) throws Exception {
        MusicTest1 mt = new MusicTest1();
        mt.setUpGUI();
    }

    public void setUpGUI(){
        JFrame frame = new JFrame("Cyber BeatBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Box buttonbBox = new Box(BoxLayout.X_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(e -> buildTrackandStart());
        buttonbBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> sequencer.stop());

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(e -> changeTempo(1.03f));
        buttonbBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(e -> changeTempo(0.97f));
        buttonbBox.add(downTempo);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (String instrumentname : instrumentNames) {
            JLabel label = new JLabel(instrumentname);
            label.setBorder(BorderFactory.createEmptyBorder(4, 1, 4, 1));
            nameBox.add(label);
        }

        background.add(BorderLayout.EAST, buttonbBox);
        background.add(BorderLayout.WEST, nameBox);

        frame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);

        JPanel mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        checkBoxList = new ArrayList<>();
        for(int i=0; i<256; i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    }

    public void setUpMidi(){
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackandStart(){
        int[] trackList;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for(int i=0; i<16; i++){
            trackList = new int[16];
            int key = instruments[i];

            for(int j=0; j<16; j++){
                JCheckBox jc = checkBoxList.get(j + 16 * i);
                if(jc.isSelected()){
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }

            makeTracks(trackList);
            track.add(makeEvent(PROGRAM_CHANGE, 9, 1, 0, 15));

            try {
                sequencer.setSequence(sequence);
                sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
                sequencer.setTempoInBPM(120);
                sequencer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void changeTempo(float tempomultiplier){
        float tempoFactor = sequencer.getTempoFactor();
        sequencer.setTempoFactor(tempoFactor * tempomultiplier);
    }

    private void makeTracks(int[] list){
        for (int i=0; i<16; i++){
            int key = list[i];

            if(key != 0){
                track.add(makeEvent(NOTE_ON, 9, key, 100, i));
                track.add(makeEvent(NOTE_OFF, 9, key, 100, i + 1));
            }
        }
    }

    public static MidiEvent makeEvent(int command, int channel, int one, int two, int tick){
        MidiEvent event = null;
        try{
            ShortMessage msg = new ShortMessage();
            msg.setMessage(command, channel, one, two);
            event = new MidiEvent(msg, tick);
        } catch(Exception e){
            e.printStackTrace();
        }

        return event;
    }
}
