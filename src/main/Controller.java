package main;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

class Controller extends Keys{
  private Map<String,Integer> controls = new HashMap<>();
  
  Controller(Camera c,Sword s){ //default controller keys
    controls.put("up", KeyEvent.VK_W);
    controls.put("down", KeyEvent.VK_S);
    controls.put("left", KeyEvent.VK_A);
    controls.put("right", KeyEvent.VK_D);
    controls.put("swordLeft", KeyEvent.VK_O);
    controls.put("swordRight", KeyEvent.VK_P);
  }

  public Map<String,Integer> getMap(){
    return controls;
  }

  public Controller setControls(Camera c, Sword s){
    setAction(controls.get("up"),c.set(Direction::up),c.set(Direction::unUp));
    setAction(controls.get("down"),c.set(Direction::down),c.set(Direction::unDown));
    setAction(controls.get("left"),c.set(Direction::left),c.set(Direction::unLeft));
    setAction(controls.get("right"),c.set(Direction::right),c.set(Direction::unRight));
    setAction(controls.get("swordLeft"),s.set(Direction::left),s.set(Direction::unLeft));
    setAction(controls.get("swordRight"),s.set(Direction::right),s.set(Direction::unRight));
    return this;
  }
}