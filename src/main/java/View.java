import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class View extends JFrame implements Observer {
    private Model model;
    private JPanel canvas;
    private JSlider playback;
    private boolean swapping_color = false;
    private boolean tick_added = false;
    /**
     * Create a new View.
     */
    public View(Model model) {
        // Set up the window.
        this.setTitle("m84wu CS349 A2");
        this.setMinimumSize(new Dimension(400, 425));
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        // Hook up this observer so that it will be notified when the model
        // changes.
        this.model = model;
        model.addObserver(this);
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(800, 25));
        menuBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        /* Adding elements to menu */
        JMenu menu = new JMenu("File");
        JMenuItem save_doodle = new JMenuItem("Save");
        save_doodle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.save();
            }
        });

        JMenuItem load_doodle = new JMenuItem("Load");
        load_doodle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.load();
            }
        });
        JMenuItem new_doodle = new JMenuItem("New");
        new_doodle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.reset();
            }
        });
        JMenuItem quit_doodle = new JMenuItem("Quit");
        quit_doodle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.quit();
            }
        });

        menu.add(new_doodle);
        menu.add(load_doodle);
        menu.add(save_doodle);
        menu.add(quit_doodle);
        menuBar.add(menu);
        JPanel mid_section = new JPanel();
        mid_section.setPreferredSize(new Dimension(800, 475));
        mid_section.setLayout(new BoxLayout(mid_section, BoxLayout.X_AXIS));
        JPanel properties = new JPanel();
        properties.setPreferredSize(new Dimension(100, 400));
        properties.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
        properties.setBackground(Color.GRAY);
        properties.setLayout(new BoxLayout(properties, BoxLayout.Y_AXIS));
        /* adding color palette */
        JPanel palette = new JPanel();
        palette.setPreferredSize(new Dimension(60, 150));
        palette.setMaximumSize(palette.getPreferredSize());
        palette.setLayout(new GridLayout(5, 2));

        JLabel msg = new JLabel("");
//        msg.setPreferredSize(new Dimension(100, 60));
//        msg.setMinimumSize(msg.getPreferredSize());
////        msg.setHorizontalAlignment(SwingConstants.LEFT);
//        msg.setMaximumSize(msg.getPreferredSize());

        JButton c1 = new JButton();
        c1.setBackground(Color.BLACK);
        JButton c2 = new JButton();
        c2.setBackground(Color.WHITE);
        JButton c3 = new JButton();
        c3.setBackground(Color.BLUE);
        JButton c4 = new JButton();
        c4.setBackground(Color.RED);
        JButton c5 = new JButton();
        c5.setBackground(Color.GREEN);
        JButton c6 = new JButton();
        c6.setBackground(Color.YELLOW);
        JButton c7 = new JButton();
        c7.setBackground(Color.CYAN);
        JButton more = new JButton("...");
//        JButton curc = new JButton() {
//            @Override
//            public void paintComponent(Graphics g){
//                super.paintComponent(g);
//                curc.setBackground(Color.BLUE);
//            }
//        };
        JPanel curc1 = new JPanel();
        JPanel curc2 = new JPanel();
        curc1.setBackground(model.curcolor);
        curc2.setBackground(model.curcolor);

        palette.add(c1);
        palette.add(c2);
        palette.add(c3);
        palette.add(c4);
        palette.add(c5);
        palette.add(c6);
        palette.add(c7);

        for (Component c : palette.getComponents()) {
            if (c instanceof JButton) {
                JButton new_c = (JButton) c;
                new_c.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!swapping_color) {
                            model.change_color(new_c.getBackground());
                            curc1.setBackground(new_c.getBackground());
                            curc2.setBackground(new_c.getBackground());
                        } else {
                            Color initial = new_c.getBackground();
                            Color updated = JColorChooser.showDialog(null, "Change Color", initial);
                            if (updated != null) {
                                new_c.setBackground(updated);
                            }
                            model.change_color(new_c.getBackground());
                            curc1.setBackground(new_c.getBackground());
                            curc2.setBackground(new_c.getBackground());
                            swapping_color = !swapping_color;
                            if (swapping_color) {
                                msg.setText("<html>Select<br/> Color</html>");
                            } else {
                                msg.setText("");
                            }
                        }
                    }
                });
            }
        }
        palette.add(more);
        palette.add(curc1);
        palette.add(curc2);
        more.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapping_color = !swapping_color;
                if (swapping_color) {
                    msg.setText("<html>Select<br/>Color</html>");
                } else {
                    msg.setText("");
                }
            }
        });
        for (Component c : palette.getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).setOpaque(true);
                ((JButton) c).setBorderPainted(false);
            }
        }
        /* adding stroke thickness options */
        JPanel stroke = new JPanel();
        stroke.setPreferredSize(new Dimension(40, 90));
        stroke.setMaximumSize(stroke.getPreferredSize());
        stroke.setLayout(new GridLayout(3, 1));
        JRadioButton t1 = new JRadioButton();
        ImageIcon t1_icon = new ImageIcon("./icons/Thin.png");
        t1.setIcon(t1_icon);

        JRadioButton t2 = new JRadioButton();
        ImageIcon t2_icon = new ImageIcon("./icons/Regular.png");
        t2.setIcon(t2_icon);

        JRadioButton t3 = new JRadioButton();
        ImageIcon t3_icon = new ImageIcon("./icons/Thick.png");
        t3.setIcon(t3_icon);

        ButtonGroup stroke_type = new ButtonGroup();
        stroke_type.add(t1);
        stroke_type.add(t2);
        stroke_type.add(t3);
        t2.setSelected(true);
        t1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.change_stroke(0);
            }
        });
        t2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.change_stroke(1);
            }
        });
        t3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.change_stroke(2);
            }
        });
        stroke.add(t1);
        stroke.add(t2);
        stroke.add(t3);
        properties.add(Box.createRigidArea(new Dimension(0, 10)));
        properties.add(palette);
        properties.add(Box.createRigidArea(new Dimension(0, 5)));
        //properties.add(Box.createVerticalGlue());
        properties.add(msg);
        properties.add(Box.createRigidArea(new Dimension(0, 10)));
        properties.add(Box.createVerticalGlue());
        properties.add(stroke);
        properties.add(Box.createRigidArea(new Dimension(0, 10)));
        /* adding panel that will act as the canvas */
        canvas = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Tick curtick = model.findcurTick(playback.getValue());
                ArrayList<Line> progress_bar = model.progress_lines(playback.getValue());
                Graphics2D g2 = (Graphics2D) g;
                for (Line line : curtick.getLines()) {
                    g2.setStroke(new BasicStroke(line.getStroke()));
                    g2.setColor(line.getColor());
                    //g2.drawLine(line.getx1(), line.gety1(), line.getx2(), line.gety2());
                    g2.draw(new Line2D.Float(line.getx1(), line.gety1(), line.getx2(), line.gety2()));
                }
                for (Line line : progress_bar) {
                    g2.setStroke(new BasicStroke(line.getStroke()));
                    g2.setColor(line.getColor());
                    //g2.drawLine(line.getx1(), line.gety1(), line.getx2(), line.gety2());
                    g2.draw(new Line2D.Float(line.getx1(), line.gety1(), line.getx2(), line.gety2()));
                }
                for (Line line : model.curline) {
                    g2.setStroke(new BasicStroke(line.getStroke()));
                    g2.setColor(line.getColor());
                    //g2.drawLine(line.getx1(), line.gety1(), line.getx2(), line.gety2());
                    g2.draw(new Line2D.Float(line.getx1(), line.gety1(), line.getx2(), line.gety2()));
                }
            }
        };
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                model.canvas_press(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                tick_added = true;
                model.add_tick();
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                model.canvas_drag(e);
            }
        });
        /* horizontal glue becomes the canvas */
        mid_section.add(properties);
        mid_section.add(canvas);

        /* adding the playback controls */
        JPanel controls = new JPanel();
        controls.setPreferredSize(new Dimension(800, 60));
        controls.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        controls.setMinimumSize(new Dimension(800, 60));
        controls.setBackground(Color.LIGHT_GRAY);
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));

        JButton play = new JButton();
        ImageIcon play_icon = new ImageIcon("./icons/PlayPause.png");
        play.setIcon(play_icon);
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.start_playing();
            }
        });

        JButton backplay = new JButton();
        ImageIcon backplay_icon = new ImageIcon("./icons/BackplayPause.png");
        backplay.setIcon(backplay_icon);
        backplay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.start_backplay();
            }
        });

        playback = new JSlider(){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                playback.setMaximum(model.numticks - 100);
                playback.setValue(model.timeline - 100);
                playback.setMajorTickSpacing(100);
                playback.setLabelTable(playback.createStandardLabels(100));
                playback.setPaintTicks(true);
                //playback.setPaintLabels(true);
            }
        };
        playback.setValue(0);
        playback.setMaximum(0);
        playback.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tick_added){
                    tick_added = !tick_added;
                } else {
                    model.slider_moved(playback.getValue());
                }
            }
        });
        JButton start = new JButton();
        ImageIcon start_icon = new ImageIcon("./icons/Rewind.png");
        start.setIcon(start_icon);
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.start_timeline();
            }
        });
        JButton end = new JButton();
        ImageIcon end_icon = new ImageIcon("./icons/Fastforward.png");
        end.setIcon(end_icon);
        end.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.end_timeline();
            }
        });
        controls.add(Box.createRigidArea(new Dimension(10, 0)));
        controls.add(backplay);
        controls.add(Box.createRigidArea(new Dimension(10, 0)));
        controls.add(play);
        controls.add(Box.createRigidArea(new Dimension(10, 0)));
        controls.add(playback);
        controls.add(Box.createRigidArea(new Dimension(10, 0)));
        controls.add(start);
        controls.add(Box.createRigidArea(new Dimension(10, 0)));
        controls.add(end);
        controls.add(Box.createRigidArea(new Dimension(10, 0)));
        this.getContentPane().add(menuBar);
        this.getContentPane().add(Box.createVerticalGlue());
        this.getContentPane().add(mid_section);
        this.getContentPane().add(Box.createVerticalGlue());
        this.getContentPane().add(controls);
        setVisible(true);
    }
    /**
     * Update with data from the model.
     */
    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.
        playback.repaint();
        canvas.repaint();
//        this.invalidate();

        setVisible(true);
    }
}