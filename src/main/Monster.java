package main;

import java.awt.Dimension;
import java.awt.Graphics;

import imgs.Img;

class Monster implements Entity{
  Point location;
  MonsterState state;
  Monster(Point location, MonsterState state){ this.location=location; this.state = state;}
  public Point location(){ return location; }
  public void location(Point p){location=p; }
  public double speed(){ return 0.05d; }
  public void ping(Model m){ state.ping(this, m); }
  public double chaseTarget(Monster outer, Point target){ return state.chaseTarget(this, outer, target);}
  public void draw(Graphics g, Point center, Dimension size) { state.draw(this, g, center, size);}
}

interface MonsterState{
  void ping(Monster self, Model m);
  double chaseTarget(Monster self, Monster outer, Point target);
  void draw(Monster self, Graphics g, Point center, Dimension size);
}

record AwakeMonster() implements MonsterState{
  public void ping(Monster self, Model m){
    var arrow = m.camera().location().distance(self.location());
    double size = arrow.size();
    arrow = arrow.times(self.speed()/size);
    self.location = self.location.add(arrow); 
    if(size>6.0d){ self.state = new SleepMonster();} //monster sleeps
    if(size<0.6d){ m.onGameOver();}
  }
  public double chaseTarget(Monster self, Monster outer, Point target){
    System.out.println("chasing target");
    var arrow = target.distance(outer.location());
    double size = arrow.size();
    arrow = arrow.times(self.speed()/size);
    outer.location(outer.location().add(arrow));
    return size;
  }
  public void draw(Monster self, Graphics g, Point center, Dimension size){
    self.drawImg(Img.AwakeMonster.image, g, center, size);
  }
}

record SleepMonster() implements MonsterState{
  public void ping(Monster self, Model m){
    var arrow = m.camera().location().distance(self.location());
    double size = arrow.size();
    if(size<6.0d){ self.state = new AwakeMonster(); } //monster awakes
  }
  public double chaseTarget(Monster self, Monster outer, Point target){return 0;}
  public void draw(Monster self, Graphics g, Point center, Dimension size){
    self.drawImg(Img.SleepMonster.image, g, center, size);
  }
}

class DeadMonster implements MonsterState{
  private int count = 0;
  public void ping(Monster self, Model m){
    if(count >= 100){m.remove(self); } //remove monster
    count++;
  }
  public double chaseTarget(Monster self, Monster outer, Point target){return 0;}
  public void draw(Monster self, Graphics g, Point center, Dimension size){
    self.drawImg(Img.DeadMonster.image, g, center, size);
  }
}

class RoamingMonster implements MonsterState{
  private int count = 0;
  private Point aimer = new Point(Math.random()*16,Math.random()*16);
  public void ping(Monster self, Model m){
    if(count >= 50){ 
      aimer = new Point(Math.random()*16,Math.random()*16);
      count = 0;
    }
    var arrow = aimer.distance(self.location());
    arrow = arrow.times(self.speed()/arrow.size());
    self.location = self.location.add(arrow); 
    if(m.camera().location().distance(self.location()).size()<0.6d){ m.onGameOver();} //monster hit hero
    count++;
  }
  public double chaseTarget(Monster self, Monster outer, Point target){return 0;}
  public void draw(Monster self, Graphics g, Point center, Dimension size){
    self.drawImg(Img.AwakeMonster.image, g, center, size);
  }
}
