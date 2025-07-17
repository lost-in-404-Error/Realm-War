package org.Game.models;

 public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

     public int distanceTo(Position position) {
        return 0;
     }
     public boolean isAdjacentTo(Position other) {
         int dx = Math.abs(this.x - other.x);
         int dy = Math.abs(this.y - other.y);
         return (dx + dy == 1);
     }

 }
