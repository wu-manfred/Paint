
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.min;

class Line {
    private Color color;
    private float width;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    Line(Color color, float width, int x1, int y1, int x2, int y2){
        this.color = color;
        this.width = width;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    public Color getColor(){
        return color;
    }
    public float getStroke(){
        return width;
    }
    public int getx1(){
        return x1;
    }
    public int gety1(){
        return y1;
    }
    public int getx2(){
        return x2;
    }
    public int gety2(){
        return y2;
    }
}
class Tick {
    private int timestamp;
    private List<Line> lines;
    Tick(int timestamp, List<Line> lines){
        this.timestamp = timestamp;
        this.lines = lines;
    }
    public int getTimestamp(){
        return timestamp;
    }
    public List<Line> getLines(){
        return lines;
    }
}
public class Model {
    // current color
    private int x1, x2, y1, y2;
    private boolean saved = true;
    public static final int SEGMENT = 100;
    public int numticks;
    public List<Line> all_lines;
    public List <Line> curline;
    public List<Tick> all_ticks;
    //public List<Color> cur_colors;
    public Color curcolor;
    public float stroke_width;
    public int timeline;
    public int progress;
    public boolean paused = true;
    /** The observers that are watching this model for changes. */
    private List<Observer> observers;
    /**
     * Create a new model.
     */
    public Model() {
        this.observers = new ArrayList();
        this.all_lines = new ArrayList<Line>();
        this.all_ticks = new ArrayList<Tick>();
        this.curline = new ArrayList<Line>();
        this.all_ticks.add(new Tick(0, new ArrayList<Line>()));
        this.curcolor = Color.BLACK;
        this.stroke_width = 6;
        this.numticks = 100; // numticks is the number of scenes (1 more than # lines)
        this.timeline = 100; // timeline is the current scene based on playback slider
        //   (1 more than # visible lines)
        this.progress = 0;
    }
    /**
     * Add an observer to be notified when this model changes.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }
    /**
     * Remove an observer from this model.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }
    /**
     * Notify all observers that the model has changed.
     */
    public void notifyObservers() {
        for (Observer observer: this.observers) {
            observer.update(this);
        }
    }
    public void canvas_press(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        x2 = x1;
        y2 = y1;
        saved = false;
//        curline.clear();
        if (timeline != numticks){
            all_lines.clear();

            for (Line line : all_ticks.get((timeline/SEGMENT) - 1).getLines()){
                all_lines.add(line);
            }
            ArrayList<Line> progress_lines = progress_lines(timeline - 100);

            List<Tick> copy = new ArrayList<Tick>();
            for (int i = 0; i<(timeline/SEGMENT); i++){
                copy.add(all_ticks.get(i));
            }
            for (Line line : progress_lines){
                all_lines.add(line);
                curline.add(line);
            }

//            if (progress != 0){
//                copy.add(new Tick(timeline/SEGMENT, all_lines));
//                progress = 0;
//            }
            all_ticks.clear();
            all_ticks = copy;
            numticks = all_ticks.size() * SEGMENT;
            if (progress != 0){
                add_tick();

//                progress = 0;
            }

            //timeline = numticks;
        } else {
            curline.clear();
        }
        curline.add(new Line(curcolor, stroke_width, x1, y1, x2, y2));
        all_lines.add(new Line(curcolor, stroke_width, x1, y1, x2, y2));
        notifyObservers();
    }
    public void canvas_drag(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        curline.add(new Line(curcolor, stroke_width, x1, y1, x2, y2));
        all_lines.add(new Line(curcolor, stroke_width, x1, y1, x2, y2));
        notifyObservers();
        x2 = x1;
        y2 = y1;
    }
    public void add_tick() {
//        curline.clear();
        List<Line> copy = new ArrayList<Line>();
        for (Line line : all_lines){
            copy.add(line);
        }
        all_ticks.add(new Tick(numticks/SEGMENT, copy));
        numticks+=100;
        timeline=numticks;
        notifyObservers();
    }
    public int get_timeline(){
        return timeline;
    }
    public int get_numticks(){
        return numticks;
    }
    public void slider_moved(int value) {
        curline.clear();
        timeline = value + 100;
        progress = value % SEGMENT;
        notifyObservers();
    }
    public Tick findcurTick(int time) {
        Tick curtick = all_ticks.get(time/SEGMENT);
        return curtick;
    }

    public ArrayList<Line> progress_lines(int time){
        ArrayList<Line> result = new ArrayList<Line>();
        ArrayList<Line> temp = new ArrayList<Line>();
        if (progress == 0) return temp;
        int start = findcurTick(time).getLines().size();
        Tick curtick = all_ticks.get(time/SEGMENT + 1);
        int total = curtick.getLines().size();
        int count = 0;
        for (int i = start; i<total- 1; i++){
            temp.add(curtick.getLines().get(i));
            count++;
        }
        int num_lines = progress * count / 100;
        for (int i = 0; i<num_lines; i++){
            result.add(temp.get(i));
        }
        return result;
    }
    public void start_timeline() {
        timeline = 0;
        notifyObservers();
    }
    public void end_timeline() {
        timeline = numticks;
        notifyObservers();
    }
    public List<Line> getCurline(){
        return curline;
    }
    public int getx1() {
        return x1;
    }
    public int getx2() {
        return x2;
    }
    public int gety1() {
        return y1;
    }
    public int gety2() {
        return y2;
    }
    public void change_color(Color color) {
        curcolor = color;
    }
    public void change_stroke(int i) {
        stroke_width = (i+1) * 3;
    }

    public void start_playing() {
        paused = !paused;
        final Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (timeline < numticks && !paused) {
                    timeline++;
                    notifyObservers();
                } else {
                    ((javax.swing.Timer) e.getSource()).stop();
                    paused = true;
                    notifyObservers();
                }
            }
        });
        timer.start();
    }

    public void start_backplay() {
        paused = !paused;
        final Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (timeline > 100 && !paused) {
                    timeline--;
                    notifyObservers();
                } else {
                    ((javax.swing.Timer) e.getSource()).stop();
                    paused = true;
                    notifyObservers();
                }
            }
        });
        timer.start();
    }

    public void save() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("./saved_drawings"));

        String save_file = "";
        String tick_string = "";
        String line_string = "";
        int num_ticks = 0;
        int num_lines = 0;
        int tick_lines = 0;
        ArrayList<Line> copy = new ArrayList<Line>();
        String tickslines = "";
        for (Tick tick : all_ticks){
            num_ticks++;
            tick_string += Integer.toString(tick.getTimestamp());
            tick_string += "\n";
            for (Line line : tick.getLines()){
                tick_lines++;
                tickslines += Integer.toString(line.getColor().getRGB()) + " ";
                tickslines += Float.toString(line.getStroke()) + " ";
                tickslines += Integer.toString(line.getx1()) + " " + Integer.toString(line.gety1()) + " " + Integer.toString(line.getx2()) + " " + Integer.toString(line.gety2());
                tickslines += "\n";
            }
            tick_string += Integer.toString(tick_lines) + "\n";
            tick_string += tickslines;
        }
        num_lines = 0;
        for (Line line : all_lines){
            num_lines++;
            line_string += Integer.toString(line.getColor().getRGB()) + " ";
            line_string += Float.toString(line.getStroke()) + " ";
            line_string += Integer.toString(line.getx1()) + " " + Integer.toString(line.gety1()) + " " + Integer.toString(line.getx2()) + " " + Integer.toString(line.gety2());
            line_string += "\n";
        }

        save_file += Integer.toString(num_ticks) + "\n";
        save_file += tick_string;
        save_file += Integer.toString(num_lines) + "\n";
        save_file += line_string;
        save_file += Integer.toString(curcolor.getRGB()) + "\n";
        save_file += Float.toString(stroke_width) + "\n";
        save_file += numticks + "\n";
        save_file += timeline + "\n";
        save_file += progress + "\n";

        int retrival = chooser.showSaveDialog(null);
        BufferedWriter writer = null;
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try {
                File file = new File(chooser.getSelectedFile().getPath() + ".data");
                if (file.exists()){
                    int response = JOptionPane.showConfirmDialog(null, "Do you want to overwrite existing file?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (response != JOptionPane.YES_OPTION){
                        return;
                    }
                }
                writer = new BufferedWriter(new FileWriter(chooser.getSelectedFile().getPath() + ".data"));
                writer.write(save_file);
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally{
                try {
                    if (null != writer) {
                        writer.close();
                    }
                } catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }
        saved = true;
    }

    public void load() {
        if (saved) {
            all_ticks.clear();
            all_lines.clear();

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("./saved_drawings"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Data files", "data");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            String file_path = "";
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file_path = chooser.getSelectedFile().getPath();
            }
            File load_file = new File(file_path);
            Scanner scanner = null;
            try {
                scanner = new Scanner(load_file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                int num_ticks = Integer.parseInt(scanner.nextLine());
                for (int i = 0; i < num_ticks; i++) {
                    int timestamp = Integer.parseInt(scanner.nextLine());
                    int num_lines = Integer.parseInt(scanner.nextLine());
                    ArrayList<Line> lines = new ArrayList<Line>();

                    for (int j = 0; j < num_lines; j++) {

                        String[] split = scanner.nextLine().split("\\s+");

                        lines.add(new Line(new Color(Integer.parseInt(split[0])), Float.parseFloat(split[1]),
                                Integer.parseInt(split[2]), Integer.parseInt(split[3]),
                                Integer.parseInt(split[4]), Integer.parseInt(split[5])));
                    }
                    all_ticks.add(new Tick(timestamp, lines));
                }
                int all_lines_total = Integer.parseInt(scanner.nextLine());
                for (int i = 0; i < all_lines_total; i++) {

                    String[] split = scanner.nextLine().split("\\s+");

                    all_lines.add(new Line(new Color(Integer.parseInt(split[0])), Float.parseFloat(split[1]),
                            Integer.parseInt(split[2]), Integer.parseInt(split[3]),
                            Integer.parseInt(split[4]), Integer.parseInt(split[5])));
                }
                curcolor = new Color(Integer.parseInt(scanner.nextLine()));
                stroke_width = Float.parseFloat(scanner.nextLine());
                numticks = Integer.parseInt(scanner.nextLine());
                timeline = Integer.parseInt(scanner.nextLine());
                progress = Integer.parseInt(scanner.nextLine());
                saved = true;
                notifyObservers();

            } finally {
                scanner.close();
            }
        } else{
            int confirmed = JOptionPane.showConfirmDialog(null, "Would you like to save before loading?","EXIT",JOptionPane.YES_NO_OPTION);
            if(confirmed == JOptionPane.YES_OPTION){
                this.save();
            } else{
                saved = true;
                load();
            }
        }

    }

    public void reset() {
        if (saved) {
            curcolor = Color.BLACK;
            stroke_width = 6;
            timeline = 100;
            numticks = 100;
            progress = 0;
            all_lines.clear();
            all_ticks.clear();
            all_ticks.add(new Tick(0, new ArrayList<Line>()));
            curline.clear();
            paused = true;
        } else{
            int confirmed = JOptionPane.showConfirmDialog(null, "Would you like to save before making a new drawing?","NEW",JOptionPane.YES_NO_OPTION);
            if(confirmed == JOptionPane.YES_OPTION){
                this.save();
            } else{
                saved = true;
                reset();
            }
        }
    }

    public void quit() {
        if (saved){
            System.exit(0);
        } else{
            int confirmed = JOptionPane.showConfirmDialog(null, "Would you like to save before quitting?","EXIT",JOptionPane.YES_NO_OPTION);
            if(confirmed == JOptionPane.YES_OPTION){
                this.save();
            } else {
                saved = true;
                quit();
            }
        }
    }

}