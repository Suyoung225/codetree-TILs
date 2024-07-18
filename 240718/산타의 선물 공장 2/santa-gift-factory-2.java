import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
  static class Box {
    int prev;
    int next;

    public Box() {
      prev = -1;
      next = -1;
    }
  }

  static class Belt {
    int head;
    int tail;
    int size;

    public Belt() {
      head = -1;
      tail = -1;
      size = 0;
    }

    void addFirst(int i) {
      if (size == 0) {
        head = i;
        tail = i;
        boxes[i].prev = -1;
        boxes[i].next = -1;
      } else {
        boxes[head].prev = i;
        boxes[i].next = head;
        boxes[i].prev = -1;
        head = i;
      }
      size++;
    }

    void addLast(int i) {
      if (size == 0) {
        addFirst(i);
        return;
      }
      boxes[tail].next = i;
      boxes[i].prev = tail;
      boxes[i].next = -1;
      tail = i;
      size++;
    }

    int popFront() {
      int i = head;
      if (size == 1) {
        tail = -1;
        head = -1;
      } else {
        int next = boxes[head].next;
        boxes[next].prev = -1;
        boxes[head].next = -1;
        head = next;
      }
      size--;
      return i;
    }

    int getBox(int idx) {
      int now = head;
      for (int i = 1; i < idx; i++) {
        now = boxes[now].next;
      }
      return now;
    }
  }

  static int n, m;
  static Box[] boxes;
  static Belt[] belts;
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int q = Integer.parseInt(br.readLine());
    while (q-- > 0) {
      String[] inputs = br.readLine().split(" ");
      int type = Integer.parseInt(inputs[0]);
      switch (type) {
        case 100:
          makeFactory(inputs);
          break;
        case 200:
          moveBox(inputs);
          break;
        case 300:
          swapBox(inputs);
          break;
        case 400:
          divideBox(inputs);
          break;
        case 500:
          getBoxInfo(inputs);
          break;
        case 600:
          getBeltInfo(inputs);
          break;
        default:
          break;
      }
    }

    System.out.println(sb);
  }

  static void makeFactory(String[] inputs) {
    n = toInt(inputs[1]);
    m = toInt(inputs[2]);
    belts = new Belt[n + 1];
    for (int i = 0; i <= n; i++) {
      belts[i] = new Belt();
    }
    boxes = new Box[m + 1];
    for (int i = 1; i <= m; i++) {
      int beltNum = toInt(inputs[i + 2]);
      boxes[i] = new Box();
      belts[beltNum].addLast(i);
    }

  }

  static void moveBox(String[] inputs) {
    int src = toInt(inputs[1]);
    int dst = toInt(inputs[2]);
    if (belts[src].size == 0) {
      sb.append(belts[dst].size).append("\n");
      return;
    }
    if (belts[dst].size == 0) {
      belts[dst].head = belts[src].head;
      belts[dst].tail = belts[src].tail;
    } else {
      int srcTail = belts[src].tail;
      int dstHead = belts[dst].head;
      boxes[srcTail].next = dstHead;
      boxes[dstHead].prev = srcTail;
      belts[dst].head = belts[src].head;
    }
    belts[dst].size += belts[src].size;
    belts[src] = new Belt();
    sb.append(belts[dst].size).append("\n");
  }

  static void swapBox(String[] inputs) {
    int src = toInt(inputs[1]);
    int dst = toInt(inputs[2]);
    if (belts[src].size == 0 && belts[dst].size == 0) {
      sb.append(belts[dst].size).append("\n");
      return;
    }
    if (belts[dst].size == 0) {
      int srcHead = belts[src].popFront();
      belts[dst].addFirst(srcHead);
    } else if (belts[src].size == 0) {
      int dstHead = belts[dst].popFront();
      belts[src].addFirst(dstHead);
    } else {
      int srcHead = belts[src].popFront();
      int dstHead = belts[dst].popFront();
      belts[dst].addFirst(srcHead);
      belts[src].addFirst(dstHead);
    }
    sb.append(belts[dst].size).append("\n");
  }

  static void divideBox(String[] inputs) {
    int src = toInt(inputs[1]);
    int dst = toInt(inputs[2]);
    if (belts[src].size < 2) {
      sb.append(belts[dst].size).append("\n");
      return;
    }
    int srcSize = belts[src].size;
    int srcStart = belts[src].head;
    int srcLast = belts[src].getBox(srcSize / 2);

    belts[src].head = boxes[srcLast].next;
    belts[src].size -= srcSize / 2;
    boxes[belts[src].head].prev = -1;
    boxes[srcLast].next = -1;

    if (belts[dst].size == 0) {
      belts[dst].head = srcStart;
      belts[dst].tail = srcLast;
    } else {
      int dstHead = belts[dst].head;
      belts[dst].head = srcStart;
      boxes[srcLast].next = dstHead;
      boxes[dstHead].prev = srcLast;
    }
    belts[dst].size += srcSize / 2;
    sb.append(belts[dst].size).append("\n");
  }

  static void getBoxInfo(String[] inputs) {
    int boxNum = toInt(inputs[1]);
    int a = (boxes[boxNum].prev == -1) ? -1 : boxes[boxNum].prev;
    int b = (boxes[boxNum].next == -1) ? -1: boxes[boxNum].next;
    sb.append(a + 2 * b).append("\n");
  }

  static void getBeltInfo(String[] inputs) {
    int beltNum = toInt(inputs[1]);
    if (belts[beltNum].size == 0) {
      sb.append(-3).append("\n");
      return;
    }
    int a = belts[beltNum].head;
    int b = belts[beltNum].tail;
    int c = belts[beltNum].size;
    sb.append(a + 2 * b + 3 * c).append("\n");
  }

  static int toInt(String s) {
    return Integer.parseInt(s);
  }
}