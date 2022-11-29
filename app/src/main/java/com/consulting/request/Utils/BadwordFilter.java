package com.consulting.request.Utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BadwordFilter {
    private ArrayList<String> bw = new ArrayList<>();
    private String char_filter = "*";
    public BadwordFilter() {

    }

    public void setup() {
        bw.add("시발");
        bw.add("씨발");
        bw.add("ㅆㅂ");
        bw.add("ㅅㅂ");
        bw.add("병신");
        bw.add("ㅂㅅ");
        bw.add("ㅄ");
        bw.add("장애");
        bw.add("새끼");
        bw.add("섀끼");
        bw.add("오나홀");
        bw.add("딜도");
        bw.add("창녀");
        bw.add("챙녀");
        bw.add("bitch");
        bw.add("pussy");
        bw.add("dick");
        bw.add("페니스");
        bw.add("penis");
        bw.add("푸씨");
        bw.add("보지");
        bw.add("자지");
        bw.add("꼬추");
        bw.add("강간");
        bw.add("꼭지");
        bw.add("성기");
        bw.add("fuck");
        bw.add("좆");
        bw.add("존나");
        bw.add("졸라");
        bw.add("씹");
        bw.add("좃");
        bw.add("좆");
        bw.add("고추");
        bw.add("질내");
        bw.add("느금마");
        bw.add("느개비");
        bw.add("닝기미");
        bw.add("니기미");
        bw.add("니개비");
    }

    public void addBadWord(String bw) {
        this.bw.add(bw);
    }

    public void removeBadWord(String bw) {
        this.bw.remove(bw);
    }

    public boolean isBadword(String input) {
        for (String badword : this.bw) {
            if (badword.length() == 1) {
                if (input.contains(badword))
                    return true;

                continue;
            }

            StringBuilder pattern = new StringBuilder();
            String sep = "[^가-힣a-zA-Z]*";
            int index = 0;
            for (String split : badword.split("")) {
                pattern.append(split);
                if (index < badword.length()-1)
                    pattern.append(sep);
                index++;
            }

            Pattern patt = Pattern.compile(pattern.toString());
            return patt.matcher(input).matches();
        }

        return false;
    }
}
