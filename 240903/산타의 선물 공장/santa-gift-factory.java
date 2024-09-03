import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
  static class Box {
    int id;
    int weight;
    int prev;
    int next;

    public Box() {
      id = -1;
      weight = -1;
      prev = -1;
      next = -1;
    }
  }

  static class Belt {
    int head;
    int tail;
    int size;
    boolean isBroken;

    public Belt() {
      this.head = -1;
      this.tail = -1;
      this.size = 0;
      this.isBroken = false;
    }

    void addLast(int i) {
      if (size == 0) {
        head = i;
        tail = i;
        boxes[i].prev = -1;
        boxes[i].next = -1;
      } else {
        boxes[i].prev = tail;
        boxes[i].next = -1;
        boxes[tail].next = i;
        tail = i;
      }
      size++;
    }

    void removeFirst() {
      if (size == 1) {
        boxes[head].prev = -1;
        boxes[head].next = -1;
        head = -1;
        tail = -1;
        size = 0;
      } else if (size > 1) {
        int nextBox = boxes[head].next;
        boxes[head].prev = -1;
        boxes[head].next = -1;
        head = nextBox;
        boxes[nextBox].prev = -1;
        size--;
      }
    }

    void moveToFirst(int cur) {
      if (size == 1) return;
      int prev = boxes[cur].prev;
      if (prev == -1) return;

      int last = tail;
      int first = head;
      tail = prev;
      head = cur;
      boxes[cur].prev = -1;
      boxes[last].next = first;
      boxes[first].prev = last;
      boxes[prev].next = -1;
    }

    int find(int id) {
      if (size == 0) return -1;
      int cur = head;
      for (int i = 0; i < size; i++) {
        if (boxes[cur].id == id) {
          return cur;
        }
        cur = boxes[cur].next;
      }
      return -1;
    }

    void remove(int cur) {
      if (cur == head) {
        removeFirst();
        return;
      }
      int prev = boxes[cur].prev;
      int next = boxes[cur].next;
      if (prev != -1) {
        boxes[prev].next = next;
      }
      if (next != -1) {
        boxes[next].prev = prev;
      }
      if (cur == tail) {
        tail = prev;
      }
      boxes[cur].prev = -1;
      boxes[cur].next = -1;
      size--;
    }
  }

  static Box[] boxes;
  static Belt[] belts;
  static int n, m;
  static StringTokenizer st;

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    st = new StringTokenizer(br.readLine());
    int q = Integer.parseInt(st.nextToken());
    while (q-- > 0) {
      st = new StringTokenizer(br.readLine());
      int input = Integer.parseInt(st.nextToken());
      switch (input) {
        case 100:
          buildFactory();
          break;
        case 200:
          getOffBox();
          break;
        case 300:
          removeBox();
          break;
        case 400:
          checkBox();
          break;
        case 500:
          malfunction();
          break;
        default:
          break;
      }
    }

  }

  private static void malfunction() {
    int beltNum = Integer.parseInt(st.nextToken()) - 1;
    // 이미 망가진 경우
    if (belts[beltNum].isBroken) {
      System.out.println("-1");
      return;
    }

    belts[beltNum].isBroken = true;
    int beltNum2 = -1;
    for (int i = 1; i < m; i++) {
      int nextBelt = (beltNum + i) % m;
      if (!belts[nextBelt].isBroken) {
        beltNum2 = nextBelt;
        break; // 벨트를 찾으면 루프를 종료
      }
    }

    System.out.println(beltNum + 1);


    if (belts[beltNum].size == 0) {
      return;
    }
    if (belts[beltNum2].size == 0) {
      belts[beltNum2].head = belts[beltNum].head;
      belts[beltNum2].tail = belts[beltNum].tail;
      belts[beltNum].head = -1;
      belts[beltNum].tail = -1;
      belts[beltNum2].size = belts[beltNum].size;
      belts[beltNum].size = 0;
      return;
    }
    int tail = belts[beltNum2].tail;
    int first = belts[beltNum].head;
    int last = belts[beltNum].tail;
    boxes[tail].next = first;
    boxes[first].prev = tail;
    belts[beltNum2].tail = last;
    belts[beltNum2].size += belts[beltNum].size;

    belts[beltNum].head = -1;
    belts[beltNum].tail = -1;
    belts[beltNum].size = 0;

  }

  private static void checkBox() {
    int id = Integer.parseInt(st.nextToken());
    for (int i = 0; i < m; i++) {
      if (belts[i].isBroken) continue;
      int box = belts[i].find(id);
      if (box != -1) {
        belts[i].moveToFirst(box);
        // 벨트 번호 출력
        System.out.println(i + 1);
        return;
      }
    }
    System.out.println("-1");
  }

  private static void removeBox() {
    int id = Integer.parseInt(st.nextToken());
    for (int i = 0; i < m; i++) {
      if (belts[i].isBroken) continue;
      int box = belts[i].find(id);
      if (box != -1) {
        belts[i].remove(box);
        System.out.println(id);
        return;
      }
    }
    System.out.println("-1");
  }

  private static void getOffBox() {
    int maxWeight = Integer.parseInt(st.nextToken());
    int sumWeight = 0;
    for (int i = 0; i < m; i++) {
      if (belts[i].isBroken) continue;
      int headBox = belts[i].head;
      if (headBox == -1) continue;
      if (boxes[headBox].weight <= maxWeight) {
        sumWeight += boxes[headBox].weight;
        // 하차
        belts[i].removeFirst();
      } else {
        // 하차
        belts[i].removeFirst();
        // 맨 뒤로 보내기
        belts[i].addLast(headBox);
      }
    }
    System.out.println(sumWeight);
  }

  private static void buildFactory() {
    n = Integer.parseInt(st.nextToken());
    m = Integer.parseInt(st.nextToken());
    boxes = new Box[n];
    belts = new Belt[m];
    for (int i = 0; i < m; i++) {
      belts[i] = new Belt();
    }
    int idx = 1;
    for (int i = 0; i < n; i++) {
      boxes[i] = new Box();
      boxes[i].id = Integer.parseInt(st.nextToken());
      if (i < n / m * idx) {
        belts[idx - 1].addLast(i);
      } else {
        belts[idx++].addLast(i);
      }
    }
    for (int i = 0; i < n; i++) {
      boxes[i].weight = Integer.parseInt(st.nextToken());
    }
  }
}