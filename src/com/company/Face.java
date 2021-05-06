package com.company;

import java.util.ArrayList;
import java.util.List;

public class Face {

  public int ni = 0;
  public List<Integer> variants = new ArrayList<>();
  public Vector n;
  public double cp;
  public ArrayList<Face> inner;
  public Face(int flags, int c) {
    this.flags = flags;
    this.c = c;
  }

  public int flags, c;

  public void addVariant(int index, int unpackVariant) {
    variants.add(index, unpackVariant);
  }
}
