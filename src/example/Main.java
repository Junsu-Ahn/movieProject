package example;
import example.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Main {
    private static List<MovieInfo> movieList = new ArrayList<>();
    private static List<Member> members = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    public static int currentMemberIdx = -1;
    public static boolean isLogin = false;
    public static void main(String[] args) {
        loadMovies();
        makeTestData();
        while (true) {
            System.out.print("명령어) ");
            String cmd = scanner.nextLine();

            switch (cmd) {
                case "exit":
                    return;
                case "member join":
                    memberJoin();
                    break;
                case "member list":
                    listMembers();
                    break;
                case "purchase":
                    purchase();
                    break;
                case "show movies":
                    showMovies();
                    break;
                case "login":
                    login();
                    break;
                case "mypage":
                    myPage();
                    break;
                default:
                    System.out.println("올바르지 않은 명령어입니다.");
            }
        }
    }

    private static void loadMovies() {
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=tab_etc&qvt=0&query=%ED%98%84%EC%9E%AC%EC%83%81%EC%98%81%EC%98%81%ED%99%94";
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
            Elements elements = doc.select(".area_text_box");

            for (Element element : elements) {
                Element link = element.select("a").first();
                if (link != null) {
                    String title = link.text();
                    movieList.add(new MovieInfo(title));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void memberJoin() {
        int id = members.size() + 1;
        String regDate = Util.getNowDateStr();
        String loginId;
        while (true) {
            System.out.print("로그인 아이디: ");
            loginId = scanner.nextLine();
            if (!isJoinableLoginId(loginId)) {
                System.out.println("이미 사용 중인 아이디입니다.");
            } else {
                break;
            }
        }
        System.out.print("로그인 비밀번호: ");
        String loginPw = scanner.nextLine();
        System.out.print("이름: ");
        String name = scanner.nextLine();

        Member member = new Member(id, regDate, loginId, loginPw, name);
        members.add(member);
        System.out.printf("%d번 회원이 생성되었습니다. 환영합니다!\n", id);
        currentMemberIdx = id - 1;
        isLogin = true;
    }

    private static void listMembers() {
        for (Member member : members) {
            System.out.println(member.getId() + "번 회원 - " + member.getName());
        }
    }

    private static void purchase() {
        if (!isLogin) {
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        System.out.print("예매할 영화 제목: ");
        String movieTitle = scanner.nextLine();

        for (MovieInfo movie : movieList) {
            if (movie.getTitle().equals(movieTitle)) {
                System.out.println("예매 가능한 좌석:");
                String[] remainingSeats = movie.getRemainingSeats();
                for (int i = 0; i < remainingSeats.length; i++) {
                    if (!remainingSeats[i].equals("X")) {
                        System.out.print(remainingSeats[i] + " ");
                    }
                }
                System.out.println();

                System.out.print("좌석 선택: ");
                int selectedSeat = Integer.parseInt(scanner.nextLine());

                if (selectedSeat < 1 || selectedSeat > 10 || remainingSeats[selectedSeat - 1].equals("X")) {
                    System.out.println("잘못된 좌석 선택입니다.");
                    return;
                }

                remainingSeats[selectedSeat - 1] = "X";
                members.get(currentMemberIdx).getMyMovie().put(movieTitle, selectedSeat);
                System.out.println("예매가 완료되었습니다.");
                return;
            }
        }
        System.out.println("해당 영화를 찾을 수 없습니다.");
    }

    private static void showMovies() {
        Collections.sort(movieList);
        for (MovieInfo movie : movieList) {
            System.out.printf("%s (%.2f) - ", movie.getTitle(), movie.getRating());
            for (String seat : movie.getRemainingSeats()) {
                System.out.print(seat + " ");
            }
            System.out.println();
        }
    }

    private static void login() {
        System.out.print("로그인 아이디: ");
        String loginId = scanner.nextLine();
        System.out.print("로그인 비밀번호: ");
        String loginPw = scanner.nextLine();

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            if (member.getLoginId().equals(loginId) && member.getLoginPw().equals(loginPw)) {
                currentMemberIdx = i;
                System.out.println("로그인 되었습니다.");
                return;
            }
        }
        System.out.println("로그인 실패. 아이디 또는 비밀번호를 확인해주세요.");
    }

    private static void myPage() {
        if (!isLogin) {
            System.out.println("로그인 후 이용해주세요.");
            return;
        }
        Member member = members.get(currentMemberIdx);
        System.out.println(member.getName() + "님의 예매 현황:");
        Map<String, Integer> myMovie = member.getMyMovie();
        for (String movieTitle : myMovie.keySet()) {
            System.out.println(movieTitle + " - 좌석 " + myMovie.get(movieTitle));
        }
        while(true)
        {
            System.out.println("메뉴를 선택하세요 : ");
            System.out.println("1. 예매취소");
            System.out.println("2. 리뷰쓰기");
            System.out.println("3. 회원탈퇴");
            System.out.println("4. 이전으로");
            String m_cmd = scanner.nextLine();

            switch(m_cmd)
            {
                case "1":
                case "예매취소":
                    cancelReservation();
                    break;
                case "2":
                case "리뷰쓰기":
                    writeReview();
                    break;
                case "3":
                case "회원탈퇴":
                    bye();
                    break;
                case "4":
                case "이전으로":
                    return;
                default:
                    System.out.println("올바르지 않은 명령어 입니다");
            }
        }
    }

    private static void bye() {
        if (!isLogin) {
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        Member currentMember = members.get(currentMemberIdx);
        if (!currentMember.getLoginPw().equals(password)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return;
        }

        members.remove(currentMemberIdx);
        System.out.println("탈퇴가 완료되었습니다.");
        isLogin = false;
        currentMemberIdx = -1;
    }

    private static void cancelReservation() {
        if (!isLogin) {
            System.out.println("로그인 후 이용해주세요.");
            return;
        }
        System.out.println("예매 현황 : ");
        for(String x : members.get(currentMemberIdx).getMyMovie().keySet())
            System.out.println(x);
        System.out.print("취소할 영화 제목: ");
        String movieTitle = scanner.nextLine();
        for (MovieInfo movie : movieList) {
            if (movie.getTitle().equals(movieTitle)) {
                Map<String, Integer> myMovie = members.get(currentMemberIdx).getMyMovie();
                if (!myMovie.containsKey(movieTitle)) {
                    System.out.println("해당 영화를 예매하지 않았습니다.");
                    return;
                }
                myMovie.remove(movieTitle);
                String[] remainingSeats = movie.getRemainingSeats();
                for (int i = 0; i < remainingSeats.length; i++) {
                    if (remainingSeats[i].equals("X")) {
                        remainingSeats[i] = Integer.toString(i + 1);
                        break;
                    }
                }
                System.out.println("예매가 취소되었습니다.");
                return;
            }
        }
        System.out.println("해당 영화를 찾을 수 없습니다.");
    }
    private static void writeReview(){
        if (!isLogin) {
            System.out.println("로그인 후 이용해주세요.");
            return;
        }
        System.out.println("예매 현황 : ");
        for(String x : members.get(currentMemberIdx).getMyMovie().keySet())
                System.out.println(x);

        System.out.print("리뷰를 작성할 영화 제목: ");
        String movieTitle = scanner.nextLine();
        for (MovieInfo movie : movieList) {
            if (movie.getTitle().equals(movieTitle)) {
                System.out.print("평점을 입력하세요 (1~5점): ");
                int rating = Integer.parseInt(scanner.nextLine());
                if (rating < 1 || rating > 5) {
                    System.out.println("1부터 5까지의 점수를 입력해주세요.");
                    return;
                }
                movie.addRating(rating);
                System.out.println("리뷰가 작성되었습니다.");

                // 리뷰 작성이 끝나면 영화좌석을 다시 인덱스로 변경
                Map<String, Integer> myMovie = members.get(currentMemberIdx).getMyMovie();
                int selectedSeat = myMovie.get(movieTitle);
                movie.getRemainingSeats()[selectedSeat - 1] = Integer.toString(selectedSeat);
                return;
            }
        }
        System.out.println("해당 영화를 찾을 수 없습니다.");
    }

    private static boolean isJoinableLoginId(String loginId) {
        for (Member member : members) {
            if (member.getLoginId().equals(loginId)) {
                return false;
            }
        }
        return true;
    }

    private static void makeTestData() {
        // 회원 가입 테스트 데이터 생성
        for (int i = 0; i < 3; i++) {
            int id = members.size() + 1;
            String regDate = Util.getNowDateStr();
            String loginId = "user" + id;
            String loginPw = "password" + id;
            String name = "User" + id;
            members.add(new Member(id, regDate, loginId, loginPw, name));
        }
        currentMemberIdx = 2;
        isLogin = true;
    }

}

class Member {
    private int id;
    private String regDate;
    private String loginId;
    private String loginPw;
    private String name;
    private Map<String, Integer> myMovie;

    public Member(int id, String regDate, String loginId, String loginPw, String name) {
        this.id = id;
        this.regDate = regDate;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.myMovie = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getLoginPw() {
        return loginPw;
    }

    public Map<String, Integer> getMyMovie() {
        return myMovie;
    }
}

class MovieInfo implements Comparable<MovieInfo> {
    private String title;
    private Map<Integer, Integer> ratings;
    private String[] remainingSeats;

    public MovieInfo(String title) {
        this.title = title;
        this.ratings = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratings.put(i, 0);
        }
        this.remainingSeats = new String[10];
        for (int i = 0; i < remainingSeats.length; i++) {
            remainingSeats[i] = Integer.toString(i + 1);
        }
    }
    public void addRating(int rating) {
        ratings.put(rating, ratings.getOrDefault(rating, 0) + 1);
    }
    public double getRating() {
        double total = 0;
        double sum = 0;
        for (Map.Entry<Integer, Integer> entry : ratings.entrySet()) {
            total += entry.getKey() * entry.getValue();
            sum += entry.getValue();
        }
        return sum == 0 ? 0 : total / sum;
    }

    public String getTitle() {
        return title;
    }

    public String[] getRemainingSeats() {
        return remainingSeats;
    }

    @Override
    public int compareTo(MovieInfo other) {
        return Double.compare(other.getRating(), this.getRating());
    }
}
