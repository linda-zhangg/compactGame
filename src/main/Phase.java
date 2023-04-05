package main;

import java.util.List;
import java.util.stream.Stream;

record Phase(Model model, Controller controller){ 
  private static Phase makePhase(Runnable next, Runnable first, Controller con, List<Entity> e){ //to minimise duplicate code
    Camera c = new Camera(new Point(5,5));
    Sword s = new Sword(c);
    Cells cells = new Cells();
    List<Entity> player = List.of(c, s);
    var m = new Model(){
      List<Entity> entities = Stream.concat(player.stream(), e.stream()).toList();
      public Camera camera(){ return c; }
      public List<Entity> entities(){ return entities; }
      public void remove(Entity e){ 
        entities = entities.stream()
          .filter(ei->!ei.equals(e))
          .toList();
      }
      public Cells cells(){ return cells; }
      public void onGameOver(){ first.run(); }
      public void onNextLevel(){ next.run(); }
    };
    return new Phase(m,con.setControls(c,s));    
  }
  static Phase level1(Runnable next, Runnable first, Controller con) {
    return makePhase(next, first,con,List.of(new Monster(new Point(0,0),new AwakeMonster())));
  }
  
  static Phase level2(Runnable next, Runnable first, Controller con){
    return makePhase(next, first,con,
    List.of(new Monster(new Point(0,0),new RoamingMonster()), 
            new Monster(new Point(0,16),new AwakeMonster()), 
            new Monster(new Point(16,0),new AwakeMonster()), 
            new Monster(new Point(16,16),new AwakeMonster())));
  }

  static Phase level3(Runnable next, Runnable first, Controller con){
    Monster m = new Monster(new Point(0,0),new RoamingMonster()); //boss is RoamingMonster
    Sword s = new Sword(m){
      public double distance(){return 1.5d;}
      public double speed(){return 0.4d;}
      public void onHit(Model m, Entity e){ if(e instanceof Camera){m.onGameOver();}}
      public Direction direction(){ return Direction.Left; }
    };
    return makePhase(next, first,con,List.of(m, s));
  }
}