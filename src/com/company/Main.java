package com.company;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

  private static int mem = 0;
  private static int[] unpacked;
  private static final String map =
      "03030e0c1d1a0150000175415c405543a8415c40e74284415c40324284415c403243a83ea4403243a83ea4403242843ea440e742843ea4405543a840e440ae3c9040e440903bae40e441cf3bae40e441cf3c903f1c41cf3bae40e640e73c903f1a40e73c903f1a41953bae40e641953bae40e641953c903f1a41953c903f1c41"
          + "cf3c903f1c40903bae3f1c40ae3c9040e640e73bae3f1a40e73bae3f9a40003f5b3f9a408f3fae4066408f40524066408f3fae3f9a3f713fae40663f714052406640003f5b40663f713fae3f9a3f7140524066400040a53f9a400040a53f9a408f40523f8040cc3ebc3fd4417a3e98408040cc3ebc404d40de40bd4000404843"
          + "903eb240a33e96408a4021403440b040213d343f5040213d34414e40a33e963ede40b53f94407c40623c063f0040923d98414e40213e8c3eb240213e8c412240b53f943ede40213f943f76402140343f9f40de4034401e41923f893f8640d53f893faf40a13d343f0040213d833f8440623c06410040923d98406140de403440"
          + "5140a13d343fe241923f89407a40d53f89400041323d6c402c417a3e983fb340de40bd412240213f944000409b3bf0410040213d83406640003f5b4066408f3fae3f9a408f40523f9a408f3fae40663f713fae3f9a3f7140523f9a40003f5b3f9a3f713fae40663f7140523f9a400040a54066400040a54066408f40523f9a40"
          + "003f5b3f9a408f3fae4066408f40524066408f3fae3f9a3f713fae40663f714052406640003f5b40663f713fae3f9a3f7140524066400040a53f9a400040a53f9a408f4052406640003f5b4066408f3fae3f9a408f40523f9a408f3fae40663f713fae3f9a3f7140523f9a40003f5b3f9a3f713fae40663f7140523f9a400040"
          + "a54066400040a54066408f40523e1d40003bdb41e340003bdb3e1d40003d2541e340003d253e1d400040db41e3400040db3e1d4000422541e340004225402d410140833fd3410140832f0311010203040311050607080388090a0b0c0388140d151603280e17180f03771011121300663f2743001144282900772e343e00772a"
          + "253700283c463a0088302c3f02662e3d473202663245342e02662a2f35330266333b312a001630463c02772a313a250077372f2a0200452b3e3402002f373635026636374429020038413940026629283e2b02883a313b2d00283f463000883a2d3c02553c2d2c3002882c473d3f02553941284400772e3e270277273f3d2e00"
          + "66253a260028423a460028463f4202773840264300884326420266432741380088423f43026625264039008842263a03770306050403506c6d6f6e03507071737202cc4428747500cc28417400cc44753904031d1d22417e40903c800a025561606a6b408040004000020561636660400040403f910255626966633f80400040"
          + "0002556a60646840804000400002556066676440003fc03f910255656766693f804000400002056467656840003f80400002556865696a40003fc0406f02056a69626b40004040406f02556b626361400040804000031711223e82409041800a02551a2423193f804000400002051a191f1c400040403f9102551b1c1f224080"
          + "40004000025523211d193f80400040000255191d201f40003fc03f9102551e221f2040804000400002051d211e2040003f80400002552123221e40003fc0406f020523241b2240004040406f0255241a1c1b40004080400003171d223e8240903c800a0255555f5e543f8040004000020555545a57400040403f91025556575a"
          + "5d40804000400002555e5c58543f8040004000025554585b5a40003fc03f910255595d5a5b4080400040000205585c595b40003f80400002555c5e5d5940003fc0406f02055e5f565d40004040406f02555f555756400040804000031d1122417e409041800a0255494852534080400040000205494b4e48400040403f910255"
          + "4a514e4b3f8040004000025552484c504080400040000255484e4f4c40003fc03f9102554d4f4e513f804000400002054c4f4d5040003f8040000255504d515240003fc0406f020552514a5340004040406f0255534a4b49400040804000051f1d0c0e1601015000016c3f094001400040f9400140003ebe401040003ed94000"
          + "40003d3e402f400040f9400142013f094001420141434010400042c1402f400042c1403041ff3ed9400441ff412a400040003e31405f40d53e65401541173e9e405f41603e65413541173dfd4071405d3e45401540683e83407140713e45416340683c10408440513c32402a40b33c57407441013c32415840b23d3440824083"
          + "3d69402a40c23da4406e410d3d69411040c641e14060407141bb400f40c841944052411f41bb414340c842d4404f41c942fc4011418a43254050415642fc416b41873ed9400543ff4143401041ff3d3e401f41fe3ebe401041fe4129400441ff4143401043ff3c7a408242543cb4402a428f3cf4406d42e03cb4415d42963d44"
          + "408442373d7e402a42853dbf406e42cb3d7e4113428441f8404b42c641ae40174305415f404b434a41ae412e43054242406e41d742244017421e41ff406e427442244147421e4353404643d3437740114397439c4040435d43774178438742874050439742aa4011436942cd4048432742aa4153436443d2404842f543ea4011"
          + "42bc43ff4048426e43ea417142ae3f094001440140f94001440141434010460042c1402f43ff3d3e402f43fe3ebe401143fe3ebe401046003ed84004460041294004460040f9400146004129400543ff3db4405d44033dc1400c44493dd1405d448b3dc1414c44493e4a404f44c03e73400c44f43eab404f453d3e73411e44f4"
          + "3d7e408244a13da7402a44e93dd44089453d3da7415144e93c94408143a33cc8402a43f03d02406744263cc8414e43f541ed405044d341bc401944f7418f4050451f41bc412f44f7426e405045a9428f4011457a42af4050452f428f414145773f094001460042c1402f46003d3d402f460028023b070b040103dd0607010202"
          + "332608090a023b2705032802992803040b0233020c29060299290c082603b30d0e0f100333111213140335151617180335191a1b1c03b31d1e1f20033521222324023b47250b07036d48470706023b2a260a4a02334b27284c02494c280b2502330629514802995129262a03352b2c2d2e03352f30313203b33334353603bb37"
          + "38393a03353b3c3d3e03353f40414203354344454602336a4e25470366506a4748023b492a4a6b023b6c4b4c4d02994d4c254e023b48514f5002494f512a4903bb5253545503335657585903bb5a5b5c5d03355e5f606103b3626364650335666768690008180c1d161419121e01015000011c3f2f4001400040cf400140003f"
          + "2f400142013f34400140003ffe400141633ffe400140c34004400140c340044001416340d4400140003ffe400143453ffe400142a54004400142a54004400143453f34400142013f344001440140cf4001440140cf4001420140d44001420140d4400144013f2f400146003f2f400144013ffe400145273ffe40014487400440"
          + "01448740044001452740cf4001460040d4400146003f34400146000903770301040e03770506070803771102091203770a0b0c0d037715030e0f037710111213037714150f1c03771617181903771a10131b00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
  private static final Map<String, Model> allModels = new HashMap<>();


  public static void main(String[] args) {
    toBytes();

    unpackArray(() -> {
      var model = new Model();
      var string = unpackString();
      var scale = 1f / (float) unpackInt();

      unpackArray(
          () -> {
            double d = unpackDouble();
            if (d >= 127) {
              throw new RuntimeException("lod distance too large: " + d);
            }
            model.lod_dist.add(d * d);
          }
      );
      // level of details models
      unpackArray(() -> {
        final LevelOfDetail lod = new LevelOfDetail();
        unpackModel(lod, scale);
//                 unpack vertex groups (as sub model)
        unpackArray(() -> {
          var name = unpackString();
          var vgroup = new VGroup(unpackV(scale));
          // faces
          unpackArray(() -> {
            Face f = unpackFace();
            // normal
            f.n = unpackV(1f);
            // viz check
            f.cp = vDot(f.n, lod.v.get(f.variants.get(0) - 1));

            vgroup.f.add(f);
          });
          lod.vgroups.put(name, vgroup);
        });

        model.lods.add(lod);
      });

      allModels.put(string, model);
    });

    System.out.println(allModels);

  }

  private static double vDot(Vector n, Vector vector) {
    return n.x * vector.x + n.y * vector.y + n.z * vector.z;
  }

  private static void unpackModel(LevelOfDetail model, float scale) {
    // vertices
    var v = model.v;
    unpackArray(() -> {
      v.add(unpackV(scale));
    });

    // faces
    unpackArray(() -> {
      Face f = unpackFace();
      // inner faces?
      if ((f.flags & 0x8) > 0) {
        f.inner = new ArrayList<Face>();
        unpackArray(() -> {
          f.inner.add(unpackFace());
        });
      }
      // normal
      f.n = vCross(makeV(v.get(f.variants.get(0)-1), v.get(f.variants.get(f.ni-1)-1)),
          makeV(v.get(f.variants.get(0)-1), v.get(f.variants.get(1)-1)));
      f.n = vNormz(f.n);
      // viz check
      f.cp = vDot(f.n, model.v.get(f.variants.get(0)-1));

      model.f.add(f);
    });
  }

  private static Vector vNormz(Vector v) {
    var d = vDot(v, v);
    if (d > 0.001) {
      d = Math.sqrt(d);
      return new Vector(v.x / d, v.y / d, v.z / d);
    }
    return v;
  }

  private static Vector vCross(Vector a, Vector b) {
    return new Vector(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
  }

  private static Vector makeV(Vector a, Vector b) {
    return new Vector(b.x - a.x, b.y - a.y, b.z - b.z);
  }

  //unpakc vector
  private static Vector unpackV(float scale) {
    return new Vector(unpackDouble(scale), unpackDouble(scale), unpackDouble(scale));

  }

  private static double unpackDouble() {
    return unpackDouble(1d);
  }

  private static double unpackDouble(double scale) {
    var f = ((double) unpackInt(2) - 16384d) / 128d;
    return f * scale;
  }

  private static String unpackString() {
    String itoa = "_0123456789abcdefghijklmnopqrstuvwxyz";
    var s = new StringBuffer();
    unpackArray(() -> {
          var c = unpackInt(1) ;
          s.append(itoa.charAt(c - 1));
        }
    );
    return s.toString();
  }

  private static int unpackInt() {
    return unpackInt(1);
  }

  //w = number of bytes 1 or 2
  private static int unpackInt(int w) {
    var i = w == 1 ? mpeek() : (mpeek() << 8) | mpeek();
    return i;
  }

  private static void toBytes() {
    String bt = "";
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (char ch : map.toCharArray()) {
      bt += ch;
      if (bt.length() == 2) {
        try {
          out.write(new byte[]{(byte) Integer.parseInt(bt, 16)});
        } catch (NumberFormatException | IOException ex) {
          throw new RuntimeException(ex);
        }
        bt = "";
      }
    }

    byte[] bytes = out.toByteArray();
    unpacked = new int[bytes.length];
    for (byte b : bytes) {
      unpacked[mem++] = (b & 0x0FF);
    }
    mem = 0;

  }

  private static void unpackArray(Runnable fn) {
    int n = unpackVariant();
    for (int i = 0; i < n; i++) {
      fn.run();
    }
  }

  private static int unpackVariant() {
    int h = mpeek();
    if ((h & 0x80) > 0) {
      h = ((h & 0x7F << 8) | mpeek());
    }
    return h;
  }

  private static int mpeek() {
    return unpacked[mem++];
  }


  private static void writeFile() {
    try (FileOutputStream out = new FileOutputStream(new File("out.bin"))) {
      String bt = "";
      for (char ch : map.toCharArray()) {
        bt += ch;
        if (bt.length() == 2) {
          try {
            out.write(new byte[]{(byte) Integer.parseInt(bt, 16)});
          } catch (NumberFormatException ex) {
            throw new RuntimeException(ex);
          }
          bt = "";
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Face unpackFace() {
    Face f = new Face(unpackInt(), unpackInt());
    if (f.c == 0x050) {
      f.c = 0x0150;
    }
    // embedded fillp mode
    f.c += 0x1000a5a5;

    f.ni = (f.flags & 0x2) > 0 ? 4 : 3;
    // vertex indices
    // quad?
    for (int i = 0; i < f.ni; i++) {
//            -- using the face itself saves more than 500KB!
      f.addVariant(i, unpackVariant());
    }
    return f;
  }

}

