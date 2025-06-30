import javax.sound.midi.*;
import javax.swing.*;
import static javax.sound.midi.ShortMessage.*;
import java.awt.*;
import java.util.Random;

public class MusicTest1 {
    private myDrawPanel panel;
    private Random random = new Random();

    public static void main(String[] args) throws Exception {
        MusicTest1 mt = new MusicTest1();
        mt.play();
    }

    public void setUpGUI(){
        JFrame frame = new JFrame("My First Music Video");
        panel = new myDrawPanel();
        frame.setContentPane(panel);
        frame.setBounds(30, 30, 300, 300);
        frame.setVisible(true);
    }

    public void play(){
        setUpGUI();

        try {
            Sequencer player = MidiSystem.getSequencer();
            player.open();

            int[] eventsIWant = {127};
            player.addControllerEventListener(panel, eventsIWant);

            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();

            int note;
            for (int i = 5; i < 60; i++) {
                note = random.nextInt(50) + 1;
                track.add(makeEvent(NOTE_ON, 1, note, 100, i));
                track.add(makeEvent(CONTROL_CHANGE, 1, 127, 0, i));
                track.add(makeEvent(NOTE_OFF, 1, note, 100, i+2));
            }
            

            player.setSequence(seq);
            player.start();
            player.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
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

    class myDrawPanel extends JPanel implements ControllerEventListener{
        private boolean msg = false;

        public void controlChange(ShortMessage event){
            msg = true;
            repaint();
        }

        public void paintComponent(Graphics g){
            if (msg) {
                int r = random.nextInt(250);
                int gr = random.nextInt(250);
                int b = random.nextInt(250);

                g.setColor(new Color(r, gr, b));

                int height = random.nextInt(120) + 10;
                int width = random.nextInt(120) + 10;
                int xpos = random.nextInt(40) + 10;
                int ypos = random.nextInt(40) + 10;

                g.fillRect(xpos, ypos, width, height);
                msg = false;
            }
        }
    }
}
