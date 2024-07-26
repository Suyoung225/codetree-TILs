import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Player {
  int x, y, d, power, gunPower, point;

  public Player(int x, int y, int d, int power) {
    this.x = x;
    this.y = y;
    this.d = d;
    this.power = power;
  }
}

public class Main {
  static int n, m, k;
  static List<Integer>[][] guns;
  static List<Integer>[][] playerCoord; // 현재 플레이어들의 좌표
  static Player[] players;
  static int[] dx = {-1, 0, 1, 0};
  static int[] dy = {0, 1, 0, -1};

  public static void main(String[] args) throws IOException {
    // 여기에 코드를 작성해주세요.
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    StringTokenizer st = new StringTokenizer(br.readLine());
    n = Integer.parseInt(st.nextToken());
    m = Integer.parseInt(st.nextToken());
    k = Integer.parseInt(st.nextToken());
    guns = new ArrayList[n + 1][n + 1];
    playerCoord = new ArrayList[n + 1][n + 1]; // x, y 1부터 시작
    players = new Player[m + 1];
    for (int i = 1; i <= n; i++) {
      st = new StringTokenizer(br.readLine());
      for (int j = 1; j <= n; j++) {
        guns[i][j] = new ArrayList<>();
        playerCoord[i][j] = new ArrayList<>();
        int gunPower = Integer.parseInt(st.nextToken());
        if (gunPower != 0) {
          guns[i][j].add(gunPower);
        }
      }
    }

    // 플레이어 1 번부터 ~ m 번까지
    for (int i = 1; i <= m; i++) {
      st = new StringTokenizer(br.readLine());
      int x = Integer.parseInt(st.nextToken());
      int y = Integer.parseInt(st.nextToken());
      int d = Integer.parseInt(st.nextToken());
      int s = Integer.parseInt(st.nextToken());
      players[i] = new Player(x, y, d, s);
      playerCoord[x][y].add(i);
    }

    while (k-- > 0) {
      for (int i = 1; i <= m; i++) {
        // 1-1 방향대로 한 칸 전진
        moveForward(i);

        // 2-1 더 쎈 총 획득하기
        Player player = players[i];
        if (playerCoord[player.x][player.y].size() == 1) {
          pickUpGun(i);
        }
        // 2-2-1. 만약 이동한 방향에 플레이어가 있는 경우에는 두 플레이어가 싸우게 됩니다
        else {
          fight(i);
        }

      }

    }

    for (int i = 1; i <= m; i++) {
      System.out.print(players[i].point);
      if (i != m) System.out.print(" ");

//      System.out.println(players[i].x + " " + players[i].y + " " + players[i].d + " " + players[i].gunPower);
    }
  }

  static void moveForward(int i) {
    Player player = players[i];
    int nx = player.x + dx[player.d];
    int ny = player.y + dy[player.d];
    int nd = player.d;
    // 해당 방향으로 나갈 때 격자를 벗어나는 경우, 반대 방향
    if (!isValid(nx, ny)) {
      nx = player.x + dx[(player.d + 2) % 4];
      ny = player.y + dy[(player.d + 2) % 4];
      nd = (player.d + 2) % 4;
    }
    movePlayer(i, nx, ny, nd);
  }

  static void movePlayer(int i, int nx, int ny, int nd) {
    Player player = players[i];
    playerCoord[player.x][player.y].remove(Integer.valueOf(i));
    playerCoord[nx][ny].add(i);
    player.x = nx;
    player.y = ny;
    player.d = nd;
  }

  static void fight(int i) {
    Player player1 = players[i];
    int j = 0;
    for (int idx : playerCoord[player1.x][player1.y]) {
      if (idx != i) {
        j = idx;
      }
    }
    Player player2 = players[j];

    int player1SumPower = player1.power + player1.gunPower;
    int player2SumPower = player2.power + player2.gunPower;
    int loser = 0;
    int winner = 0;
    int point = Math.abs(player1SumPower - player2SumPower);
    if (player1SumPower > player2SumPower) {
      loser = j;
      winner = i;
    } else if (player2SumPower > player1SumPower) {
      loser = i;
      winner = j;
    } else {
      loser = (player1.power > player2.power) ? j : i;
      winner = (player1.power > player2.power) ? i : j;
    }

    players[winner].point += point;

    moveLostPlayer(loser);
    pickUpGun(loser);
    pickUpGun(winner);
  }

  static void moveLostPlayer(int i) {
    Player player = players[i];
    // 총이 있는 경우 내려놓기
    if (player.gunPower != 0) {
      guns[player.x][player.y].add(player.gunPower);
      player.gunPower = 0;
    }
    for (int j = 0; j < 4; j++) {
      int nd = (player.d + j) % 4;
      int nx = player.x + dx[nd];
      int ny = player.y + dy[nd];
      if (isValid(nx, ny) && playerCoord[nx][ny].isEmpty()) {
        movePlayer(i, nx, ny, nd);
        break;
      }
    }
  }

  static void pickUpGun(int i) {
    Player player = players[i];
    if (!guns[player.x][player.y].isEmpty()) {
      guns[player.x][player.y].sort(Comparator.reverseOrder());
      int gun = guns[player.x][player.y].get(0);
      // 총 바꾸기
      if (player.gunPower < gun) {
        guns[player.x][player.y].remove(Integer.valueOf(gun));
        // 총을 이미 가지고 있는 경우
        if (player.gunPower != 0) {
          guns[player.x][player.y].add(player.gunPower);
        }
        player.gunPower = gun;
      }
    }
  }

  static boolean isValid(int nx, int ny) {
    return nx >= 1 && nx <= n && ny >= 1 && ny <= n;
  }
}