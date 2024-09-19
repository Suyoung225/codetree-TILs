import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Rabbit {
  int num;
  int pid;
  int d;
  // 현재 위치
  int r, c;
  // 점프 횟수
  int jump;
  int score;

  Rabbit(int num, int pid, int d) {
    this.num = num;
    this.pid = pid;
    this.d = d;
  }
}

public class Main {
  static int N, M, P, K, S;
  static Rabbit[] rabbits;
  static StringTokenizer st;
  static int[] dr = {-1, 0, 1, 0};
  static int[] dc = {0, 1, 0, -1};

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    st = new StringTokenizer(br.readLine());
    int Q = Integer.parseInt(st.nextToken());
    while (Q-- > 0) {
      st = new StringTokenizer(br.readLine());
      int input = Integer.parseInt(st.nextToken());
      switch (input) {
        case 100:
          ready();
          break;
        case 200:
          race();
          break;
        case 300:
          changeDist();
          break;
        case 400:
          selectBest();
      }
    }
  }

  private static void selectBest() {
    int maxScore = 0;
    for (int i = 0; i < P; i++) {
      maxScore = Math.max(maxScore, rabbits[i].score);
    }
    System.out.println(maxScore);
  }

  private static void changeDist() {
    int pid = Integer.parseInt(st.nextToken());
    int L = Integer.parseInt(st.nextToken());
    for (int i = 0; i < P; i++) {
      if (rabbits[i].pid == pid) {
        rabbits[i].d *= L;
        return;
      }
    }
  }

  private static void race() {
    K = Integer.parseInt(st.nextToken());
    S = Integer.parseInt(st.nextToken());
    Set<Integer> selectedRabbits = new HashSet<>();
    PriorityQueue<Rabbit> pq = new PriorityQueue<>((r1, r2) -> {
      if (r1.jump == r2.jump) {
        if (r1.r + r1.c == r2.r + r2.c) {
          if (r1.r == r2.r) {
            if (r1.c == r2.c) {
              return r1.pid - r2.pid;
            }
            return r1.c - r2.c;
          }
          return r1.r - r2.r;
        }
        return (r1.r + r1.c) - (r2.r + r2.c);
      }
      return r1.jump - r2.jump;
    });

    for (int i = 0; i < P; i++) {
      pq.offer(rabbits[i]);
    }

    while (K-- > 0) {
      Rabbit cur = pq.poll();
      selectedRabbits.add(cur.num);
      rabbits[cur.num].jump++;

      List<int[]> locations = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
        int nr = Math.abs(cur.r + cur.d * dr[i]) % (2 * (N - 1));
        nr = (nr >= N) ? (2 * (N - 1)) - nr : nr;
        int nc = Math.abs(cur.c + cur.d * dc[i]) % (2 * (M - 1));
        nc = (nc >= M) ? (2 * (M - 1)) - nc : nc;
        locations.add(new int[]{nr, nc});
      }

      locations.sort((a, b) -> {
        if (a[0] + a[1] == b[0] + b[1]) {
          if (a[0] == b[0]) {
            return b[1] - a[1];
          }
          return b[0] - a[0];
        }
        return (b[0] + b[1]) - (a[0] + a[1]);
      });

      int[] location = locations.get(0);
      rabbits[cur.num].r = location[0];
      rabbits[cur.num].c = location[1];
      int score = location[0] + location[1] + 2;
      for (int i = 0; i < P; i++) {
        if (i != cur.num) {
          rabbits[i].score += score;
        }
      }

      pq.offer(rabbits[cur.num]);
    }

    PriorityQueue<Rabbit> pq2 = new PriorityQueue<>((r1, r2) -> {
      if (r1.r + r1.c == r2.r + r2.c) {
        if (r1.r == r2.r) {
          if (r1.c == r2.c) {
            return r2.pid - r1.pid;
          }
          return r2.c - r1.c;
        }
        return r2.r - r1.r;
      }
      return (r2.r + r2.c) - (r1.r + r1.c);
    });

    for (int i = 0; i < P; i++) {
      if (selectedRabbits.contains(i)) {
        pq2.offer(rabbits[i]);
      }
    }
    Rabbit last = pq2.poll();
    rabbits[last.num].score += S;
  }

  private static void ready() {
    N = Integer.parseInt(st.nextToken());
    M = Integer.parseInt(st.nextToken());
    P = Integer.parseInt(st.nextToken());
    rabbits = new Rabbit[P];
    for (int i = 0; i < P; i++) {
      int pid = Integer.parseInt(st.nextToken());
      int d = Integer.parseInt(st.nextToken());
      rabbits[i] = new Rabbit(i, pid, d);
    }
  }
}