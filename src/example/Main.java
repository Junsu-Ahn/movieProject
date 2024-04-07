package example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;

// url : https://search.naver.com/p/crd/rd?m=1&px=272&py=241&sx=272&sy=141&p=im4GPdpzL8Vssc8CZIVssssstv4-510574&q=%EC%83%81%EC%98%81%EC%A4%91%EC%9D%B8%EC%98%81%ED%99%94&ie=utf8&rev=1&ssc=tab.nx.all&f=nexearch&w=nexearch&s=RA%2BFrOcotj97DZabcYror6sA&time=1712470222595&abt=%5B%7B%22eid%22%3A%22SHP-AD-STYLE-UX%22%2C%22vid%22%3A%221%22%7D%5D&a=nco_x0a*M.runtab&r=1&i=1800009D_000000000000&u=https%3A%2F%2Fsearch.naver.com%2Fsearch.naver%3Fwhere%3Dnexearch%26sm%3Dtab_etc%26qvt%3D0%26query%3D%25ED%2598%2584%25EC%259E%25AC%25EC%2583%2581%25EC%2598%2581%25EC%2598%2581%25ED%2599%2594&cr=1

public class Main {
    private static List<String> movie_list;
    private static List<Member> members;
    public static void main(String[] args) {

        movie_list = new ArrayList<>();  // 영화 정보
        Scanner sc = new Scanner(System.in);
        members = new ArrayList<>(); // 회원 리스트
        String regDate;
        String loginId;
        String loginPw;
        String name;
        int id;
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=tab_etc&qvt=0&query=%ED%98%84%EC%9E%AC%EC%83%81%EC%98%81%EC%98%81%ED%99%94";
        try {
            // 첫 번째 링크를 가져오기
            getDataFromUrl(url);
            for (String movie : movie_list) {
                System.out.println(movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true) {
            System.out.print("명령어) ");     // 명령어 입력
            String cmd = sc.nextLine();
            if(cmd.equals("exit"))
                break;

            if (cmd.equals("member join"))   // 회원 가입
            {
                id = members.size() + 1;
                regDate = Util.getNowDateStr();
                while(true){
                    System.out.print("로그인 아이디 : ");
                    loginId = sc.nextLine();
                    if(!isJoinableLoginId(loginId))
                    {
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

            if (cmd.equals("member list"))
            {
                for(int i = 0 ; i < members.size(); i++)
                {
                    System.out.println(members.get(i).loginId);
                }
            }

            if(cmd.equals("member bye")) {
                int sw = 0;

                System.out.print("탈퇴할 아이디 입력 : ");
                String byeId = sc.nextLine();
                System.out.print("비번 입력 : ");
                String byePw = sc.nextLine();
                for (int i = 0; i < members.size(); i++) {
                    if (members.get(i).loginId.equals(byeId)) {
                        if(members.get(i).loginPw.equals(byePw))
                        {
                            members.remove(i);
                            System.out.println("회원탈퇴 완료");
                        }
                        else
                            System.out.println("비번이 다릅니다");
                        sw = 1;
                        break;
                    }
                }
                if (sw==0)
                    System.out.println("해당 아이디 없음");
            }
        }

    }
    private static void getDataFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        Elements elements = doc.select(".area_text_box");
        Iterator var3 = elements.iterator();

        while(var3.hasNext()) {
            Element element = (Element)var3.next();
            Element link = element.select("a").first();
            if (link != null) {
                String text = link.text();
                movie_list.add(text);
            }
        }
    }

    private static boolean isJoinableLoginId(String logId)
    {
        if(members.size()==0)
            return true;
        for(int i = 0 ; i < members.size(); i++)
        {
            if(members.get(i).loginId.equals(logId))
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

    public Member(int id, String regDate, String loginId, String loginPw, String name)
    {
        this.id = id;
        this.regDate = regDate;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
    }
}
