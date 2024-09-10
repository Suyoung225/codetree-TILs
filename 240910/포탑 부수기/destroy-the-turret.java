import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
  static int N, M, K, collapsed;
  static int[][] lastAttack;
  static boolean[][] involved;
  static int[][] map;

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    StringTokenizer st = new StringTokenizer(br.readLine());
    N = Integer.parseInt(st.nextToken());
    M = Integer.parseInt(st.nextToken());
    K = Integer.parseInt(st.nextToken());
    map = new int[N][M];
    lastAttack = new int[N][M];
    for (int i = 0; i < N; i++) {
      st = new StringTokenizer(br.readLine());
      for (int j = 0; j < M; j++) {
        map[i][j] = Integer.parseInt(st.nextToken());
        if (map[i][j] == 0) {
          collapsed++;
        }
      }
    }
    for (int round = 1; round <= K; round++) {
      involved = new boolean[N][M];
      // 공격자 선정
      int[] attacker = selectAttacker(true);
      // 공격 대상 선정
      int[] attacked = selectAttacker(false);

      lastAttack[attacker[0]][attacker[1]] = round; // 마지막 공격 시간 업데이트
      map[attacker[0]][attacker[1]] += (N + M); // 공격력 증가
      involved[attacker[0]][attacker[1]] = true;
      involved[attacked[0]][attacked[1]] = true;


      boolean isAttacked = laserAttack(attacker, attacked);
      if (!isAttacked) {
        bombAttack(attacker, attacked);
      }

      // 공격
      map[attacked[0]][attacked[1]] -= map[attacker[0]][attacker[1]];
      if (map[attacked[0]][attacked[1]] <= 0) {
        collapsed++;
        map[attacked[0]][attacked[1]] = 0;
      }

      // 포탑 정비
      maintenance();

      // 부서지지 않은 포탑의 개수가 1개인 경우 종료
      if (collapsed >= N * M - 1) {
        break;
      }

    }
    // 가장 강한 포탑의 공격력 출력
    int max = Arrays.stream(map).flatMapToInt(Arrays::stream).max().getAsInt();
    System.out.println(max);
  }

  private static void maintenance() {
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < M; j++) {
        if (map[i][j] != 0 && !involved[i][j]) {
          map[i][j] += 1;
        }
      }
    }
  }

  private static void bombAttack(int[] attacker, int[] attacked) {
    int[] dr = {0, 1, 1, 1, 0, -1, -1, -1}; // 우 -> 하 -> 좌 -> 상
    int[] dc = {1, 1, 0, -1, -1, -1, 0, 1};

    // 공격력 절반만큼 피해
    int attack = map[attacker[0]][attacker[1]] / 2;

    for (int d = 0; d < 8; d++) {
      // 가장자리에서 막히면 반대편으로 넘어가기
      int nr = (attacked[0] + dr[d] + N) % N;
      int nc = (attacked[1] + dc[d] + M) % M;

      // 공격자, 공격 받는 포탑은 제외
      if (nr == attacker[0] && nc == attacker[1] || nr == attacked[0] && nc == attacked[1]) {
        continue;
      }
      if (map[nr][nc] > 0) {
        map[nr][nc] -= attack;
        involved[nr][nc] = true;
        if (map[nr][nc] <= 0) {
          map[nr][nc] = 0;
          collapsed++;
        }
      }
    }
  }

  private static boolean laserAttack(int[] attacker, int[] attacked) {
    int[] dr = {0, 1, 0, -1}; // 우, 하, 좌, 상
    int[] dc = {1, 0, -1, 0};

    boolean[][] visited = new boolean[N][M];
    Queue<Node> q = new LinkedList<>();
    q.offer(new Node(attacker[0], attacker[1], null));
    visited[attacker[0]][attacker[1]] = true;
    while (!q.isEmpty()) {
      Node cur = q.poll();
      if (cur.r == attacked[0] && cur.c == attacked[1]) {
        laserAttack(cur, attacker);
        return true;
      }

      for (int d = 0; d < 4; d++) {
        // 가장자리에서 막히면 반대편으로 넘어가기
        int nr = (cur.r + dr[d] + N) % N;
        int nc = (cur.c + dc[d] + M) % M;

        if (!visited[nr][nc] && map[nr][nc] != 0) {
          visited[nr][nc] = true;
          q.offer(new Node(nr, nc, cur));
        }
      }
    }
    return false;
  }

  private static void laserAttack(Node cur, int[] attacker) {
    // 공격력 절반만큼 피해
    int attack = map[attacker[0]][attacker[1]] / 2;
    Node node = cur.prev;
    while (node != null) {
      // node가 attacker인 경우 종료
      if (node.prev == null) {
        return;
      }
      map[node.r][node.c] -= attack;
      involved[node.r][node.c] = true;

      if (map[node.r][node.c] <= 0) {
        map[node.r][node.c] = 0;
        collapsed++;
      }
      node = node.prev;
    }
  }

  private static int[] selectAttacker(boolean isAttacker) {
    // 공격력이 가장 낮은/높은 포탑 구하기
    int power = Arrays.stream(map).flatMapToInt(Arrays::stream)
            .filter(i -> i != 0)
            .reduce(isAttacker ? Integer::min : Integer::max)
            .orElse(0);

    List<int[]> selected = new ArrayList<>();
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < M; j++) {
        if (map[i][j] == power) {
          selected.add(new int[]{i, j});
        }
      }
    }

    if (selected.size() == 1) {
      return selected.get(0);
    }

    // 가장 최근/예전에 공격한 포탑
    int attackTime = selected.stream()
            .mapToInt(a -> lastAttack[a[0]][a[1]])
            .reduce(isAttacker ? Integer::max : Integer::min)
            .orElse(0);

    List<int[]> filteredByAttackTime = new ArrayList<>();
    for (int[] s : selected) {
      if (lastAttack[s[0]][s[1]] == attackTime) {
        filteredByAttackTime.add(s);
      }
    }

    if (filteredByAttackTime.size() == 1) {
      return filteredByAttackTime.get(0);
    }

    // 행과 열의 합이 가장 큰/작은 포탑 구하기
    int sum = filteredByAttackTime.stream()
            .mapToInt(a -> a[0] + a[1])
            .reduce(isAttacker ? Integer::max : Integer::min)
            .orElse(0);

    List<int[]> filteredBySum = new ArrayList<>();
    for (int[] s : filteredByAttackTime) {
      if ((s[0] + s[1]) == sum) {
        filteredBySum.add(s);
      }
    }

    if (filteredBySum.size() == 1) {
      return filteredBySum.get(0);
    }

    // 열 값이 가장 큰/작은 포탑
    filteredBySum.sort((a, b) -> isAttacker ? b[1] - a[1] : a[1] - b[1]);
    return filteredBySum.get(0);
  }

}

class Node {
  int r, c;
  Node prev;

  public Node(int r, int c, Node prev) {
    this.r = r;
    this.c = c;
    this.prev = prev;
  }
}