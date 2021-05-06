package com.company;

import java.util.ArrayList;
import java.util.List;

public class VGroup {

  public final Vector offset;
  public final List<Face> f;

  public VGroup(Vector vector) {
    this.offset = vector;
    this.f = new ArrayList<>();
  }
}
