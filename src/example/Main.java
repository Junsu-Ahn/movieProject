package example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Main {
    private static List<MovieInfo> movieList;
    private static List<Member> members;

    public static void main(String[] args) {
        movieList = new ArrayList<>();
        members = new ArrayList<>();

        Scanner sc = new Scanner(System.in);

        String regDate;
        String loginId;
        String loginPw;
        String name;
        int id;
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=tab_etc&qvt=0&query=%ED%98%84%EC%9E%AC%EC%83%81%EC%98%81%EC%98%81%ED%99%94";
        int sw = 0;
        boolean islogin = false;
        boolean isX = false;
        int current_member_idx = 0;

        try {
            getDataFromUrl(url);
            System.out.printf("%-15s|| %5s %10s\n", "제목", "평점\t||", "남은 좌석");
            System.out.println("===============================================");
            for (MovieInfo movie : movieList) {
                System.out.printf("%-35s  ||  %5s  ||   ", movie.getTitle(), movie.getRating());
                for(int i = 0 ; i < movie.getRemainingSeats().length;i++)
                    System.out.print(movie.getRemainingSeats()[i]+ " ");
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            System.out.print("명령어) ");
            String cmd = sc.nextLine();
            if (cmd.equals("exit"))
                break;

            if (cmd.equals("member join")) {
                id = members.size() + 1;
                regDate = Util.getNowDateStr();
                while (true) {
                    System.out.print("로그인 아이디 : ");
                    loginId = sc.nextLine();
                    if (!isJoinableLoginId(loginId)) {
                        System.out.println("사용중인 아이디입니다.");
                        continue;
                    }
                    break;
                }
                System.out.print("로그인 비번 : ");
                loginPw = sc.nextLine();
                System.out.print("로그인 비번 확인 : ");
                String loginPw2 = sc.nextLine();
                if (!loginPw.equals(loginPw2)) {
                    System.out.println("비밀번호가 일치하지 않습니다.");
                    continue;
                }
                System.out.print("이름 : ");
                name = sc.nextLine();
                Member member = new Member(id, regDate, loginId, loginPw, name);
                members.add(member);
                System.out.printf("%d번 회원이 생성되었습니다. 환영합니다 !\n", id);
            }

            if (cmd.equals("member list")) {
                for (Member member : members) {
                    System.out.println(member.loginId);
                }
            }



            if(cmd.equals("purchase"))      // 예매
            {
                if(!islogin) {
                    System.out.println("로그인 하세요");
                    continue;
                }
                System.out.print("예매할 영화 제목 : ");
                String p_title = sc.nextLine();

                for (int i = 0; i < movieList.size(); i++) {
                    if (movieList.get(i).getTitle().equals(p_title)) {
                        while (true) {
                            System.out.println("좌석을 선택하세요");
                            for (int j = 0; j < 10; j++)
                                System.out.print(movieList.get(i).getRemainingSeats()[j] + " ");
                            System.out.println();
                            int seat = sc.nextInt();
                            sc.nextLine();
                            if (seat < 1 || seat > 10) {
                                System.out.println("1~10만 입력해주세요");
                                continue;
                            }
                            if (!movieList.get(i).getRemainingSeats()[seat - 1].equals("X")) {
                                movieList.get(i).getRemainingSeats()[seat - 1] = "X";
                                members.get(current_member_idx).mp.myMovie.put("Title", movieList.get(i).getTitle());
                                members.get(current_member_idx).mp.myMovie.put("Seat", Integer.toString(seat));
                                System.out.println("예매가 완료되었습니다");
                                break;
                            } else {
                                System.out.println("이미 예약된 좌석입니다.");
                                break;
                            }
                        }
                    }
                }
            }




            if(cmd.equals("show movies"))
            {
                Collections.sort(movieList);
                for (MovieInfo movie : movieList) {
                    System.out.printf("%-35s  ||  %5s  ||   ", movie.getTitle(), movie.getRating());
                    for(int i = 0 ; i < movie.getRemainingSeats().length;i++)
                        System.out.print(movie.getRemainingSeats()[i]+ " ");
                    System.out.println();
                }
            }

            if(cmd.equals("login"))
            {
                System.out.print("로그인 아이디 : ");
                String logid = sc.nextLine();
                System.out.print("로그인 비번 : ");
                String logpw = sc.nextLine();

                for(int i = 0; i < members.size(); i++)
                {
                    if(members.get(i).loginId.equals(logid) && members.get(i).loginPw.equals(logpw));
                    {
                        System.out.println("로그인 완료");
                        islogin = true;
                        current_member_idx = i;
                        sw = 1;
                    }
                }
                if(sw == 0)
                    System.out.println("로그인 실패");
                sw = 0;
            }

            if(cmd.equals("mypage")) {
                if (!islogin) {
                    System.out.println("로그인 하세요");
                    continue;
                }
                while (true) {
                    System.out.println("옵션을 선택하세요 : ");
                    System.out.println("1. 나의예매현황");
                    System.out.println("2. 평점쓰기");
                    System.out.println("3. 회원탈퇴");
                    System.out.println("4. 이전으로");
                    String m_cmd = sc.nextLine();

                    if (m_cmd.equals("1") || m_cmd.equals("나의예매현황")) {
                        System.out.println(members.get(current_member_idx).mp.myMovie.keySet());
                    } else if (m_cmd.equals("2") || m_cmd.equals("평점쓰기")) {
                        if (members.get(current_member_idx).mp.myMovie.size() != 0) {
                            System.out.println(members.get(current_member_idx).mp.myMovie.keySet());
                            System.out.print("평점 쓸 영화 선택 : ");
                            String s = sc.nextLine();
                            System.out.print("5점 4점 3점 2점 1점  당신의 점수는 ? (숫자만 입력): ");
                            int d = sc.nextInt();
                            if (d > 5 || d < 1) {
                                System.out.println("잘못된 입력입니다.");
                                continue;
                            }
                            for (MovieInfo movie : movieList) {
                                if (movie.getTitle().equals(s)) {
                                    movie.ratings.put(d, movie.ratings.get(d) + 1);
                                }
                            }
                        }
                    } else if (m_cmd.equals("3") || m_cmd.equals("회원탈퇴")) {
                        System.out.print("비번 입력 : ");
                        String byePw = sc.nextLine();
                        if (members.get(current_member_idx).loginPw.equals(byePw)) {
                            members.remove(current_member_idx);
                            System.out.println("회원탈퇴 완료");
                            islogin = false;
                        } else
                            System.out.println("비번이 다릅니다");
                    }
                    else
                        break;
                } //  회원탈퇴 while 끝
            }
        } // 시스템 while 끝
    }

    private static void getDataFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        Elements elements = doc.select(".area_text_box");

        for (Element element : elements) {
            Element link = element.select("a").first();
            if (link != null) {
                String title = link.text();
                MovieInfo movie = new MovieInfo(title);   // 영화 클래스 추가
                movieList.add(movie);                     // 영화 리스트 추가
            }
        }
    }

    private static boolean isJoinableLoginId(String logId) {
        if (members.size() == 0)
            return true;
        for (Member member : members) {
            if (member.loginId.equals(logId))
                return false;
        }
        return true;
    }
}

class Member {
    int id;
    String regDate;
    String loginId;
    String loginPw;
    String name;
    MyPage mp;
    public Member(int id, String regDate, String loginId, String loginPw, String name) {
        this.id = id;
        this.regDate = regDate;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.mp = new MyPage();
    }
}

class MovieInfo implements Comparable<MovieInfo>{
    private String title;
    public Map<Integer, Integer> ratings;
    private double rating;
    private String[] remainingSeats;

    public MovieInfo(String title) {
        this.title = title;
        this.ratings = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratings.put(i, 0); // 초기값 설정
        }
        this.remainingSeats = new String[10];
        for (int i = 0; i < remainingSeats.length; i++) {
            remainingSeats[i] = Integer.toString(i + 1); // 좌석번호 초기값 설정
        };
        this.rating = getRating();
    }
    public int compareTo(MovieInfo other) {
        // 등급(rating)이 높은 순서대로 정렬하도록 구현
        return Double.compare(other.getRating(), this.getRating());
    }
    public String getTitle() {
        return title;
    }

    public double getRating() {
        double total = 0;
        double sum = 0;
        for (Map.Entry<Integer, Integer> entry : ratings.entrySet()) {
            total += entry.getKey() * entry.getValue();
            sum += entry.getValue();
        }
        if (sum == 0) {
            return 0; // 평점이 없을 경우
        }
        return total / sum;
    }

    public String[] getRemainingSeats() {
        return remainingSeats;
    }
}

class MyPage {
    Map<String, String> myMovie;

    MyPage()
    {
        myMovie = new HashMap<>();
    }
}