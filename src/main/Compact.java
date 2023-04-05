package main;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

class Compact extends JFrame{
  private static final long serialVersionUID = 1L;
  private Controller controller = new Controller(null, null);
  Runnable closePhase = ()->{};
  Phase currentPhase;
  Compact(){
    assert SwingUtilities.isEventDispatchThread();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    phaseZero();
    setVisible(true);
    addWindowListener(new WindowAdapter(){
      public void windowClosed(WindowEvent e){closePhase.run();}
    });
  }
  private void defaultPhase(JLabel message, JButton start){ //used for phaseZero and phaseWin
    var changeControls = new JButton("Change controls");
    closePhase.run();
    closePhase=()->{
      remove(message);
      remove(changeControls);
      remove(start);
    };
    add(BorderLayout.CENTER,message);
    add(BorderLayout.SOUTH,start);
    add(BorderLayout.EAST,changeControls);
    start.addActionListener(e->phaseOne());
    changeControls.addActionListener(e->controlSettings());
    changeControls.setFocusable(false);
    setPreferredSize(new Dimension(800,400));
    addKeyListener(controller);
    setFocusable(true);
    pack();
  }
  private void phaseZero() {
    defaultPhase(new JLabel("Welcome to Compact. A compact Java game!"), new JButton("Start!"));
  }
  private void controlSettings(){ //control setting "phase" to customise keys
    var message = new JLabel("Control Settings");
    var up = new JButton("up: "+(char)((int)controller.getMap().get("up"))); 
    var down = new JButton("down: "+(char)((int)controller.getMap().get("down")));
    var left = new JButton("left: "+(char)((int)controller.getMap().get("left"))); 
    var right = new JButton("right: "+(char)((int)controller.getMap().get("right")));
    var swordLeft = new JButton("swordLeft: "+(char)((int)controller.getMap().get("swordLeft"))); 
    var swordRight = new JButton("swordRight: "+(char)((int)controller.getMap().get("swordRight")));
    var back = new JButton("back");

    setLayout(new GridLayout(2,4));
      add(up); add(down); add(left); add(right);
      add(swordLeft); add(swordRight); add(back); add(message);

    closePhase.run();
    closePhase = ()->{
      setLayout(new BorderLayout());
      remove(up); remove(down); remove(left); remove(right);
      remove(swordLeft); remove(swordRight); remove(message); remove(back);
    };
    back.addActionListener(e->phaseZero());
    setPreferredSize(getSize());

    //change the keys when button is pressed
    up.addKeyListener(new KeyAdapter(){public void keyPressed(KeyEvent e){changeAKey(e, up, "up");}});
    down.addKeyListener(new KeyAdapter(){public void keyPressed(KeyEvent e){changeAKey(e, down, "down");}});
    left.addKeyListener(new KeyAdapter(){public void keyPressed(KeyEvent e){changeAKey(e, left, "left");}});
    right.addKeyListener(new KeyAdapter(){public void keyPressed(KeyEvent e){changeAKey(e, right, "right");}});
    swordLeft.addKeyListener(new KeyAdapter(){public void keyPressed(KeyEvent e){changeAKey(e, swordLeft, "swordLeft");}});
    swordRight.addKeyListener(new KeyAdapter(){public void keyPressed(KeyEvent e){changeAKey(e, swordRight, "swordRight");}});
    requestFocus();
    pack();
  }

  public void changeAKey(KeyEvent e, JButton keyButton, String keyNameFromMap){ //rebind keys
    //check that the key selected is not already set for another action
    for(int code: controller.getMap().values()){
      if(e.getKeyCode() == code){
        JOptionPane.showMessageDialog(null, "Key already set for another action.");
        return;
      }
    }
    controller.getMap().put(keyNameFromMap,e.getKeyCode());
    keyButton.setText(keyNameFromMap+": "+Character.toUpperCase(e.getKeyChar()));
  }

  private void phaseOne(){
    setPhase(Phase.level1(()->phaseTwo(),()->phaseZero(), controller));
  }
  private void phaseTwo(){
    setPhase(Phase.level2(()->phaseThree(),()->phaseZero(), controller));
  }
  private void phaseThree(){
    setPhase(Phase.level3(()->phaseWin(),()->phaseZero(), controller));
  }
  private void phaseWin(){
    defaultPhase(new JLabel("Congratulations! You won the game"), new JButton("Play again!"));
  }
  void setPhase(Phase p){
    //set up the viewport and the timer
    Viewport v = new Viewport(p.model());
    v.addKeyListener(p.controller());
    v.setFocusable(true);
    Timer timer = new Timer(34,unused->{
      assert SwingUtilities.isEventDispatchThread();
      p.model().ping();
      v.repaint();
    });
    closePhase.run();//close phase before adding any element of the new phase
    closePhase=()->{ timer.stop(); remove(v); };
    add(BorderLayout.CENTER,v);//add the new phase viewport
    setPreferredSize(getSize());//to keep the current size
    pack();                     //after pack
    v.requestFocus();//need to be after pack
    timer.start();
  }
}